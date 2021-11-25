package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.Beer
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.BeerRepository
import com.lutty.beer.beermanager.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

import java.util.*

@RestController
@RequestMapping("/api")
class Rest(private val userRepository: UserRepository, private val beerRepository: BeerRepository) {

    private val Logger = LoggerFactory.getLogger(Rest::class.java)

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any?>? {
        Logger.error(principal.toString())
        return Collections.singletonMap("name", principal.getAttribute("name"))
    }

    @GetMapping("/users")
    fun users(): MutableList<User> {
      return userRepository.findAll()
    }

    @GetMapping("/beer/{size}")
    fun addBeer(@AuthenticationPrincipal principal: OAuth2User, @PathVariable size: Int): String {
        beerRepository.save(Beer(size= size,user= userRepository.findById(principal.getAttribute("email")!!).get() ))
        return "{\"status\":\"OK\"}"
    }
    @DeleteMapping("/beer/{date}")
    fun removeBeer(@AuthenticationPrincipal principal: OAuth2User, @PathVariable date: Long): String {
        beerRepository.deleteById(date)
        return "{\"status\":\"OK\"}"
    }
}
