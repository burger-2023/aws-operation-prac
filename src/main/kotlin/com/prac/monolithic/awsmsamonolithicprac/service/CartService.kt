package com.prac.monolithic.awsmsamonolithicprac.service

import com.prac.monolithic.awsmsamonolithicprac.entity.CartItem
import com.prac.monolithic.awsmsamonolithicprac.repository.CartRepository
import com.prac.monolithic.awsmsamonolithicprac.repository.ProductRepository
import com.prac.monolithic.awsmsamonolithicprac.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addToCart(productId: Long, quantity: Int, userId: Long): CartItem {
        val product = productRepository.findById(productId).orElseThrow { Exception("Product not found") } // 제품 정보 조회
        val user = userRepository.findById(userId).orElseThrow { Exception("User not found") } // 유저 정보 조회

        val cartItem = CartItem(product = product, quantity = quantity, user = user)
        cartItem.productName = product.name

        return cartRepository.save(cartItem)
    }

    @Transactional(readOnly = true)
    fun findAllProducts(userId: Long): List<CartItem> {
        val cartItems = cartRepository.findByUserId(userId)
        cartItems.forEach {
            it.productName = it.product.name
        }
        return cartItems
    }
}
