package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.util.*

@RestController
@RequestMapping("/api")
class Rest(private val userRepository: UserRepository) {

    private val Logger = LoggerFactory.getLogger(Rest::class.java)

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any?>? {
        Logger.error(principal.toString())
        userRepository.save(User(principal.getAttribute("azp")!!,principal.getAttribute("name")!!,principal.getAttribute("email")!!))
        return Collections.singletonMap("name", principal.getAttribute("name"))
    }

    @GetMapping("/users")
    fun users(): MutableList<User> {
      return userRepository.findAll()
    }
}
