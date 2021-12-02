package com.lutty.beer.beermanager.service

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.BeerRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BeerService(private val beerRepository: BeerRepository) {

    fun getAllBeerForUser(user: User): List<Beer?>? {
        return beerRepository.findByUser(user)
    }
    fun countBeerForUser(user: User): Long {
        return beerRepository.countByUser(user)
    }
    fun getAllBeerForUseFromDate(user: User, date: LocalDateTime): List<Beer?>? {
        return beerRepository.findByUserAndDate(user, date)
    }

    fun getAllBeerForFut(fut: Fut): List<Beer?>? {
        return beerRepository.findAllByDateLessThanEqualAndDateGreaterThanEqual(fut.end, fut.open)
    }
    fun getAllBeerForFutAndUser(fut: Fut, user: User): List<Beer?>? {
        return beerRepository.findAllByDateLessThanEqualAndDateGreaterThanEqualAndUser(fut.end, fut.open, user)
    }
}
