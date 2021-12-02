package com.lutty.beer.beermanager.repository

import com.lutty.beer.beermanager.entity.Bill
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BillRepository : JpaRepository<Bill, Long> {

    fun findOneByBillId(billId: Long): Bill?
    fun findByUser(user: User): List<Bill?>
    fun findOneByUserAndFut(user: User, fut: Fut): Bill?
    fun findAllByPaid(paid: Boolean): List<Bill>
}
