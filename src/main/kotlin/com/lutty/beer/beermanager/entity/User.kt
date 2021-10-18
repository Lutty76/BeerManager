package com.lutty.beer.beermanager.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
data class User(@Id val id: String = "", val name: String = "", val email: String = "")