package com.flower.manager.service.article;

import com.flower.manager.dto.article.ArticleImportRequest;
import com.flower.manager.dto.article.ArticleResponse;
import com.flower.manager.entity.Article;
import com.flower.manager.enums.ArticleStatus;
import com.flower.manager.repository.ArticleRepository;
import com.flower.manager.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service de scrape/import bai viet tu website khac
 * 
 * Ho tro nhieu nguon tin tuc Viet Nam:
 * - VnExpress
 * - Kenh14
 * - Dantri
 * - Tuoi Tre
 * - Generic websites
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleScraperService {

    private final ArticleRepository articleRepository;

    private static final int TIMEOUT_MS = 10000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    /**
     * Import bai viet tu URL
     * Tra ve Article da luu (status = DRAFT)
     */
    @Transactional
    public ArticleResponse importFromUrl(ArticleImportRequest request) {
        String url = request.getUrl();
        log.info("Importing article from URL: {}", url);

        try {
            // Fetch va parse HTML
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();

            // Extract content theo domain
            ScrapedContent content = scrapeContent(doc, url);

            // Override with custom values if provided
            if (request.getDefaultAuthor() != null && !request.getDefaultAuthor().isBlank()) {
                content.setAuthor(request.getDefaultAuthor());
            }
            if (request.getDefaultTags() != null && !request.getDefaultTags().isBlank()) {
                content.setTags(request.getDefaultTags());
            }
            if (request.getCustomThumbnail() != null && !request.getCustomThumbnail().isBlank()) {
                content.setThumbnail(request.getCustomThumbnail());
            }

            // Tao Article entity
            Article article = Article.builder()
                    .title(content.getTitle())
                    .slug(SlugUtils.toUniqueSlug(content.getTitle()))
                    .summary(content.getSummary())
                    .content(content.getContent())
                    .thumbnail(content.getThumbnail())
                    .author(content.getAuthor() != null ? content.getAuthor() : "FlowerCorner")
                    .tags(content.getTags())
                    .status(ArticleStatus.DRAFT)
                    .aiGenerated(false)
                    .aiPrompt("Imported from: " + url)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Article saved = articleRepository.save(article);
            log.info("Imported article successfully. ID: {}, Title: {}", saved.getId(), saved.getTitle());

            return ArticleResponse.fromEntity(saved);

        } catch (IOException e) {
            log.error("Failed to import article from URL: {}", url, e);
            throw new RuntimeException("Khong the lay noi dung tu URL: " + e.getMessage());
        }
    }

    /**
     * Scrape content tu Document, tu dong detect domain
     */
    private ScrapedContent scrapeContent(Document doc, String url) {
        String domain = extractDomain(url);

        return switch (domain) {
            case "vnexpress.net" -> scrapeVnExpress(doc);
            case "kenh14.vn" -> scrapeKenh14(doc);
            case "dantri.com.vn" -> scrapeDantri(doc);
            case "tuoitre.vn" -> scrapeTuoiTre(doc);
            case "24h.com.vn" -> scrape24h(doc);
            case "hoavietnam.vn" -> scrapeHoaVietnam(doc);
            default -> scrapeGeneric(doc);
        };
    }

    /**
     * HoaVietnam.vn scraper - website chuyen ve hoa
     */
    private ScrapedContent scrapeHoaVietnam(Document doc) {
        ScrapedContent content = new ScrapedContent();

        // Title - h1
        Element titleEl = doc.selectFirst("h1, .entry-title, .page-title");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        // Summary from meta description
        Element metaDesc = doc.selectFirst("meta[name=description], meta[property=og:description]");
        content.setSummary(metaDesc != null ? metaDesc.attr("content") : "");

        // Main content - article or entry-content
        Element articleBody = doc.selectFirst("article, .entry-content, .post-content, main");
        if (articleBody != null) {
            // Remove non-content elements
            articleBody.select("script, style, nav, .sidebar, .related-posts, .comments, .share-buttons").remove();
            content.setContent(cleanHtml(articleBody.html()));
        } else {
            // Fallback to paragraphs
            Elements paragraphs = doc.select("p");
            StringBuilder sb = new StringBuilder();
            for (Element p : paragraphs) {
                if (p.text().length() > 30) {
                    sb.append("<p>").append(p.text()).append("</p>\n");
                }
            }
            content.setContent(sb.toString());
        }

        // Thumbnail
        Element imgEl = doc.selectFirst("meta[property=og:image]");
        if (imgEl != null) {
            content.setThumbnail(imgEl.attr("content"));
        } else {
            Element firstImg = doc.selectFirst("article img, .entry-content img, main img");
            content.setThumbnail(firstImg != null ? firstImg.absUrl("src") : "");
        }

        // Author
        Element authorEl = doc.selectFirst(".author, .byline, [rel=author]");
        content.setAuthor(authorEl != null ? authorEl.text() : "HoaVietnam");

        // Tags
        Elements tagEls = doc.select(".tags a, .categories a, .breadcrumb a");
        content.setTags(extractTags(tagEls));

        return content;
    }

    /**
     * VnExpress scraper
     */
    private ScrapedContent scrapeVnExpress(Document doc) {
        ScrapedContent content = new ScrapedContent();

        // Title
        Element titleEl = doc.selectFirst("h1.title-detail, h1.title_news_detail");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        // Summary/Description
        Element descEl = doc.selectFirst("p.description, p.lead");
        content.setSummary(descEl != null ? descEl.text() : "");

        // Main content
        Element articleBody = doc.selectFirst("article.fck_detail, .fck_detail");
        if (articleBody != null) {
            // Remove ads, related articles
            articleBody.select(".box_tinlienquan, .box_quangcao, script, style").remove();
            content.setContent(cleanHtml(articleBody.html()));
        }

        // Thumbnail
        Element imgEl = doc.selectFirst("meta[property=og:image]");
        content.setThumbnail(imgEl != null ? imgEl.attr("content") : "");

        // Author
        Element authorEl = doc.selectFirst("p.author_mail span, .author");
        content.setAuthor(authorEl != null ? authorEl.text() : "VnExpress");

        // Tags
        Elements tagEls = doc.select(".breadcrumb a, .tags a");
        content.setTags(extractTags(tagEls));

        return content;
    }

    /**
     * Kenh14 scraper
     */
    private ScrapedContent scrapeKenh14(Document doc) {
        ScrapedContent content = new ScrapedContent();

        Element titleEl = doc.selectFirst("h1.kbwc-title, .klw-new-title");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        Element descEl = doc.selectFirst(".knc-sapo, .klw-new-des");
        content.setSummary(descEl != null ? descEl.text() : "");

        Element articleBody = doc.selectFirst(".knc-content, .klw-new-content");
        if (articleBody != null) {
            articleBody.select("script, style, .box-related").remove();
            content.setContent(cleanHtml(articleBody.html()));
        }

        Element imgEl = doc.selectFirst("meta[property=og:image]");
        content.setThumbnail(imgEl != null ? imgEl.attr("content") : "");

        content.setAuthor("Kenh14");

        Elements tagEls = doc.select(".kbwcm-cate a, .tags a");
        content.setTags(extractTags(tagEls));

        return content;
    }

    /**
     * Dantri scraper
     */
    private ScrapedContent scrapeDantri(Document doc) {
        ScrapedContent content = new ScrapedContent();

        Element titleEl = doc.selectFirst("h1.title-page, h1.e-magazine__title");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        Element descEl = doc.selectFirst(".singular-sapo, h2.e-magazine__sapo");
        content.setSummary(descEl != null ? descEl.text() : "");

        Element articleBody = doc.selectFirst(".singular-content, .e-magazine__body");
        if (articleBody != null) {
            articleBody.select("script, style, .box-tinlienquan").remove();
            content.setContent(cleanHtml(articleBody.html()));
        }

        Element imgEl = doc.selectFirst("meta[property=og:image]");
        content.setThumbnail(imgEl != null ? imgEl.attr("content") : "");

        Element authorEl = doc.selectFirst(".author-name, .e-magazine__author");
        content.setAuthor(authorEl != null ? authorEl.text() : "Dan Tri");

        Elements tagEls = doc.select(".breadcrumb a, .singular-tag a");
        content.setTags(extractTags(tagEls));

        return content;
    }

    /**
     * Tuoi Tre scraper
     */
    private ScrapedContent scrapeTuoiTre(Document doc) {
        ScrapedContent content = new ScrapedContent();

        Element titleEl = doc.selectFirst("h1.article-title, h1.detail-title");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        Element descEl = doc.selectFirst("h2.sapo, .detail-sapo");
        content.setSummary(descEl != null ? descEl.text() : "");

        Element articleBody = doc.selectFirst(".detail-content, #main-detail-body");
        if (articleBody != null) {
            articleBody.select("script, style, .VCSortableIn498").remove();
            content.setContent(cleanHtml(articleBody.html()));
        }

        Element imgEl = doc.selectFirst("meta[property=og:image]");
        content.setThumbnail(imgEl != null ? imgEl.attr("content") : "");

        Element authorEl = doc.selectFirst(".author-info .name, .detail-author");
        content.setAuthor(authorEl != null ? authorEl.text() : "Tuoi Tre");

        Elements tagEls = doc.select(".detail-tag a, .tags a");
        content.setTags(extractTags(tagEls));

        return content;
    }

    /**
     * 24h.com.vn scraper
     */
    private ScrapedContent scrape24h(Document doc) {
        ScrapedContent content = new ScrapedContent();

        Element titleEl = doc.selectFirst("h1.cate-title, h1");
        content.setTitle(titleEl != null ? titleEl.text() : doc.title());

        Element descEl = doc.selectFirst(".cate-sapo, .sapo");
        content.setSummary(descEl != null ? descEl.text() : "");

        Element articleBody = doc.selectFirst(".cate-body, .content");
        if (articleBody != null) {
            articleBody.select("script, style").remove();
            content.setContent(cleanHtml(articleBody.html()));
        }

        Element imgEl = doc.selectFirst("meta[property=og:image]");
        content.setThumbnail(imgEl != null ? imgEl.attr("content") : "");

        content.setAuthor("24h");
        content.setTags("tin tuc");

        return content;
    }

    /**
     * Generic scraper cho cac website khac
     */
    private ScrapedContent scrapeGeneric(Document doc) {
        ScrapedContent content = new ScrapedContent();

        // Title - try multiple selectors
        Element titleEl = doc.selectFirst("h1, [class*=title], [class*=headline]");
        if (titleEl == null) {
            content.setTitle(doc.title());
        } else {
            content.setTitle(titleEl.text());
        }

        // Summary - try meta description first
        Element metaDesc = doc.selectFirst("meta[name=description], meta[property=og:description]");
        if (metaDesc != null) {
            content.setSummary(metaDesc.attr("content"));
        } else {
            Element descEl = doc.selectFirst("[class*=sapo], [class*=summary], [class*=excerpt], [class*=description]");
            content.setSummary(descEl != null ? descEl.text() : "");
        }

        // Content - try article, main content areas
        Element articleBody = doc.selectFirst("article, [class*=content], [class*=body], main");
        if (articleBody != null) {
            // Remove common non-content elements
            articleBody.select(
                    "script, style, nav, header, footer, aside, [class*=comment], [class*=related], [class*=share], [class*=social]")
                    .remove();
            content.setContent(cleanHtml(articleBody.html()));
        } else {
            // Fallback: get all paragraphs
            Elements paragraphs = doc.select("p");
            StringBuilder sb = new StringBuilder();
            for (Element p : paragraphs) {
                if (p.text().length() > 50) { // Skip short paragraphs
                    sb.append("<p>").append(p.text()).append("</p>\n");
                }
            }
            content.setContent(sb.toString());
        }

        // Thumbnail
        Element imgEl = doc.selectFirst("meta[property=og:image]");
        if (imgEl != null) {
            content.setThumbnail(imgEl.attr("content"));
        } else {
            Element firstImg = doc.selectFirst("article img, [class*=content] img");
            content.setThumbnail(firstImg != null ? firstImg.absUrl("src") : "");
        }

        // Author
        Element authorEl = doc.selectFirst("[class*=author], [rel=author]");
        content.setAuthor(authorEl != null ? authorEl.text() : "");

        // Tags from keywords meta
        Element keywordsMeta = doc.selectFirst("meta[name=keywords]");
        if (keywordsMeta != null) {
            content.setTags(keywordsMeta.attr("content"));
        }

        return content;
    }

    /**
     * Extract domain tu URL
     */
    private String extractDomain(String url) {
        try {
            Pattern pattern = Pattern.compile("https?://(?:www\\.)?([^/]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1).toLowerCase();
            }
        } catch (Exception e) {
            log.warn("Failed to extract domain from URL: {}", url);
        }
        return "unknown";
    }

    /**
     * Extract tags tu Elements
     */
    private String extractTags(Elements tagElements) {
        List<String> tags = new ArrayList<>();
        for (Element el : tagElements) {
            String text = el.text().trim();
            if (!text.isEmpty() && text.length() < 50) {
                tags.add(text);
            }
            if (tags.size() >= 5)
                break; // Max 5 tags
        }
        return String.join(", ", tags);
    }

    /**
     * Clean HTML content
     */
    private String cleanHtml(String html) {
        // Remove excessive whitespace
        html = html.replaceAll("\\s+", " ");
        // Remove empty tags
        html = html.replaceAll("<(p|div|span)>\\s*</(p|div|span)>", "");
        // Remove inline styles (optional, keep for formatting)
        // html = html.replaceAll("style=\"[^\"]*\"", "");
        return html.trim();
    }

    /**
     * Inner class to hold scraped content
     */
    @lombok.Data
    public static class ScrapedContent {
        private String title = "";
        private String summary = "";
        private String content = "";
        private String thumbnail = "";
        private String author = "";
        private String tags = "";
    }
}
