package com.prototype.newkmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform