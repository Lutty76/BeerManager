package com.lutty.beer.beermanager.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Beer( @Id
                 @GeneratedValue(strategy = GenerationType.IDENTITY)
                 val beerId : Long = 0 , val date: LocalDateTime = LocalDateTime.now(), @ManyToOne @JoinColumn(name="id") val user: User = User(), val size: Int = 25)