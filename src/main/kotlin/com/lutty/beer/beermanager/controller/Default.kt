package com.lutty.beer.beermanager.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class Default {

    @GetMapping("/")
    fun home(
        @AuthenticationPrincipal principal: OAuth2User?,model: Model): String {
        if (principal != null)
            model.addAttribute("name", principal.getAttribute("name"))
        return "home"
    }

    @GetMapping("/greeting")
    fun greeting(
        @AuthenticationPrincipal principal: OAuth2User,
        @RequestParam(name = "name", required = false, defaultValue = "World") name: String?,
        model: Model
    ): String {
        model.addAttribute("name", principal.getAttribute("name"))
        return "greeting"
    }
    @GetMapping("/login")
    fun login(
        @RequestParam(name = "name", required = false, defaultValue = "World") name: String?,
        model: Model
    ): String {
        model.addAttribute("name", name)
        return "login"
    }
}
