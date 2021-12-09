package com.lutty.beer.beermanager.service

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.DateFut
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.BeerRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BeerService(private val beerRepository: BeerRepository, private val futService: FutService) {

    fun getAllBeerForUser(user: User): List<Beer?>? {
        return beerRepository.findByUser(user)
    }
    fun countBeerForUser(user: User): Long {
        return beerRepository.countByUser(user)
    }
    fun getAllBeerForUserFromDate(user: User, date: LocalDateTime): List<Beer?>? {
        return beerRepository.findAllByUserAndDateGreaterThanEqual(user, date)
    }

    fun getAllBeerForFut(fut: Fut): List<Beer?>? {

        return getAllBeerForFutAndDate(futService.getDatesFut(fut))
    }
    fun getAllBeerForFutAndUser(fut: Fut, user: User): List<Beer?>? {
        return getAllBeerForFutAndDateAndUser(futService.getDatesFut(fut), user)
    }

    fun getAllBeerForFutAndDate(dates: List<DateFut>): List<Beer?> {
        val listBeer = dates.flatMap { date -> beerRepository.findAllByDateLessThanEqualAndDateGreaterThanEqual(date.end, date.open)!! }
        return listBeer
    }

    fun getAllBeerForFutAndDateAndUser(dates: List<DateFut>, user: User): List<Beer?>? {
        val listBeer = dates.flatMap { date -> beerRepository.findAllByDateLessThanEqualAndDateGreaterThanEqualAndUser(date.end, date.open, user)!! }
        return listBeer
    }
}
