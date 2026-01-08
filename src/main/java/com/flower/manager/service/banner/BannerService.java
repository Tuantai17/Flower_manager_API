package com.flower.manager.service.banner;

import com.flower.manager.dto.banner.BannerDTO;
import java.util.List;

public interface BannerService {
    List<BannerDTO> getAllBanners();

    List<BannerDTO> getActiveBanners();

    BannerDTO getBannerById(Long id);

    BannerDTO createBanner(BannerDTO bannerDTO);

    BannerDTO updateBanner(Long id, BannerDTO bannerDTO);

    void deleteBanner(Long id);

    BannerDTO toggleActive(Long id);

    void reorderBanners(java.util.Map<Long, Integer> orderMap);
}
