package com.lutty.beer.beermanager.repository

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository :JpaRepository<User, Long>{
    fun findOneByEmail(email: String): User?
}