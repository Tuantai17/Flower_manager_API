package com.flower.manager.service.cart;

import com.flower.manager.dto.cart.AddToCartRequest;
import com.flower.manager.dto.cart.CartDTO;
import com.flower.manager.dto.cart.CartItemDTO;
import com.flower.manager.dto.cart.UpdateCartItemRequest;
import com.flower.manager.entity.Cart;
import com.flower.manager.entity.CartItem;
import com.flower.manager.entity.Product;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.CartRepository;
import com.flower.manager.repository.ProductRepository;
import com.flower.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation của CartService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Lấy user hiện tại từ Security Context
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("Vui lòng đăng nhập để sử dụng giỏ hàng");
        }

        String identifier = authentication.getName();
        return userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    /**
     * Lấy hoặc tạo mới giỏ hàng cho user
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    /**
     * Chuyển đổi Cart entity sang CartDTO
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
                .map(this::convertToItemDTO)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Chuyển đổi CartItem entity sang CartItemDTO
     */
    private CartItemDTO convertToItemDTO(CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemDTO.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .productThumbnail(product.getThumbnail())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(subtotal)
                .build();
    }

    @Override
    public CartDTO getCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        log.info("Getting cart for user: {}", user.getUsername());
        return convertToDTO(cart);
    }

    @Override
    public CartDTO addItem(AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        log.info("Adding product {} to cart for user {}", product.getName(), user.getUsername());

        // ======== KIỂM TRA TỒN KHO ========
        // 1. Kiểm tra sản phẩm còn active không
        if (!product.isActive()) {
            throw new BusinessException("PRODUCT_UNAVAILABLE",
                    "Sản phẩm '" + product.getName() + "' hiện không còn bán");
        }

        // 2. Kiểm tra số lượng nhập vào phải > 0
        if (request.getQuantity() <= 0) {
            throw new BusinessException("INVALID_QUANTITY",
                    "Số lượng phải lớn hơn 0");
        }

        // 3. Kiểm tra còn hàng không
        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        if (currentStock <= 0) {
            throw new BusinessException("OUT_OF_STOCK",
                    "Sản phẩm '" + product.getName() + "' đã hết hàng");
        }

        // Tìm xem sản phẩm đã có trong giỏ chưa
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        // 4. Tính tổng số lượng sau khi thêm
        int existingQty = existingItem != null ? existingItem.getQuantity() : 0;
        int totalRequestedQty = existingQty + request.getQuantity();

        // 5. Kiểm tra không vượt quá tồn kho
        if (totalRequestedQty > currentStock) {
            throw new BusinessException("EXCEED_STOCK",
                    "Không thể thêm. Tồn kho còn " + currentStock +
                            ", trong giỏ đã có " + existingQty + " sản phẩm");
        }

        if (existingItem != null) {
            // Nếu đã có, cộng dồn số lượng
            existingItem.setQuantity(totalRequestedQty);
            log.info("Updated quantity for existing item. New quantity: {}", existingItem.getQuantity());
        } else {
            // Nếu chưa có, thêm mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getCurrentPrice())
                    .build();
            cart.getItems().add(newItem);
            log.info("Added new item to cart");
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Override
    public CartDTO updateItem(UpdateCartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        log.info("Updating quantity for product {} in cart for user {}", request.getProductId(), user.getUsername());

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ hàng"));

        // ======== KIỂM TRA TỒN KHO ========
        if (request.getQuantity() < 0) {
            throw new BusinessException("INVALID_QUANTITY", "Số lượng không được âm");
        }

        if (request.getQuantity() == 0) {
            // Nếu quantity = 0, xóa item
            cart.getItems().remove(item);
            log.info("Removed item from cart due to quantity = 0");
        } else {
            // Kiểm tra tồn kho
            Product product = item.getProduct();
            int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

            if (request.getQuantity() > currentStock) {
                throw new BusinessException("EXCEED_STOCK",
                        "Sản phẩm '" + product.getName() + "' chỉ còn " + currentStock + " trong kho");
            }

            item.setQuantity(request.getQuantity());
            log.info("Updated item quantity to: {}", request.getQuantity());
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Override
    public CartDTO removeItem(Long productId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        log.info("Removing product {} from cart for user {}", productId, user.getUsername());

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new ResourceNotFoundException("Sản phẩm không có trong giỏ hàng");
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Override
    public void clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        log.info("Clearing cart for user: {}", user.getUsername());

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
