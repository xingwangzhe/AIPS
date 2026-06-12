package com.needhelp.aips.controller;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.common.dto.PageResult;
import com.needhelp.aips.dto.order.*;
import com.needhelp.aips.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 订单接口。
 * POST /api/v1/cart/item
 * POST /api/v1/order
 * GET  /api/v1/order/list
 */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/cart/item")
    public ApiResponse<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request,
                                                    Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        CartItemResponse resp = orderService.addToCart(userId, request.getMedicineId(), request.getQuantity());
        return ApiResponse.success(resp);
    }

    @PostMapping("/order")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request,
                                                   Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        OrderResponse resp = orderService.createOrder(userId, request);
        return ApiResponse.success(resp);
    }

    @GetMapping("/order/list")
    public ApiResponse<PageResult<OrderResponse>> listOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        PageResult<OrderResponse> result = orderService.getOrderList(userId, status, page, pageSize);
        return ApiResponse.success(result);
    }
}
