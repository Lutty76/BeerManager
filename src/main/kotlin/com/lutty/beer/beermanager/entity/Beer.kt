package com.lutty.beer.beermanager.entity

import java.time.LocalDateTime

data class Beer(val size: Int = 1, val date: LocalDateTime = LocalDateTime.now(), val User: User)
