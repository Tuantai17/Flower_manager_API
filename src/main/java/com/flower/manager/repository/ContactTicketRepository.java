package com.flower.manager.repository;

import com.flower.manager.entity.ContactTicket;
import com.flower.manager.entity.User;
import com.flower.manager.enums.TicketCategory;
import com.flower.manager.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactTicketRepository extends JpaRepository<ContactTicket, Long> {

        /**
         * Tìm ticket theo mã
         */
        Optional<ContactTicket> findByTicketCode(String ticketCode);

        /**
         * Tìm tickets theo email
         */
        List<ContactTicket> findByEmailOrderByCreatedAtDesc(String email);

        /**
         * Tìm tickets theo user
         */
        List<ContactTicket> findByUserOrderByCreatedAtDesc(User user);

        /**
         * Tìm tickets theo email hoặc user
         */
        @Query("SELECT t FROM ContactTicket t WHERE t.email = :email OR (t.user IS NOT NULL AND t.user.id = :userId) ORDER BY t.createdAt DESC")
        List<ContactTicket> findByEmailOrUserId(@Param("email") String email, @Param("userId") Long userId);

        /**
         * Tìm tickets theo status
         */
        List<ContactTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status);

        /**
         * Tìm tickets theo danh sách status
         */
        List<ContactTicket> findByStatusInOrderByCreatedAtDesc(List<TicketStatus> statuses);

        /**
         * Đếm theo status
         */
        long countByStatus(TicketStatus status);

        /**
         * Admin: Tìm tất cả tickets với filter và phân trang
         */
        @Query("SELECT t FROM ContactTicket t WHERE " +
                        "(:status IS NULL OR t.status = :status) AND " +
                        "(:category IS NULL OR t.category = :category) AND " +
                        "(:search IS NULL OR t.ticketCode LIKE %:search% OR t.name LIKE %:search% OR t.email LIKE %:search% OR t.subject LIKE %:search%)")
        Page<ContactTicket> findAllWithFilters(
                        @Param("status") TicketStatus status,
                        @Param("category") TicketCategory category,
                        @Param("search") String search,
                        Pageable pageable);

        /**
         * Tìm tickets được giao cho admin
         */
        List<ContactTicket> findByAssignedAdminOrderByCreatedAtDesc(User admin);

        /**
         * Đếm tickets mới (chưa xử lý)
         */
        @Query("SELECT COUNT(t) FROM ContactTicket t WHERE t.status = 'OPEN'")
        long countOpenTickets();

        /**
         * Đếm tickets đang xử lý
         */
        @Query("SELECT COUNT(t) FROM ContactTicket t WHERE t.status = 'IN_PROGRESS'")
        long countInProgressTickets();
}
