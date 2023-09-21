package com.prac.monolithic.awsmsamonolithicprac.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "cart_items")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val quantity: Int
<<<<<<< HEAD
) {

    @Transient
    var productName: String? = null
=======
){

    @Transient
    var productName: String? = null

>>>>>>> 55ac7c42e4322ca9b638cc839b028aa94553c409
}