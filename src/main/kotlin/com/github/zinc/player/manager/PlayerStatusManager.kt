package com.github.zinc.player.manager

import com.github.zinc.player.domain.PlayerDTO
import com.github.zinc.player.domain.StatusType
import org.bukkit.attribute.Attribute

class PlayerStatusManager(
    private val playerDTO: PlayerDTO
) {
    private val playerEntity = playerDTO.playerEntity!!

    fun getTotalStatus(): Int {
        val str = playerDTO.playerStrength ?: return 0
        val con = playerDTO.playerConcentration ?: return 0
        val bal = playerDTO.playerBalance ?: return 0
        val swt = playerDTO.playerSwiftness ?: return 0

        return str + con + bal + swt
    }

    fun applyAll() {
        applyStrength()
        applyBalance()
        applySwiftness()
        applyConcentration()
    }

    fun applyStatus(type: StatusType) {
        when(type) {
            StatusType.STRENGTH -> applyStrength()
            StatusType.BALANCE -> applyBalance()
            StatusType.SWIFTNESS -> applySwiftness()
            StatusType.CONCENTRATION -> applyConcentration()
            StatusType.REMAIN -> return
        }
    }

    private fun applyStrength() {
        val damageAttr = playerEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return
        damageAttr.baseValue = getAdditionalHealth(playerDTO.playerStrength ?: return)
    }

    private fun applyBalance() {
        val healthAttr = playerEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        healthAttr.baseValue = getAdditionalHealth(playerDTO.playerBalance ?: return)
        playerEntity.health = healthAttr.baseValue
    }

    private fun applySwiftness() {
        val speedAttr = playerEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return
        speedAttr.baseValue = getAdditionalHealth(playerDTO.playerSwiftness ?: return)
    }

    private fun applyConcentration() {
        //TODO CUSTOM
    }

    fun updateStatus(change: Pair<StatusType, Int>) {
        val type = change.first
        val amount = change.second
        when(type) {
            StatusType.STRENGTH ->
                playerDTO.playerStrength = playerDTO.playerStrength?.plus(amount) ?: return
            StatusType.BALANCE ->
                playerDTO.playerBalance = playerDTO.playerBalance?.plus(amount) ?: return
            StatusType.SWIFTNESS ->
                playerDTO.playerSwiftness = playerDTO.playerSwiftness?.plus(amount) ?: return
            StatusType.CONCENTRATION ->
                playerDTO.playerConcentration = playerDTO.playerConcentration?.plus(amount) ?: return
            StatusType.REMAIN ->
                playerDTO.playerStatusRemain = playerDTO.playerStatusRemain?.plus(amount) ?: return
        }
    }

    companion object {
        private const val defaultDamage = 1.0
        private const val defaultHealth = 20.0
        private const val defaultSpeed = 1.0
        private const val defaultCriticalProbability = 0.0

        private const val INTERVAL1 = 150
        private const val INTERVAL2 = 250
        private const val INTERVAL3 = 400
        private const val INTERVAL4 = 550

        private fun getAdditionalDamage(strength: Int): Double {
            var damage = 0.0

            damage += when (strength) {
                in 0..INTERVAL1 -> //150
                    strength * 5.0 / INTERVAL1
                //max 6 = 1 + 5
                // 5/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    6 + (strength - INTERVAL1) * 2.0 / (INTERVAL2 - INTERVAL1)
                //max 8 = 1 + 5 + 2
                // 3/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    8 + (strength - INTERVAL2) * 1.0 / (INTERVAL3 - INTERVAL2)
                //max 9 = 1 + 5 + 2 + 1
                // 1/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    9 + (strength - INTERVAL3) * 2.0 / (INTERVAL4 - INTERVAL3)
                //max 11 = 1 + 5 + 2 + 1 + 2
                // 3/150 ps
                else -> //600
                    11 + (strength - INTERVAL4) * 4.0 / (600 - INTERVAL4)
                //max 15 = 1 + 5 + 2 + 1 + 2 + 4
                // 12/150 ps
            }
            return damage + defaultDamage
        }

        private fun getAdditionalHealth(balance: Int): Double {
            var health = 0.0

            health += when(balance) {
                in 0..INTERVAL1 -> //150
                    balance * 10.0 / INTERVAL1
                //max 30 = 20 + 10
                // 10/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    10 + (balance - INTERVAL1) * 6.0 / (INTERVAL2 - INTERVAL1)
                //max 36 = 20 + 10 + 6
                // 9/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    16 + (balance - INTERVAL2) * 4.0 / (INTERVAL3 - INTERVAL2)
                //max 40 = 20 + 10 + 6 + 4
                // 4/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    20 + (balance - INTERVAL3) * 6.0 / (INTERVAL4 - INTERVAL3)
                //max 46 = 20 + 10 + 6 + 4 + 6
                // 6/150 ps
                else -> //600
                    26 + (balance - INTERVAL4) * 14.0 / (600 - INTERVAL4)
                //max 60 = 20 + 10 + 6 + 4 + 6 + 14
                // 42/150 ps
            }
            return health + defaultHealth
        }

        //TODO 상대속도 -> 절대속도
        private fun getAdditionalSpeed(swiftness: Int): Double {
            var speed = 0.0

            speed += when(swiftness) {
                in 0..INTERVAL1 -> //150
                    swiftness * .4 / INTERVAL1
                //max 1.4 = 1 + .4
                // 0.4/150 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    1.4 + (swiftness - INTERVAL1) * .2 / (INTERVAL2 - INTERVAL1)
                //max 1.6 = 1 + .4 + .2
                // 0.3/150 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    1.6 + (swiftness - INTERVAL2) * .2 / (INTERVAL3 - INTERVAL2)
                //max 1.8 = 1 + .4 + .2 + .2
                // 0.2/150 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    1.8 + (swiftness - INTERVAL3) * .4 / (INTERVAL4 - INTERVAL3)
                //max 2.2 = 1 + .4 + .2 + .2 + .4
                // 0.4/150 ps
                else -> //600
                    2.2 + (swiftness - INTERVAL4) * .8 / (600 - INTERVAL4)
                //max 3 = 1 + .4 + .2 + .2 + .4 + .8
                // 2.4/150 ps
            }
            return speed + defaultSpeed
        }

        //활이 메인딜
        private fun getAdditionalCriticalProbability(concentration: Int): Double {
            var criticalProbability = 0.0

            criticalProbability += when(concentration) {
                in 0..INTERVAL1 -> //150
                    concentration * .2 / INTERVAL1
                //max 0.2 = 0 + .2
                // 20/15000 ps
                in INTERVAL1 + 1..INTERVAL2 -> //250
                    1.4 + (concentration - INTERVAL1) * .05 / (INTERVAL2 - INTERVAL1)
                //max 0.25 = 0 + .2 + .05
                // 5/15000 ps
                in INTERVAL2 + 1..INTERVAL3 -> //400
                    1.6 + (concentration - INTERVAL2) * .05 / (INTERVAL3 - INTERVAL2)
                //max 0.3 = 0 + .2 + .05 +.05
                // 5/15000 ps
                in INTERVAL3 + 1..INTERVAL4 -> //550
                    1.8 + (concentration - INTERVAL3) * .1 / (INTERVAL4 - INTERVAL3)
                //max 0.4 = 0 + .2 + .05 + .05 + .1
                // 10/15000 ps
                else -> //600
                    2.2 + (concentration - INTERVAL4) * .6 / (600 - INTERVAL4)
                //max 1 = 0 + .2 + .05 + .05 + .1 + .6
                // 60/15000 ps
            }
            return criticalProbability + defaultCriticalProbability
        }
    }
}