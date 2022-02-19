package com.lutty.beer.beermanager.repository

import com.lutty.beer.beermanager.entity.DateFut
import com.lutty.beer.beermanager.entity.Fut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DateFutRepository : JpaRepository<DateFut, Long> {
    fun findAllByFut(fut: Fut): List<DateFut>
    fun findAllByOpenLessThanEqualAndOpenGreaterThanEqual(end: LocalDateTime, start: LocalDateTime): List<DateFut>
    fun findAllByEndLessThanEqualAndEndGreaterThanEqual(end: LocalDateTime, start: LocalDateTime): List<DateFut>
    fun findAllByEndGreaterThanEqualAndOpenLessThanEqual(end: LocalDateTime, start: LocalDateTime): DateFut?
}
