package com.lutty.beer.beermanager.repository

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BeerRepository : JpaRepository<Beer, Long> {

    fun findByUser(user: User): List<Beer?>?
    fun findAllByUserAndDateGreaterThanEqual(user: User, date: LocalDateTime): List<Beer?>?
    fun findAllByDateLessThanEqualAndDateGreaterThanEqual(end: LocalDateTime, start: LocalDateTime): List<Beer?>?
    fun findAllByDateLessThanEqualAndDateGreaterThanEqualAndUser(end: LocalDateTime, start: LocalDateTime, user: User): List<Beer?>?
    fun findAllByDateGreaterThanEqualAndUser(start: LocalDateTime, user: User): List<Beer?>?

    fun countByUser(user: User): Long
}
