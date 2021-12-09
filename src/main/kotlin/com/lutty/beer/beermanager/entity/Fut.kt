package com.lutty.beer.beermanager.entity

import javax.persistence.*

@Entity
data class Fut(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val futId: Long = 0,
    val name: String = "",
    val price: Float = 0.0f
)
