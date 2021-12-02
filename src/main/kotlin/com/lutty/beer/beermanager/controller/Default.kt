package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.Beer
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping


@Controller
class Default(private val beerService: BeerService, private val userRepository: UserRepository, private val futRepository: FutRepository) {

    @GetMapping("/")
    fun home(
        @AuthenticationPrincipal principal: OAuth2User?,
        model: Model
    ): String {
        if (principal != null) {
            if (userRepository.findOneByEmail(principal.getAttribute("email")!!) == null )
                userRepository.save(
                    User(
                       email= principal.getAttribute("email")!!,
                        name= principal.getAttribute("name")!!

                    )
                )

            model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        }
        return "home"
    }

    @GetMapping("/greeting")
    fun greeting(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        return "greeting"
    }
    @GetMapping("/conso")
    fun conso(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("beers",beerService.getAllBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!))
        return "conso"
    }

    @GetMapping("/users")
    fun users(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String  {

        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        model.addAttribute("user", user)
        if (user!!.admin.not())
            return "403"
        model.addAttribute("users",  userRepository.findAll())

        return "users"
    }


    @GetMapping("/futs")
    fun futs(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String  {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        model.addAttribute("user", user)
        if (user!!.admin.not())
            return "403"
        model.addAttribute("fut", Fut())
        model.addAttribute("futs",  futRepository.findAll())
        return "futs"
    }

    @PostMapping("/futs")
    fun futsSubmit(
        @AuthenticationPrincipal principal: OAuth2User,
        @ModelAttribute fut: Fut,
        model: Model
    ): String  {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        if (user!!.admin.not())
            return "403"
        futRepository.save(fut)
        model.addAttribute("user", user)
        model.addAttribute("futs",  futRepository.findAll())
        return "futs"
    }


    @GetMapping("/profil")
    fun profil(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("nbBeer",beerService.countBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!))
        return "profile"
    }

    @GetMapping("/bill")
    fun bill(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        val listFut = futRepository.findAll()
        val nbBeerByUserAndByFut =  listFut.associate { fut -> fut to beerService.getAllBeerForFutAndUser(fut, user)!!.map{it!!.size}.sum() }
        val nbBeerByFut  =  listFut.associate { fut ->fut to  beerService.getAllBeerForFut(fut)!!.map{it!!.size}.sum() }

        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("futs",listFut)
        model.addAttribute("nbBeerUserFut",nbBeerByUserAndByFut)
        model.addAttribute("nbBeerFut",nbBeerByFut)
        return "bill"
    }
    @GetMapping("/detailbill/{futId}")
    fun detailbill(
        @AuthenticationPrincipal principal: OAuth2User, @PathVariable futId: Long,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        val fut = futRepository.findById(futId).get()
        val beerByUserAndByFut =  beerService.getAllBeerForFutAndUser(fut, user)

        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("fut",fut)
        model.addAttribute("beerUserFut",beerByUserAndByFut)
        return "detailledbill"
    }
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

}
