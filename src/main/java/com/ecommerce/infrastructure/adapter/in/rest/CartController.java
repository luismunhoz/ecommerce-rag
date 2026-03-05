package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.CartItemRequest;
import com.ecommerce.application.dto.CartResponse;
import com.ecommerce.application.dto.MessageResponse;
import com.ecommerce.application.service.CartService;
import com.ecommerce.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management — all operations require authentication")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getId()));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the cart. If the product is already in the cart, increases its quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added, cart returned"),
            @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(principal.getId(), request));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Update item quantity", description = "Sets the quantity of a specific product in the cart. Use quantity=0 to remove the item.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated, cart returned"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not in cart")
    })
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Parameter(description = "New quantity") @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(principal.getId(), productId, quantity));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed, cart returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not in cart")
    })
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(principal.getId(), productId));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart cleared"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MessageResponse> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok(MessageResponse.of("Cart cleared successfully"));
    }
}
