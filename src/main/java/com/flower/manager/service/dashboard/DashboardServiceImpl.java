package com.flower.manager.service.dashboard;

import com.flower.manager.dto.dashboard.*;
import com.flower.manager.entity.Order;
import com.flower.manager.entity.Product;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.repository.CategoryRepository;
import com.flower.manager.repository.OrderRepository;
import com.flower.manager.repository.ProductRepository;
import com.flower.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation của DashboardService
 * Xử lý logic thống kê cho Dashboard Admin
 * 
 * Log convention:
 * - [DASHBOARD:OVERVIEW] - Thống kê tổng quan
 * - [DASHBOARD:REVENUE] - Thống kê doanh thu
 * - [DASHBOARD:ORDER] - Thống kê đơn hàng
 * - [DASHBOARD:STOCK] - Thống kê tồn kho
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;
    private static final int RECENT_ORDERS_LIMIT = 10;
    private static final int TOP_PRODUCTS_LIMIT = 10;

    @Override
    public DashboardOverviewDTO getOverview() {
        log.info("[DASHBOARD:OVERVIEW] Generating dashboard overview");
        long startTime = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfYear = now.toLocalDate().withDayOfYear(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        // Revenue Stats
        BigDecimal todayRevenue = orderRepository.calculateRevenueInRange(startOfToday, endOfToday);
        BigDecimal monthRevenue = orderRepository.calculateRevenueInRange(startOfMonth, endOfToday);
        BigDecimal yearRevenue = orderRepository.calculateRevenueInRange(startOfYear, endOfToday);
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        BigDecimal lastMonthRevenue = orderRepository.calculateRevenueInRange(startOfLastMonth, endOfLastMonth);

        Double revenueGrowthPercent = calculateGrowthPercent(monthRevenue, lastMonthRevenue);

        // Order Stats
        long totalOrders = orderRepository.count();
        Long todayOrders = orderRepository.countOrdersInRange(startOfToday, endOfToday);
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long shippingOrders = orderRepository.countByStatus(OrderStatus.SHIPPING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED)
                + orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        // Product Stats
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActiveTrue();
        Long lowStockProducts = productRepository.countLowStock(DEFAULT_LOW_STOCK_THRESHOLD);
        Long outOfStockProducts = productRepository.countOutOfStock();

        // User Stats
        long totalUsers = userRepository.count();
        Long newUsersThisMonth = userRepository.countNewUsersInRange(startOfMonth, endOfToday);

        // Category Stats
        long totalCategories = categoryRepository.count();

        // Recent Orders
        List<RecentOrderDTO> recentOrders = getRecentOrderDTOs(RECENT_ORDERS_LIMIT);

        // Top Selling Products
        List<TopProductDTO> topSellingProducts = getTopSellingProductDTOs(TOP_PRODUCTS_LIMIT);

        // Low Stock Products
        List<LowStockProductDTO> lowStockProductList = getLowStockProductDTOs(DEFAULT_LOW_STOCK_THRESHOLD);

        long duration = System.currentTimeMillis() - startTime;
        log.info("[DASHBOARD:OVERVIEW] Generated in {}ms", duration);

        return DashboardOverviewDTO.builder()
                // Revenue
                .todayRevenue(todayRevenue)
                .monthRevenue(monthRevenue)
                .yearRevenue(yearRevenue)
                .totalRevenue(totalRevenue)
                .revenueGrowthPercent(revenueGrowthPercent)
                // Orders
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .shippingOrders(shippingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                // Products
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                // Users
                .totalUsers(totalUsers)
                .newUsersThisMonth(newUsersThisMonth)
                // Categories
                .totalCategories(totalCategories)
                // Lists
                .recentOrders(recentOrders)
                .topSellingProducts(topSellingProducts)
                .lowStockProductList(lowStockProductList)
                .build();
    }

    @Override
    public RevenueStatsDTO getRevenueStats(LocalDate fromDate, LocalDate toDate, String groupBy) {
        log.info("[DASHBOARD:REVENUE] Generating revenue stats: from={}, to={}, groupBy={}",
                fromDate, toDate, groupBy);

        if (fromDate == null) {
            fromDate = LocalDate.now().minusDays(30);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (groupBy == null || groupBy.isBlank()) {
            groupBy = "DAY";
        }

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.calculateRevenueInRange(fromDateTime, toDateTime);
        Long totalOrders = orderRepository.countOrdersInRange(fromDateTime, toDateTime);

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders != null && totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 0, RoundingMode.HALF_UP);
        }

        List<RevenueStatsDTO.RevenueDataPoint> dataPoints;

        switch (groupBy.toUpperCase()) {
            case "MONTH":
                dataPoints = getRevenueDataPointsByMonth(fromDateTime, toDateTime);
                break;
            case "YEAR":
                dataPoints = getRevenueDataPointsByYear(fromDateTime, toDateTime);
                break;
            case "DAY":
            default:
                dataPoints = getRevenueDataPointsByDay(fromDateTime, toDateTime);
                break;
        }

        // Calculate growth (compare with previous period)
        long periodDays = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        LocalDateTime prevFromDateTime = fromDateTime.minusDays(periodDays);
        LocalDateTime prevToDateTime = fromDateTime.minusSeconds(1);
        BigDecimal prevRevenue = orderRepository.calculateRevenueInRange(prevFromDateTime, prevToDateTime);
        Double growthPercent = calculateGrowthPercent(totalRevenue, prevRevenue);

        log.info("[DASHBOARD:REVENUE] Total revenue: {}, Orders: {}, Growth: {}%",
                totalRevenue, totalOrders, growthPercent);

        return RevenueStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .dataPoints(dataPoints)
                .growthPercent(growthPercent)
                .fromDate(fromDate)
                .toDate(toDate)
                .groupBy(groupBy.toUpperCase())
                .build();
    }

    @Override
    public OrderStatsDTO getOrderStats() {
        log.info("[DASHBOARD:ORDER] Generating order stats");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.toLocalDate().minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);

        long totalOrders = orderRepository.count();
        Long todayOrders = orderRepository.countOrdersInRange(startOfToday, endOfToday);
        Long weekOrders = orderRepository.countOrdersInRange(startOfWeek, endOfToday);
        Long monthOrders = orderRepository.countOrdersInRange(startOfMonth, endOfToday);

        // Count by status
        long pendingCount = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedCount = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long shippingCount = orderRepository.countByStatus(OrderStatus.SHIPPING);
        long deliveredCount = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long completedCount = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long cancelledCount = orderRepository.countByStatus(OrderStatus.CANCELLED);

        // Order status map
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        ordersByStatus.put(OrderStatus.PENDING.name(), pendingCount);
        ordersByStatus.put(OrderStatus.CONFIRMED.name(), confirmedCount);
        ordersByStatus.put(OrderStatus.SHIPPING.name(), shippingCount);
        ordersByStatus.put(OrderStatus.DELIVERED.name(), deliveredCount);
        ordersByStatus.put(OrderStatus.COMPLETED.name(), completedCount);
        ordersByStatus.put(OrderStatus.CANCELLED.name(), cancelledCount);

        // Rates
        double completionRate = totalOrders > 0 ? (double) (completedCount + deliveredCount) / totalOrders * 100 : 0;
        double cancellationRate = totalOrders > 0 ? (double) cancelledCount / totalOrders * 100 : 0;

        // Recent orders
        List<RecentOrderDTO> recentOrders = getRecentOrderDTOs(RECENT_ORDERS_LIMIT);

        // Daily order stats (7 days)
        List<OrderStatsDTO.DailyOrderCount> dailyOrders = getDailyOrderCounts(7);

        log.info("[DASHBOARD:ORDER] Total: {}, Pending: {}, Completed: {}",
                totalOrders, pendingCount, completedCount + deliveredCount);

        return OrderStatsDTO.builder()
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .weekOrders(weekOrders)
                .monthOrders(monthOrders)
                .ordersByStatus(ordersByStatus)
                .pendingCount(pendingCount)
                .confirmedCount(confirmedCount)
                .shippingCount(shippingCount)
                .deliveredCount(deliveredCount)
                .completedCount(completedCount)
                .cancelledCount(cancelledCount)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .recentOrders(recentOrders)
                .dailyOrders(dailyOrders)
                .build();
    }

    @Override
    public StockStatsDTO getStockStats(Integer lowStockThreshold) {
        log.info("[DASHBOARD:STOCK] Generating stock stats with threshold={}", lowStockThreshold);

        int threshold = lowStockThreshold != null ? lowStockThreshold : DEFAULT_LOW_STOCK_THRESHOLD;

        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActiveTrue();
        long inactiveProducts = productRepository.countByActiveFalse();
        Long lowStockProducts = productRepository.countLowStock(threshold);
        Long outOfStockProducts = productRepository.countOutOfStock();
        long inStockProducts = activeProducts - (lowStockProducts != null ? lowStockProducts : 0)
                - (outOfStockProducts != null ? outOfStockProducts : 0);

        BigDecimal totalStockValue = productRepository.calculateTotalStockValue();

        List<LowStockProductDTO> lowStockProductList = getLowStockProductDTOs(threshold);
        List<LowStockProductDTO> outOfStockProductList = getOutOfStockProductDTOs();

        log.info("[DASHBOARD:STOCK] Total: {}, LowStock: {}, OutOfStock: {}",
                totalProducts, lowStockProducts, outOfStockProducts);

        return StockStatsDTO.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .inactiveProducts(inactiveProducts)
                .inStockProducts(inStockProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalStockValue(totalStockValue)
                .lowStockThreshold(threshold)
                .lowStockProductList(lowStockProductList)
                .outOfStockProductList(outOfStockProductList)
                .build();
    }

    // =============== HELPER METHODS ===============

    private Double calculateGrowthPercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal growth = current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return growth.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private List<RecentOrderDTO> getRecentOrderDTOs(int limit) {
        List<Order> recentOrders = orderRepository.findRecentOrders(limit);
        return recentOrders.stream()
                .map(this::mapToRecentOrderDTO)
                .collect(Collectors.toList());
    }

    private RecentOrderDTO mapToRecentOrderDTO(Order order) {
        return RecentOrderDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .finalPrice(order.getFinalPrice())
                .status(order.getStatus().name())
                .statusDisplayName(order.getStatus().getDisplayName())
                .isPaid(order.getIsPaid())
                .paymentMethod(order.getPaymentMethod().name())
                .createdAt(order.getCreatedAt())
                .totalItems(order.getTotalQuantity())
                .build();
    }

    private List<TopProductDTO> getTopSellingProductDTOs(int limit) {
        List<Object[]> stats = productRepository.findTopSellingProductStats(limit);
        if (stats.isEmpty()) {
            return Collections.emptyList();
        }

        // Get product IDs
        List<Long> productIds = stats.stream()
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());

        // Fetch products
        Map<Long, Product> productMap = productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return stats.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    Product product = productMap.get(productId);
                    if (product == null)
                        return null;

                    return TopProductDTO.builder()
                            .id(productId)
                            .name(product.getName())
                            .slug(product.getSlug())
                            .thumbnail(product.getThumbnail())
                            .price(product.getPrice())
                            .salePrice(product.getSalePrice())
                            .totalSold(((Number) row[1]).longValue())
                            .totalRevenue((BigDecimal) row[2])
                            .orderCount(((Number) row[3]).longValue())
                            .stockQuantity(product.getStockQuantity())
                            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<LowStockProductDTO> getLowStockProductDTOs(int threshold) {
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return products.stream()
                .map(p -> mapToLowStockProductDTO(p, "WARNING"))
                .collect(Collectors.toList());
    }

    private List<LowStockProductDTO> getOutOfStockProductDTOs() {
        List<Product> products = productRepository.findOutOfStockProducts();
        return products.stream()
                .map(p -> mapToLowStockProductDTO(p, "CRITICAL"))
                .collect(Collectors.toList());
    }

    private LowStockProductDTO mapToLowStockProductDTO(Product product, String alertLevel) {
        return LowStockProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .thumbnail(product.getThumbnail())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.getActive())
                .alertLevel(alertLevel)
                .estimatedDaysLeft(null) // Could calculate based on sales velocity
                .build();
    }

    private List<RevenueStatsDTO.RevenueDataPoint> getRevenueDataPointsByDay(
            LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Object[]> rawData = orderRepository.getRevenueByDay(fromDateTime, toDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return rawData.stream()
                .map(row -> RevenueStatsDTO.RevenueDataPoint.builder()
                        .date((LocalDate) row[0])
                        .label(((LocalDate) row[0]).format(formatter))
                        .revenue((BigDecimal) row[1])
                        .orders(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<RevenueStatsDTO.RevenueDataPoint> getRevenueDataPointsByMonth(
            LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Object[]> rawData = orderRepository.getRevenueByMonth(fromDateTime, toDateTime);

        return rawData.stream()
                .map(row -> {
                    int year = ((Number) row[0]).intValue();
                    int month = ((Number) row[1]).intValue();
                    String monthStr = String.format("%d-%02d", year, month);

                    return RevenueStatsDTO.RevenueDataPoint.builder()
                            .month(monthStr)
                            .year(year)
                            .label("Tháng " + month)
                            .revenue((BigDecimal) row[2])
                            .orders(((Number) row[3]).longValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<RevenueStatsDTO.RevenueDataPoint> getRevenueDataPointsByYear(
            LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        // For yearly stats, we group by year using the monthly query and then aggregate
        List<Object[]> rawData = orderRepository.getRevenueByMonth(fromDateTime, toDateTime);

        Map<Integer, RevenueStatsDTO.RevenueDataPoint> yearMap = new LinkedHashMap<>();

        for (Object[] row : rawData) {
            int year = ((Number) row[0]).intValue();
            BigDecimal revenue = (BigDecimal) row[2];
            long orders = ((Number) row[3]).longValue();

            yearMap.compute(year, (k, v) -> {
                if (v == null) {
                    return RevenueStatsDTO.RevenueDataPoint.builder()
                            .year(year)
                            .label(String.valueOf(year))
                            .revenue(revenue)
                            .orders(orders)
                            .build();
                } else {
                    return RevenueStatsDTO.RevenueDataPoint.builder()
                            .year(year)
                            .label(String.valueOf(year))
                            .revenue(v.getRevenue().add(revenue))
                            .orders(v.getOrders() + orders)
                            .build();
                }
            });
        }

        return new ArrayList<>(yearMap.values());
    }

    private List<OrderStatsDTO.DailyOrderCount> getDailyOrderCounts(int days) {
        LocalDateTime fromDate = LocalDate.now().minusDays(days - 1).atStartOfDay();
        List<Object[]> rawData = orderRepository.getDailyOrderStats(fromDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return rawData.stream()
                .map(row -> OrderStatsDTO.DailyOrderCount.builder()
                        .date(((LocalDate) row[0]).format(formatter))
                        .count(((Number) row[1]).longValue())
                        .completed(((Number) row[2]).longValue())
                        .cancelled(((Number) row[3]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // =============== CHART APIs IMPLEMENTATION ===============

    @Override
    public RevenueChartDataDTO getRevenueChartData(String period) {
        log.info("[DASHBOARD:CHART] Generating revenue chart data for period={}", period);

        // Xác định khoảng thời gian dựa trên period
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        String periodLabel;

        switch (period != null ? period.toUpperCase() : "7_DAYS") {
            case "30_DAYS":
                startDate = endDate.minusDays(29);
                periodLabel = "30_DAYS";
                break;
            case "3_MONTHS":
                startDate = endDate.minusMonths(3);
                periodLabel = "3_MONTHS";
                break;
            case "12_MONTHS":
                startDate = endDate.minusMonths(12);
                periodLabel = "12_MONTHS";
                break;
            case "7_DAYS":
            default:
                startDate = endDate.minusDays(6);
                periodLabel = "7_DAYS";
                break;
        }

        LocalDateTime fromDateTime = startDate.atStartOfDay();
        LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

        // Lấy dữ liệu từ DB
        List<Object[]> rawData = orderRepository.getRevenueByDay(fromDateTime, toDateTime);

        // Chuyển thành Map để lookup nhanh
        Map<LocalDate, Object[]> dataMap = new HashMap<>();
        for (Object[] row : rawData) {
            dataMap.put((LocalDate) row[0], row);
        }

        // Tạo danh sách đầy đủ các ngày (fill missing days với value 0)
        List<RevenueChartDataDTO.DataPoint> dataPoints = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
        LocalDate current = startDate;

        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalOrders = 0;

        while (!current.isAfter(endDate)) {
            Object[] row = dataMap.get(current);
            BigDecimal revenue = BigDecimal.ZERO;
            long orders = 0;

            if (row != null) {
                revenue = (BigDecimal) row[1];
                orders = ((Number) row[2]).longValue();
            }

            totalRevenue = totalRevenue.add(revenue);
            totalOrders += orders;

            dataPoints.add(RevenueChartDataDTO.DataPoint.builder()
                    .label(current.format(formatter))
                    .date(current)
                    .revenue(revenue)
                    .orders(orders)
                    .build());

            current = current.plusDays(1);
        }

        // Tính growth so với kỳ trước
        long periodDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDateTime prevFromDateTime = fromDateTime.minusDays(periodDays);
        LocalDateTime prevToDateTime = fromDateTime.minusSeconds(1);
        BigDecimal prevRevenue = orderRepository.calculateRevenueInRange(prevFromDateTime, prevToDateTime);
        Double growthPercent = calculateGrowthPercent(totalRevenue, prevRevenue);

        log.info("[DASHBOARD:CHART] Revenue chart: {} points, total={}, growth={}%",
                dataPoints.size(), totalRevenue, growthPercent);

        return RevenueChartDataDTO.builder()
                .period(periodLabel)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .growthPercent(growthPercent)
                .dataPoints(dataPoints)
                .build();
    }

    @Override
    public OrderDistributionDTO getOrderDistribution() {
        log.info("[DASHBOARD:CHART] Generating order distribution");

        long totalOrders = orderRepository.count();

        // Định nghĩa màu sắc cho từng trạng thái (phù hợp với UI)
        Map<OrderStatus, String> statusColors = Map.of(
                OrderStatus.PENDING, "#F59E0B", // Vàng cam
                OrderStatus.CONFIRMED, "#3B82F6", // Xanh dương
                OrderStatus.SHIPPING, "#8B5CF6", // Tím
                OrderStatus.DELIVERED, "#10B981", // Xanh lá
                OrderStatus.COMPLETED, "#059669", // Xanh lá đậm
                OrderStatus.CANCELLED, "#EF4444" // Đỏ
        );

        // Định nghĩa tên hiển thị tiếng Việt
        Map<OrderStatus, String> statusNames = Map.of(
                OrderStatus.PENDING, "Đang chờ",
                OrderStatus.CONFIRMED, "Đã xác nhận",
                OrderStatus.SHIPPING, "Đang giao",
                OrderStatus.DELIVERED, "Đã giao",
                OrderStatus.COMPLETED, "Hoàn thành",
                OrderStatus.CANCELLED, "Đã hủy");

        List<OrderDistributionDTO.Segment> segments = new ArrayList<>();

        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            if (count > 0) {
                double percentage = totalOrders > 0 ? (double) count / totalOrders * 100 : 0;

                segments.add(OrderDistributionDTO.Segment.builder()
                        .name(statusNames.getOrDefault(status, status.getDisplayName()))
                        .status(status.name())
                        .value(count)
                        .percentage(Math.round(percentage * 10.0) / 10.0)
                        .color(statusColors.getOrDefault(status, "#6B7280"))
                        .build());
            }
        }

        log.info("[DASHBOARD:CHART] Order distribution: {} segments, total={}", segments.size(), totalOrders);

        return OrderDistributionDTO.builder()
                .totalOrders(totalOrders)
                .segments(segments)
                .build();
    }

    @Override
    public DashboardStatsDTO getQuickStats() {
        log.info("[DASHBOARD:STATS] Generating quick stats");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        // Revenue
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        BigDecimal todayRevenue = orderRepository.calculateRevenueInRange(startOfToday, endOfToday);
        BigDecimal monthRevenue = orderRepository.calculateRevenueInRange(startOfMonth, endOfToday);
        BigDecimal lastMonthRevenue = orderRepository.calculateRevenueInRange(startOfLastMonth, endOfLastMonth);
        Double revenueGrowth = calculateGrowthPercent(monthRevenue, lastMonthRevenue);

        // Orders
        long totalOrders = orderRepository.count();
        Long todayOrders = orderRepository.countOrdersInRange(startOfToday, endOfToday);
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        Long lastMonthOrders = orderRepository.countOrdersInRange(startOfLastMonth, endOfLastMonth);
        Long thisMonthOrders = orderRepository.countOrdersInRange(startOfMonth, endOfToday);
        Double ordersGrowth = lastMonthOrders != null && lastMonthOrders > 0
                ? ((double) (thisMonthOrders - lastMonthOrders) / lastMonthOrders * 100)
                : 0.0;

        // Products
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActiveTrue();
        Long lowStockCount = productRepository.countLowStock(DEFAULT_LOW_STOCK_THRESHOLD);

        // Categories
        long totalCategories = categoryRepository.count();

        // Users
        long totalUsers = userRepository.count();
        Long newUsersThisMonth = userRepository.countNewUsersInRange(startOfMonth, endOfToday);

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .monthRevenue(monthRevenue)
                .revenueGrowth(revenueGrowth)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .pendingOrders(pendingOrders)
                .ordersGrowth(Math.round(ordersGrowth * 100.0) / 100.0)
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .lowStockCount(lowStockCount)
                .productsGrowth(0.0) // Would need historical data
                .totalCategories(totalCategories)
                .categoriesGrowth(0.0)
                .totalUsers(totalUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .build();
    }
}
