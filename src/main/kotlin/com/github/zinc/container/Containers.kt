package com.github.zinc.container

import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.quest.domain.QuestVO

object PlayerContainer: Container<String, PlayerData>()
object QuestContainer: Container<String, QuestVO>()