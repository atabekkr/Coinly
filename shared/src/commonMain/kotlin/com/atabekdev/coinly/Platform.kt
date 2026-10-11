package com.atabekdev.coinly

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform