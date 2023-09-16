package com.prac.monolithic.awsmsamonolithicprac.controller

import com.prac.monolithic.awsmsamonolithicprac.entity.Product
import com.prac.monolithic.awsmsamonolithicprac.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    /**
     * POST - 새 제품을 추가합니다. Request body 에는 name, description, price 가 포함되어야 합니다.
     * */
    @PostMapping("/register")
    fun addProduct(
        @RequestBody product: Product
    ): ResponseEntity<Product> {
        val createdProduct = productService.addProduct(product)

        return ResponseEntity.ok(createdProduct)
    }

    /**
     * GET - 현재 사용자의 장바구니 내용을 검색합니다.
     * */
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        val products = productService.findAllProducts()

        return ResponseEntity.ok(products)
    }
}
