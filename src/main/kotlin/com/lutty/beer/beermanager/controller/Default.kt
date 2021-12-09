package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.Bill
import com.lutty.beer.beermanager.entity.DateFut
import com.lutty.beer.beermanager.entity.Fut
import com.lutty.beer.beermanager.entity.User
import com.lutty.beer.beermanager.repository.DateFutRepository
import com.lutty.beer.beermanager.repository.FutRepository
import com.lutty.beer.beermanager.repository.UserRepository
import com.lutty.beer.beermanager.service.BeerService
import com.lutty.beer.beermanager.service.BillService
import com.lutty.beer.beermanager.service.FutService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.view.RedirectView
import java.time.LocalDateTime

@Controller
class Default(
    private val beerService: BeerService,
    private val billService: BillService,
    private val futService: FutService,
    private val userRepository: UserRepository,
    private val futRepository: FutRepository,
    private val dateFutRepository: DateFutRepository
) {

    @GetMapping("/")
    fun home(
        @AuthenticationPrincipal principal: OAuth2User?,
        model: Model
    ): String {
        if (principal != null) {
            if (userRepository.findOneByEmail(principal.getAttribute("email")!!) == null)
                userRepository.save(
                    User(
                        email = principal.getAttribute("email")!!,
                        name = principal.getAttribute("name")!!

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
        model.addAttribute("beers", beerService.getAllBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!))
        return "conso"
    }

    @GetMapping("/users")
    fun users(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {

        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        model.addAttribute("user", user)
        if (user!!.admin.not())
            return "403"
        model.addAttribute("users", userRepository.findAll())

        return "users"
    }

    @GetMapping("/hall")
    fun hall(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {

        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        val users = userRepository.findAll()
        model.addAttribute("user", user)

        val nbBeerByUserOfMonth = users.associate { user -> user to beerService.getAllBeerForUserFromDate(user, LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, 1, 0, 0))!!.map { it!!.size }.sum() }
        model.addAttribute("date", LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, 1, 0, 0))
        model.addAttribute("nbBeerByUserOfMonth", nbBeerByUserOfMonth.toList().sortedByDescending { it.second })

        return "shame"
    }
    @GetMapping("/futs")
    fun futs(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        model.addAttribute("user", user)
        if (user!!.admin.not())
            return "403"
        val futs = futRepository.findAll()
        model.addAttribute("fut", Fut())
        model.addAttribute("futs", futs)
        model.addAttribute("billeds", futs.associate { fut -> fut to billService.isBillForFut(fut) })
        return "futs"
    }

    @PostMapping("/futs")
    fun futsSubmit(
        @AuthenticationPrincipal principal: OAuth2User,
        @ModelAttribute fut: Fut,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        if (user!!.admin.not())
            return "403"
        futRepository.save(fut)
        billService.generateBill(fut)
        val futs = futRepository.findAll()
        model.addAttribute("user", user)
        model.addAttribute("futs", futs)
        model.addAttribute("billeds", futs.associate { fut -> fut to billService.isBillForFut(fut) })
        return "futs"
    }
    @GetMapping("/fut/{futId}")
    fun fut(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable futId: Long,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        model.addAttribute("user", user)
        if (user!!.admin.not())
            return "403"
        val fut = futRepository.getById(futId)
        model.addAttribute("fut", fut)
        model.addAttribute("error", "")
        model.addAttribute("dateFut", DateFut())
        model.addAttribute("billed", billService.isBillForFut(fut))
        model.addAttribute("dates", dateFutRepository.findAllByFut(fut))
        return "fut"
    }

    @PostMapping("/fut/{futId}")
    fun futSubmit(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable futId: Long,
        @ModelAttribute dateFut: DateFut,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        val errorMsg: String
        if (user!!.admin.not())
            return "403"
        if (! futService.isAlreadyPluggedFut(dateFut)) {
            if (dateFut.open > dateFut.end) {
                errorMsg = "Mets les date dans le bon ordre cretin !"
            } else {
                dateFutRepository.save(
                    DateFut(
                        fut = futRepository.getById(futId),
                        open = dateFut.open,
                        end = dateFut.end
                    )
                )
                errorMsg = "Date added"
            }
        } else {
            errorMsg = "Already a fut plugged at this date"
        }

        val fut = futRepository.getById(futId)
        model.addAttribute("fut", fut)
        model.addAttribute("user", user)
        model.addAttribute("error", errorMsg)
        model.addAttribute("dateFut", DateFut())
        model.addAttribute("billed", billService.isBillForFut(fut))
        model.addAttribute("dates", dateFutRepository.findAllByFut(fut))
        return "fut"
    }
    @GetMapping("/manageBill")
    fun managebill(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        if (user!!.admin.not())
            return "403"
        model.addAttribute("user", user)
        model.addAttribute("bill", Bill())
        model.addAttribute("listBills", billService.getAllUnpaidBill())
        model.addAttribute("valBills", billService.getAllUnpaidBill().associate { bill -> bill to billService.getValueBill(bill.billId) })
        return "manageBill"
    }

    @PostMapping("/manageBill")
    fun paidbill(
        @AuthenticationPrincipal principal: OAuth2User,
        @ModelAttribute bill: Bill,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        if (user!!.admin.not())
            return "403"
        billService.paidBill(bill.billId)
        model.addAttribute("user", user)
        model.addAttribute("bill", bill)
        model.addAttribute("listBills", billService.getAllUnpaidBill())
        model.addAttribute("valBills", billService.getAllUnpaidBill().associate { bill -> bill to billService.getValueBill(bill.billId) })
        return "manageBill"
    }

    @GetMapping("/generateBill/{futId}")
    fun generatebill(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable futId: Long,
        model: Model
    ): RedirectView {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)
        if (user!!.admin.not())
            return RedirectView("/")

        billService.generateBill(futRepository.getById(futId))

        return RedirectView("/manageBill")
    }
    @GetMapping("/profil")
    fun profil(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("nbBeer", beerService.countBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!))
        return "profile"
    }

    @GetMapping("/bill")
    fun bill(
        @AuthenticationPrincipal principal: OAuth2User,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        val listFut = futService.findAllBilledFutForUser(user)
        val nbBeerByUserAndByFut = listFut.associate { fut -> fut to beerService.getAllBeerForFutAndUser(fut, user)!!.map { it!!.size }.sum() }
        val nbBeerByFut = listFut.associate { fut -> fut to beerService.getAllBeerForFut(fut)!!.map { it!!.size }.sum() }
        val isPaidByFut = listFut.associate { fut -> fut to billService.isPaidFut(user, fut) }

        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("futs", listFut)
        model.addAttribute("nbBeerUserFut", nbBeerByUserAndByFut)
        model.addAttribute("nbBeerFut", nbBeerByFut)
        model.addAttribute("isPaidByFut", isPaidByFut)
        return "bill"
    }
    @GetMapping("/detailbill/{futId}")
    fun detailbill(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable futId: Long,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        val fut = futRepository.findById(futId).get()
        val beerByUserAndByFut = beerService.getAllBeerForFutAndUser(fut, user)

        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("fut", fut)
        model.addAttribute("dateFuts", dateFutRepository.findAllByFut(fut))
        model.addAttribute("beerUserFut", beerByUserAndByFut)
        return "detailledbill"
    }
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }
}
