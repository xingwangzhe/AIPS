package com.needhelp.aips.service.order;

import com.needhelp.aips.common.dto.PageResult;
import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.order.*;
import com.needhelp.aips.entity.*;
import com.needhelp.aips.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单服务。
 * 处理购物车管理、订单创建、订单查询。
 */
@Service
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    public OrderService(CartItemRepository cartItemRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MedicineRepository medicineRepository,
                        UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    /**
     * 加入购物车。
     */
    public CartItemResponse addToCart(Long userId, Long medicineId, Integer quantity) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new BusinessException(404, "药品不存在"));

        if (medicine.getStatus() != 1) {
            throw new BusinessException(400, "药品已下架");
        }
        if (medicine.getStock() < quantity) {
            throw new BusinessException(400, "库存不足");
        }

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndMedicineId(userId, medicineId);
        CartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem();
            item.setUser(new User()); // 简化：设置 userId 通过懒加载
            item.setMedicine(medicine);
        }
        // 使用原始 SQL 更新方式更简洁
        // 实际上这里需要通过 repository 操作...
        // 简化：直接用 JPA save
        if (!existing.isPresent()) {
            User userRef = new User();
            userRef.setId(userId);
            item.setUser(userRef);
            item.setMedicine(medicine);
            item.setQuantity(quantity);
            item.setIsSelected(1);
        }
        cartItemRepository.save(item);

        CartItemResponse resp = new CartItemResponse();
        resp.setCartItemId(item.getId());
        resp.setMedicineName(medicine.getName());
        resp.setQuantity(existing.isPresent() ? existing.get().getQuantity() : quantity);
        resp.setSubtotal(medicine.getPrice().multiply(BigDecimal.valueOf(resp.getQuantity())));
        resp.setCartTotalCount(cartItemRepository.countByUserId(userId));
        return resp;
    }

    /**
     * 创建订单。
     */
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        // 获取选中的购物车项
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndIsSelected(userId, 1);
        if (cartItems.isEmpty()) {
            cartItems = cartItemRepository.findByUserIdOrderByCreateTimeDesc(userId);
            // 从请求中筛选
            cartItems = cartItems.stream()
                    .filter(ci -> request.getCartItemIds().contains(ci.getId()))
                    .collect(Collectors.toList());
        }

        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "没有选中的商品");
        }

        // 计算金额
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            subtotal = subtotal.add(ci.getMedicine().getPrice()
                    .multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        BigDecimal deliveryFee = BigDecimal.valueOf(request.getDeliveryType() == 1 ? 8.0 : 15.0);
        BigDecimal total = subtotal.add(deliveryFee);

        // 创建订单
        Order order = new Order();
        order.setOrderNo("OPS" + System.currentTimeMillis());
        User userRef = new User();
        userRef.setId(userId);
        order.setUser(userRef);
        order.setAddressId(request.getAddressId());
        order.setDeliveryType(request.getDeliveryType());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setSubtotalAmount(subtotal);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(total);
        order.setStatus(10);
        order.setIsPrivacyPack(request.getIsPrivacyPack() != null ? request.getIsPrivacyPack() : 0);
        order.setRemark(request.getRemark());
        order = orderRepository.save(order);

        // 创建订单明细（药品快照）
        for (CartItem ci : cartItems) {
            Medicine med = ci.getMedicine();
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setMedicineId(med.getId());
            oi.setMedicineName(med.getName());
            oi.setSpecification(med.getSpecification());
            oi.setUnitPrice(med.getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setSubtotal(med.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            orderItemRepository.save(oi);

            // 扣减库存
            med.setStock(med.getStock() - ci.getQuantity());
            medicineRepository.save(med);
        }

        // 清空已下单的购物车项
        List<Long> idsToDelete = cartItems.stream().map(CartItem::getId).collect(Collectors.toList());
        cartItemRepository.deleteAllById(idsToDelete);

        // 组装响应
        OrderResponse resp = new OrderResponse();
        resp.setOrderId(order.getId());
        resp.setOrderNo(order.getOrderNo());
        resp.setTotalAmount(order.getTotalAmount());
        resp.setStatus(order.getStatus());
        resp.setStatusText("待付款");
        resp.setPaymentExpireTime(LocalDateTime.now().plusMinutes(30)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        List<OrderResponse.OrderItemInfo> items = cartItems.stream().map(ci -> {
            OrderResponse.OrderItemInfo info = new OrderResponse.OrderItemInfo();
            info.setMedicineName(ci.getMedicine().getName());
            info.setQuantity(ci.getQuantity());
            return info;
        }).collect(Collectors.toList());
        resp.setItems(items);

        return resp;
    }

    /**
     * 查询订单列表。
     */
    public PageResult<OrderResponse> getOrderList(Long userId, Integer status, int page, int pageSize) {
        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findByUserIdAndStatusOrderByCreateTimeDesc(
                    userId, status, PageRequest.of(page - 1, pageSize));
        } else {
            orders = orderRepository.findByUserIdOrderByCreateTimeDesc(
                    userId, PageRequest.of(page - 1, pageSize));
        }

        List<OrderResponse> list = orders.getContent().stream().map(order -> {
            OrderResponse resp = new OrderResponse();
            resp.setOrderId(order.getId());
            resp.setOrderNo(order.getOrderNo());
            resp.setTotalAmount(order.getTotalAmount());
            resp.setStatus(order.getStatus());
            resp.setStatusText(mapOrderStatus(order.getStatus()));

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            List<OrderResponse.OrderItemInfo> itemList = items.stream().map(i -> {
                OrderResponse.OrderItemInfo info = new OrderResponse.OrderItemInfo();
                info.setMedicineName(i.getMedicineName());
                info.setQuantity(i.getQuantity());
                return info;
            }).collect(Collectors.toList());
            resp.setItems(itemList);

            return resp;
        }).collect(Collectors.toList());

        return new PageResult<>(list, orders.getTotalElements(), page, pageSize);
    }

    private String mapOrderStatus(Integer status) {
        return switch (status) {
            case 10 -> "待付款";
            case 20 -> "待发货";
            case 30 -> "配送中";
            case 40 -> "已完成";
            case 50 -> "已取消";
            default -> "未知";
        };
    }
}
