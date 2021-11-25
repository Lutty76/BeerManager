package com.lutty.beer.beermanager.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(@Id val email: String = "",  val name: String = "", val admin: Boolean = false)