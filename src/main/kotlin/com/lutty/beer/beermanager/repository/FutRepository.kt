package com.lutty.beer.beermanager.repository

import com.lutty.beer.beermanager.entity.Fut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FutRepository :JpaRepository<Fut, Long>{

}