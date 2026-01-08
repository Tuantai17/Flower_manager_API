package com.flower.manager.service.banner;

import com.flower.manager.dto.banner.BannerDTO;
import com.flower.manager.entity.Banner;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Override
    public List<BannerDTO> getAllBanners() {
        return bannerRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BannerDTO> getActiveBanners() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return bannerRepository.findAllByActiveTrueOrderBySortOrderAsc().stream()
                .filter(banner -> {
                    if (banner.getStartDate() != null && now.isBefore(banner.getStartDate())) {
                        return false;
                    }
                    if (banner.getEndDate() != null && now.isAfter(banner.getEndDate())) {
                        return false;
                    }
                    return true;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDTO getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        return convertToDTO(banner);
    }

    @Override
    @Transactional
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = Banner.builder()
                .title(bannerDTO.getTitle())
                .subtitle(bannerDTO.getSubtitle())
                .imageUrl(bannerDTO.getImageUrl())
                .linkUrl(bannerDTO.getLinkUrl())
                .buttonText(bannerDTO.getButtonText() != null ? bannerDTO.getButtonText() : "Xem Ngay")
                .sortOrder(bannerDTO.getSortOrder() != null ? bannerDTO.getSortOrder() : 0)
                .active(bannerDTO.getActive() != null ? bannerDTO.getActive() : true)
                .startDate(bannerDTO.getStartDate())
                .endDate(bannerDTO.getEndDate())
                .backgroundColor(bannerDTO.getBackgroundColor())
                .textColor(bannerDTO.getTextColor())
                .description(bannerDTO.getDescription())
                .build();

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Override
    @Transactional
    public BannerDTO updateBanner(Long id, BannerDTO bannerDTO) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        banner.setTitle(bannerDTO.getTitle());
        banner.setSubtitle(bannerDTO.getSubtitle());
        banner.setImageUrl(bannerDTO.getImageUrl());
        banner.setLinkUrl(bannerDTO.getLinkUrl());
        banner.setButtonText(bannerDTO.getButtonText());
        if (bannerDTO.getSortOrder() != null) {
            banner.setSortOrder(bannerDTO.getSortOrder());
        }
        if (bannerDTO.getActive() != null) {
            banner.setActive(bannerDTO.getActive());
        }
        banner.setStartDate(bannerDTO.getStartDate());
        banner.setEndDate(bannerDTO.getEndDate());
        banner.setBackgroundColor(bannerDTO.getBackgroundColor());
        banner.setTextColor(bannerDTO.getTextColor());
        banner.setDescription(bannerDTO.getDescription());

        Banner updatedBanner = bannerRepository.save(banner);
        return convertToDTO(updatedBanner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banner", "id", id);
        }
        bannerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BannerDTO toggleActive(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        banner.setActive(!banner.getActive());
        Banner updatedBanner = bannerRepository.save(banner);
        return convertToDTO(updatedBanner);
    }

    private BannerDTO convertToDTO(Banner banner) {
        return BannerDTO.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .subtitle(banner.getSubtitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .buttonText(banner.getButtonText())
                .sortOrder(banner.getSortOrder())
                .active(banner.getActive())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .backgroundColor(banner.getBackgroundColor())
                .textColor(banner.getTextColor())
                .description(banner.getDescription())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void reorderBanners(java.util.Map<Long, Integer> orderMap) {
        orderMap.forEach((id, sortOrder) -> {
            Banner banner = bannerRepository.findById(id).orElse(null);
            if (banner != null) {
                banner.setSortOrder(sortOrder);
                bannerRepository.save(banner);
            }
        });
    }
}
