package com.hisense.einkservice.model

enum class EinkSpeed( val speed: Int ) {
    CLEAR(0),
    BALANCED(1),
    SMOOTH(2),
    FAST(3);
    
    companion object {
        fun fromInt( speed: Int ): EinkSpeed {
            return values().firstOrNull { it.speed == speed } ?: BALANCED
        }
    }
}
