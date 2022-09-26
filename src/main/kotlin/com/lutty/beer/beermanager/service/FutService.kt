package com.lutty.beer.beermanager.service

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.DateFut
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.BillRepository
import com.lutty.beer.beermanager.repository.DateFutRepository
import org.springframework.stereotype.Service

@Service
class FutService(private val dateFutRepository: DateFutRepository, private val billRepository: BillRepository) {

    fun getDatesFut(fut: Fut): List<DateFut> = dateFutRepository.findAllByFut(fut)
    fun findAllBilledFutForUser(user: User): List<Fut> = billRepository.findAllByUser(user).map { it.fut }
    fun getFutForBeer(beer: Beer): Fut? = dateFutRepository.findAllByEndGreaterThanEqualAndOpenLessThanEqual(beer.date, beer.date)?.fut
    fun isAlreadyPluggedFut(dateFut: DateFut): Boolean =
        !dateFutRepository.findAllByEndLessThanEqualAndEndGreaterThan(dateFut.end, dateFut.open).isNullOrEmpty() ||
            !dateFutRepository.findAllByOpenLessThanEqualAndOpenGreaterThanEqual(dateFut.end, dateFut.open).isNullOrEmpty()
}
