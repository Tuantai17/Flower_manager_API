package com.flower.manager.repository;

import com.flower.manager.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByActiveTrueOrderBySortOrderAsc();

    List<Banner> findAllByOrderBySortOrderAsc();
}
