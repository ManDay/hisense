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
        fun fromInt(speed: Int): EinkSpeed {
            return when (speed) {
                515 -> CLEAR
                513 -> BALANCED
                518 -> SMOOTH
                521 -> FAST
                else -> throw IllegalArgumentException("Unknown speed: $speed")
            }
        }
        fun fromChar(speed: Char): EinkSpeed {
            return when (speed) {
                'c' -> CLEAR
                'b' -> BALANCED
                's' -> SMOOTH
                'p' -> FAST
                else -> throw IllegalArgumentException("Unknown speed: $speed")
            }
        }
    }
}
