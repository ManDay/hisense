package com.hisense.einkservice.model

enum class EinkSpeed(private val speed: Char) {
    CLEAR('c'),
    BALANCED('b'),
    SMOOTH('s'),
    FAST('p'),
    ;

    fun toChar(): Char {
        return speed
    }

    companion object {
        fun fromSpeed(speed: Int): EinkSpeed {
            return when (speed) {
                515 -> CLEAR
                513 -> BALANCED
                518 -> SMOOTH
                521 -> FAST
                else -> throw IllegalArgumentException("Unknown speed: $speed")
            }
        }
    }
}
