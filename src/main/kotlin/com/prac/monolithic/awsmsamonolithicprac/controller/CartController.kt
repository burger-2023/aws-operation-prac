package com.prac.monolithic.awsmsamonolithicprac.controller

import com.prac.monolithic.awsmsamonolithicprac.entity.CartItem
import com.prac.monolithic.awsmsamonolithicprac.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/carts")
class CartController(
    private val cartService: CartService
) {

    /**
     * POST - 제품을 장바구니에 추가합니다.
     * */
    @PostMapping("/register")
    fun addToCart(
        @RequestParam productId: Long,
        @RequestParam quantity: Int,
        @RequestParam userId: Long
    ): ResponseEntity<CartItem> {
        val updatedCart = cartService.addToCart(productId, quantity, userId)

        return ResponseEntity.ok(updatedCart)
    }

    /**
     * GET - 모든 장바구니 항목을 검색합니다.
     * */
    @GetMapping
    fun getAllCarts(
        @RequestParam userId: Long
    ): ResponseEntity<List<CartItem>> {
        val cartItems = cartService.findAllProducts(userId)

        return ResponseEntity.ok(cartItems)
    }
}
