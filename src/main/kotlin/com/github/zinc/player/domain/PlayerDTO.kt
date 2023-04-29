package com.github.zinc.player.domain

import org.bukkit.entity.Player

data class PlayerDTO(
    var playerId: Long,
    var playerName: String,
    var playerLevel: Int,
    var playerExperience: Int,
    var playerStatusRemain: Int,
    var playerStrength: Int,
    var playerSwiftness: Int,
    var playerConcentration: Int,
    var playerBalance: Int,
    var playerEntity: Player
)
