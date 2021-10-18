package com.lutty.beer.beermanager.entity

class GoogleUserInfo {

    constructor(attributes: Map<String, Any>)

    private var attributes: Map<String, Any>? = null

    fun getId(): String? {
        return attributes!!["sub"] as String?
    }

    fun getName(): String? {
        return attributes!!["name"] as String?
    }

    fun getEmail(): String? {
        return attributes!!["email"] as String?
    }
}
