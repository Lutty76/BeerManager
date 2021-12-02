package com.lutty.beer.beermanager.entity

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Fut(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val futId: Long = 0,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") val open: LocalDateTime = LocalDateTime.now(),
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") val end: LocalDateTime = LocalDateTime.now(),
    val name: String = "",
    val price: Float = 0.0f
)
