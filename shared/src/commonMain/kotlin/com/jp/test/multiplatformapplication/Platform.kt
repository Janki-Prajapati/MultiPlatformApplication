package com.jp.test.multiplatformapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform