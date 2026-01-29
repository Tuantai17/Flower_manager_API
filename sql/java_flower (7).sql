-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 29, 2026 at 09:10 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `java_flower`
--

-- --------------------------------------------------------

--
-- Table structure for table `articles`
--

CREATE TABLE `articles` (
  `id` bigint(20) NOT NULL,
  `ai_generated` bit(1) NOT NULL,
  `ai_prompt` text DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `content` longtext DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `published_at` datetime(6) DEFAULT NULL,
  `scheduled_at` datetime(6) DEFAULT NULL,
  `slug` varchar(255) NOT NULL,
  `status` enum('ARCHIVED','DRAFT','PUBLISHED','SCHEDULED') NOT NULL,
  `summary` text DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `thumbnail` varchar(500) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `articles`
--

INSERT INTO `articles` (`id`, `ai_generated`, `ai_prompt`, `author`, `content`, `created_at`, `published_at`, `scheduled_at`, `slug`, `status`, `summary`, `tags`, `thumbnail`, `title`, `updated_at`) VALUES
(2, b'0', 'Imported from: https://znews.vn/thuong-thuc-nghe-thuat-cam-hoa-nhat-ban-tai-tphcm-post1026876.html', 'FlowerCorner Team', '<section class=\"main\"> <p class=\"the-article-summary\">Ngày 14/12 tại Palace Saigon, triển lãm hoa đào Nhật Bản chủ đề \"Sakura và nghệ thuật cắm hoa truyền thống của người Nhật\" đã được tổ chức, thu hút sự chú ý của công chúng yêu hoa.</p> <div class=\"the-article-body\"> <p>Buổi triển lãm do thương hiệu hoa Lavendershop94 phối hợp cùng Công ty Naniwa Flower Auction - đơn vị chuyên kinh doanh hoa cắt cành tại Osaka - tổ chức, nhằm giới thiệu đến người yêu hoa cùng chủ tiệm các loại hoa độc đáo, chất lượng cao của Nhật Bản.</p> <p>Tham dự triển lãm, khách hàng không chỉ trực tiếp chiêm ngưỡng nhiều loại hoa đẹp do Công ty Naniwa cung cấp, mà còn được giao lưu với ông Okuda Yoshihiko - TGĐ Naniwa, chuyên gia trồng hoa cát tường Shimanouchi Isao, và nhà thiết kế hoa Nakamura Tomoko.</p> <table class=\"picture\" align=\"center\"> <tbody> <tr> <td class=\"pic\"><img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/10_1.jpg\" title=\"Lavendershop94 ảnh 1\" alt=\"Lavendershop94 anh 1\"></td> </tr> <tr> <td class=\"pCaption caption\">Chị Hương Giang (ngoài cùng bên phải) - CEO Lavendershop94 - tặng hoa cho các đại diện Công ty Naniwa.</td> </tr> </tbody> </table> <p><span style=\"font-size: 16px;\">Phát biểu tại buổi triển lãm, chị Hương Giang - CEO Lavendershop94 - cho biết: \"Lavendershop94 là một trong những đơn vị cung cấp hoa lâu năm tại TP.HCM. Chúng tôi luôn cố gắng đem đến cho khách hàngnhững loại hoa chất lượng, cả hoa trong nước và nhập khẩu. Buổi triển lãm hôm nay sẽ là cầu nối giữa khách hàng với thương hiệu Lavendershop94 và Công ty Naniwa, để các loại hoa cắt cành của Nhật Bản đến tay người tiêu dùng Việt Nam\".</span></p> <table class=\"picture\" align=\"center\"> <tbody> <tr> <td class=\"pic\"><img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/11_1.jpg\" title=\"Lavendershop94 ảnh 2\" alt=\"Lavendershop94 anh 2\"></td> </tr> <tr> <td class=\"pCaption caption\"> <p>Các loại hoa cắt cành Nhật Bản được trưng bày tại triển lãm.</p></td> </tr> </tbody> </table> <p><span style=\"font-size: 16px;\">Hàng trăm loại hoa cắt cành từ đất nước mặt trời mọc đã được trưng bày và khoe sắc tại triển lãm, mang đến cho khách tham quan những trải nghiệm tuyệt vời. Bên cạnh việc chiêm ngưỡng những loại hoa lạ, đầy màu sắc, khách tham dự còn được thưởng thức màn biểu diễn cắm hoa theo phong cách Nhật Bản do cô Nakamura Tomoko thực hiện.</span></p> <p>Cô Nakamura Tomoko là nhà thiết kế hoa chuyên nghiệp và nổi tiếng, có 26 năm kinh nghiệm trong lĩnh vực sáng tạo sản phẩm hoa trang trí hội trường, khách sạn, tiệc cưới. Cô từng hợp tác với các shop hoa hàng đầu Nhật Bản và là trợ lý cho các buổi trang trí tiệc đón tiếp nguyên thủ quốc gia, với cách phối màu đặc trưng.</p> <table class=\"picture gallery\" align=\"center\"> <tbody> <tr> <td class=\"pic\"><img title=\"Lavendershop94 ảnh 3\" alt=\"Lavendershop94 anh 3\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/13.jpg\" width=\"1300\" height=\"815\"><img title=\"Lavendershop94 ảnh 4\" alt=\"Lavendershop94 anh 4\" src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/14.jpg\" width=\"1760\" height=\"1290\"></td> </tr> <tr> <td class=\"pCaption caption\">Tại buổi triển lãm, cô Tomoko đã giới thiệu đến&nbsp;khách mời&nbsp;tác phẩm trang trí cho năm mới và tác phẩm hoa bó theo phong cách Nhật Bản.</td> </tr> </tbody> </table> <p><span style=\"font-size: 16px;\">Tác phẩm \"Bốn mùa sắc hoa Nhật Bản\" sử dụng các loại hoa cắt cành Nhật, phối các màu truyền thống của xứ sở anh đào. Cô Tomoko cho biết ở Nhật có bốn mùa và mỗi mùa có nhiệt độ, cảnh sắc cũng như nhiều loại hoa khác nhau. Người Nhật từ xưa rất quan trọng việc sử dụng hoa theo mùa, cũng như kết hợp màu sắc hài hòa.</span></p> <p>Trong tác phẩm \"Bốn mùa sắc hoa Nhật Bản\", kanzakura (hoa anh đào Nhật Bản) và boke được sử dụng như biểu tượng cho mùa xuân ấm áp, hoa cẩm tú cầu biểu tượng cho mùa hè. Hoa cúc và lá khô tượng trưng cho mùa thu, và mùa đông được thể hiện bằng hoa unryuuyanagi và cát tường trắng. Cuối cùng, hoa thược dược là biểu tượng cho sức mạnh của trời và đất.</p> <table class=\"picture\" align=\"center\"> <tbody> <tr> <td class=\"pic\"><img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/15.jpg\" title=\"Lavendershop94 ảnh 5\" alt=\"Lavendershop94 anh 5\"></td> </tr> <tr> <td class=\"pCaption caption\">Tác phẩm \"Harmony\" và hoa bó theo phong cách Nhật Bản.</td> </tr> </tbody> </table> <p>Tác phẩm thứ 2 có tên \"Harmony\" thể hiện sự hòa hợp giữa Việt Nam và Nhật Bản. Tác phẩm có nội dung hướng đến mùa Tết, cũng sử dụng bằng các loại hoa cắt cành của Nhật. Cô Tomoko chia sẻ những năm gần đây, các nhà thiết kế hoa ở Nhật thường hướng đến việc tạo hình, sắp xếp tạo dáng bình hoa theo dạng tròn. Trong \"Harmony\", cô sử dụng các loại hoa lá với màu sắc như vàng, cam, đỏ phổ biến tại cả hai quốc gia.</p> <p>Tác phẩm hoa bó theo phong cách Nhật Bản sử dụng nhiều dạng lá khác nhau để tạo cảm giác hòa vào thiên nhiên. Nhà thiết kế đã dùng 2 tông màu chủ đạo là trắng kết hợp với xanh lá, mát mắt nhưng không kém phần tinh tế. Sự phối hợp màu sắc nhẹ nhàng nhưng vẫn giữ được sự sang trọng tạo cảm giác tươi mới cho người nhận bó hoa này.</p> <table class=\"picture\" align=\"center\"> <tbody> <tr> <td class=\"pic\"><img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/16.jpg\" title=\"Lavendershop94 ảnh 6\" alt=\"Lavendershop94 anh 6\"></td> </tr> <tr> <td class=\"pCaption caption\">Khách mời tham quan và chụp hình với các loại hoa.</td> </tr> </tbody> </table> <p><span style=\"font-size: 16px;\">Buổi triển lãm với không gian trưng bày đa dạng, các loại hoa phong phú cùng phần biểu diễn cắm hoa theo phong cách Nhật Bản đã đem đến cho các khách mời những giây phút thăng hoa cùng nghệ thuật và trải nghiệm khó quên.</span></p> <div class=\"notebox ncenter\"> <p>Lavendershop94 Flower</p> <p>Địa chỉ:&nbsp; 94 Điện Biên Phủ, phường Đa Kao, quận 1, TP.HCM</p> <p>ĐT: 0966188588</p> <p>Email: Lavendershop94@gmail.com</p> <p>Website:&nbsp;<a href=\"https://www.facebook.com/Lavendershop94flowers\" target=\"_blank\" rel=\"nofollow\">Lavendershop94 Flowers</a></p> </div> </div> <div id=\"sidebarArticle\" class=\"hide\"> <article class=\"article-item type-text pr\" article-id=\"1026805\" topic-id=\"5600\" serie-id=\"\"> <p class=\"article-thumbnail\"><a href=\"https://znews.vn/iphone-xs-max-gia-tu-15-2-trieu-dong-tai-di-dong-viet-post1026805.html\"> <img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/unhuai2/2019_12_18/COVER.JPG\" alt=\"iPhone XS Max gia tu 15,2 trieu dong tai Di Dong Viet hinh anh\"> </a></p> </article> <article class=\"article-item type-text pr\" article-id=\"1026810\" topic-id=\"\" serie-id=\"\"> <p class=\"article-thumbnail\"><a href=\"https://znews.vn/yeu-to-tao-nen-suc-hut-khong-bao-gio-cu-cho-ha-long-post1026810.html\"> <img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/wyhktpu/2019_12_18/3_7.JPG\" alt=\"Yeu to tao nen suc hut khong bao gio cu cho Ha Long hinh anh\"> </a></p> </article> <article class=\"article-item type-text picked-featured short\" article-id=\"1623884\" topic-id=\"1000,2000,3883,4009,4064,7121\" serie-id=\"\"> <p class=\"article-thumbnail\"><a href=\"https://znews.vn/gia-xang-tang-len-gan-19000-donglit-post1623884.html\"> <img src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" data-src=\"https://photo.znews.vn/Uploaded/rohuoct/2024_07_24/phamthang_znews.jpg\" alt=\"Gia xang tang len gan 19.000 dong/lit hinh anh\"> </a></p> </article> </div> <div class=\"the-article-credit\"> <p class=\"author\">Diệp Trà</p> </div> <p class=\"the-article-tags\"><strong>Lavendershop94</strong> <a href=\"https://znews.vn/hoa%2Dc%E1%BA%AFt%2Dc%C3%A0nh%2Dnh%E1%BA%ADt%2Db%E1%BA%A3n-tin-tuc.html\" title=\"Hoa cắt cành Nhật Bản\">Hoa cắt cành Nhật Bản</a> <a href=\"https://znews.vn/hoa%2Dnh%E1%BA%ADt-tin-tuc.html\" title=\"Hoa Nhật\">Hoa Nhật</a></p> </section> <div id=\"ZingNews_Masthead_Inline_1\" style=\"display:none;\"></div> <section class=\"section recommendation hide\"> <div class=\"section-content\"> <div class=\"article-list layout-grid-3\"> </div> </div> </section>', '2026-01-29 14:57:20.000000', '2026-01-29 14:57:25.000000', NULL, 'thuong-thuc-nghe-thuat-cam-hoa-nhat-ban-tai-tphcm-40669', 'PUBLISHED', 'Ngày 14/12 tại Palace Saigon, triển lãm hoa đào Nhật Bản chủ đề \"Sakura và nghệ thuật cắm hoa truyền thống của người Nhật\" đã được tổ chức, thu hút sự chú ý của công chúng yêu hoa.', 'Lavendershop94', 'https://photo.znews.vn/w1250/Uploaded/ngtmns/2019_12_18/15.jpg', 'Thưởng thức nghệ thuật cắm hoa Nhật Bản tại TP.HCM', '2026-01-29 14:57:25.000000'),
(3, b'0', 'Imported from: https://tuoitre.vn/tam-su-cua-chu-shop-hoa-20240309081009151.htm', 'FlowerCorner Team', '<p class=\"\">Em là chủ shop <a class=\"link-inline-content\" href=\"/hoa.html\" title=\"hoa\" data-rel=\"follow\">hoa</a>.</p> <p class=\"\">Em đọc báo biết rằng khởi nguồn của ngày 8-3 là phong trào đấu tranh đòi <a class=\"VCCTagItemInNews\" data-zoneid=\"0\" data-id=\"0\" href=\"https://tuoitre.vn/quyen-binh-dang.html\" target=\"_blank\" title=\"quyền bình đẳng\">quyền bình đẳng</a> của nữ công nhân lao động, ngày nay trở thành ngày đề cao vai trò và bình quyền của phụ nữ trong xã hội.</p> <p class=\"\">Đó là em đọc trên báo. Còn là chủ shop hoa, em thấy với nhiều người, hình như ngày <a class=\"link-inline-content\" href=\"/8-3.html\" title=\"8-3\" data-rel=\"follow\">8-3</a> chỉ là ngày tặng quà và đòi quà.</p><!-- bonewsrelation --> <div class=\"VCSortableInPreviewMode alignRight\" type=\"RelatedNewsBox\" data-style=\"align-right\" relatednewsboxtype=\"type-3\"> <div class=\"kbwscwl-relatedbox type-3 tuoitre\" style=\"\"> <!-- react-text: 99 --> <!-- /react-text --> <ul class=\"kbwscwlr-list\"> <li class=\"kbwscwlrl\" data-date=\"07/03/2024 14:35\" data-id=\"20240307102605135\" data-avatar=\"https://cdn2.tuoitre.vn/471584752817336320/2024/3/7/oig1dcz3kjlgh2adx-1709779824894190722721.jpg\" data-url=\"/8-3-dan-ong-tang-qua-de-du-trend-lam-mau-coi-rat-di-hom-20240307102605135.htm\" data-title=\"8-3: Đàn ông tặng quà để đu trend, làm màu coi rất dị hợm\"> <h4 class=\"kbwscwlrl-title\"><a target=\"_blank\" href=\"/8-3-dan-ong-tang-qua-de-du-trend-lam-mau-coi-rat-di-hom-20240307102605135.htm\" class=\"title link-callout\">8-3: Đàn ông tặng quà để đu trend, làm màu coi rất dị hợm</a><a target=\"_blank\" href=\"/8-3-dan-ong-tang-qua-de-du-trend-lam-mau-coi-rat-di-hom-20240307102605135.htm\" class=\"kbwscwlrl-next link-callout\">ĐỌC NGAY</a> <div style=\"clear: both;\"></div><!-- react-text: 106 --> <!-- /react-text --></h4></li> </ul> </div> </div><!-- eonewsrelation --> <p class=\"\">Em cảm động biết bao khi có mấy anh phụ huynh cất công đến mua hoa tặng cô giáo, mấy ông chồng cặm cụi mua hoa tặng vợ để vợ vui, mấy chàng trai trẻ mua hoa làm lòng thành bày tỏ tình yêu...</p> <p class=\"\">Cũng có người tâm sự không mua hoa, quà mà chỉ tặng bằng những việc làm thiết thực cho vợ, người yêu, đồng nghiệp.</p> <p class=\"\">Nhưng em cũng chứng kiến nhiều anh đến shop em mua hoa nói là tặng cho xong nghĩa vụ, nhiều chị nhận hoa thì chụp ảnh đăng phây khoe xong rồi để đó, hoa giả - hoa thật mua từ shop em tặng xong có nhiều người bỏ rơi quên lấy về...</p> <p class=\"\">Có cả anh đặt shop em mấy lẵng hoa ship đến mấy cô gái với lời nhắn gửi như nhau: Anh chỉ yêu mình em!</p> <p class=\"\">Hoa vốn đẹp vậy mà đằng sau đó cũng lắm chuyện cười ra nước mắt!</p> <div type=\"RelatedOneNews\" class=\"VCSortableInPreviewMode\"> <a href=\"/dua-voi-co-ha-ti-20240304102557248.htm\" style=\"padding: 0px;\"><img src=\"https://cdn2.tuoitre.vn/471584752817336320/2024/3/4/d38eb474-48d7-45ee-b723-f923170c92b4-17095232400441025842954-178-0-818-1024-crop-1709523271402720573236.jpg\" style=\"float: left; width: 150px; height: 120px; margin-right: 10px;\" alt=\"Đùa với cô hả Tí?\" title=\"Tâm sự của chủ shop hoa- Ảnh 1.\" fetchpriority=\"high\" width=\"100%\" height=\"auto\"></a><a href=\"/dua-voi-co-ha-ti-20240304102557248.htm\" style=\"text-align: left;\" class=\"OneNewsTitle\">Đùa với cô hả Tí?</a> <p class=\"VCObjectBoxRelatedNewsItemSapo\">Cô giáo phát hiện trong lớp có cu Tí hay thò tay lấy đồ của bạn.</p> </div> <div class=\"readmore-body-box d-none\"> <a href=\"#\" class=\"readmore-body-btn\" title=\"Đọc tiếp\"> <svg width=\"22\" height=\"17\" viewBox=\"0 0 22 17\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"> <rect width=\"22\" height=\"17\" fill=\"url(#pattern0_2234_10086)\" /> <defs> <pattern id=\"pattern0_2234_10086\" patternContentUnits=\"objectBoundingBox\" width=\"1\" height=\"1\"> <use xlink:href=\"#image0_2234_10086\" transform=\"matrix(0.0125 0 0 0.016129 -0.0625 -0.225806)\" /> </pattern> <image id=\"image0_2234_10086\" width=\"90\" height=\"90\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAAACXBIWXMAAAsTAAALEwEAmpwYAAAB4UlEQVR4nO3cSU7DQBBG4awIR8jAEQms4nBChhVwFBQWZPWjlhzE4O64K1a5LN4nsUFRpfIwBvXCsxkAAAAAAAAAoETSpaRbSU+S3oWj1OJR0o2k+VlXkaQrSa9fo5HzklqdcyUTuS52/ZXd3i5QZ2MJne7JqPNgCb2vfBNIe0toGBDaCaGdENoJoZ0QeoKhD5IaSav2a9d+L+dD0lbSsuf7Ltv5pZlhdxsy9K7jtekD5Wyr3/z0zLC7WYbl/PnpS1oUXr8wfpjSzLC7WYblrDpeu6758H2cmBl2N8uwml/Pu8LrG+OHKc0Mu5tlWM6hvUfV/MFpuq62zPv2mRl2tyFDo4DQTgjthNBOCO2E0E4I7YTQTgg9wdCjH0VG3m3I0KMfRUbezTIs7FFk5N0sw8IeRUbezTIs7FFk5N0sw8IeRUbebcjQKCC0E0I7IbQTQjshtBNCOyG0E0JPMPToR5GRdxsy9OhHkZF3swwLexQZeTfLsLBHkZF3swwLexQZeTfLsLBHkZF3GzI0CgjthNBOCO2E0E4IHTg0D0ap92YJzaN+nB71k57nhjrXltDz9hFj6OdZ0kV16G8PGCR2v8hrU+RfV/Ym3X/4A/lD+mfhPt0uzFcyAAAAAAAAAMz+jU86twUW8UdlXwAAAABJRU5ErkJggg==\" /> </defs> </svg> Đọc tiếp </a> <a class=\"return-thread-body-btn\" href=\"/nhom-chu-de.htm\" title=\"Về trang chủ đề\"> <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"> <rect width=\"12\" height=\"12\" fill=\"url(#pattern0_2234_10094)\" /> <defs> <pattern id=\"pattern0_2234_10094\" patternContentUnits=\"objectBoundingBox\" width=\"1\" height=\"1\"> <use xlink:href=\"#image0_2234_10094\" transform=\"translate(-0.161765 -0.117647) scale(0.0147059)\" /> </pattern> <image id=\"image0_2234_10094\" width=\"90\" height=\"90\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAAACXBIWXMAAAsTAAALEwEAmpwYAAADT0lEQVR4nO2cPWsUURSGj2IUFBsFl5XdzbzvHUxc7LbQYJFaERW1Fa0sxCLaiGBARf+A4lcp4i8IYuNnrW2M4Aa7EIiIX4XxIysXNySGRDOT7Nwzs+eBU2x39uFw7p0z946IYRiGYRiGYRiGMQeAS/N+Gp2STLJldjOQTBOdjWSa6Gwk00RnI5kmOhvJNNHZSKaJzkYyTXQ2kmmis5FME52NZJrobCSzQwFgmuQHAE0ATwHcAXCG5C4RWSN5Rotk/j8mAdwluTd30nMkubUgxkiebDQaPaKdHEtuzYt3AI6KVgoiuTUbAB6RrIkmiiaZc/ERwDHRQoFFt0jOkBwWLRRcdovkDRFZKxogeV6BkFYH45ZooQsq+6JooeCVPeOcOyRaKHJlA3jvnKuKFjQMlRqNRg/JbVEU7SY51J53/FgF2Q9FExrHpJVKZQuAywC+rFD4EdGE1sE/gBKAeyuo6vHBwcF1oonlLJASCOfcKQDfUwo/IdrQ/HIWwIGUvfu1yhHrvypbwuc2lKaqnXMDohHNB2hI3k/Rq2+LVrhIZYsC/FYQwOeEsidVto+lZIsSAFxJWtVxHNdFM1B4bLe/v39r0oURwGnRDtuVLYoA8CxhVd+UPABlVysAnE1Y0U9C55xLnHMDCUWPh845lwAoJRQ9FTrnXBLH8YaEor+FzjmXxCY6G6x1ZIQthhkB4Jxt7zKA5ItCPrBoorCP4NogeS3pUKm3t3dn6LxzRa1WK5P8mlD0hOoxqUZIPsj1UbEi7jQ41zb2hM49N0RRdJDkz6SSAYyGzj03uD/HDVKdXoqi6Hjo/NVTrVa3p+zJs9XcVHeARts+meTVFLuLv8I5dzj0f1FDvV5f7wdEfnbRXuyep+nFi8SIFJVSqbTJywLwCsCnVZCVtmVMxXFckSICYIc/fhVK7oLLQ/ukiPT19W0m+UaBZF/NF6SokBwOLbgrJnQAmqElA7iu5vpbB9/d/QrZkwvdLjSI9peCSO6XbgHA2wCiRwq7hVsKf4kywypuqrpHGGB7N9ZhwaN+QNT1swvnXOxlrLLgCT+0V3s1IhTlcnlj+1LmS/8tjWVW6rRf1HyfB/DYi/UvUtUfHDcMwzAMwzAMwzAMwzAMw5DC8xu/hHvn7sxdHwAAAABJRU5ErkJggg==\" /> </defs> </svg> Về trang Chủ đề </a> </div>', '2026-01-29 14:58:10.000000', '2026-01-29 14:58:13.000000', NULL, 'tam-su-cua-chu-shop-hoa-90828', 'PUBLISHED', 'Em là chủ shop hoa. Nhưng em thấy với nhiều người, hình như ngày 8-3 chỉ là ngày tặng quà và đòi quà.', '', 'https://cdn2.tuoitre.vn/thumb_w/1200/471584752817336320/2024/3/9/hoa-1709947100477460802039-35-0-417-730-crop-1709947142964798683820.jpg', 'Tâm sự của chủ shop hoa', '2026-01-29 14:58:13.000000'),
(4, b'0', 'Imported from: https://tuoitre.vn/shop-hoa-yeu-thuong-1063471.htm', 'FlowerCorner Team', '<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"desc_image slide_content\"> <tbody> <tr> <td><img alt=\"Shop hoa yêu thương của đội công tác xã hội tại cổng Trường ĐH Kinh tế TP.HCM - Ảnh: Ngọc Hiển\" class=\"ck_image\" src=\"//static.tuoitre.vn/tto/i/s626/2016/03/08/ee4e0d7e.jpg\"></td> </tr> <tr> <td class=\"ck_legend caption\">Shop hoa yêu thương của đội công tác xã hội tại cổng Trường ĐH Kinh tế TP.HCM - Ảnh: Ngọc Hiển</td> </tr> </tbody> </table> <p><em><strong>* Triển lãm mỹ thuật chủ đề “Nhịp sống yêu thương”</strong></em></p> <p>Đây là năm thứ bảy đội duy trì hoạt động bán hoa để gây quỹ giúp đỡ học sinh nghèo tại các tỉnh miền Tây. Để mở ra những shop hoa thời vụ này, các thành viên của đội đã cùng nhau góp vốn, góp công và thay phiên nhau trực bán hoa tại cả ba địa điểm.</p> <p>Bạn Nguyễn Thị Cẩm Tú, đội phó đội công tác xã hội, cho biết hoạt động này tuy nhỏ, số tiền thu lại không lớn nhưng giá trị của những bó hoa này mang nhiều ý nghĩa.</p> <p>“Mỗi người mua một bó hoa, thể hiện tình yêu thương đối với người thân nhưng đồng thời góp phần san sẻ tình cảm đó đến các học sinh nghèo có thêm chiếc áo mới, chiếc cặp mới hay đơn giản chỉ là một cuốn tập mới” - Tú nói.</p> <p>* Triển lãm mỹ thuật chủ đề “Nhịp sống yêu thương” của CLB họa sĩ nữ Ngân Hà sẽ được trưng bày tại Nhà văn hóa Phụ nữ TP.HCM vào sáng 8-3 với sự góp mặt của 30 tác phẩm lấy ý tưởng từ hình ảnh người phụ nữ, quê hương, đất nước…</p> <p>Tại đây, chiều cùng ngày cũng diễn ra buổi giao lưu “Hoa trên đất thép”, gặp gỡ và chia sẻ với nhà văn Mã Thiện Đồng và các nữ du kích Củ Chi được nhắc đến trong tác phẩm <em>Huyền thoại trong lòng đất</em> của tác giả Mã Thiện Đồng.</p> <p>Chương trình văn nghệ “Xinh tươi Việt Nam” cũng sẽ diễn ra vào buổi tối với sự tham gia của nhiều ca sĩ, nghệ sĩ, vào cửa miễn phí.</p> <div class=\"readmore-body-box d-none\"> <a href=\"#\" class=\"readmore-body-btn\" title=\"Đọc tiếp\"> <svg width=\"22\" height=\"17\" viewBox=\"0 0 22 17\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"> <rect width=\"22\" height=\"17\" fill=\"url(#pattern0_2234_10086)\" /> <defs> <pattern id=\"pattern0_2234_10086\" patternContentUnits=\"objectBoundingBox\" width=\"1\" height=\"1\"> <use xlink:href=\"#image0_2234_10086\" transform=\"matrix(0.0125 0 0 0.016129 -0.0625 -0.225806)\" /> </pattern> <image id=\"image0_2234_10086\" width=\"90\" height=\"90\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAAACXBIWXMAAAsTAAALEwEAmpwYAAAB4UlEQVR4nO3cSU7DQBBG4awIR8jAEQms4nBChhVwFBQWZPWjlhzE4O64K1a5LN4nsUFRpfIwBvXCsxkAAAAAAAAAoETSpaRbSU+S3oWj1OJR0o2k+VlXkaQrSa9fo5HzklqdcyUTuS52/ZXd3i5QZ2MJne7JqPNgCb2vfBNIe0toGBDaCaGdENoJoZ0QeoKhD5IaSav2a9d+L+dD0lbSsuf7Ltv5pZlhdxsy9K7jtekD5Wyr3/z0zLC7WYbl/PnpS1oUXr8wfpjSzLC7WYblrDpeu6758H2cmBl2N8uwml/Pu8LrG+OHKc0Mu5tlWM6hvUfV/MFpuq62zPv2mRl2tyFDo4DQTgjthNBOCO2E0E4I7YTQTgg9wdCjH0VG3m3I0KMfRUbezTIs7FFk5N0sw8IeRUbezTIs7FFk5N0sw8IeRUbebcjQKCC0E0I7IbQTQjshtBNCOyG0E0JPMPToR5GRdxsy9OhHkZF3swwLexQZeTfLsLBHkZF3swwLexQZeTfLsLBHkZF3GzI0CgjthNBOCO2E0E4IHTg0D0ap92YJzaN+nB71k57nhjrXltDz9hFj6OdZ0kV16G8PGCR2v8hrU+RfV/Ym3X/4A/lD+mfhPt0uzFcyAAAAAAAAAMz+jU86twUW8UdlXwAAAABJRU5ErkJggg==\" /> </defs> </svg> Đọc tiếp </a> <a class=\"return-thread-body-btn\" href=\"/nhom-chu-de.htm\" title=\"Về trang chủ đề\"> <svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"> <rect width=\"12\" height=\"12\" fill=\"url(#pattern0_2234_10094)\" /> <defs> <pattern id=\"pattern0_2234_10094\" patternContentUnits=\"objectBoundingBox\" width=\"1\" height=\"1\"> <use xlink:href=\"#image0_2234_10094\" transform=\"translate(-0.161765 -0.117647) scale(0.0147059)\" /> </pattern> <image id=\"image0_2234_10094\" width=\"90\" height=\"90\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAAACXBIWXMAAAsTAAALEwEAmpwYAAADT0lEQVR4nO2cPWsUURSGj2IUFBsFl5XdzbzvHUxc7LbQYJFaERW1Fa0sxCLaiGBARf+A4lcp4i8IYuNnrW2M4Aa7EIiIX4XxIysXNySGRDOT7Nwzs+eBU2x39uFw7p0z946IYRiGYRiGYRiGMQeAS/N+Gp2STLJldjOQTBOdjWSa6Gwk00RnI5kmOhvJNNHZSKaJzkYyTXQ2kmmis5FME52NZJrobCSzQwFgmuQHAE0ATwHcAXCG5C4RWSN5Rotk/j8mAdwluTd30nMkubUgxkiebDQaPaKdHEtuzYt3AI6KVgoiuTUbAB6RrIkmiiaZc/ERwDHRQoFFt0jOkBwWLRRcdovkDRFZKxogeV6BkFYH45ZooQsq+6JooeCVPeOcOyRaKHJlA3jvnKuKFjQMlRqNRg/JbVEU7SY51J53/FgF2Q9FExrHpJVKZQuAywC+rFD4EdGE1sE/gBKAeyuo6vHBwcF1oonlLJASCOfcKQDfUwo/IdrQ/HIWwIGUvfu1yhHrvypbwuc2lKaqnXMDohHNB2hI3k/Rq2+LVrhIZYsC/FYQwOeEsidVto+lZIsSAFxJWtVxHNdFM1B4bLe/v39r0oURwGnRDtuVLYoA8CxhVd+UPABlVysAnE1Y0U9C55xLnHMDCUWPh845lwAoJRQ9FTrnXBLH8YaEor+FzjmXxCY6G6x1ZIQthhkB4Jxt7zKA5ItCPrBoorCP4NogeS3pUKm3t3dn6LxzRa1WK5P8mlD0hOoxqUZIPsj1UbEi7jQ41zb2hM49N0RRdJDkz6SSAYyGzj03uD/HDVKdXoqi6Hjo/NVTrVa3p+zJs9XcVHeARts+meTVFLuLv8I5dzj0f1FDvV5f7wdEfnbRXuyep+nFi8SIFJVSqbTJywLwCsCnVZCVtmVMxXFckSICYIc/fhVK7oLLQ/ukiPT19W0m+UaBZF/NF6SokBwOLbgrJnQAmqElA7iu5vpbB9/d/QrZkwvdLjSI9peCSO6XbgHA2wCiRwq7hVsKf4kywypuqrpHGGB7N9ZhwaN+QNT1swvnXOxlrLLgCT+0V3s1IhTlcnlj+1LmS/8tjWVW6rRf1HyfB/DYi/UvUtUfHDcMwzAMwzAMwzAMwzAMw5DC8xu/hHvn7sxdHwAAAABJRU5ErkJggg==\" /> </defs> </svg> Về trang Chủ đề </a> </div>', '2026-01-29 14:58:31.000000', '2026-01-29 14:58:35.000000', NULL, 'shop-hoa-yeu-thuong-11821', 'PUBLISHED', 'TT - Đó là những shop hoa do đội công tác xã hội (ĐH Kinh tế TP.HCM) mở ra dịp 8-3 tại đường Nguyễn Đình Chiểu, Nguyễn Tri Phương và An Dương Vương (TP.HCM).', '', 'https://cdn2.tuoitre.vn/thumb_w/1200/tto/i/s626/2016/03/08/ee4e0d7e.jpg', 'Shop hoa yêu thương', '2026-01-29 14:58:35.000000');

-- --------------------------------------------------------

--
-- Table structure for table `banners`
--

CREATE TABLE `banners` (
  `id` bigint(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `subtitle` varchar(200) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `link_url` varchar(500) DEFAULT NULL,
  `button_text` varchar(50) DEFAULT 'Xem Ngay',
  `sort_order` int(11) DEFAULT 0,
  `active` tinyint(1) DEFAULT 1,
  `background_color` varchar(7) DEFAULT NULL,
  `text_color` varchar(7) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `banners`
--

INSERT INTO `banners` (`id`, `title`, `image_url`, `subtitle`, `description`, `link_url`, `button_text`, `sort_order`, `active`, `background_color`, `text_color`, `start_date`, `end_date`, `created_at`, `updated_at`) VALUES
(2, 'FlowerCorner Online', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767598321/flower-shop/products/tcc1sqvxikizu1i9qeid.webp', 'fsafs', 'fsaf', 'about', 'Xem Ngay', 1, 1, NULL, NULL, '2026-01-05 14:32:00', '2026-01-31 19:32:00', '2026-01-05 14:32:18', '2026-01-28 02:46:35'),
(4, 'Tài khoản đầu tiên', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767599949/flower-shop/products/bfs1lxrrupuautsd64ah.webp', 'ada', 'sda', '/shop', NULL, 0, 1, NULL, NULL, '2026-01-05 14:59:00', '2026-01-31 14:59:00', '2026-01-05 14:59:37', '2026-01-05 15:43:35'),
(5, 'giảm giá', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769666504/flower-shop/products/yejkjkdsj1vuo4oqpkjk.png', NULL, NULL, '/vouchers', 'Xem Ngay', 0, 1, NULL, NULL, '2026-01-28 12:28:00', '2026-02-07 12:28:00', '2026-01-29 12:32:43', '2026-01-29 13:01:48');

-- --------------------------------------------------------

--
-- Table structure for table `carts`
--

CREATE TABLE `carts` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `carts`
--

INSERT INTO `carts` (`id`, `created_at`, `updated_at`, `user_id`) VALUES
(2, '2025-12-22 16:21:30.000000', '2025-12-22 16:21:30.000000', 8),
(3, '2026-01-02 11:31:43.000000', '2026-01-02 11:31:43.000000', 12),
(4, '2026-01-08 11:11:32.000000', '2026-01-08 11:11:32.000000', 13),
(5, '2026-01-14 15:01:53.000000', '2026-01-14 15:01:53.000000', 21),
(6, '2026-01-28 23:40:56.000000', '2026-01-28 23:40:56.000000', 23);

-- --------------------------------------------------------

--
-- Table structure for table `cart_items`
--

CREATE TABLE `cart_items` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `price` decimal(38,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cart_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `slug` varchar(100) NOT NULL,
  `sort_order` int(11) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `parent_id`, `active`, `created_at`, `description`, `image_url`, `slug`, `sort_order`, `updated_at`) VALUES
(1, 'Hoa tươi', NULL, b'1', '2025-12-15 22:00:30.476001', 'Các loại hoa tươi', NULL, 'hoa-tuoi', 1, '2025-12-22 14:38:22.000000'),
(2, 'Hoa bó', 1, b'1', '2025-12-15 22:01:11.493236', NULL, NULL, 'hoa-bo', 1, '2025-12-15 22:01:11.493236'),
(3, 'Hoa giỏ', 1, b'1', '2025-12-15 22:01:18.238122', NULL, NULL, 'hoa-gio', 2, '2025-12-15 22:01:18.238122'),
(4, 'Hoa dịp lễ', NULL, b'1', '2025-12-15 22:01:37.477610', NULL, NULL, 'hoa-dip-le', 2, '2025-12-15 22:01:37.477610'),
(5, 'Hoa sinh nhật', 4, b'1', '2025-12-15 22:03:19.249079', NULL, NULL, 'hoa-sinh-nhat', 1, '2025-12-15 22:03:19.249603'),
(6, 'Hoa khai trương', 4, b'1', '2025-12-15 22:03:25.967080', NULL, NULL, 'hoa-khai-truong', 2, '2025-12-15 22:03:25.967080'),
(7, 'Hoa kỉ niệm', 4, b'1', '2025-12-15 22:03:39.837209', NULL, NULL, 'hoa-ki-niem', 3, '2025-12-15 22:03:39.837209'),
(8, 'Hoa cảm ơn', 4, b'1', '2025-12-15 22:03:51.569584', NULL, NULL, 'hoa-cam-on', 4, '2025-12-15 22:03:51.569584'),
(9, 'Hoa chia buồn', 4, b'1', '2025-12-15 22:03:59.295398', NULL, NULL, 'hoa-chia-buon', 5, '2025-12-15 22:03:59.295398'),
(10, 'Hoa chậu & cây cảnh', NULL, b'1', '2025-12-15 22:05:12.942438', NULL, NULL, 'hoa-chau-cay-canh', 3, '2025-12-15 22:05:12.950877'),
(11, 'Hoa chậu', 10, b'1', '2025-12-15 22:06:55.192158', NULL, NULL, 'hoa-chau', 1, '2025-12-15 22:06:55.192158'),
(12, 'Cây mini', 10, b'1', '2025-12-15 22:07:01.675313', NULL, NULL, 'cay-mini', 2, '2025-12-15 22:07:01.675313'),
(13, 'Cây phong thuỷ', 10, b'1', '2025-12-15 22:07:08.152655', NULL, NULL, 'cay-phong-thuy', 3, '2025-12-15 22:07:08.152655'),
(14, 'Set quà', NULL, b'1', '2025-12-15 22:07:37.241782', NULL, NULL, 'set-qua', 4, '2025-12-15 22:07:37.241782'),
(15, 'Hộp + Hoa', 14, b'1', '2025-12-15 22:08:23.954802', NULL, NULL, 'hop-hoa', 1, '2025-12-15 22:08:23.954802'),
(16, 'Gấu + Hoa', 14, b'1', '2025-12-15 22:08:35.653749', NULL, NULL, 'gau-hoa', 2, '2025-12-15 22:08:35.653749'),
(17, 'Socola + Hoa', 14, b'1', '2025-12-15 22:08:43.821198', NULL, NULL, 'socola-hoa', 3, '2025-12-15 22:08:43.821198'),
(18, 'Thiệp - Phụ kiện', 14, b'1', '2025-12-15 22:08:51.099658', NULL, NULL, 'thiep-phu-kien', 4, '2025-12-15 22:08:51.099658');

-- --------------------------------------------------------

--
-- Table structure for table `chat_messages`
--

CREATE TABLE `chat_messages` (
  `id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `message_type` varchar(20) DEFAULT NULL,
  `metadata` text DEFAULT NULL,
  `sender_id` bigint(20) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_type` varchar(10) NOT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `session_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chat_messages`
--

INSERT INTO `chat_messages` (`id`, `content`, `is_read`, `message_type`, `metadata`, `sender_id`, `sender_name`, `sender_type`, `sent_at`, `session_id`) VALUES
(112, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 10:43:37.000000', 8),
(113, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 10:44:04.000000', 8),
(114, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 10:44:05.000000', 8),
(115, 'Tôi cần hoa cho dịp sinh nhật', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 10:44:26.000000', 8),
(116, '🎉 Hoa cho dịp đặc biệt:\n• Sinh nhật: Bó hoa hồng mix, hoa hướng dương\n• Valentine/Lễ tình nhân: Hoa hồng đỏ, hộp hoa tim\n• 8/3, 20/10: Bó hoa mix, hoa ly\n• Kỷ niệm: Hoa lan, hoa cao cấp\n\nTôi có thể gợi ý mẫu cụ thể cho bạn!', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 10:44:27.000000', 8),
(117, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 10:52:33.000000', 8),
(118, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 10:52:34.000000', 8),
(119, '👨‍💼 Nhân viên null đang hỗ trợ bạn.', b'1', 'TEXT', NULL, NULL, 'Hệ thống', 'SYSTEM', '2026-01-03 10:58:13.000000', 8),
(120, '🤖 Chế độ AI đã được bật. Bot sẽ tự động trả lời.', b'1', 'TEXT', NULL, NULL, 'Hệ thống', 'SYSTEM', '2026-01-03 10:58:18.000000', 8),
(121, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 10:58:33.000000', 8),
(122, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 10:58:33.000000', 8),
(123, '👨‍💼 Nhân viên null đang hỗ trợ bạn.', b'1', 'TEXT', NULL, NULL, 'Hệ thống', 'SYSTEM', '2026-01-03 10:58:53.000000', 8),
(124, '🤖 Chế độ AI đã được bật. Bot sẽ tự động trả lời.', b'1', 'TEXT', NULL, NULL, 'Hệ thống', 'SYSTEM', '2026-01-03 11:06:41.000000', 8),
(125, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 11:06:52.000000', 8),
(126, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 11:06:52.000000', 8),
(127, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 11:23:00.000000', 8),
(128, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 11:23:00.000000', 8),
(129, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 11:57:50.000000', 8),
(130, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'1', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 11:57:51.000000', 8),
(131, 'hello', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-03 14:48:22.000000', 8),
(132, 'Chào bạn! 👋 Tôi là trợ lý ảo của FlowerCorner, cửa hàng hoa tươi chất lượng cao.\n\nBạn cần tìm loại hoa nào hay cần tư vấn gì ạ? 🌸', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 14:48:26.000000', 8),
(133, 'tôi có 200000 thì tôi mua được những loại hoa nào bên shop', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-03 14:48:40.000000', 8),
(134, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 14:48:40.000000', 8),
(135, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 16:13:49.000000', 9),
(136, 'Giá hoa hồng?', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-03 16:13:51.000000', 9),
(137, 'Rất tiếc, hiện tại FlowerCorner chưa có sản phẩm Hoa Hồng cụ thể trong danh sách sản phẩm đang bán rồi ạ. 😥\n\nBạn có thể tham khảo các mẫu hoa có sẵn với giá ưu đãi nhé:\n\n*   Bó hoa cẩm tú cầu xanh: 90.000đ\n*   Giỏ Hoa xinh: 300.000đ\n*   Hoa sinh nhật: 300.000đ\n\nNếu bạn cần tư vấn thêm về các mẫu này, cứ nhắn tôi nhé! 😊', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-03 16:13:55.000000', 9),
(138, 'sản phẩm hoa sinh nhật', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-05 16:17:18.000000', 8),
(139, 'Chào bạn! 😊 Hiện tại FlowerCorner có các sản phẩm thuộc danh mục Hoa sinh nhật sau:\n\n1.  Sản phẩm **dsadad** với giá **150.000đ**.\n2.  Sản phẩm **Hoa sinh nhật** với giá **300.000đ**.\n\nBạn muốn tham khảo thêm thông tin chi tiết về sản phẩm nào không ạ? 💐', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-05 16:17:21.000000', 8),
(140, 'hello', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-05 16:22:16.000000', 9),
(141, 'Chào bạn! 👋 FlowerCorner rất vui được phục vụ.\n\nBạn đang tìm kiếm loại hoa nào ạ? Hiện tại, chúng tôi đang có các sản phẩm nổi bật như:\n*   Bó hoa cẩm tú cầu xanh: 90.000đ\n*   Giỏ Hoa xinh: 300.000đ\n\nNếu bạn cần tư vấn thêm, cứ nhắn tôi nhé! 😊', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-05 16:22:21.000000', 9),
(142, 'sản phẩm cho dịp hoa sinh nhật', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-08 19:59:14.000000', 9),
(143, 'Chào bạn! 🎉 FlowerCorner có sẵn hai mẫu hoa rất đẹp cho dịp sinh nhật đây ạ:\n\n1.  **Hoa sinh nhật:** Với giá ưu đãi 300.000đ (Giá gốc 450.000đ).\n2.  **dsadad:** Giá chỉ 150.000đ (Giá gốc 200.000đ).\n\nBạn muốn tôi tư vấn chi tiết hơn về mẫu nào không ạ? 😊', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-08 19:59:19.000000', 9),
(144, 'tôi tính tặng quà kèm với hoa bên bạn có set quà nào như vậy không', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-08 20:18:44.000000', 8),
(145, 'Chào bạn! 😊 FlowerCorner có cung cấp set quà kết hợp hoa và quà tặng ạ.\n\nHiện tại, shop có sản phẩm **Chocolate Và Hoa** (thuộc danh mục Hộp + Hoa) với giá là **350.000đ**. 🍫💐\n\nBạn tham khảo sản phẩm này nhé! Nếu muốn xem thêm, shop còn có các danh mục set quà như Gấu + Hoa và Socola + Hoa nữa ạ.', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-08 20:18:49.000000', 8),
(146, 'hiện tại tôi đang có 200000 thì đủ để mua được sản phẩm nào không ạ', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-08 20:19:20.000000', 8),
(147, 'Chào bạn! 😊 Với ngân sách **200.000đ**, bạn hoàn toàn có thể chọn mua được những sản phẩm sau tại FlowerCorner ạ:\n\n1.  **Bó hoa cẩm tú cầu xanh** (Danh mục Hoa bó) với giá **90.000đ**. 💙\n2.  Sản phẩm **dsadad** (Danh mục Hoa sinh nhật) với giá **150.000đ**. 🎂\n\nBạn quan tâm đến sản phẩm nào hơn hay muốn tư vấn thêm không ạ? ✨', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-08 20:19:23.000000', 8),
(148, 'Giá hoa hồng?', b'0', 'TEXT', NULL, 12, 'tuan tai', 'USER', '2026-01-08 20:31:14.000000', 8),
(149, 'Chào bạn! 😊\n\nHiện tại, trong danh sách sản phẩm cụ thể của FlowerCorner, chúng tôi chưa có sản phẩm nào tên là \"Hoa hồng\" ạ. 🙏\n\nTuy nhiên, shop có nhiều lựa chọn hoa tươi khác mà bạn có thể tham khảo:\n\n*   **Bó hoa cẩm tú cầu xanh** (Danh mục Hoa bó): **90.000đ** 💙\n*   **Giỏ Hoa xinh** (Danh mục Hoa giỏ): **300.000đ** 🧺\n*   **Chocolate Và Hoa** (Set quà): **350.000đ** 🍫\n\nBạn quan tâm đến loại hoa nào trong danh sách trên không ạ? ✨', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-08 20:31:19.000000', 8),
(150, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-14 14:59:10.000000', 10),
(151, 'tôi cần sản phẩm về dịp sinh nhật sắp tới bạn tôi', b'0', 'TEXT', NULL, 21, NULL, 'USER', '2026-01-14 14:59:35.000000', 10),
(152, 'Chào bạn! Chúc mừng sinh nhật bạn của bạn nhé! 🎁\n\nFlowerCorner có nhiều lựa chọn hoa và quà tặng rất phù hợp cho dịp sinh nhật ạ. Bạn có thể tham khảo các sản phẩm sau:\n\n1.  **Hoa sinh nhật** với giá **300.000đ** (đang giảm giá).\n2.  Sản phẩm **dsadad** với giá **150.000đ** (đang giảm giá).\n\nNếu bạn muốn chọn set quà kết hợp, có:\n*   **Chocolate Và Hoa** với giá **350.000đ**.\n\nHoặc bạn có thể chọn một **Giỏ Hoa xinh** giá **300.000đ** hoặc **Bó hoa cẩm tú cầu xanh** giá **90.000đ** cũng rất xinh ạ! 🥰\n\nBạn quan tâm đến sản phẩm nào nhất ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-14 14:59:40.000000', 10),
(153, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-14 15:26:01.000000', 11),
(154, 'Giá hoa hồng?', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-27 19:39:15.000000', 9),
(155, 'Rất tiếc, hiện tại FlowerCorner vẫn chưa có sản phẩm Hoa Hồng cụ thể trong danh sách sản phẩm đang bán rồi ạ. 😔\n\nBạn có thể tham khảo các mẫu hoa và set quà có sẵn với giá ưu đãi nhé:\n\n*   **Bó hoa cẩm tú cầu xanh:** Chỉ 90.000đ\n*   **Giỏ Hoa xinh:** 300.000đ\n*   **Hoa sinh nhật:** 300.000đ\n*   **Chocolate Và Hoa:** 350.000đ\n\nNếu bạn cần tư vấn thêm về các mẫu này, cứ nhắn tôi nhé! 😊', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-27 19:39:21.000000', 9),
(156, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-27 21:42:42.000000', 12),
(157, 'Giá hoa hồng?', b'0', 'TEXT', NULL, NULL, 'Khách', 'USER', '2026-01-27 21:42:45.000000', 12),
(158, '🌹 Hoa hồng của chúng tôi có nhiều mức giá:\n• Bó hồng nhỏ (5-7 bông): từ 150,000đ\n• Bó hồng trung (10-15 bông): từ 350,000đ\n• Bó hồng lớn (20+ bông): từ 500,000đ\n\nBạn muốn xem mẫu cụ thể không ạ?', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó hoa cẩm tú cầu xanh\",\"slug\":\"bo-hoa-cam-tu-cau-xanh\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"350.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa xinh\",\"slug\":\"gio-hoa-xinh-2xqt\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Hoa sinh nhật\",\"slug\":\"hoa-sinh-nhat-084y\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-27 21:42:45.000000', 12),
(159, 'Xin chào! 🌸 Tôi là trợ lý ảo của Flower Shop.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn hoa\n• Thông tin giao hàng, thanh toán\n• Chính sách đổi trả\n\nBạn cần hỗ trợ gì ạ?', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 00:08:04.000000', 13),
(160, 'tôi cần 1 bó hoa tặng người yêu dịp kỉ niệm ngày quen nhau', b'0', 'TEXT', NULL, 23, NULL, 'USER', '2026-01-29 00:08:29.000000', 13),
(161, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó Hoa Tulip Trắng Hồng\",\"slug\":\"bo-hoa-tulip-trang-hong\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"90.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa Cúc Trắng\",\"slug\":\"gio-hoa-cuc-trang\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Bó Hoa Hồng Spray Phối Nơ Ren\",\"slug\":\"bo-hoa-hong-spray-phoi-no-ren\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"1.199.000đ\",\"salePrice\":\"300.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 00:08:30.000000', 13),
(162, 'Giá hoa hồng?', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 11:25:28.000000', 10),
(163, 'Chào bạn! FlowerCorner có rất nhiều sản phẩm hoa hồng với các mức giá khác nhau tùy theo kiểu dáng bó, giỏ hoặc kệ ạ. 🌹\n\nBạn có thể tham khảo một số sản phẩm hoa hồng nổi bật sau:\n\n**Các loại Bó Hoa Hồng:**\n\n*   **Bó Hoa Hồng Vàng:** **450.000đ**\n*   **Bó Hoa Hồng Trắng Hồng:** **480.000đ**\n*   **Bó Hoa Hồng Spray Phối Nơ:** **580.000đ**\n*   **Bó Hoa Hồng Garden Phối Nơ:** **1.099.000đ** (đang giảm từ 1.200.000đ)\n\n**Giỏ Hoa Hồng:**\n\n*   **Giỏ Hoa Hồng** (cơ bản): **300.000đ**\n*   **Giỏ Hoa Hồng Mix Hoa Cẩm Chướng:** **350.000', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó Hoa Tulip Trắng Hồng\",\"slug\":\"bo-hoa-tulip-trang-hong\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"430.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa Cúc Trắng\",\"slug\":\"gio-hoa-cuc-trang\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Bó Hoa Hồng Spray Phối Nơ Ren\",\"slug\":\"bo-hoa-hong-spray-phoi-no-ren\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"1.299.000đ\",\"salePrice\":\"1.199.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 11:25:33.000000', 10),
(164, 'tồi cần kiếm sản phẩm hoa bó bên shop có không ạ', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 13:56:13.000000', 10),
(165, 'Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. 🤔\n\nBạn có thể:\n• Hỏi về giá hoa, giao hàng, đặt hàng\n• Chọn các câu hỏi gợi ý bên dưới\n• Hoặc nhấn \"Cần nhân viên hỗ trợ\" để được tư vấn trực tiếp', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó Hoa Tulip Trắng Hồng\",\"slug\":\"bo-hoa-tulip-trang-hong\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"430.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa Cúc Trắng\",\"slug\":\"gio-hoa-cuc-trang\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Bó Hoa Hồng Spray Phối Nơ Ren\",\"slug\":\"bo-hoa-hong-spray-phoi-no-ren\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"1.299.000đ\",\"salePrice\":\"1.199.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 13:56:18.000000', 10),
(166, 'tôi cần kiếm hoa có dưới 200000', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 13:57:13.000000', 10),
(167, 'Chào bạn! FlowerCorner có 2 sản phẩm thuộc danh mục Cây mini có giá từ 200.000đ trở xuống rất phù hợp với yêu cầu của bạn ạ! 🥰\n\nBạn có thể tham khảo:\n\n1.  **Chậu Kim Tiền Mini** với giá **175.000đ**.\n2.  **Chậu Cây Hoàng Dương Mini** với giá **200.000đ**.\n\nNếu bạn muốn xem thêm các sản phẩm hoa tươi bó hoặc giỏ, mức giá thấp nhất hiện tại là **250.000đ** cho **Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp** ạ! 💐\n\nBạn quan tâm đến sản phẩm nào ạ?', b'0', 'PRODUCT', '[{\"id\":7,\"name\":\"Bó Hoa Tulip Trắng Hồng\",\"slug\":\"bo-hoa-tulip-trang-hong\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg\",\"price\":\"450.000đ\",\"salePrice\":\"430.000đ\"},{\"id\":8,\"name\":\"Giỏ Hoa Cúc Trắng\",\"slug\":\"gio-hoa-cuc-trang\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg\",\"price\":\"300.000đ\",\"salePrice\":null},{\"id\":9,\"name\":\"Bó Hoa Hồng Spray Phối Nơ Ren\",\"slug\":\"bo-hoa-hong-spray-phoi-no-ren\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg\",\"price\":\"1.299.000đ\",\"salePrice\":\"1.199.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 13:57:17.000000', 10),
(168, 'Cách đặt hàng?', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 13:57:40.000000', 10),
(169, 'Chào bạn! Rất đơn giản ạ! 😊\n\nBạn có thể đặt hàng theo các bước sau:\n\n1.  **Chọn Sản Phẩm:** Bạn chọn mẫu hoa ưng ý (ví dụ: **Giỏ Hoa Hồng** giá **300.000đ** hoặc **Bó Hoa Hồng Trắng Hồng** giá **480.000đ**).\n2.  **Liên Hệ Đặt Hàng:** Gọi trực tiếp đến Hotline: **1900 633 045** hoặc **0865 160 360** để cung cấp thông tin người nhận và nội dung thiệp.\n\nChúng tôi hỗ trợ thanh toán qua **COD, MoMo, hoặc chuyển khoản**. Đơn hàng từ **500.000đ** sẽ được miễn phí giao hàng nội thành ạ! 🛵', b'0', 'TEXT', NULL, NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 13:57:44.000000', 10),
(170, 'tôi cần kiếm hoa có dưới 200000', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:08:05.000000', 10),
(171, 'Chào bạn! Với ngân sách dưới 200.000đ, FlowerCorner có các lựa chọn cây cảnh mini rất xinh xắn ạ: 🪴\n\nBạn có thể tham khảo hai sản phẩm sau:\n\n1.  **Chậu Kim Tiền Mini** với giá **175.000đ**.\n2.  **Chậu Cây Hoàng Dương Mini** với giá **200.000đ**.\n\nNếu bạn muốn xem thêm các sản phẩm hoa tươi bó hoặc giỏ, sản phẩm có giá thấp nhất hiện tại là **Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp** với giá **250.000đ** ạ! 💐\n\nBạn quan tâm đến sản phẩm nào ạ? 😊', b'0', 'PRODUCT', '[{\"id\":52,\"name\":\"Chậu Kim Tiền Mini\",\"slug\":\"chau-cay-kim-tien-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg\",\"price\":\"175.000đ\",\"salePrice\":null},{\"id\":51,\"name\":\"Chậu Cây Hoàng Dương Mini\",\"slug\":\"chau-cay-hoang-duong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg\",\"price\":\"200.000đ\",\"salePrice\":null}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:08:12.000000', 10),
(172, 'tôi cần hoa dưới 200000', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:14:45.000000', 10),
(173, 'Chào bạn! Với ngân sách dưới 200.000đ (dưới 200.000đ), FlowerCorner hiện có sản phẩm cây cảnh mini sau: 🪴\n\n*   **Chậu Kim Tiền Mini** - **175.000đ**\n\nBạn quan tâm đến sản phẩm này không ạ? 😊', b'0', 'PRODUCT', '[{\"id\":52,\"name\":\"Chậu Kim Tiền Mini\",\"slug\":\"chau-cay-kim-tien-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg\",\"price\":\"175.000đ\",\"salePrice\":null},{\"id\":51,\"name\":\"Chậu Cây Hoàng Dương Mini\",\"slug\":\"chau-cay-hoang-duong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg\",\"price\":\"200.000đ\",\"salePrice\":null}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:14:50.000000', 10),
(174, 'tôi cần tìm sản phẩm hoa có giá 500000', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:15:46.000000', 10),
(175, 'Xin lỗi, hiện không có sản phẩm phù hợp với mức giá chính xác 500.000đ ạ. 🙏', b'0', 'PRODUCT', '[{\"id\":52,\"name\":\"Chậu Kim Tiền Mini\",\"slug\":\"chau-cay-kim-tien-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg\",\"price\":\"175.000đ\",\"salePrice\":null},{\"id\":51,\"name\":\"Chậu Cây Hoàng Dương Mini\",\"slug\":\"chau-cay-hoang-duong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg\",\"price\":\"200.000đ\",\"salePrice\":null},{\"id\":50,\"name\":\"Chậu Hoa Cúc Hồng Mini\",\"slug\":\"chau-hoa-cuc-hong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541669/flower-shop/products/buiiicvuctjchteh9ue8.jpg\",\"price\":\"235.000đ\",\"salePrice\":null},{\"id\":38,\"name\":\"Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp\",\"slug\":\"bo-hoa-cuc-dong-tien-mix-cuc-tana-phoi-gau-tot-nghiep\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769540176/flower-shop/products/kwtmaczn2i9mi7bqg6k6.jpg\",\"price\":\"250.000đ\",\"salePrice\":null}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:15:49.000000', 10),
(176, 'Giá hoa hồng?', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:16:02.000000', 10),
(177, '🌹 Hoa hồng của chúng tôi có nhiều mức giá:\n• Bó hồng nhỏ (5-7 bông): từ 150,000đ\n• Bó hồng trung (10-15 bông): từ 350,000đ\n• Bó hồng lớn (20+ bông): từ 500,000đ\n\nBạn muốn xem mẫu cụ thể không ạ?', b'0', 'PRODUCT', '[{\"id\":52,\"name\":\"Chậu Kim Tiền Mini\",\"slug\":\"chau-cay-kim-tien-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg\",\"price\":\"175.000đ\",\"salePrice\":null},{\"id\":51,\"name\":\"Chậu Cây Hoàng Dương Mini\",\"slug\":\"chau-cay-hoang-duong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg\",\"price\":\"200.000đ\",\"salePrice\":null},{\"id\":50,\"name\":\"Chậu Hoa Cúc Hồng Mini\",\"slug\":\"chau-hoa-cuc-hong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541669/flower-shop/products/buiiicvuctjchteh9ue8.jpg\",\"price\":\"235.000đ\",\"salePrice\":null},{\"id\":38,\"name\":\"Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp\",\"slug\":\"bo-hoa-cuc-dong-tien-mix-cuc-tana-phoi-gau-tot-nghiep\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769540176/flower-shop/products/kwtmaczn2i9mi7bqg6k6.jpg\",\"price\":\"250.000đ\",\"salePrice\":null}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:16:07.000000', 10),
(178, 'tôi cần tìm hoa tặng cho người yêu vào dịp sinh nhật', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:17:44.000000', 10),
(179, 'Chào bạn! FlowerCorner có các mẫu hoa lãng mạn thuộc danh mục Hoa Sinh Nhật rất phù hợp để tặng người yêu ạ! 💕\n\nDưới đây là danh sách sản phẩm:\n\n*   Bó Hoa Baby Hồng - 450.000đ\n*   Bó Hoa Cẩm Tú Cầu Mix Hồng Spray - 720.000đ\n*   Bó Hoa Hồng Phối Ren Gắn Nơ - 920.000đ\n*   Bó Hoa Hồng Garden Phối Nơ - 1.099.000đ\n*   Bó Hoa Hồng Spray Phối Nơ Ren - 1.199.000đ\n*   Bó Hoa Hồng Spray Tone Đen - 1.500.000đ\n*   Bó Hoa Hồng Spray Phối Nơ To - 1.750.000đ\n\nBạn quan tâm đến mức giá nào ạ? 😊', b'0', 'PRODUCT', '[{\"id\":52,\"name\":\"Chậu Kim Tiền Mini\",\"slug\":\"chau-cay-kim-tien-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg\",\"price\":\"175.000đ\",\"salePrice\":null},{\"id\":51,\"name\":\"Chậu Cây Hoàng Dương Mini\",\"slug\":\"chau-cay-hoang-duong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg\",\"price\":\"200.000đ\",\"salePrice\":null},{\"id\":50,\"name\":\"Chậu Hoa Cúc Hồng Mini\",\"slug\":\"chau-hoa-cuc-hong-mini\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541669/flower-shop/products/buiiicvuctjchteh9ue8.jpg\",\"price\":\"235.000đ\",\"salePrice\":null},{\"id\":38,\"name\":\"Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp\",\"slug\":\"bo-hoa-cuc-dong-tien-mix-cuc-tana-phoi-gau-tot-nghiep\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769540176/flower-shop/products/kwtmaczn2i9mi7bqg6k6.jpg\",\"price\":\"250.000đ\",\"salePrice\":null}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:17:48.000000', 10),
(180, 'tôi cần tìm hoa tặng cho người yêu vào dịp sinh nhật', b'0', 'TEXT', NULL, 21, 'ngerr', 'USER', '2026-01-29 14:24:44.000000', 10),
(181, 'Chào bạn! FlowerCorner có các mẫu hoa thuộc danh mục **Hoa Sinh Nhật** rất lãng mạn để tặng người yêu ạ! 💕\n\n*   Bó Hoa Baby Hồng - 450.000đ\n*   Bó Hoa Cẩm Tú Cầu Mix Hồng Spray - 720.000đ\n*   Bó Hoa Hồng Phối Ren Gắn Nơ - 920.000đ\n*   Bó Hoa Hồng Garden Phối Nơ - 1.099.000đ\n*   Bó Hoa Hồng Spray Phối Nơ Ren - 1.199.000đ\n*   Bó Hoa Hồng Spray Tone Đen - 1.500.000đ\n*   Bó Hoa Hồng Spray Phối Nơ To - 1.750.000đ\n\nBạn quan tâm đến mẫu nào hay có mức ngân sách cụ thể không ạ? 😊', b'0', 'PRODUCT', '[{\"id\":45,\"name\":\"Bó Hoa Baby Hồng\",\"slug\":\"bo-hoa-baby-hong\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541093/flower-shop/products/uyewd1xhtxafoxqmepz4.jpg\",\"price\":\"450.000đ\",\"salePrice\":null},{\"id\":48,\"name\":\"Bó Hoa Cẩm Tú Cầu Mix Hồng Spray\",\"slug\":\"bo-hoa-cam-tu-cau-mix-hong-spray\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769541664/flower-shop/products/qkcpfjrgbif7vjgwrha7.jpg\",\"price\":\"720.000đ\",\"salePrice\":null},{\"id\":46,\"name\":\"Bó Hoa Hồng Phối Ren Gắn Nơ\",\"slug\":\"bo-hoa-hong-phoi-ren-gan-no\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769542159/flower-shop/products/ejwzawvexcyr8hs7ei0c.jpg\",\"price\":\"920.000đ\",\"salePrice\":null},{\"id\":10,\"name\":\"Bó Hoa Hồng Garden Phối Nơ\",\"slug\":\"bo-hoa-hong-garden-phoi-no\",\"thumbnail\":\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767334407/flower-shop/products/mi7jajku9mfcyaf036ft.jpg\",\"price\":\"1.200.000đ\",\"salePrice\":\"1.099.000đ\"}]', NULL, 'Trợ lý Flower Shop', 'BOT', '2026-01-29 14:24:51.000000', 10);

-- --------------------------------------------------------

--
-- Table structure for table `chat_sessions`
--

CREATE TABLE `chat_sessions` (
  `id` bigint(20) NOT NULL,
  `ended_at` datetime(6) DEFAULT NULL,
  `guest_id` varchar(100) DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chat_sessions`
--

INSERT INTO `chat_sessions` (`id`, `ended_at`, `guest_id`, `started_at`, `status`, `title`, `updated_at`, `staff_id`, `user_id`) VALUES
(8, NULL, NULL, '2026-01-03 10:43:37.000000', 'ACTIVE', 'tôi có 200000 thì tôi mua được những loại hoa n...', '2026-01-03 11:57:51.000000', NULL, 12),
(9, NULL, 'guest_24v3qcfe1jc', '2026-01-03 16:13:49.000000', 'ACTIVE', 'Giá hoa hồng?', '2026-01-03 16:13:55.000000', NULL, NULL),
(10, NULL, NULL, '2026-01-14 14:59:10.000000', 'ACTIVE', 'tôi cần sản phẩm về dịp sinh nhật sắp tới bạn tôi', '2026-01-14 14:59:40.000000', NULL, 21),
(11, NULL, NULL, '2026-01-14 15:26:01.000000', 'ACTIVE', 'Cuộc trò chuyện mới', '2026-01-14 15:26:01.000000', NULL, 8),
(12, NULL, 'guest_nfspxgsq6x', '2026-01-27 21:42:42.000000', 'ACTIVE', 'Giá hoa hồng?', '2026-01-27 21:42:45.000000', NULL, NULL),
(13, NULL, NULL, '2026-01-29 00:08:04.000000', 'ACTIVE', 'tôi cần 1 bó hoa tặng người yêu dịp kỉ niệm ngà...', '2026-01-29 00:08:30.000000', NULL, 23);

-- --------------------------------------------------------

--
-- Table structure for table `contact_tickets`
--

CREATE TABLE `contact_tickets` (
  `id` bigint(20) NOT NULL,
  `category` enum('FEEDBACK','ORDER','OTHER','PARTNERSHIP','SUPPORT') NOT NULL,
  `closed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `status` enum('CLOSED','IN_PROGRESS','OPEN','RESOLVED') NOT NULL,
  `subject` varchar(255) NOT NULL,
  `ticket_code` varchar(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `assigned_admin_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contact_tickets`
--

INSERT INTO `contact_tickets` (`id`, `category`, `closed_at`, `created_at`, `email`, `name`, `phone`, `priority`, `status`, `subject`, `ticket_code`, `updated_at`, `assigned_admin_id`, `user_id`) VALUES
(7, 'FEEDBACK', NULL, '2026-01-07 15:52:17.000000', 'ngerhello@gmail.com', 'tuan tai ', '0359934894', 3, 'IN_PROGRESS', 'Góp ý', 'TCK759375377A9F', '2026-01-07 16:00:09.000000', NULL, 12),
(8, 'ORDER', NULL, '2026-01-27 20:12:35.000000', 'hellontt.nger1705@gmail.com', 'tuan tai', '0345567895', 3, 'IN_PROGRESS', 'Đặt hàng', 'TCK195557534469', '2026-01-27 20:12:45.000000', 18, 21),
(9, 'ORDER', NULL, '2026-01-29 00:16:52.000000', 'vy2011@gmail.com', 'vy', '0788667878', 3, 'IN_PROGRESS', 'Đặt hàng', 'TCK206121464E4A', '2026-01-29 00:18:44.000000', 18, 23);

-- --------------------------------------------------------

--
-- Table structure for table `contact_ticket_messages`
--

CREATE TABLE `contact_ticket_messages` (
  `id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `sender_id` bigint(20) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_type` varchar(10) NOT NULL,
  `ticket_id` bigint(20) NOT NULL,
  `images` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contact_ticket_messages`
--

INSERT INTO `contact_ticket_messages` (`id`, `content`, `created_at`, `is_read`, `sender_id`, `sender_name`, `sender_type`, `ticket_id`, `images`) VALUES
(10, 'Sản phẩm chưa được chỉnh chu lắm\n', '2026-01-07 15:52:17.000000', b'1', 12, 'tuan tai ', 'USER', 7, NULL),
(11, 'sadsad', '2026-01-07 15:52:47.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(12, 'Trạng thái đã thay đổi từ \"Mới\" sang \"Đang xử lý\"', '2026-01-07 16:00:09.000000', b'1', 8, 'Hệ thống', 'SYSTEM', 7, NULL),
(13, 'Cảm ơn bạn đã liên hệ. Chúng tôi đã nhận được yêu cầu và sẽ phản hồi trong thời gian sớm nhất.', '2026-01-07 16:00:18.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(14, 'oke đã lắng nghe', '2026-01-07 16:00:36.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(15, 'oke', '2026-01-07 16:00:46.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(16, 'hello shop', '2026-01-07 16:37:18.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(17, 'bạn cần hỗ trợ gì', '2026-01-07 16:37:39.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(18, 'hhahah', '2026-01-07 16:38:10.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(19, 'hihiih', '2026-01-07 16:38:31.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(20, 'chào bạn bạn còn cần giúp gì không', '2026-01-07 17:00:54.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(21, 'bạn có đó không', '2026-01-07 17:13:13.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(22, 'có sao không', '2026-01-07 17:13:24.000000', b'1', 8, 'Admin', 'ADMIN', 7, NULL),
(23, 'jgk', '2026-01-07 17:14:43.000000', b'1', 12, 'tuan tai', 'USER', 7, NULL),
(24, 'dat hang sao chưa giao ', '2026-01-27 20:12:35.000000', b'1', 21, 'tuan tai', 'USER', 8, '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769519551/flower-shop/products/bhu73szk4lmwmm35mi4q.png\"]'),
(25, 'Trạng thái đã thay đổi từ \"Mới\" sang \"Đang xử lý\"', '2026-01-27 20:12:45.000000', b'1', 18, 'Hệ thống', 'SYSTEM', 8, NULL),
(26, 'cho shop xin mã đơn hàng với nha', '2026-01-27 20:13:11.000000', b'1', 18, 'Admin', 'ADMIN', 8, NULL),
(27, 'hỗ trợ tư vấn hoa', '2026-01-29 00:16:52.000000', b'1', 23, 'vy', 'USER', 9, '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769620597/flower-shop/products/qcrbqpcbsvdwcpjcj4uv.jpg\"]'),
(28, 'tôi muốn tư vấn mẫu này', '2026-01-29 00:17:15.000000', b'1', 23, 'vy', 'USER', 9, NULL),
(29, 'mẫu này cỡ lớn nơ to giá giao động 1.xxx.xxx nhé bạn', '2026-01-29 00:18:44.000000', b'0', 18, 'Admin', 'ADMIN', 9, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `email_verification_tokens`
--

CREATE TABLE `email_verification_tokens` (
  `id` bigint(20) NOT NULL,
  `confirmed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(100) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `email_verification_tokens`
--

INSERT INTO `email_verification_tokens` (`id`, `confirmed_at`, `created_at`, `expires_at`, `token`, `user_id`) VALUES
(7, NULL, '2026-01-14 14:56:13.000000', '2026-01-15 14:56:13.000000', '99e2594aa82d483f8363eb53743261062b2a0dfb489e469e', 21),
(8, NULL, '2026-01-24 09:43:41.000000', '2026-01-25 09:43:41.000000', '653bb997244f40d4883465933aaf37f51b0a7a6be0e64e8f', 22),
(9, NULL, '2026-01-28 03:02:05.000000', '2026-01-29 03:02:05.000000', '917135971dda42229320ebe7ccee9753aac93d7f4fdf4653', 23);

-- --------------------------------------------------------

--
-- Table structure for table `newsletter_subscribers`
--

CREATE TABLE `newsletter_subscribers` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `subscribed_at` datetime(6) DEFAULT NULL,
  `unsubscribed_at` datetime(6) DEFAULT NULL,
  `voucher_code` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `newsletter_subscribers`
--

INSERT INTO `newsletter_subscribers` (`id`, `email`, `is_active`, `subscribed_at`, `unsubscribed_at`, `voucher_code`) VALUES
(5, 'ngerng031@gmail.com', b'1', '2026-01-29 13:54:22.000000', NULL, 'WELCOME30');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `content` text DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `recipient_id` bigint(20) DEFAULT NULL,
  `recipient_role` varchar(20) NOT NULL,
  `reference_id` bigint(20) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `type` varchar(50) NOT NULL,
  `url` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `content`, `created_at`, `is_read`, `recipient_id`, `recipient_role`, `reference_id`, `title`, `type`, `url`) VALUES
(2, 'Ticket TCK759375377A9F có phản hồi mới', '2026-01-07 17:13:24.000000', b'1', 12, 'USER', 7, 'Phản hồi từ Admin', 'TICKET_REPLY', '/tickets/7'),
(6, 'Sản phẩm: Bó hoa cẩm tú cầu xanh', '2026-01-07 23:07:21.000000', b'1', 12, 'USER', 7, 'Shop đã phản hồi đánh giá của bạn', 'REVIEW_REPLY', '/profile/orders'),
(7, 'Đánh giá cho Bó hoa cẩm tú cầu xanh đã được hiển thị', '2026-01-07 23:08:27.000000', b'1', 12, 'USER', 7, 'Đánh giá của bạn đã được duyệt!', 'REVIEW_APPROVED', '/product/bo-hoa-cam-tu-cau-xanh'),
(13, 'Sản phẩm: Chocolate Và Hoa', '2026-01-08 20:18:05.000000', b'0', 12, 'USER', 8, 'Shop đã phản hồi đánh giá của bạn', 'REVIEW_REPLY', '/profile/orders'),
(29, 'Trạng thái: Đã xác nhận', '2026-01-27 20:24:37.000000', b'1', 21, 'USER', 35, 'Cập nhật đơn hàng #ORD20268447D4AD', 'ORDER_STATUS', '/profile/orders/35'),
(30, 'Trạng thái: Đang xử lý', '2026-01-27 20:24:47.000000', b'1', 21, 'USER', 35, 'Cập nhật đơn hàng #ORD20268447D4AD', 'ORDER_STATUS', '/profile/orders/35'),
(31, 'Trạng thái: Đang giao hàng', '2026-01-27 20:24:57.000000', b'1', 21, 'USER', 35, 'Cập nhật đơn hàng #ORD20268447D4AD', 'ORDER_STATUS', '/profile/orders/35'),
(32, 'Trạng thái: Đã giao hàng', '2026-01-27 20:24:59.000000', b'1', 21, 'USER', 35, 'Cập nhật đơn hàng #ORD20268447D4AD', 'ORDER_STATUS', '/profile/orders/35'),
(33, 'Trạng thái: Hoàn thành', '2026-01-27 20:25:02.000000', b'1', 21, 'USER', 35, 'Cập nhật đơn hàng #ORD20268447D4AD', 'ORDER_STATUS', '/profile/orders/35'),
(35, 'Sản phẩm: Giỏ Hoa xinh', '2026-01-27 20:28:28.000000', b'1', 21, 'USER', 9, 'Shop đã phản hồi đánh giá của bạn', 'REVIEW_REPLY', '/profile/orders'),
(36, 'Trạng thái: Hoàn thành', '2026-01-27 20:41:19.000000', b'1', 21, 'USER', 33, 'Cập nhật đơn hàng #ORD18701608D9A4', 'ORDER_STATUS', '/profile/orders/33'),
(37, 'Trạng thái: Hoàn thành', '2026-01-27 20:43:56.000000', b'1', 21, 'USER', 32, 'Cập nhật đơn hàng #ORD8168831011CD', 'ORDER_STATUS', '/profile/orders/32'),
(39, 'Sản phẩm: Hoa sinh nhật', '2026-01-27 20:45:06.000000', b'1', 21, 'USER', 10, 'Shop đã phản hồi đánh giá của bạn', 'REVIEW_REPLY', '/product/hoa-sinh-nhat-084y#reviews'),
(41, 'Đơn hàng 385,000 VNĐ đang chờ xử lý', '2026-01-27 21:00:20.000000', b'1', 21, 'USER', 36, 'Đặt hàng thành công #ORD2242046164C8', 'ORDER_CREATED', '/profile/orders/36'),
(42, 'Trạng thái: Hoàn thành', '2026-01-27 21:00:34.000000', b'1', 21, 'USER', 36, 'Cập nhật đơn hàng #ORD2242046164C8', 'ORDER_STATUS', '/profile/orders/36'),
(44, 'Vyyyyy - 940,000 VNĐ', '2026-01-28 23:41:16.000000', b'1', NULL, 'ALL_ADMINS', 37, 'Đơn hàng mới #ORD184763384182', 'ORDER_NEW', '/admin/orders/37'),
(45, 'Đơn hàng 940,000 VNĐ đang chờ xử lý', '2026-01-28 23:41:16.000000', b'0', 23, 'USER', 37, 'Đặt hàng thành công #ORD184763384182', 'ORDER_CREATED', '/profile/orders/37'),
(46, 'Đặt hàng', '2026-01-29 00:16:52.000000', b'1', NULL, 'ALL_ADMINS', 9, 'Ticket mới từ vy', 'TICKET_NEW', '/admin/tickets/9'),
(47, 'Tin nhắn từ vy', '2026-01-29 00:17:15.000000', b'1', NULL, 'ALL_ADMINS', 9, 'Tin nhắn mới - TCK206121464E4A', 'TICKET_MESSAGE', '/admin/tickets/9'),
(48, 'Ticket TCK206121464E4A có phản hồi mới', '2026-01-29 00:18:44.000000', b'0', 23, 'USER', 9, 'Phản hồi từ Admin', 'TICKET_REPLY', '/tickets/9'),
(49, 'Trạng thái: Đã xác nhận', '2026-01-29 00:19:06.000000', b'0', 23, 'USER', 37, 'Cập nhật đơn hàng #ORD184763384182', 'ORDER_STATUS', '/profile/orders/37'),
(50, 'Trạng thái: Đã hủy', '2026-01-29 00:19:45.000000', b'0', 23, 'USER', 37, 'Cập nhật đơn hàng #ORD184763384182', 'ORDER_STATUS', '/profile/orders/37'),
(51, 'Cần thay đổi phương thức thanh toán', '2026-01-29 00:19:45.000000', b'1', NULL, 'ALL_ADMINS', 37, 'Đơn hàng bị hủy #ORD184763384182', 'ORDER_CANCELLED', '/admin/orders/37'),
(52, 'ngerr - 480,000 VNĐ', '2026-01-29 11:19:09.000000', b'1', NULL, 'ALL_ADMINS', 38, 'Đơn hàng mới #ORD60349766248A', 'ORDER_NEW', '/admin/orders/38'),
(53, 'Đơn hàng 480,000 VNĐ đang chờ xử lý', '2026-01-29 11:19:09.000000', b'1', 21, 'USER', 38, 'Đặt hàng thành công #ORD60349766248A', 'ORDER_CREATED', '/profile/orders/38'),
(54, 'Trạng thái: Đã hủy', '2026-01-29 11:22:15.000000', b'1', 21, 'USER', 38, 'Cập nhật đơn hàng #ORD60349766248A', 'ORDER_STATUS', '/profile/orders/38');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `cancelled_reason` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `discount_amount` decimal(12,2) DEFAULT NULL,
  `final_price` decimal(12,2) NOT NULL,
  `is_paid` bit(1) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `order_code` varchar(20) NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` enum('BANK_TRANSFER','COD','MOMO','VNPAY') NOT NULL,
  `shipping_address` varchar(255) NOT NULL,
  `shipping_fee` decimal(12,2) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','DELIVERED','PENDING','PROCESSING','REFUNDED','SHIPPING') NOT NULL,
  `total_price` decimal(12,2) NOT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `voucher_id` bigint(20) DEFAULT NULL,
  `address_detail` varchar(255) NOT NULL,
  `delivery_date` date DEFAULT NULL,
  `delivery_time` varchar(255) DEFAULT NULL,
  `district` varchar(255) NOT NULL,
  `province` varchar(255) NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `recipient_phone` varchar(255) NOT NULL,
  `sender_email` varchar(255) DEFAULT NULL,
  `sender_name` varchar(255) NOT NULL,
  `sender_phone` varchar(255) NOT NULL,
  `geo_provider` varchar(20) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lng` double DEFAULT NULL,
  `place_id` varchar(120) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `cancelled_at`, `cancelled_reason`, `created_at`, `discount_amount`, `final_price`, `is_paid`, `note`, `order_code`, `paid_at`, `payment_method`, `shipping_address`, `shipping_fee`, `status`, `total_price`, `transaction_id`, `updated_at`, `user_id`, `voucher_id`, `address_detail`, `delivery_date`, `delivery_time`, `district`, `province`, `recipient_name`, `recipient_phone`, `sender_email`, `sender_name`, `sender_phone`, `geo_provider`, `lat`, `lng`, `place_id`) VALUES
(4, NULL, NULL, '2025-12-22 16:21:30.000000', 60000.00, 240000.00, b'1', NULL, 'ORD952908891544', '2025-12-22 16:43:29.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu', 0.00, 'COMPLETED', 300000.00, NULL, '2025-12-22 16:43:29.000000', 8, 5, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(5, NULL, NULL, '2025-12-22 16:22:35.000000', 100000.00, 1100000.00, b'1', NULL, 'ORD95355644FB88', '2025-12-22 16:42:12.000000', 'COD', 'thuduc', 0.00, 'COMPLETED', 1200000.00, NULL, '2025-12-22 16:42:12.000000', 8, 5, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(6, NULL, NULL, '2025-12-22 16:43:57.000000', 0.00, 300000.00, b'0', NULL, 'ORD96637225B039', NULL, 'MOMO', 'dong nai', 0.00, 'COMPLETED', 300000.00, NULL, '2025-12-22 16:52:50.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(7, NULL, NULL, '2025-12-22 16:45:00.000000', 0.00, 300000.00, b'0', NULL, 'ORD96700106B60A', NULL, 'MOMO', 'thuduc', 0.00, 'COMPLETED', 300000.00, NULL, '2025-12-22 16:52:48.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(8, '2025-12-22 17:10:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2025-12-22 16:53:09.000000', 0.00, 300000.00, b'0', NULL, 'ORD971890609966', NULL, 'MOMO', 'thu duc', 0.00, 'CANCELLED', 300000.00, NULL, '2025-12-22 17:10:00.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(9, '2025-12-22 21:35:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2025-12-22 16:59:18.000000', 0.00, 90000.00, b'0', NULL, 'ORD97558152A517', NULL, 'MOMO', 'thuduc', 0.00, 'CANCELLED', 90000.00, NULL, '2025-12-22 21:35:00.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(10, '2025-12-22 21:35:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2025-12-22 17:12:54.000000', 0.00, 90000.00, b'0', NULL, 'ORD983743907B4F', NULL, 'MOMO', 'dong nai', 0.00, 'CANCELLED', 90000.00, NULL, '2025-12-22 21:35:00.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(11, '2025-12-22 22:00:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2025-12-22 21:41:32.000000', 0.00, 300000.00, b'0', NULL, 'ORD144921379A8B', NULL, 'MOMO', 'dong nai', 0.00, 'CANCELLED', 300000.00, NULL, '2025-12-22 22:00:00.000000', 8, NULL, '', NULL, NULL, '', '', '', '', NULL, '', '', NULL, NULL, NULL, NULL),
(12, NULL, NULL, '2026-01-02 11:47:46.000000', 0.00, 570000.00, b'1', NULL, 'ORD2926657485C4', '2026-01-02 11:48:57.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 570000.00, NULL, '2026-01-02 11:48:57.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-02', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(13, NULL, NULL, '2026-01-02 12:59:33.000000', 0.00, 300000.00, b'1', NULL, 'ORD335731035866', '2026-01-02 13:01:36.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 300000.00, NULL, '2026-01-02 13:01:36.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-02', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(14, NULL, NULL, '2026-01-02 13:14:16.000000', 0.00, 150000.00, b'1', NULL, 'ORD344568522216', '2026-01-02 13:14:30.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Phú Nhuận, Hồ Chí Minh', 0.00, 'COMPLETED', 150000.00, NULL, '2026-01-02 13:14:30.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-02', '16:00 - 20:00', 'Phú Nhuận', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(15, NULL, NULL, '2026-01-02 15:26:47.000000', 100000.00, 350000.00, b'1', NULL, 'ORD42407625D94E', '2026-01-02 15:27:58.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Bình Tân, Hồ Chí Minh', 0.00, 'COMPLETED', 450000.00, NULL, '2026-01-02 15:27:58.000000', 12, 8, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-02', '16:00 - 20:00', 'Bình Tân', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(16, NULL, NULL, '2026-01-05 16:16:27.000000', 0.00, 390000.00, b'1', 'fdfgn', 'ORD045873742C2A', '2026-01-05 16:17:39.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 390000.00, NULL, '2026-01-05 16:17:39.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-06', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(17, '2026-01-07 17:25:46.000000', NULL, '2026-01-07 17:02:11.000000', 0.00, 90000.00, b'0', 'ầg', 'ORD801317284A15', NULL, 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Gò Vấp, Hồ Chí Minh', 0.00, 'CANCELLED', 90000.00, NULL, '2026-01-07 17:25:46.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-08', 'Linh hoạt', 'Gò Vấp', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(18, '2026-01-07 17:25:44.000000', NULL, '2026-01-07 17:13:55.000000', 0.00, 300000.00, b'0', 'adfgh', 'ORD80835092ADAE', NULL, 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Phú Nhuận, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-07 17:25:44.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-08', '16:00 - 20:00', 'Phú Nhuận', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(19, NULL, NULL, '2026-01-07 17:24:56.000000', 0.00, 300000.00, b'1', 'cV', 'ORD814963005C40', '2026-01-07 17:25:40.000000', 'COD', '123 Võ Văn Ngân\nPhường Linh Chiểu, Phú Nhuận, Hồ Chí Minh', 0.00, 'COMPLETED', 300000.00, NULL, '2026-01-07 17:25:40.000000', 12, NULL, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2026-01-08', '16:00 - 20:00', 'Phú Nhuận', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerhello@gmail.com', 'tuan tai', '0356621028', NULL, NULL, NULL, NULL),
(20, NULL, NULL, '2026-01-08 11:11:32.000000', 0.00, 300000.00, b'1', 'êqeqeqeqeq', 'ORD454927489FDA', '2026-01-08 11:15:41.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 300000.00, NULL, '2026-01-08 11:15:41.000000', 13, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-09', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'hello.ntt1705@gmail.com', 'Tai Nguyen', '0987764329', 'NOMINATIM', 10.836653631291673, 106.77651001444643, NULL),
(21, NULL, NULL, '2026-01-08 11:19:03.000000', 0.00, 90000.00, b'1', NULL, 'ORD4594302815D9', '2026-01-08 11:24:25.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 90000.00, NULL, '2026-01-08 11:24:28.000000', 13, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-09', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'hello.ntt1705@gmail.com', 'Tai Nguyen', '0987764329', 'NOMINATIM', 10.836657839147879, 106.7765084915405, NULL),
(22, NULL, NULL, '2026-01-08 20:01:08.000000', 0.00, 300000.00, b'1', 'sdafsa', 'ORD77268540A947', '2026-01-08 20:01:43.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Tân Phú, Hồ Chí Minh', 0.00, 'COMPLETED', 300000.00, NULL, '2026-01-08 20:01:43.000000', 12, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-09', '16:00 - 20:00', 'Tân Phú', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerhello@gmail.com', 'tuan tai', '0356621028', 'NOMINATIM', 10.836675802040874, 106.77655111686195, NULL),
(23, NULL, NULL, '2026-01-08 20:15:43.000000', 70000.00, 280000.00, b'1', 'mãi iu', 'ORD78143558CB29', '2026-01-08 20:16:12.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 350000.00, NULL, '2026-01-08 20:16:15.000000', 12, 6, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-09', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerhello@gmail.com', 'tuan tai', '0356621028', 'NOMINATIM', 10.836657619832954, 106.77659328026121, NULL),
(24, '2026-01-14 15:20:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2026-01-14 15:01:53.000000', 0.00, 90000.00, b'0', 'chúc mừng ngày mới', 'ORD77713655320F', NULL, 'MOMO', 'thủ đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 90000.00, NULL, '2026-01-14 15:20:00.000000', 21, NULL, 'thủ đức', '2026-01-15', '12:00 - 16:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(25, NULL, NULL, '2026-01-14 15:20:17.000000', 0.00, 90000.00, b'1', NULL, 'ORD788174291ED7', '2026-01-14 15:22:43.000000', 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 90000.00, '4649848134', '2026-01-14 15:26:30.000000', 8, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-15', '12:00 - 16:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'hellontt.nger1705@gmail.com', 'ngercuae', '0359921028', 'NOMINATIM', 10.83671304411614, 106.77654524242196, NULL),
(26, '2026-01-14 15:49:04.000000', NULL, '2026-01-14 15:36:11.000000', 0.00, 300000.00, b'0', NULL, 'ORD79771474DE36', NULL, 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-14 15:49:04.000000', 8, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-15', '12:00 - 16:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'hellontt.nger1705@gmail.com', 'ngercuae', '0359921028', 'NOMINATIM', 10.836701457253996, 106.77654361096442, NULL),
(27, '2026-01-14 15:49:02.000000', NULL, '2026-01-14 15:44:45.000000', 0.00, 300000.00, b'0', NULL, 'ORD802850802BC0', NULL, 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-14 15:49:02.000000', 8, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-15', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'hellontt.nger1705@gmail.com', 'ngercuae', '0359921028', 'NOMINATIM', 10.836705637141874, 106.77652674455628, NULL),
(28, '2026-01-14 16:05:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2026-01-14 15:47:03.000000', 0.00, 300000.00, b'0', NULL, 'ORD804232251FA7', NULL, 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-14 16:05:00.000000', 8, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-15', 'Linh hoạt', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'hellontt.nger1705@gmail.com', 'ngercuae', '0359921028', NULL, NULL, NULL, NULL),
(29, '2026-01-14 16:10:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2026-01-14 15:50:14.000000', 0.00, 300000.00, b'0', NULL, 'ORD8061497398F9', NULL, 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-14 16:10:00.000000', 8, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-15', '08:00 - 12:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'hellontt.nger1705@gmail.com', 'ngercuae', '0359921028', 'NOMINATIM', 10.83666575893632, 106.7765159945712, NULL),
(30, '2026-01-24 07:55:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2026-01-14 16:00:21.000000', 0.00, 300000.00, b'0', NULL, 'ORD812211363BFD', NULL, 'MOMO', 'thủ đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-24 07:55:00.000000', 21, NULL, 'thủ đức', '2026-01-15', '12:00 - 16:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(31, '2026-01-24 07:55:00.000000', 'Don hang tu dong huy do qua thoi gian thanh toan MoMo (15 phut)', '2026-01-14 16:07:28.000000', 0.00, 300000.00, b'0', NULL, 'ORD816487013E31', NULL, 'MOMO', 'thủ đức, Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 300000.00, NULL, '2026-01-24 07:55:00.000000', 21, NULL, 'thủ đức', '2026-01-15', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(32, NULL, NULL, '2026-01-14 16:08:08.000000', 0.00, 300000.00, b'1', 'dáddad', 'ORD8168831011CD', '2026-01-14 16:09:34.000000', 'MOMO', 'thủ đức, Thủ Đức, Hồ Chí Minh', 0.00, 'COMPLETED', 300000.00, '4649849602', '2026-01-27 20:43:56.000000', 21, NULL, 'thủ đức', '2026-01-15', '16:00 - 20:00', 'Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(33, NULL, NULL, '2026-01-27 19:58:21.000000', 0.00, 125000.00, b'1', NULL, 'ORD18701608D9A4', '2026-01-27 20:41:19.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, TP. Thủ Đức, Hồ Chí Minh', 35000.00, 'COMPLETED', 90000.00, NULL, '2026-01-27 20:41:19.000000', 21, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-28', '08:00 - 12:00', 'TP. Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerng031@gmail.com', 'ngerr', '0358892378', 'NOMINATIM', 10.836670168521582, 106.77645983879593, NULL),
(34, NULL, NULL, '2026-01-27 20:08:02.000000', 0.00, 185000.00, b'1', NULL, 'ORD1928270296B6', '2026-01-27 20:09:49.000000', 'COD', 'thủ đức, TP. Thủ Đức, Hồ Chí Minh', 35000.00, 'COMPLETED', 150000.00, NULL, '2026-01-27 20:09:52.000000', 21, NULL, 'thủ đức', '2026-01-28', '12:00 - 16:00', 'TP. Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0356621028', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(35, NULL, NULL, '2026-01-27 20:24:28.000000', 0.00, 325000.00, b'1', NULL, 'ORD20268447D4AD', '2026-01-27 20:24:59.000000', 'COD', 'thủ đức, Quận Phú Nhuận, Hồ Chí Minh', 25000.00, 'COMPLETED', 300000.00, NULL, '2026-01-27 20:25:02.000000', 21, NULL, 'thủ đức', '2026-01-28', '12:00 - 16:00', 'Quận Phú Nhuận', 'Hồ Chí Minh', 'tuan tai', '0987746352', 'ngerng031@gmail.com', 'ngerr', '0358892378', NULL, NULL, NULL, NULL),
(36, NULL, NULL, '2026-01-27 21:00:20.000000', 0.00, 385000.00, b'1', NULL, 'ORD2242046164C8', '2026-01-27 21:00:34.000000', 'COD', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, Huyện Củ Chi, Hồ Chí Minh', 35000.00, 'COMPLETED', 350000.00, NULL, '2026-01-27 21:00:34.000000', 21, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-28', '08:00 - 12:00', 'Huyện Củ Chi', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerng031@gmail.com', 'ngerr', '0358892378', 'NOMINATIM', 10.836691415546483, 106.77648914111025, NULL),
(37, '2026-01-29 00:19:45.000000', 'Cần thay đổi phương thức thanh toán', '2026-01-28 23:41:16.000000', 0.00, 940000.00, b'0', 'hihi', 'ORD184763384182', NULL, 'COD', '182, Lã Xuân Oai, TP. Thủ Đức, Hồ Chí Minh', 0.00, 'CANCELLED', 940000.00, NULL, '2026-01-29 00:19:45.000000', 23, NULL, '182, Lã Xuân Oai', '2026-01-29', '12:00 - 16:00', 'TP. Thủ Đức', 'Hồ Chí Minh', 'vy', '0878765543', 'vy2011@gmail.com', 'Vyyyyy', '0878765543', NULL, NULL, NULL, NULL),
(38, '2026-01-29 11:22:15.000000', NULL, '2026-01-29 11:19:09.000000', 0.00, 480000.00, b'1', 'happy', 'ORD60349766248A', '2026-01-29 11:20:32.000000', 'MOMO', 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức, TP. Thủ Đức, Hồ Chí Minh', 35000.00, 'CANCELLED', 445000.00, '4657471950', '2026-01-29 11:22:15.000000', 21, NULL, 'Đường số 10 Tăng Nhơn Phú B, Phường Tăng Nhơn Phú, Thành phố Thủ Đức', '2026-01-30', '08:00 - 12:00', 'TP. Thủ Đức', 'Hồ Chí Minh', 'tuan tai', '0987764329', 'ngerng031@gmail.com', 'ngerr', '0358892378', 'NOMINATIM', 10.836668291748387, 106.776472058226, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` bigint(20) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_thumbnail` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `price`, `product_name`, `product_thumbnail`, `quantity`, `subtotal`, `order_id`, `product_id`) VALUES
(4, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 4, 9),
(5, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 4, 1200000.00, 5, 9),
(6, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 6, 9),
(7, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 7, 9),
(8, 300000.00, 'Giỏ Hoa xinh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', 1, 300000.00, 8, 8),
(9, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 9, 7),
(10, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 10, 7),
(11, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 11, 9),
(12, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 3, 270000.00, 12, 7),
(13, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 12, 9),
(14, 300000.00, 'Giỏ Hoa xinh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', 1, 300000.00, 13, 8),
(15, 150000.00, 'dsadad', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767334407/flower-shop/products/mi7jajku9mfcyaf036ft.jpg', 1, 150000.00, 14, 10),
(16, 150000.00, 'dsadad', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767334407/flower-shop/products/mi7jajku9mfcyaf036ft.jpg', 1, 150000.00, 15, 10),
(17, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 15, 9),
(18, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 16, 7),
(19, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 16, 9),
(20, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 17, 7),
(21, 300000.00, 'Giỏ Hoa xinh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', 1, 300000.00, 18, 8),
(22, 300000.00, 'Giỏ Hoa xinh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', 1, 300000.00, 19, 8),
(23, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 20, 9),
(24, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 21, 7),
(25, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 22, 9),
(26, 350000.00, 'Chocolate Và Hoa', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767878032/flower-shop/products/phtckyaktzfgia5yngpk.jpg', 1, 350000.00, 23, 11),
(27, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 24, 7),
(28, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 25, 7),
(29, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 26, 9),
(30, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 27, 9),
(31, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 28, 9),
(32, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 29, 9),
(33, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 30, 9),
(34, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 31, 9),
(35, 300000.00, 'Hoa sinh nhật', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', 1, 300000.00, 32, 9),
(36, 90000.00, 'Bó hoa cẩm tú cầu xanh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', 1, 90000.00, 33, 7),
(37, 150000.00, 'dsadad', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767334407/flower-shop/products/mi7jajku9mfcyaf036ft.jpg', 1, 150000.00, 34, 10),
(38, 300000.00, 'Giỏ Hoa xinh', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', 1, 300000.00, 35, 8),
(39, 350000.00, 'Chocolate Và Hoa', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767878032/flower-shop/products/phtckyaktzfgia5yngpk.jpg', 1, 350000.00, 36, 11),
(40, 235000.00, 'Chậu Hoa Cúc Hồng Mini', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541669/flower-shop/products/buiiicvuctjchteh9ue8.jpg', 4, 940000.00, 37, 50),
(41, 445000.00, 'Bó Hoa Cẩm Tú Cầu Xanh ', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537059/flower-shop/products/nbaoktdoeehyn8ophsxf.jpg', 1, 445000.00, 38, 23);

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(100) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `password_reset_tokens`
--

INSERT INTO `password_reset_tokens` (`id`, `created_at`, `expiry_date`, `token`, `used`, `user_id`) VALUES
(7, '2026-01-14 13:39:04.000000', '2026-01-14 14:09:04.000000', '1322b583-5594-4de6-bc1f-3f3ac054bfb2', b'1', 12),
(8, '2026-01-14 13:40:09.000000', '2026-01-14 14:10:09.000000', '49f9df3b-7c31-4974-9fcc-0b1da224976d', b'1', 12),
(9, '2026-01-14 13:50:52.000000', '2026-01-14 14:20:52.000000', 'af34768e-d5a7-4f66-83bc-fbef393272e3', b'1', 12),
(10, '2026-01-14 13:51:34.000000', '2026-01-14 14:21:34.000000', 'f9dd7e8d-dc53-4254-b6a1-1241f4881cae', b'0', 12),
(11, '2026-01-14 13:59:45.000000', '2026-01-14 14:29:45.000000', '8d80c214-2a42-496c-bf4c-678fdd04c928', b'1', 8),
(12, '2026-01-14 14:00:50.000000', '2026-01-14 14:30:50.000000', 'bb99b1e3-ea06-4924-bed4-c023aa81a2e6', b'1', 18),
(13, '2026-01-14 14:17:00.000000', '2026-01-14 14:47:00.000000', 'eb673f32-b918-43d2-8040-c137ddd73313', b'1', 8),
(14, '2026-01-14 14:17:43.000000', '2026-01-14 14:47:43.000000', '21e23b5d-ec32-486d-9062-0fa065afce0c', b'1', 8),
(15, '2026-01-14 14:18:10.000000', '2026-01-14 14:48:10.000000', '6e471cfc-591c-453b-8fad-cab19c30fcf4', b'0', 18),
(17, '2026-01-14 14:31:47.000000', '2026-01-14 15:01:47.000000', '32094117-ca06-4b47-9379-ff85bdb96780', b'1', 8);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `sale_price` decimal(15,2) DEFAULT NULL,
  `slug` varchar(200) NOT NULL,
  `status` int(11) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `thumbnail` varchar(500) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `sku` varchar(100) DEFAULT NULL,
  `sold_count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `active`, `created_at`, `description`, `name`, `price`, `sale_price`, `slug`, `status`, `stock_quantity`, `thumbnail`, `updated_at`, `category_id`, `sku`, `sold_count`) VALUES
(7, b'1', '2025-12-22 14:38:42.000000', 'Phong cách: Sang chảnh, hiện đại và kiêu sa.\n\nĐặc điểm: Những bông Tulip nhập khẩu với sắc trắng và hồng xen kẽ, dáng hoa thanh mảnh được tôn vinh bởi cách gói giấy đơn giản nhưng đẳng cấp. Đây là mẫu hoa đang rất \"hot\" trên mạng xã hội.\n\nPhù hợp: Dành tặng cho những cô nàng yêu thích sự tinh tế, chụp ảnh \"sống ảo\" hoặc làm hoa cầm tay cho cô dâu trong những buổi tiệc cưới hiện đại.', 'Bó Hoa Tulip Trắng Hồng', 450000.00, 430000.00, 'bo-hoa-tulip-trang-hong', 1, 13, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766328403/flower-shop/products/s9xbcg9nabittmrudpkd.jpg', '2026-01-29 00:15:02.000000', 2, NULL, 7),
(8, b'1', '2025-12-22 14:50:34.000000', 'Phong cách: Thuần khiết, mộc mạc và chân thành.\n\nĐặc điểm: Những bông cúc trắng muốt được cắm đầy đặn trong giỏ mây trắng, điểm xuyết thêm chút ruy băng hồng pastel điệu đà.\n\nPhù hợp: Tặng bạn bè, người thân vào những dịp kỷ niệm nhẹ nhàng hoặc dùng để trang trí góc làm việc, cửa sổ để tạo cảm giác bình yên.', 'Giỏ Hoa Cúc Trắng', 300000.00, NULL, 'gio-hoa-cuc-trang', 1, 12, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389310/flower-shop/products/nl85sbm9k64kk0j0bnte.jpg', '2026-01-28 01:00:45.000000', 3, NULL, 3),
(9, b'1', '2025-12-22 14:51:27.000000', 'Phong cách: Hiện đại, sang trọng và đầy quyền lực. Sự kết hợp giữa sắc hồng rực rỡ và tông đen huyền bí tạo nên một vẻ đẹp không thể rời mắt.\n\nĐặc điểm nổi bật:\n\nLoại hoa: Sử dụng số lượng lớn hoa hồng tỉ muội sắc hồng đậm, với những nụ hoa chúm chím xếp khít nhau tạo thành một vòm hoa tròn đầy đặn và viên mãn.\n\nGiấy gói: Lớp giấy gói đen nhám (matte) cao cấp được xếp lớp tỉ mỉ, giúp tôn vinh tối đa sắc hồng của hoa, làm cho bó hoa trở nên nổi bật và đẳng cấp hơn hẳn các cách gói thông thường.\n\nPhụ kiện: Điểm xuyết bằng nơ ruy băng lụa màu đen thanh mảnh, cùng một chiếc thiệp nhỏ xinh xắn, tạo nên tổng thể hài hòa và tinh tế.\n\nÝ nghĩa: Tượng trưng cho một tình yêu nồng cháy, sự ngưỡng mộ sâu sắc và lời khẳng định về một vị trí duy nhất trong lòng người tặng.\n\nDịp phù hợp: Cực kỳ lý tưởng để tặng trong các dịp kỷ niệm yêu nhau, ngày lễ Valentine, hoặc làm quà sinh nhật cho những người phụ nữ hiện đại, tự tin và yêu thích sự khác biệt.', 'Bó Hoa Hồng Spray Phối Nơ Ren', 1299000.00, 1199000.00, 'bo-hoa-hong-spray-phoi-no-ren', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1766389905/flower-shop/products/qf5mycc8lvb9nlt9agfc.jpg', '2026-01-29 00:14:37.000000', 5, NULL, 13),
(10, b'1', '2026-01-02 13:13:31.000000', 'Phong cách: Kiểu dáng xòe tròn, bồng bềnh và cực kỳ nữ tính.\n\nĐặc điểm: Sự kết hợp giữa những bông hồng Garden size lớn và hoa hồng tỉ muội đậm màu tạo điểm nhấn. Điểm đặc biệt là lớp giấy gói voan xếp ly tinh tế cùng rất nhiều nơ ruy băng lụa tạo cảm giác như một chiếc váy dạ hội.\n\nPhù hợp: Tặng bạn gái trong các buổi kỷ niệm, làm quà tặng bất ngờ hoặc dành cho các', 'Bó Hoa Hồng Garden Phối Nơ', 1200000.00, 1099000.00, 'bo-hoa-hong-garden-phoi-no', 1, 10, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767334407/flower-shop/products/mi7jajku9mfcyaf036ft.jpg', '2026-01-28 00:57:32.000000', 5, NULL, 3),
(11, b'1', '2026-01-08 20:14:59.000000', '- chocolate\n- Hoa hồng 10 bông', 'Set Quà Chocolate Phối Hoa', 400000.00, 350000.00, 'set-qua-chocolate-phoi-hoa', 1, 18, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1767878032/flower-shop/products/phtckyaktzfgia5yngpk.jpg', '2026-01-28 00:58:46.000000', 15, NULL, 2),
(13, b'1', '2026-01-27 23:46:26.000000', 'Phong cách: Lãng mạn, ngọt ngào và sang trọng.\n\nĐặc điểm: Sự kết hợp hoàn hảo giữa các sắc độ hồng của hoa hồng cùng lá bạc (tùng thơm) tạo nên chiều sâu. Giỏ hoa toát lên vẻ thanh lịch, hiện đại.\n\nPhù hợp: Quà tặng lý tưởng cho người yêu, vợ trong ngày sinh nhật, lễ tình nhân hoặc các dịp chúc mừng quan trọng.', 'Giỏ Hoa Hồng', 300000.00, NULL, 'gio-hoa-hong', 1, 5, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769532330/flower-shop/products/eiphqwenmuwspcsjxocb.jpg', '2026-01-27 23:46:26.000000', 3, NULL, 0),
(14, b'1', '2026-01-27 23:53:48.000000', 'Phong cách: Rạng rỡ, năng lượng và hy vọng.\n\nĐặc điểm: Những đóa hướng dương vàng rực rỡ nổi bật trên nền giỏ mây nâu truyền thống. Màu vàng tượng trưng cho sự may mắn và lời chúc về một tương lai tươi sáng.\n\nPhù hợp: Tặng dịp khai trương, lễ tốt nghiệp hoặc chúc mừng thăng tiến với ý nghĩa \"luôn hướng về phía mặt trời\".', 'Giỏ Hoa Hướng Dương', 320000.00, NULL, 'gio-hoa-huong-duong-jss2', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769532838/flower-shop/products/avkyb27wxmmzlyluezzw.jpg', '2026-01-27 23:54:09.000000', 3, NULL, 0),
(21, b'1', '2026-01-28 01:04:24.000000', 'Phong cách: Tinh tế, quý phái và đậm chất thơ.\n\nĐặc điểm: Hoa tulip hồng kết hợp cùng hoa phụ nhỏ li ti và giấy gói voan mềm mại. Đây là mẫu hoa mang hơi thở châu Âu, cực kỳ bắt mắt và \"trendy\".\n\nPhù hợp: Dành cho những ai yêu thích sự mới lạ, sang trọng. Thích hợp làm quà tặng sự kiện hoặc trang trí những buổi tiệc trà tinh tế.', 'Giỏ Hoa Tulip', 330000.00, NULL, 'gio-hoa-tulip', 1, 10, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537054/flower-shop/products/krecaa8ziu4amtl5gmqb.jpg', '2026-01-28 01:04:24.000000', 3, NULL, 0),
(22, b'1', '2026-01-28 01:04:26.000000', 'Phong cách: Nhẹ nhàng, ngọt ngào và thơ mộng.\n\nĐặc điểm: Sự phối hợp hài hòa giữa hoa hồng kem dâu và hoa hồng trắng, kết hợp cùng giấy gói voan mờ và nơ lụa mềm mại. Bó hoa mang vẻ đẹp thuần khiết, dịu dàng như một thiếu nữ.\n\nPhù hợp: Quà tặng sinh nhật cho phái nữ, quà kỷ niệm ngày yêu nhau hoặc dùng cho các buổi hẹn hò lãng mạn.', 'Bó Hoa Hồng Trắng Hồng', 480000.00, NULL, 'bo-hoa-hong-trang-hong', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537056/flower-shop/products/vvfdoe49ng1uhcdvrlv9.jpg', '2026-01-28 01:04:26.000000', 2, NULL, 0),
(23, b'1', '2026-01-28 01:04:30.000000', 'Phong cách: Thanh cao, dịu mát và bình yên.\n\nĐặc điểm: Sử dụng những đóa cẩm tú cầu xanh biển lớn làm chủ đạo, được bao bọc bởi lớp giấy gói màu xanh đậm (navy) sang trọng. Màu xanh mang lại cảm giác dễ chịu và sự tin tưởng.\n\nPhù hợp: Tặng bạn bè, đồng nghiệp hoặc đối tác nam giới. Đây cũng là lựa chọn tuyệt vời để gửi lời cảm ơn hoặc chúc mừng thành công.', 'Bó Hoa Cẩm Tú Cầu Xanh ', 450000.00, 445000.00, 'bo-hoa-cam-tu-cau-xanh', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537059/flower-shop/products/nbaoktdoeehyn8ophsxf.jpg', '2026-01-29 11:22:15.000000', 2, NULL, 0),
(24, b'1', '2026-01-28 01:04:32.000000', 'Phong cách: Ấm áp, rạng rỡ và chân thành.\n\nĐặc điểm: Những bông hồng vàng óng ả được gói trong lớp giấy màu kem nhã nhặn. Màu vàng tượng trưng cho tình bạn gắn kết và sự khởi đầu mới đầy hứng khởi.\n\nPhù hợp: Tặng bạn thân, chúc mừng tốt nghiệp hoặc làm quà tặng khích lệ tinh thần người thân đang gặp khó khăn.', 'Bó Hoa Hồng Vàng', 450000.00, NULL, 'bo-hoa-hong-trang-vang', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537062/flower-shop/products/suorfki5qaz2ruvnyjit.jpg', '2026-01-29 00:13:44.000000', 2, NULL, 0),
(25, b'1', '2026-01-28 01:04:34.000000', 'Phong cách: Bay bổng, nhẹ nhàng và đầy nghệ thuật.\n\nĐặc điểm: Sự pha trộn tinh tế giữa các loại hoa nhỏ như cát tường tím nhạt, hoa hồng kem và các loại hoa nhí đi kèm. Cách cắm tự nhiên, tạo cảm giác như một khu vườn hoa thu nhỏ trong giỏ mây.\n\nPhù hợp: Tặng mẹ, tặng chị gái hoặc dùng trang trí phòng khách để tạo không gian thư giãn, nhẹ nhàng.', 'Bó Hoa Cẩm Tú Cầu Mix Tulip', 375000.00, NULL, 'bo-hoa-cam-tu-cau-mix-tulip', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537064/flower-shop/products/lvolrlvwkxabysjdu0zt.jpg', '2026-01-28 01:19:02.000000', 8, NULL, 0),
(26, b'1', '2026-01-28 01:04:36.000000', 'Phong cách: Trong trẻo, mộc mạc và cực kỳ dễ thương.\n\nĐặc điểm: Những bông cúc nhỏ với nhụy vàng nổi bật trên nền cánh trắng, được gói bằng giấy kraft hoặc giấy màu xi măng để giữ vẻ đẹp tự nhiên nhất.\n\nPhù hợp: Dành cho các bạn học sinh, sinh viên, tặng bạn bè dịp chụp ảnh kỷ yếu hoặc đơn giản là một món quà \"không nhân dịp gì cả\" để làm ai đó mỉm cười.', 'Bó Hoa Cúc Trắng Mix Cúc  Tana', 350000.00, NULL, 'bo-hoa-cuc-trang-mix-cuc-tana', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769537066/flower-shop/products/uhey2dcqny3dyvauhw3a.jpg', '2026-01-28 01:18:51.000000', 8, NULL, 0),
(27, b'1', '2026-01-28 01:23:59.000000', 'Phong cách: Quyến rũ, quyền lực và không bao giờ lỗi mốt.\n\nĐặc điểm: Sử dụng hoa hồng đỏ thẫm làm chủ đạo, được sắp xếp dày đặc, tạo cảm giác đủ đầy và viên mãn. Giỏ mây đen hoặc nâu đậm càng làm tôn lên sự rực rỡ của sắc đỏ.\n\nPhù hợp: Chúc mừng kỷ niệm ngày cưới, tặng người yêu hoặc những dịp cần sự trang trọng và thể hiện tình cảm mãnh liệt.', 'Bó Hoa Đồng Tiền Mix Cẩm Chướng', 350000.00, NULL, 'bo-hoa-dong-tien-mix-cam-chuong', 1, 7, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769538229/flower-shop/products/gwlvskzh3khsyvrhvni1.jpg', '2026-01-28 01:23:59.000000', 8, NULL, 0),
(28, b'1', '2026-01-28 01:24:01.000000', 'Phong cách: Rực rỡ, truyền thống và đầy năng lượng.\n\nĐặc điểm: Một khối hoa hồng đỏ thắm rực rỡ được cắm trên kệ gỗ cao, kết hợp cùng các loại lá phụ xanh mướt và nơ ruy băng đỏ dài tạo hiệu ứng thác đổ. Màu đỏ tượng trưng cho sự may mắn và thành công rực rỡ.\n\nPhù hợp: Chúc mừng khai trương cửa hàng, lễ động thổ hoặc các sự kiện ra mắt quan trọng.', 'Kệ Hoa Hồng Đỏ \"Hồng Phát Đại Cát\"', 1500000.00, NULL, 'ke-hoa-hong-do-hong-phat-dai-cat', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769538231/flower-shop/products/ouezpzjxkfzu3s7gddfu.jpg', '2026-01-28 01:24:01.000000', 6, NULL, 0),
(29, b'1', '2026-01-28 01:24:03.000000', 'Phong cách: Sang trọng, thanh lịch và nhẹ nhàng.\n\nĐặc điểm: Sự kết hợp tinh tế giữa hoa hồng kem, hoa lan hồ điệp trắng và các loại hoa nhập khẩu tone hồng phấn. Dáng hoa cắm xòe tự nhiên, bay bổng trên kệ gỗ tối giản.\n\nPhù hợp: Tặng khai trương spa, thẩm mỹ viện, shop thời trang hoặc các không gian nghệ thuật.', 'Kệ Hoa Tone Pastel \"Hồng Phúc Khai Hoa\"', 1200000.00, NULL, 'ke-hoa-tone-pastel-hong-phuc-khai-truong', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769538233/flower-shop/products/fvb60v4xqx4ikskh8wwu.jpg', '2026-01-28 01:24:03.000000', 6, NULL, 0),
(30, b'1', '2026-01-28 01:24:05.000000', 'Phong cách: Ấm cúng, đủ đầy và thân thiện.\n\nĐặc điểm: Hoa hồng đỏ phối cùng hoa đồng tiền trắng và hồng được cắm đầy đặn trong giỏ mây tre đan mộc mạc. Thiết kế mang lại cảm giác gần gũi nhưng vẫn rất trang trọng nhờ sắc đỏ chủ đạo.\n\nPhù hợp: Quà tặng chúc mừng tại văn phòng, trang trí sảnh tiệc hoặc tặng người thân nhân dịp đặc biệt.', 'Giỏ Hoa Mây \"Phúc Lộc Viên Mãn\"', 1350000.00, NULL, 'ke-hoa-hoa-may-phuc-loc-vien-man', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769538235/flower-shop/products/bpcvobao4mf0attydn4d.jpg', '2026-01-28 02:42:11.000000', 6, NULL, 0),
(31, b'1', '2026-01-28 01:37:20.000000', 'Phong cách: Đa dạng, đa sắc màu và tràn đầy sức sống.\n\nĐặc điểm: Kết hợp nhiều loại hoa từ hoa lá phụ đến hoa chính với các tông màu nóng như cam, đỏ, vàng xen kẽ lá xanh. Giấy gói tối màu giúp các lớp hoa trở nên nổi bật và có chiều sâu.\n\nPhù hợp: Tặng khai trương, chúc mừng tân gia hoặc các sự kiện cần sự náo nhiệt, vui vẻ.', 'Bó Hoa Hồng Mix Lan Hồ Điệp', 300000.00, NULL, 'bo-hoa-hong-mix-lan-ho-diep', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539030/flower-shop/products/zmbvkqobfe6zjakbs3bt.jpg', '2026-01-28 01:37:20.000000', 8, NULL, 0),
(32, b'1', '2026-01-28 01:37:23.000000', 'Phong cách: Tươi sáng, thịnh vượng và tràn đầy hy vọng.\n\nĐặc điểm: Những đóa hướng dương vàng rực rỡ làm chủ đạo, phối cùng hoa lan vũ nữ và các loại hoa tone vàng cam. Lẵng hoa như mang theo ánh mặt trời, đại diện cho sự phát triển không ngừng.', 'Kệ Hoa Hướng Dương \"Kim Ngọc Phát Tài\"', 1200000.00, NULL, 'ke-hoa-huong-duong-kim-ngoc-phat-tai', 1, 5, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539032/flower-shop/products/lt3veit4fjm5xpyzjzhh.jpg', '2026-01-28 01:37:23.000000', 6, NULL, 0),
(33, b'1', '2026-01-28 01:37:25.000000', 'Phong cách: Hiện đại, tối giản và đẳng cấp.\n\nĐặc điểm: Thiết kế trên giá tranh (giá chữ A) độc đáo, kết hợp giữa hoa hồng Ohara, hoa môn đỏ và các loại lá bạc nhập khẩu. Đây là mẫu hoa mang tính nghệ thuật cao, tạo điểm nhấn khác biệt.\n\nPhù hợp: Chúc mừng các sự kiện triển lãm, hội nghị hoặc khai trương các showroom nội thất sang trọng.', 'Kệ Hoa Chữ A \"Vinh Hoa Thịnh Vượng\"', 1050000.00, NULL, 'ke-hoa-chu-a-vinh-hoa-thinh-vuong', 1, 7, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539035/flower-shop/products/fvuphxltiydng49b4yfm.jpg', '2026-01-28 01:37:25.000000', 6, NULL, 0),
(34, b'1', '2026-01-28 01:37:27.000000', 'Phong cách: Cổ điển, nhẹ nhàng và ấm áp.\n\nĐặc điểm: Những đóa cẩm chướng hồng phấn xếp lớp tỉ mỉ, được bao bọc bởi lớp giấy gói màu kem và nâu nhạt tạo cảm giác mộc mạc nhưng đầy tinh tế.\n\nPhù hợp: Tặng Mẹ, tặng cô giáo hoặc những người phụ nữ bạn kính trọng để bày tỏ lòng biết ơn.', 'Bó Hoa Cẩm Chướng Hồng Phố Nơ', 650000.00, NULL, 'bo-hoa-cam-chuong-hong-phoi-no', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539037/flower-shop/products/whr5rdgrknex1mgflylc.jpg', '2026-01-28 01:37:27.000000', 7, NULL, 0),
(35, b'1', '2026-01-28 01:37:29.000000', 'Phong cách: Ngọt ngào, trẻ trung và nổi bật.\n\nĐặc điểm: Sử dụng hoa cẩm chướng với viền cánh màu hồng đậm độc đáo, kết hợp giấy gói tông hồng rực rỡ mang đến năng lượng tích cực.\n\nPhù hợp: Quà tặng sinh nhật cho bạn bè hoặc người yêu.', 'Bó Hoa Cẩm Chướng Viền Hồng Phối Nơ Ren', 645000.00, NULL, 'bo-hoa-cam-chuong-vien-hong-phoi-no-ren', 1, 3, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539039/flower-shop/products/q0yirixlopsifho4nkmv.jpg', '2026-01-28 01:37:29.000000', 7, NULL, 0),
(36, b'1', '2026-01-28 01:37:32.000000', 'Phong cách: Đáng yêu, sáng tạo và ý nghĩa.\n\nĐặc điểm: Một sự kết hợp thú vị giữa hoa cẩm tú cầu xanh, hoa đồng tiền hồng và một chú gấu bông nhỏ đội mũ cử nhân xinh xắn đặt trong giỏ mây.\n\nPhù hợp: Quà tặng lưu niệm tuyệt vời cho bạn bè trong ngày lễ tốt nghiệp.', 'Giỏ Hoa Cẩm Chướng Phối Gấu Tốt ', 300000.00, NULL, 'gio-hoa-cam-chuong-phoi-gau-tot-nghiep', 1, 15, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769539042/flower-shop/products/efo2clfdxrwtnziu4jzq.jpg', '2026-01-28 01:37:32.000000', 7, NULL, 0),
(37, b'1', '2026-01-28 01:56:23.000000', 'Phong cách: Trong trẻo, tinh khôi và trang trọng.\n\nĐặc điểm: Sắc trắng của hoa đồng tiền và cúc trắng làm chủ đạo, điểm xuyết thêm những chú gấu cử nhân tí hon len lỏi giữa các đóa hoa. Giấy gói trắng đồng điệu giúp bó hoa trông rất thanh lịch.\n\nPhù hợp: Dành cho các tân cử nhân yêu thích sự đơn giản và tinh tế.', 'Bó Hoa Đồng Tiền Mix Cúc Trắng Phối Gấu Tốt Nghiệp', 450000.00, NULL, 'bo-hoa-dong-tien-mix-cuc-trang-phoi-gau-tot-nghiep', 1, 5, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540173/flower-shop/products/pblrqdnrlaul7abdzalk.jpg', '2026-01-28 01:56:23.000000', 7, NULL, 0),
(38, b'1', '2026-01-28 01:56:26.000000', 'Phong cách: Hiện đại, rạng rỡ và đầy sức sống.\n\nĐặc điểm: Sự phối hợp hoàn hảo giữa hoa cúc Tana nhỏ xinh và hoa đồng tiền hồng nhạt. Điểm nhấn chính là chú gấu tốt nghiệp mặc áo cử nhân đen nổi bật giữa trung tâm bó hoa.\n\nPhù hợp: Tặng bạn bè chụp ảnh kỷ yếu, mang lại những khung hình rạng rỡ và đáng nhớ.', 'Bó Hoa Đồng Tiền Mix Cúc Tana Phối Gấu Tốt Nghiệp', 250000.00, NULL, 'bo-hoa-cuc-dong-tien-mix-cuc-tana-phoi-gau-tot-nghiep', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540176/flower-shop/products/kwtmaczn2i9mi7bqg6k6.jpg', '2026-01-28 01:56:26.000000', 7, NULL, 0),
(39, b'1', '2026-01-28 01:56:27.000000', 'Phong cách: Rạng rỡ, năng lượng và tràn đầy hy vọng.\n\nĐặc điểm: Những đóa hướng dương vàng rực rỡ phối cùng hoa hồng cam và lá bạc, điểm xuyết một chú gấu bông cử nhân đội mũ xinh xắn.\n\nPhù hợp: Làm quà tặng ý nghĩa cho bạn bè, người thân trong lễ tốt nghiệp, thay lời chúc về một hành trình mới thành công rực rỡ.', 'Bó Hoa Hướng Dương Phối Gấu Tốt Nghiệp', 275000.00, NULL, 'bo-hoa-huong-duong-phoi-gau-tot-nghiep', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540178/flower-shop/products/phnrt9nafopcgerhki4g.jpg', '2026-01-28 01:56:27.000000', 7, NULL, 0),
(40, b'1', '2026-01-28 01:56:29.000000', 'Phong cách: Cổ điển, ấm cúng và mang tính bền vững cao.\n\nĐặc điểm:\n\nSự kết hợp hài hòa giữa những đóa hoa hồng sáp tông màu hồng đậm, nhạt xen kẽ cùng hoa lan trắng thanh khiết.\n\nHoa được cắm tỉ mỉ trong giỏ mây tre đan truyền thống, tạo cảm giác gần gũi và mộc mạc.\n\nƯu điểm lớn nhất là hoa có thể giữ được màu sắc rực rỡ và hương thơm nhẹ nhàng trong thời gian rất dài mà không cần chăm sóc.\n\nPhù hợp: * Làm quà tặng kỷ niệm cho những người yêu thích sự bền lâu.\n\nTrang trí phòng khách, kệ sách hoặc không gian làm việc để tạo điểm nhấn ấm áp và sang trọng.', 'Giỏ Hoa Sáp Hoa Hồng Mix  Lan', 320000.00, NULL, 'gio-hoa-sap-hong-mix-lan', 1, 14, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540180/flower-shop/products/nqhmohwgpaus2tvg85ay.jpg', '2026-01-28 01:56:29.000000', 7, NULL, 0),
(41, b'1', '2026-01-28 01:56:31.000000', 'Phong cách: Độc đáo, thực tế và vô cùng sang trọng.\n\nĐặc điểm: Những tờ tiền mệnh giá cao được tạo hình khéo léo thành những bông hoa, bao bọc bởi lớp giấy gói voan trắng và lưới cao cấp.\n\nPhù hợp: Tặng vào các dịp sinh nhật, kỷ niệm hoặc ngày lễ khi bạn muốn gửi gắm cả giá trị tinh thần lẫn vật chất một cách tinh tế.', 'Bó Hoa Nhét Tiền - Theo Order', 300000.00, NULL, 'bo-hoa-nhet-tien-theo-order', 1, 50, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540182/flower-shop/products/jk5xp31jacztuo9g1lho.jpg', '2026-01-28 01:56:31.000000', 7, NULL, 0),
(42, b'1', '2026-01-28 01:56:33.000000', 'Phong cách: Cổ điển, ấm áp và chân thành.\n\nĐặc điểm: Sự kết hợp hài hòa giữa hoa hồng kem và cẩm chướng hồng trong giỏ mây, mang lại cảm giác bình yên và sự thấu hiểu.\n\nPhù hợp: Tặng Mẹ, người lớn tuổi hoặc bạn bè thân thiết nhân dịp sinh nhật, chúc mừng để thể hiện sự quan tâm chu đáo.', 'Giỏ Hoa Hồng Mix Hoa Cẩm Chướng', 350000.00, NULL, 'gio-hoa-hong-mix-hoa-cam-chuong', 1, 5, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769540183/flower-shop/products/tf4ei67fsdwcrnspvk8u.jpg', '2026-01-28 01:56:33.000000', 7, NULL, 0),
(43, b'1', '2026-01-28 02:11:39.000000', 'Phong cách: Phóng khoáng, tự nhiên như một khu vườn nhỏ.\n\nĐặc điểm: Sử dụng các loại hoa cánh mỏng như cát tường và hồng Garden với tông màu pastel đa dạng, cách cắm tự nhiên với nhiều lá xanh phụ trợ.\n\nPhù hợp: Trang trí bàn tiệc, tặng tân gia hoặc làm quà tặng cho những người có tâm hồn bay bổng, yêu thiên nhiên.', 'Giỏ Hoa Hồng Garden Mix Hoa Cát Tường', 300000.00, 290000.00, 'gio-hoa-hong-garden-mix-hoa-cat-tuong', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541089/flower-shop/products/oqyoqj2hbu7ddcviahrd.jpg', '2026-01-28 02:11:39.000000', 7, NULL, 0),
(44, b'1', '2026-01-28 02:11:41.000000', 'Phong cách: Hiện đại, thanh khiết và mang hơi hướng tối giản nhưng cực kỳ sang trọng.\n\nĐặc điểm: * Sử dụng hàng chục cành hồng spray (hay còn gọi là hồng chùm) với những nụ hoa nhỏ xinh, tạo nên một vòm hoa tròn trịa, bồng bềnh như một áng mây hồng.\n\nHoa được bao bọc bởi lớp giấy gói dập nổi màu trắng cao cấp, giúp tôn vinh triệt để sắc hồng pastel dịu dàng của hoa.\n\nĐiểm nhấn là chiếc nơ ruy băng lụa mảnh, tạo nên vẻ ngoài chỉn chu và tinh tế.\n\nPhù hợp: * Tặng cho những cô nàng yêu thích sự nhẹ nhàng, thơ mộng.\n\nRất lý tưởng để làm đạo cụ chụp ảnh \"sống ảo\", tặng sinh nhật hoặc dùng làm quà tặng bất ngờ trong các buổi hẹn hò.', 'Bó Hoa Hồng Spray Phối Nơ', 580000.00, NULL, 'bo-hoa-hong-spray-phoi-no', 1, 3, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541091/flower-shop/products/wodlnrxy122wb5jdv1s8.jpg', '2026-01-29 00:13:09.000000', 7, NULL, 0),
(45, b'1', '2026-01-28 02:11:43.000000', 'Phong cách: Ngọt ngào, bồng bềnh và lãng mạn như một áng mây.\n\nĐặc điểm: Những đóa hoa baby hồng li ti được cắm dày đặc, tạo hình khối tròn hoàn hảo trong hộp quà màu hồng pastel đồng điệu. Điểm nhấn là chiếc nơ ruy băng lụa mềm mại.\n\nPhù hợp: Tặng quà sinh nhật, kỷ niệm hoặc trang trí bàn tiệc, không gian phòng ngủ để tạo cảm giác nhẹ nhàng, thư thái.', 'Bó Hoa Baby Hồng', 450000.00, NULL, 'bo-hoa-baby-hong', 1, 5, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541093/flower-shop/products/uyewd1xhtxafoxqmepz4.jpg', '2026-01-28 02:11:43.000000', 5, NULL, 0),
(46, b'1', '2026-01-28 02:11:46.000000', 'Phong cách: Rực rỡ, ấn tượng và thu hút mọi ánh nhìn.\n\nĐặc điểm: Sử dụng hồng spray tông màu hồng đậm (hot pink) cực kỳ nổi bật. Bó hoa được gói bằng giấy trắng cao cấp cùng lớp voan lưới bồng bềnh, thắt nơ bản lớn tạo sự quý phái.\n\nPhù hợp: Dành tặng cho những người có cá tính mạnh mẽ, yêu thích sự rạng rỡ trong các dịp lễ tình nhân hoặc kỷ niệm đặc biệt.', 'Bó Hoa Hồng Phối Ren Gắn Nơ', 920000.00, NULL, 'bo-hoa-hong-phoi-ren-gan-no', 1, 2, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769542159/flower-shop/products/ejwzawvexcyr8hs7ei0c.jpg', '2026-01-29 00:12:53.000000', 5, NULL, 0),
(47, b'1', '2026-01-28 02:11:46.000000', 'Phong cách: Rực rỡ, ấn tượng và thu hút mọi ánh nhìn.\n\nĐặc điểm: Sử dụng hồng spray tông màu hồng đậm (hot pink) cực kỳ nổi bật. Bó hoa được gói bằng giấy trắng cao cấp cùng lớp voan lưới bồng bềnh, thắt nơ bản lớn tạo sự quý phái.\n\nPhù hợp: Dành tặng cho những người có cá tính mạnh mẽ, yêu thích sự rạng rỡ trong các dịp lễ tình nhân hoặc kỷ niệm đặc biệt.', 'Bó Hoa Hồng Spray Phối Nơ To', 1750000.00, NULL, 'bo-hoa-hong-spray-phoi-no-to', 1, 3, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769542116/flower-shop/products/eyfjjbwxau6qqlikm4mr.jpg', '2026-01-29 00:11:41.000000', 5, NULL, 0),
(48, b'1', '2026-01-28 02:21:14.000000', 'Phong cách: Sang trọng, tinh tế và đầy tính nghệ thuật.\n\nĐặc điểm: Sự kết hợp giữa đóa cẩm tú cầu trắng muốt làm nền cho những nhành hồng spray hồng phấn nổi bật. Bình hoa được trang trí thêm nơ thắt điệu đà, tạo nên vẻ đẹp tiểu thư đài các.\n\nPhù hợp: Chúc mừng khai trương nhỏ, tặng đối tác hoặc trang trí tại các quầy lễ tân sang trọng.', 'Bó Hoa Cẩm Tú Cầu Mix Hồng Spray', 720000.00, NULL, 'bo-hoa-cam-tu-cau-mix-hong-spray', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541664/flower-shop/products/qkcpfjrgbif7vjgwrha7.jpg', '2026-01-29 00:12:33.000000', 5, NULL, 0),
(49, b'1', '2026-01-28 02:21:17.000000', 'Phong cách: Huyền bí, đẳng cấp và vô cùng hiện đại.\n\nĐặc điểm: Những nụ hồng spray sắc hồng rực rỡ được bao bọc bởi lớp giấy gói đen tuyền. Sự tương phản mạnh mẽ giữa hai tông màu đen - hồng tạo nên sức hút khó cưỡng.\n\nPhù hợp: Món quà hoàn hảo để tạo bất ngờ cho người yêu hoặc dùng trong các sự kiện thời trang, nghệ thuật.', 'Bó Hoa Hồng Spray Tone Đen', 1500000.00, NULL, 'bo-hoa-hong-spray-tone-den', 1, 4, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541667/flower-shop/products/dglhmojrctrptx8fsyo4.jpg', '2026-01-29 00:12:18.000000', 5, NULL, 0),
(50, b'1', '2026-01-28 02:21:19.000000', 'Phong cách: Dễ thương, tươi tắn và tràn đầy sức sống.\n\nĐặc điểm: Những bông cúc lụa màu hồng phấn rạng rỡ được cắm gọn gàng trong chậu sứ trắng vuông tối giản. Cây có kích thước nhỏ gọn, không tốn công chăm sóc mà vẫn giữ được màu sắc tươi mới quanh năm.\n\nPhù hợp: Trang trí bàn học, kệ sách hoặc làm điểm nhấn cho không gian làm việc thêm phần sinh động.', 'Chậu Hoa Cúc Hồng Mini', 235000.00, NULL, 'chau-hoa-cuc-hong-mini', 1, 6, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541669/flower-shop/products/buiiicvuctjchteh9ue8.jpg', '2026-01-29 00:19:45.000000', 12, NULL, 0),
(51, b'1', '2026-01-28 02:21:21.000000', 'Phong cách: Hiện đại, xanh mát và thanh lịch.\n\nĐặc điểm: Dáng cây tròn trịa với lá xanh mướt mắt, tượng trưng cho sự sung túc và may mắn. Chậu sứ trắng tròn giúp làm nổi bật màu xanh tự nhiên của lá cây.\n\nPhù hợp: Đặt tại bàn làm việc, quầy lễ tân hoặc làm quà tặng khai trương nhỏ với ý nghĩa mong cầu sự phát triển bền vững.', 'Chậu Cây Hoàng Dương Mini', 200000.00, NULL, 'chau-cay-hoang-duong-mini', 1, 7, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541671/flower-shop/products/wf8a2rl3r441d6chkytb.jpg', '2026-01-28 02:21:21.000000', 12, NULL, 0),
(52, b'1', '2026-01-28 02:23:01.000000', 'Phong cách: Sang trọng, phong thủy và chuyên nghiệp.\n\nĐặc điểm: Những tán lá xanh đậm, dày và bóng mọc vươn cao, được rải sỏi trắng tinh tế phía dưới gốc. Đây là loại cây phong thủy hàng đầu mang lại tài lộc.\n\nPhù hợp: Quà tặng tân gia, quà tặng đối tác hoặc trang trí văn phòng với mong muốn chiêu tài, hút lộc cho gia chủ.', 'Chậu Kim Tiền Mini', 175000.00, NULL, 'chau-cay-kim-tien-mini', 1, 17, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541771/flower-shop/products/bale8p6nnsweustqffdp.jpg', '2026-01-28 02:23:01.000000', 12, NULL, 0),
(53, b'1', '2026-01-28 02:23:03.000000', 'Phong cách: Độc đáo, tinh tế và đầy nghệ thuật.\n\nĐặc điểm: Những nhánh sen đá căng mọng, xếp lớp như những chuỗi ngọc bích quý giá. Màu sắc nhã nhặn của cây kết hợp hoàn hảo với chậu sứ trắng bóng bẩy.\n\nPhù hợp: Dành cho những người yêu thích sưu tầm cây mọng nước, dùng để trang trí ban công, cửa sổ hoặc góc trà thư giãn.', 'Chậu Sen Đá Chuỗi Ngọc', 275000.00, NULL, 'chau-sen-da-chuoi-mini', 1, 13, 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769541773/flower-shop/products/ttay70ckinfb5porgdmw.jpg', '2026-01-28 02:23:03.000000', 12, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` bigint(20) NOT NULL,
  `admin_reply` text DEFAULT NULL,
  `comment` text DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `images` varchar(2000) DEFAULT NULL,
  `rating` int(11) NOT NULL,
  `replied_at` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `admin_reply_images` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`id`, `admin_reply`, `comment`, `created_at`, `images`, `rating`, `replied_at`, `status`, `updated_at`, `order_id`, `product_id`, `user_id`, `admin_reply_images`) VALUES
(1, NULL, 'ewqedsawqdsqdadsa', '2026-01-02 12:22:03.000000', NULL, 5, NULL, 'APPROVED', '2026-01-02 12:22:51.000000', 12, 7, 12, NULL),
(2, NULL, 'dưqdascasfaff', '2026-01-02 12:22:27.000000', NULL, 5, NULL, 'APPROVED', '2026-01-02 12:22:49.000000', 12, 9, 12, NULL),
(3, NULL, 'sàdasdsaccccccc', '2026-01-02 13:02:05.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767333722/flower-shop/products/mqmjpfurmii5a4blalph.jpg\"]', 4, NULL, 'APPROVED', '2026-01-02 13:02:24.000000', 13, 8, 12, NULL),
(4, NULL, 'dep xinh  xczcx', '2026-01-02 13:15:17.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767334506/flower-shop/products/ho33m47ukwrhseggzdos.png\"]', 5, NULL, 'APPROVED', '2026-01-02 13:15:35.000000', 14, 10, 12, NULL),
(5, NULL, 'csdfgsfghjk', '2026-01-05 16:18:16.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767604690/flower-shop/products/sgd6tqwxlqr9jqds1nom.jpg\"]', 1, NULL, 'APPROVED', '2026-01-05 16:18:37.000000', 16, 9, 12, NULL),
(6, 'cảm ơn bạn đã ủng hộ\n', 'hoa cinh qua', '2026-01-07 22:16:16.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767798967/flower-shop/products/oapeukcu6msn4iaphkef.jpg\"]', 4, '2026-01-07 22:18:24.000000', 'APPROVED', '2026-01-07 22:18:24.000000', 19, 8, 12, NULL),
(7, 'oke\n', 'jjjkjkkjvvnnnbbn', '2026-01-07 22:47:22.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767800835/flower-shop/products/zzlqufylgtetoq4ak6hp.jpg\"]', 5, '2026-01-07 23:07:21.000000', 'APPROVED', '2026-01-07 23:08:27.000000', 16, 7, 12, NULL),
(8, 'Cảm ơn bạn đã ủng hộ shop\n', 'Sản phẩm đẹp chất lượng ăn ngon', '2026-01-08 20:17:21.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1767878239/flower-shop/products/q0vtdel8zhp3p38rbvgy.jpg\"]', 5, '2026-01-08 20:18:05.000000', 'APPROVED', '2026-01-08 20:18:05.000000', 23, 11, 12, NULL),
(9, 'cảm ơn bạn đã ủng hộ ', 'hoa đẹp lắm nha shop', '2026-01-27 20:26:41.000000', '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769520396/flower-shop/products/vf0raibrozje6yvfky7q.png\"]', 5, '2026-01-27 20:28:28.000000', 'APPROVED', '2026-01-27 20:28:28.000000', 35, 8, 21, '[\"https://res.cloudinary.com/db1b15yn4/image/upload/v1769520503/flower-shop/products/sgawl8n3m9f85ie995eo.png\"]'),
(10, 'cam on', 'dep lắm\nshop piow', '2026-01-27 20:44:24.000000', NULL, 5, '2026-01-27 20:45:06.000000', 'APPROVED', '2026-01-27 20:45:06.000000', 32, 9, 21, NULL),
(11, NULL, 'tooooooooooooooooooooooo', '2026-01-27 21:00:49.000000', NULL, 5, NULL, 'APPROVED', '2026-01-27 21:00:49.000000', 36, 11, 21, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `saved_vouchers`
--

CREATE TABLE `saved_vouchers` (
  `id` bigint(20) NOT NULL,
  `is_available` bit(1) DEFAULT NULL,
  `saved_at` datetime(6) DEFAULT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `voucher_id` bigint(20) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `used_count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `saved_vouchers`
--

INSERT INTO `saved_vouchers` (`id`, `is_available`, `saved_at`, `used_at`, `user_id`, `voucher_id`, `quantity`, `used_count`) VALUES
(3, b'1', '2026-01-02 15:36:11.000000', NULL, 12, 10, NULL, NULL),
(8, b'1', '2026-01-08 20:19:53.000000', NULL, 12, 6, NULL, NULL),
(9, b'1', '2026-01-29 13:54:22.000000', NULL, 21, 15, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `shipping_district_rules`
--

CREATE TABLE `shipping_district_rules` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `base_fee` int(11) NOT NULL,
  `city` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `delivery_type` enum('RUSH','STANDARD') NOT NULL,
  `district` varchar(100) NOT NULL,
  `estimated_time` varchar(30) NOT NULL,
  `free_ship_threshold` int(11) NOT NULL,
  `holiday_multiplier` decimal(3,2) DEFAULT NULL,
  `peak_fee` int(11) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `zone` enum('INNER','OUTER') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shipping_district_rules`
--

INSERT INTO `shipping_district_rules` (`id`, `active`, `base_fee`, `city`, `created_at`, `delivery_type`, `district`, `estimated_time`, `free_ship_threshold`, `holiday_multiplier`, `peak_fee`, `updated_at`, `zone`) VALUES
(1, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 1', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(2, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 3', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(3, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 4', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(4, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 5', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(5, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 6', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(6, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 7', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(7, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 8', '3-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(8, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 10', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(9, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 11', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(10, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Phú Nhuận', '2-3 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(11, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Bình Thạnh', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(12, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Tân Bình', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(13, b'1', 25000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Gò Vấp', '2-4 giờ', 500000, 1.00, 0, '2026-01-29 11:08:07.000000', 'INNER'),
(14, b'1', 35000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận 12', '4-5 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(15, b'1', 35000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'TP. Thủ Đức', '4-5 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(16, b'1', 35000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Bình Tân', '4-5 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(17, b'1', 30000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Quận Tân Phú', '3-4 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(18, b'1', 40000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Huyện Hóc Môn', '5-6 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(19, b'1', 45000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Huyện Củ Chi', '5-6 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(20, b'1', 40000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Huyện Bình Chánh', '5-6 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(21, b'1', 40000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Huyện Nhà Bè', '5-6 giờ', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER'),
(22, b'1', 60000, 'TPHCM', '2026-01-29 11:08:07.000000', 'STANDARD', 'Huyện Cần Giờ', '1 ngày', 700000, 1.00, 0, '2026-01-29 11:08:07.000000', 'OUTER');

-- --------------------------------------------------------

--
-- Table structure for table `stock_history`
--

CREATE TABLE `stock_history` (
  `id` bigint(20) NOT NULL,
  `change_quantity` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `final_quantity` int(11) NOT NULL,
  `note` varchar(500) DEFAULT NULL,
  `order_code` varchar(50) DEFAULT NULL,
  `reason` enum('ADMIN_ADJUST','DAMAGED','EXPORT','IMPORT','INITIAL','ORDER_CANCELLED','ORDER_PLACED','RETURN') NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stock_history`
--

INSERT INTO `stock_history` (`id`, `change_quantity`, `created_at`, `created_by`, `final_quantity`, `note`, `order_code`, `reason`, `product_id`) VALUES
(1, 7, '2025-12-22 22:49:14.000000', 'ngercuae', 200, '', NULL, 'IMPORT', 9),
(2, -10, '2025-12-22 22:49:38.000000', 'ngercuae', 190, '', NULL, 'EXPORT', 9),
(3, 10, '2025-12-22 22:51:44.000000', 'ngercuae', 200, 'fdg', NULL, 'IMPORT', 9),
(4, 10, '2025-12-23 20:53:24.000000', 'ngercuae', 210, '', NULL, 'ADMIN_ADJUST', 9),
(5, -3, '2026-01-02 11:47:46.000000', 'ngerhello', 97, 'Trừ tồn kho khi đặt hàng', 'ORD2926657485C4', 'ORDER_PLACED', 7),
(6, -1, '2026-01-02 11:47:46.000000', 'ngerhello', 209, 'Trừ tồn kho khi đặt hàng', 'ORD2926657485C4', 'ORDER_PLACED', 9),
(7, -1, '2026-01-02 12:59:33.000000', 'ngerhello', 99, 'Trừ tồn kho khi đặt hàng', 'ORD335731035866', 'ORDER_PLACED', 8),
(8, -1, '2026-01-02 13:14:16.000000', 'ngerhello', 99, 'Trừ tồn kho khi đặt hàng', 'ORD344568522216', 'ORDER_PLACED', 10),
(9, -1, '2026-01-02 15:26:47.000000', 'ngerhello', 98, 'Trừ tồn kho khi đặt hàng', 'ORD42407625D94E', 'ORDER_PLACED', 10),
(10, -1, '2026-01-02 15:26:47.000000', 'ngerhello', 208, 'Trừ tồn kho khi đặt hàng', 'ORD42407625D94E', 'ORDER_PLACED', 9),
(11, -1, '2026-01-05 16:16:27.000000', 'ngerhello', 96, 'Trừ tồn kho khi đặt hàng', 'ORD045873742C2A', 'ORDER_PLACED', 7),
(12, -1, '2026-01-05 16:16:27.000000', 'ngerhello', 207, 'Trừ tồn kho khi đặt hàng', 'ORD045873742C2A', 'ORDER_PLACED', 9),
(13, -1, '2026-01-07 17:02:11.000000', 'ngerhello', 95, 'Trừ tồn kho khi đặt hàng', 'ORD801317284A15', 'ORDER_PLACED', 7),
(14, -1, '2026-01-07 17:13:55.000000', 'ngerhello', 98, 'Trừ tồn kho khi đặt hàng', 'ORD80835092ADAE', 'ORDER_PLACED', 8),
(15, -1, '2026-01-07 17:24:57.000000', 'ngerhello', 97, 'Trừ tồn kho khi đặt hàng', 'ORD814963005C40', 'ORDER_PLACED', 8),
(16, 1, '2026-01-07 17:25:44.000000', 'ngercuae', 98, 'Hoàn lại tồn kho khi hủy đơn', 'ORD80835092ADAE', 'ORDER_CANCELLED', 8),
(17, 1, '2026-01-07 17:25:46.000000', 'ngercuae', 96, 'Hoàn lại tồn kho khi hủy đơn', 'ORD801317284A15', 'ORDER_CANCELLED', 7),
(18, -1, '2026-01-08 11:11:32.000000', 'hellontt1705', 206, 'Trừ tồn kho khi đặt hàng', 'ORD454927489FDA', 'ORDER_PLACED', 9),
(19, -1, '2026-01-08 11:19:03.000000', 'hellontt1705', 95, 'Trừ tồn kho khi đặt hàng', 'ORD4594302815D9', 'ORDER_PLACED', 7),
(20, -1, '2026-01-08 20:01:08.000000', 'ngerhello', 205, 'Trừ tồn kho khi đặt hàng', 'ORD77268540A947', 'ORDER_PLACED', 9),
(21, -1, '2026-01-08 20:15:43.000000', 'ngerhello', 19, 'Trừ tồn kho khi đặt hàng', 'ORD78143558CB29', 'ORDER_PLACED', 11),
(22, -1, '2026-01-14 15:01:53.000000', 'ngerng', 94, 'Trừ tồn kho khi đặt hàng', 'ORD77713655320F', 'ORDER_PLACED', 7),
(23, -1, '2026-01-14 15:20:17.000000', 'ngercuae', 94, 'Trừ tồn kho khi đặt hàng', 'ORD788174291ED7', 'ORDER_PLACED', 7),
(24, -1, '2026-01-14 15:36:11.000000', 'ngercuae', 204, 'Trừ tồn kho khi đặt hàng', 'ORD79771474DE36', 'ORDER_PLACED', 9),
(25, -1, '2026-01-14 15:44:45.000000', 'ngercuae', 203, 'Trừ tồn kho khi đặt hàng', 'ORD802850802BC0', 'ORDER_PLACED', 9),
(26, -1, '2026-01-14 15:47:03.000000', 'ngercuae', 202, 'Trừ tồn kho khi đặt hàng', 'ORD804232251FA7', 'ORDER_PLACED', 9),
(27, 1, '2026-01-14 15:49:02.000000', 'ngeroday', 203, 'Hoàn lại tồn kho khi hủy đơn', 'ORD802850802BC0', 'ORDER_CANCELLED', 9),
(28, 1, '2026-01-14 15:49:04.000000', 'ngeroday', 204, 'Hoàn lại tồn kho khi hủy đơn', 'ORD79771474DE36', 'ORDER_CANCELLED', 9),
(29, -1, '2026-01-14 15:50:14.000000', 'ngercuae', 203, 'Trừ tồn kho khi đặt hàng', 'ORD8061497398F9', 'ORDER_PLACED', 9),
(30, -1, '2026-01-14 16:00:21.000000', 'ngerng', 202, 'Trừ tồn kho khi đặt hàng', 'ORD812211363BFD', 'ORDER_PLACED', 9),
(31, -1, '2026-01-14 16:07:28.000000', 'ngerng', 202, 'Trừ tồn kho khi đặt hàng', 'ORD816487013E31', 'ORDER_PLACED', 9),
(32, -1, '2026-01-14 16:08:08.000000', 'ngerng', 201, 'Trừ tồn kho khi đặt hàng', 'ORD8168831011CD', 'ORDER_PLACED', 9),
(33, -1, '2026-01-27 19:58:21.000000', 'ngerng', 93, 'Trừ tồn kho khi đặt hàng', 'ORD18701608D9A4', 'ORDER_PLACED', 7),
(34, -1, '2026-01-27 20:08:02.000000', 'ngerng', 97, 'Trừ tồn kho khi đặt hàng', 'ORD1928270296B6', 'ORDER_PLACED', 10),
(35, -1, '2026-01-27 20:24:28.000000', 'ngerng', 97, 'Trừ tồn kho khi đặt hàng', 'ORD20268447D4AD', 'ORDER_PLACED', 8),
(36, -1, '2026-01-27 21:00:20.000000', 'ngerng', 18, 'Trừ tồn kho khi đặt hàng', 'ORD2242046164C8', 'ORDER_PLACED', 11),
(37, -4, '2026-01-28 23:41:16.000000', 'Vyyyyy', 2, 'Trừ tồn kho khi đặt hàng', 'ORD184763384182', 'ORDER_PLACED', 50),
(38, 4, '2026-01-29 00:19:45.000000', 'Vyyyyy', 6, 'Hoàn lại tồn kho khi hủy đơn', 'ORD184763384182', 'ORDER_CANCELLED', 50),
(39, -1, '2026-01-29 11:19:09.000000', 'ngerng', 3, 'Trừ tồn kho khi đặt hàng', 'ORD60349766248A', 'ORDER_PLACED', 23),
(40, 1, '2026-01-29 11:22:15.000000', 'ngeroday', 4, 'Hoàn lại tồn kho khi hủy đơn', 'ORD60349766248A', 'ORDER_CANCELLED', 23);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','STAFF') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `email_verified` bit(1) DEFAULT NULL,
  `auth_provider` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `address`, `created_at`, `email`, `full_name`, `is_active`, `password`, `phone_number`, `role`, `updated_at`, `username`, `email_verified`, `auth_provider`, `avatar`) VALUES
(8, NULL, '2025-12-21 12:07:11.000000', 'hellontt.nger1705@gmail.com', NULL, b'1', '$2a$10$YDe6Se6arc8GQzb1jkSv2OMIFGQ2FHzNLj/B5BtxmHBv58nMpGvrm', '0359921028', 'CUSTOMER', '2026-01-14 14:32:24.000000', 'ngercuae', NULL, NULL, NULL),
(12, '123 Võ Văn Ngân\nPhường Linh Chiểu', '2025-12-21 20:46:44.000000', 'ngerhello@gmail.com', 'tuan tai', b'1', '$2a$10$GwkKkg2zO168vauruByipOwGinV0fIVRWuG5.h3nQMpijbTv8xSIi', '0356621028', 'CUSTOMER', '2025-12-22 15:48:40.000000', 'ngerhello', NULL, NULL, NULL),
(13, NULL, '2026-01-02 15:37:24.000000', 'hello.ntt1705@gmail.com', 'Tai Nguyen', b'1', '$2a$10$rfrtPMBJImFO9Grmvv5NlufnnAbD6GgruJO3Ph7vWW9DZ3UPyY2fS', NULL, 'CUSTOMER', '2026-01-07 14:57:18.000000', 'hellontt1705', NULL, NULL, NULL),
(17, NULL, '2026-01-14 13:50:28.000000', 'testuser123@gmail.com', NULL, b'1', '$2a$10$uhi7IWZTVCswcdYK27EJcOj957AG4niwRewKKDp7UwCM3YJlIZmfO', '0911111111', 'CUSTOMER', '2026-01-14 13:50:28.000000', 'testuser123', b'0', 'LOCAL', NULL),
(18, NULL, '2026-01-14 13:55:55.000000', 'ngeroday1705@gmail.com', NULL, b'1', '$2a$10$/q2GiV3UWsdEvhEPuO.7TuO5putQY7sexrEDqtDtLz0uAubEmR7hi', '0934456789', 'ADMIN', '2026-01-29 11:24:31.000000', 'ngeroday', b'0', 'LOCAL', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769660671/flower-shop/users/q5x4mwtawasvoo9pniyk.png'),
(21, 'thủ đức', '2026-01-14 14:56:13.000000', 'ngerng031@gmail.com', 'ngerr', b'1', '$2a$10$e8CQk2A.ZHRsXBnasYw3Eu0Zn.w48XCemqeciQPzRx0ygEffPe9rK', '0358892378', 'CUSTOMER', '2026-01-29 11:24:00.000000', 'ngerng', b'0', 'LOCAL', 'https://res.cloudinary.com/db1b15yn4/image/upload/v1769660641/flower-shop/users/fqmewgeyxbciz89ngy9o.webp'),
(22, NULL, '2026-01-24 09:43:41.000000', 'tuantainguyen13579@gmail.com', NULL, b'1', '$2a$10$jjYHymJx01d.NJv9TEWVk.hx3crM4Y1rEVLACtFnOI.HvPhveG2IW', '0356673489', 'CUSTOMER', '2026-01-24 09:43:41.000000', 'NgTuanTai', b'0', 'LOCAL', NULL),
(23, NULL, '2026-01-28 03:02:05.000000', 'vy2011@gmail.com', NULL, b'1', '$2a$10$NkU.feba24G/yiicmOfbmuitNqVYHmxkPLAWGLOhRW5EI/P9X0DoW', '0878765543', 'CUSTOMER', '2026-01-28 03:02:05.000000', 'Vyyyyy', b'0', 'LOCAL', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `vouchers`
--

CREATE TABLE `vouchers` (
  `id` bigint(20) NOT NULL,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_value` decimal(12,2) NOT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_percent` bit(1) NOT NULL,
  `max_discount` decimal(12,2) DEFAULT NULL,
  `min_order_value` decimal(12,2) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `usage_count` int(11) DEFAULT NULL,
  `usage_limit` int(11) DEFAULT NULL,
  `voucher_type` enum('ORDER','SHIPPING') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vouchers`
--

INSERT INTO `vouchers` (`id`, `code`, `created_at`, `description`, `discount_value`, `end_date`, `is_active`, `is_percent`, `max_discount`, `min_order_value`, `start_date`, `usage_count`, `usage_limit`, `voucher_type`) VALUES
(5, 'GIAM20', '2025-12-22 14:58:01.000000', '', 20.00, '2025-12-31 23:59:59.000000', b'1', b'1', 100000.00, 300000.00, '2025-12-22 00:00:00.000000', 2, 50, NULL),
(6, 'FLOWER20', '2026-01-02 13:47:00.000000', 'Giảm 20% cho đơn từ 200K', 20.00, '2026-01-31 23:59:59.000000', b'1', b'1', 100000.00, 200000.00, '2026-01-02 00:00:00.000000', 1, 20, NULL),
(7, 'WELCOME-C5B0', '2026-01-02 15:24:16.000000', 'Voucher chào mừng - Giảm 30% cho đơn hàng đầu tiên', 30.00, '2026-02-01 15:24:16.000000', b'0', b'1', 100000.00, 0.00, '2026-01-02 15:24:16.000000', 0, 1, NULL),
(8, 'WELCOME-93A3', '2026-01-02 15:25:32.000000', 'Voucher chào mừng - Giảm 30% cho đơn hàng đầu tiên', 30.00, '2026-02-01 15:25:32.000000', b'1', b'1', 100000.00, 0.00, '2026-01-02 15:25:32.000000', 1, 1, NULL),
(9, 'WELCOME-477F', '2026-01-02 15:30:11.000000', 'Voucher chào mừng - Giảm 30% cho đơn hàng đầu tiên', 30.00, '2026-02-01 15:30:11.000000', b'0', b'1', 100000.00, 0.00, '2026-01-02 15:30:11.000000', 0, 1, NULL),
(10, 'WELCOME-D4B1', '2026-01-02 15:36:11.000000', 'Voucher chào mừng - Giảm 30% cho đơn hàng đầu tiên', 30.00, '2026-02-01 15:36:11.000000', b'0', b'1', 100000.00, 0.00, '2026-01-02 15:36:11.000000', 0, 1, NULL),
(11, 'FREESHIP1', '2026-01-29 13:49:34.000000', '	\"Miễn phí vận chuyển\"', 100.00, '2026-02-07 23:59:59.000000', b'1', b'1', NULL, 200000.00, '2026-01-29 00:00:00.000000', 0, 20, 'ORDER'),
(12, 'FREESHIP50', '2026-01-29 13:51:19.000000', 'Giảm 50% đơn 200000', 50.00, '2026-02-07 23:59:59.000000', b'1', b'1', 30000.00, 200000.00, '2026-01-29 00:00:00.000000', 0, 20, 'ORDER'),
(13, 'FREESHIP20K', '2026-01-29 13:52:22.000000', 'Giảm cố định 20.000đ phí ship', 20000.00, '2026-02-07 23:59:59.000000', b'1', b'0', NULL, 200000.00, '2026-01-29 00:00:00.000000', 0, 30, 'ORDER'),
(14, 'FLOWER50', '2026-01-29 13:53:59.000000', 'giảm 50k đơn tối thiểu 300000', 50000.00, '2026-02-07 23:59:59.000000', b'1', b'0', NULL, 300000.00, '2026-01-29 00:00:00.000000', 0, 30, 'ORDER'),
(15, 'WELCOME30', '2026-01-29 13:54:22.000000', 'Voucher chào mừng thành viên mới - Giảm 30% cho đơn hàng đầu tiên', 30.00, '2027-01-29 13:54:22.000000', b'1', b'1', 100000.00, 0.00, '2026-01-29 13:54:22.000000', 0, NULL, 'ORDER');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `articles`
--
ALTER TABLE `articles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_articles_slug` (`slug`),
  ADD KEY `idx_articles_status_published` (`status`,`published_at`),
  ADD KEY `idx_articles_status_scheduled` (`status`,`scheduled_at`);

--
-- Indexes for table `banners`
--
ALTER TABLE `banners`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_active_sort` (`active`,`sort_order`),
  ADD KEY `idx_dates` (`start_date`,`end_date`);

--
-- Indexes for table `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK64t7ox312pqal3p7fg9o503c2` (`user_id`);

--
-- Indexes for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  ADD KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`),
  ADD KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`);

--
-- Indexes for table `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK3cpkdtwdxndrjhrx3gt9q5ux9` (`session_id`);

--
-- Indexes for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKiet8yvc26y0rlk9f2bt3xc1jr` (`staff_id`),
  ADD KEY `FK82ky97glaomlmhjqae1d0esmy` (`user_id`);

--
-- Indexes for table `contact_tickets`
--
ALTER TABLE `contact_tickets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_ticket_code` (`ticket_code`),
  ADD KEY `idx_ticket_status` (`status`),
  ADD KEY `idx_ticket_email` (`email`),
  ADD KEY `idx_ticket_user` (`user_id`),
  ADD KEY `FKp6gr7qst9252kqkcd3hcfq5s1` (`assigned_admin_id`);

--
-- Indexes for table `contact_ticket_messages`
--
ALTER TABLE `contact_ticket_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_ticket_message_ticket` (`ticket_id`);

--
-- Indexes for table `email_verification_tokens`
--
ALTER TABLE `email_verification_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_verification_token` (`token`),
  ADD UNIQUE KEY `UKs3mje1c85ftmp2uld6dt1bffs` (`user_id`),
  ADD KEY `idx_verification_user` (`user_id`);

--
-- Indexes for table `newsletter_subscribers`
--
ALTER TABLE `newsletter_subscribers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_newsletter_email` (`email`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_notification_recipient` (`recipient_id`,`recipient_role`),
  ADD KEY `idx_notification_unread` (`recipient_id`,`is_read`),
  ADD KEY `idx_notification_created` (`created_at`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_order_code` (`order_code`),
  ADD KEY `idx_order_user` (`user_id`),
  ADD KEY `idx_order_status` (`status`),
  ADD KEY `FKdimvsocblb17f45ikjr6xn1wj` (`voucher_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  ADD KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK71lqwbwtklmljk3qlsugr1mig` (`token`),
  ADD KEY `FKk3ndxg5xp6v7wd4gjyusp15gq` (`user_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKostq1ec3toafnjok09y9l7dox` (`slug`),
  ADD KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_review_product` (`product_id`),
  ADD KEY `idx_review_user` (`user_id`),
  ADD KEY `idx_review_order` (`order_id`),
  ADD KEY `idx_review_status` (`status`);

--
-- Indexes for table `saved_vouchers`
--
ALTER TABLE `saved_vouchers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKry3dcpcip16koacn7n6p0cnca` (`user_id`,`voucher_id`),
  ADD KEY `idx_saved_voucher_user` (`user_id`),
  ADD KEY `idx_saved_voucher_voucher` (`voucher_id`);

--
-- Indexes for table `shipping_district_rules`
--
ALTER TABLE `shipping_district_rules`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_district_delivery` (`city`,`district`,`delivery_type`),
  ADD KEY `idx_active_city` (`active`,`city`),
  ADD KEY `idx_zone` (`zone`);

--
-- Indexes for table `stock_history`
--
ALTER TABLE `stock_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKjssgif5kuhhjh6bwyxq5xdbsf` (`product_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`);

--
-- Indexes for table `vouchers`
--
ALTER TABLE `vouchers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_voucher_code` (`code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `articles`
--
ALTER TABLE `articles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `banners`
--
ALTER TABLE `banners`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `carts`
--
ALTER TABLE `carts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=182;

--
-- AUTO_INCREMENT for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `contact_tickets`
--
ALTER TABLE `contact_tickets`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `contact_ticket_messages`
--
ALTER TABLE `contact_ticket_messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `email_verification_tokens`
--
ALTER TABLE `email_verification_tokens`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `newsletter_subscribers`
--
ALTER TABLE `newsletter_subscribers`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=55;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=54;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `saved_vouchers`
--
ALTER TABLE `saved_vouchers`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `shipping_district_rules`
--
ALTER TABLE `shipping_district_rules`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `stock_history`
--
ALTER TABLE `stock_history`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `vouchers`
--
ALTER TABLE `vouchers`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`);

--
-- Constraints for table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`);

--
-- Constraints for table `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `FK3cpkdtwdxndrjhrx3gt9q5ux9` FOREIGN KEY (`session_id`) REFERENCES `chat_sessions` (`id`);

--
-- Constraints for table `chat_sessions`
--
ALTER TABLE `chat_sessions`
  ADD CONSTRAINT `FK82ky97glaomlmhjqae1d0esmy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKiet8yvc26y0rlk9f2bt3xc1jr` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `contact_tickets`
--
ALTER TABLE `contact_tickets`
  ADD CONSTRAINT `FKm37lkeixthw0rbc5m4uhvqukv` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKp6gr7qst9252kqkcd3hcfq5s1` FOREIGN KEY (`assigned_admin_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `contact_ticket_messages`
--
ALTER TABLE `contact_ticket_messages`
  ADD CONSTRAINT `FKm35qrxn1nje0srdc9j2w81iyg` FOREIGN KEY (`ticket_id`) REFERENCES `contact_tickets` (`id`);

--
-- Constraints for table `email_verification_tokens`
--
ALTER TABLE `email_verification_tokens`
  ADD CONSTRAINT `FKi1c4mmamlb8keqt74k4lrtwhc` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKdimvsocblb17f45ikjr6xn1wj` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD CONSTRAINT `FKk3ndxg5xp6v7wd4gjyusp15gq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FKqwgq1lxgahsxdspnwqfac6sv6` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

--
-- Constraints for table `saved_vouchers`
--
ALTER TABLE `saved_vouchers`
  ADD CONSTRAINT `FK7g6t2jibsv07b7j07qoyhc1cv` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKhgg3hndsk8niq6tio7elh0moa` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`);

--
-- Constraints for table `stock_history`
--
ALTER TABLE `stock_history`
  ADD CONSTRAINT `FKjssgif5kuhhjh6bwyxq5xdbsf` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
