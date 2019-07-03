package net.teamfruit.frequency.util

data class ResponseJson (
        val body: Body
)

data class Body (
        val url: Url
)

data class Url (
        val url: String
)