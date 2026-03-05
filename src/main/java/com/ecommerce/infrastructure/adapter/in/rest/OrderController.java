package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.CreateOrderRequest;
import com.ecommerce.application.dto.OrderResponse;
import com.ecommerce.application.service.OrderService;
import com.ecommerce.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management. Status flow: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED. Orders can be cancelled at any point before delivery.")
public class OrderController {

    private final OrderService orderService;

    // ------------------------------------------------------------------ create

    @PostMapping
    @Operation(summary = "Create order from item list", description = "Creates an order directly from a list of products and quantities.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/from-cart")
    @Operation(summary = "Create order from cart", description = "Converts the current user's cart into an order. The cart is cleared on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created from cart"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Full shipping address", example = "123 Main St, Springfield, IL 62701")
            @RequestParam String shippingAddress,
            @Parameter(description = "Payment method", example = "CREDIT_CARD")
            @RequestParam String paymentMethod) {
        OrderResponse order = orderService.createOrderFromCart(principal.getId(), shippingAddress, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // ------------------------------------------------------------------ user reads

    @GetMapping
    @Operation(summary = "Get my orders", description = "Returns all orders for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.getId()));
    }

    @GetMapping("/paged")
    @Operation(summary = "Get my orders (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<OrderResponse>> getMyOrdersPaged(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.getId(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Order numbers have the format `ORD-<timestamp>`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @Parameter(description = "Order number, e.g. ORD-1710000000000") @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    // ------------------------------------------------------------------ admin

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all orders [ADMIN]", description = "Returns a paginated list of all orders across all users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of all orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm order [ADMIN]", description = "Moves the order from PENDING to CONFIRMED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order confirmed"),
            @ApiResponse(responseCode = "400", description = "Order cannot be confirmed in its current state"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> confirmOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PatchMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ship order [ADMIN]", description = "Moves the order from CONFIRMED/PROCESSING to SHIPPED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as shipped"),
            @ApiResponse(responseCode = "400", description = "Order cannot be shipped in its current state"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> shipOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.shipOrder(id));
    }

    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deliver order [ADMIN]", description = "Moves the order from SHIPPED to DELIVERED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered"),
            @ApiResponse(responseCode = "400", description = "Order cannot be delivered in its current state"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> deliverOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.deliverOrder(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order. Users can cancel their own orders; ADMIN can cancel any order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in its current state"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
