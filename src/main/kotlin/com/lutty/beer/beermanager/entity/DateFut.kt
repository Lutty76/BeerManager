package com.lutty.beer.beermanager.entity

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class DateFut(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val dateId: Long = 0,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") val open: LocalDateTime = LocalDateTime.now(),
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") val end: LocalDateTime = LocalDateTime.now(),
    @ManyToOne @JoinColumn(name = "futId") val fut: Fut = Fut()
)
