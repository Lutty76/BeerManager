package com.lutty.beer.beermanager.service

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.Bill
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.BillRepository
import com.lutty.beer.beermanager.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class BillService(private val billRepository: BillRepository, private val userRepository: UserRepository, private val beerService: BeerService, private val futService: FutService) {

    fun generateBill(fut: Fut): Boolean {
        val users = userRepository.findAll()
        users.forEach { user ->
            val beerByUserAndFut = beerService.getAllBeerForFutAndUser(fut, user)
            if (beerByUserAndFut != null && beerByUserAndFut.count() != 0) {
                billRepository.save(Bill(user = user, fut = fut))
            }
        }
        return true
    }
    fun paidBill(billId: Long): Boolean {
        val bill = billRepository.findOneByBillId(billId)
        billRepository.save(Bill(bill!!.billId, bill.user, bill.fut, true))
        return true
    }
    fun userPaidBills(userId: Long): Boolean {
        getAllUnpaidBillForUser(userRepository.getById(userId)).map { paidBill(it.billId) }
        return true
    }

    fun isBillForFut(fut: Fut): Boolean = !billRepository.findAllByFut(fut).isNullOrEmpty()
    fun isPaidFut(user: User, fut: Fut): Boolean = billRepository.findOneByUserAndFut(user, fut)?.paid ?: true
    fun getAllUnpaidBill(): List<Bill> = billRepository.findAllByPaid(false)
    fun getAllUnpaidBillForUser(user: User): List<Bill> = billRepository.findAllByUserAndPaid(user, false)
    fun getSumDuForUser(user: User) = billRepository.findAllByUserAndPaid(user, false).sumOf { getValueBill(it.billId).toDouble() }

    fun getValueBill(billId: Long): Float {
        val bill = billRepository.findOneByBillId(billId)
        return bill!!.fut.price / beerService.getAllBeerForFut(bill.fut)!!.map { it!!.size }.sum() * beerService.getAllBeerForFutAndUser(bill.fut, bill.user)!!.map { it!!.size }.sum()
    }

    fun isBillOnBeer(beer: Beer): Boolean {
        val fut = futService.getFutForBeer(beer)
        return if (fut == null)
            false
        else
            isBillForFut(fut)
    }
}
