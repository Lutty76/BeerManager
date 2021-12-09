package com.lutty.beer.beermanager.entity

import javax.persistence.*

@Entity
data class Bill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val billId: Long = 0,
    @ManyToOne @JoinColumn(name = "idUser") val user: User = User(),
    @OneToOne @JoinColumn(name = "idFut") val fut: Fut = Fut(),
    val paid: Boolean = false
)
