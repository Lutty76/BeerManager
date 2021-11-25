package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.FutRepository
import com.lutty.beer.beermanager.repository.UserRepository
import com.lutty.beer.beermanager.service.BeerService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping


@Controller
class Default(private val beerService: BeerService, private val userRepository: UserRepository, private val futRepository: FutRepository) {

    @GetMapping("/")
    fun home(
        @AuthenticationPrincipal principal: OAuth2User?,
        model: Model
    ): String {
        if (principal != null) {
            if (userRepository.findById(principal.getAttribute("email")!!).isEmpty)
                userRepository.save(
                    User(
                        principal.getAttribute("email")!!,
                        principal.getAttribute("name")!!

                    )
                )

            model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        }
        return "home"
    }

    @GetMapping("/greeting")
    fun greeting(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        return "greeting"
    }
    @GetMapping("/conso")
    fun conso(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        model.addAttribute("beers",beerService.getAllBeerForUser(userRepository.findById(principal.getAttribute("email")!!).get()))
        return "conso"
    }

    @GetMapping("/users")
    fun users(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String  {

        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        model.addAttribute("users",  userRepository.findAll())

        return "users"
    }


    @GetMapping("/futs")
    fun futs(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String  {
        model.addAttribute("fut", Fut())
        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        model.addAttribute("futs",  futRepository.findAll())
        return "futs"
    }

    @PostMapping("/futs")
    fun futsSubmit(
        @AuthenticationPrincipal principal: OAuth2User,
        @ModelAttribute fut: Fut,
        model: Model
    ): String  {
        futRepository.save(fut)
        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        model.addAttribute("futs",  futRepository.findAll())
        return "futs"
    }


    @GetMapping("/profil")
    fun profil(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findById(principal.getAttribute("email")!!).get())
        model.addAttribute("nbBeer",beerService.countBeerForUser(userRepository.findById(principal.getAttribute("email")!!).get()))
        return "profile"
    }
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

}
