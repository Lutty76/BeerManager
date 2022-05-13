package com.lutty.beer.beermanager.controller

import com.lutty.beer.beermanager.entity.*
import com.lutty.beer.beermanager.repository.BeerRepository
import com.lutty.beer.beermanager.repository.DateFutRepository
import com.lutty.beer.beermanager.repository.FutRepository
import com.lutty.beer.beermanager.repository.UserRepository
import com.lutty.beer.beermanager.service.BeerService
import com.lutty.beer.beermanager.service.BillService
import com.lutty.beer.beermanager.service.FutService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
import java.time.ZoneId

@Controller
class Default(
    private val beerService: BeerService,
    private val billService: BillService,
    private val futService: FutService,
    private val userRepository: UserRepository,
    private val futRepository: FutRepository,
    private val beerRepository: BeerRepository,
    private val dateFutRepository: DateFutRepository
) {

    var logger: Logger = LoggerFactory.getLogger(Default::class.java)
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
        val listBeer = beerService.getAllBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!)
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("beers", listBeer)
        model.addAttribute("beer", Beer())
        model.addAttribute("isBilled", listBeer!!.associate { beer -> beer to billService.isBillOnBeer(beer!!) })

        return "conso"
    }
    @PostMapping("/conso")
    fun deleteConso(
        @AuthenticationPrincipal principal: OAuth2User,
        @ModelAttribute beer: Beer,
        model: Model
    ): String {
        if (beer.beerId != null && beerRepository.findOneByBeerId(beer.beerId).user == userRepository.findOneByEmail(principal.getAttribute("email")!!)!!  )
            beerRepository.delete(beer)
        val listBeer = beerService.getAllBeerForUser(userRepository.findOneByEmail(principal.getAttribute("email")!!)!!)
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("beers", listBeer)
        model.addAttribute("isBilled", listBeer!!.associate { beer -> beer to billService.isBillOnBeer(beer!!) })

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
        val nbBeerByUserOfYear = users.associate { user -> user to beerService.getAllBeerForUserFromDate(user, LocalDateTime.of(LocalDateTime.now().year, 1, 1, 0, 0))!!.map { it!!.size }.sum() }
        val nbBeerByUserOfWeek = users.associate { user -> user to beerService.getAllBeerForUserFromDate(user, LocalDateTime.of(LocalDateTime.now().minusDays(7).year, LocalDateTime.now().minusDays(7).month, LocalDateTime.now().minusDays(7).dayOfMonth, 0, 0))!!.map { it!!.size }.sum() }
        val nbBeerByUserOfTotal = users.associate { user -> user to beerService.getAllBeerForUserFromDate(user, LocalDateTime.of(2022, 1, 1, 0, 0))!!.map { it!!.size }.sum() }


        model.addAttribute("date", LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, 1, 0, 0))
        model.addAttribute("nbBeerByUserOfMonth", nbBeerByUserOfMonth.toList().sortedByDescending { it.second }.filterNot { it.second == 0 })
        model.addAttribute("nbBeerByUserOfYear", nbBeerByUserOfYear.toList().sortedByDescending { it.second }.filterNot { it.second == 0 })
        model.addAttribute("nbBeerByUserOfWeek", nbBeerByUserOfWeek.toList().sortedByDescending { it.second }.filterNot { it.second == 0 })
        model.addAttribute("nbBeerByUserOfTotal", nbBeerByUserOfTotal.toList().sortedByDescending { it.second }.filterNot { it.second == 0 })

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
        model.addAttribute("dateFut", DateFut(open=(dateFutRepository.findMaxDateFut()?:DateFut()).end))
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
        model.addAttribute("lastDate",dateFutRepository.findMaxDateFut()?:DateFut())
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
        val listBills = billService.getAllUnpaidBill()
        val nbBeerByUserAndByFut = listBills.associate { bill -> bill to beerService.getAllBeerForFutAndUser(bill.fut, bill.user)!!.map { it!!.size }.sum() }
        val nbBeerByFut = listBills.associate { bill -> bill to beerService.getAllBeerForFut(bill.fut)!!.map { it!!.size }.sum() }
        model.addAttribute("user", user)
        model.addAttribute("bill", Bill())
        model.addAttribute("nbBeerUserFut", nbBeerByUserAndByFut)
        model.addAttribute("nbBeerFut", nbBeerByFut)
        model.addAttribute("listBills", listBills)
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
        val listBills = billService.getAllUnpaidBill()
        val nbBeerByUserAndByFut = listBills.associate { bill -> bill to beerService.getAllBeerForFutAndUser(bill.fut, bill.user)!!.map { it!!.size }.sum() }
        val nbBeerByFut = listBills.associate { bill -> bill to beerService.getAllBeerForFut(bill.fut)!!.map { it!!.size }.sum() }
        model.addAttribute("user", user)
        model.addAttribute("bill", bill)
        model.addAttribute("nbBeerUserFut", nbBeerByUserAndByFut)
        model.addAttribute("nbBeerFut", nbBeerByFut)
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
        val totalDu = isPaidByFut.filter{ paid -> !paid.value }.map{paid -> paid.key.price / nbBeerByFut[paid.key]!! * nbBeerByUserAndByFut[paid.key!!]!!}.sum()
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("futs", listFut)
        model.addAttribute("nbBeerUserFut", nbBeerByUserAndByFut)
        model.addAttribute("nbBeerFut", nbBeerByFut)
        model.addAttribute("isPaidByFut", isPaidByFut)
        model.addAttribute("totalDu", totalDu)
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

    @GetMapping("/api/beer/{size}")
    fun addBeer(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable size: Int,
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
        }

        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))
        model.addAttribute("size", size)
        val alreadyPay = beerRepository.findAllByDateGreaterThanEqualAndUser(LocalDateTime.now(ZoneId.of("Europe/Paris")).minusMinutes(2), user).isNullOrEmpty().not()
        if (alreadyPay.not()) {
            if (size>0 && size <100){
                beerRepository.save(Beer(size = size, user = user))
                return "fill"
            }
            else{
                return "{\"status\":\"ERROR\",\"message\":\"Wrong size.\"}"
            }
        } else {
            return "notfill"
        }
    }
    @GetMapping("/api/beerF/{size}")
    fun addBeerForced(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable size: Int,
        model: Model
    ): String {
        val user = userRepository.findOneByEmail(principal.getAttribute("email")!!)!!
        model.addAttribute("user", userRepository.findOneByEmail(principal.getAttribute("email")!!))

        beerRepository.save(Beer(size = size, user = user))
        return "fill"
    }
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }
}
