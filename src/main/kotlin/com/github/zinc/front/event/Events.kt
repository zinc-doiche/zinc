package com.github.zinc.front.event

import com.github.zinc.core.equipment.Equipment
import com.github.zinc.core.equipment.ZincEquipment
import com.github.zinc.core.player.PlayerData
import org.bukkit.entity.Enemy
import org.bukkit.inventory.ItemStack

class QuestClearEvent(val playerData: PlayerData, val enemy: Enemy) : EventAdapter(true)
class ItemChangeEnchantEvent(val item: ItemStack) : EventAdapter()
class EquipmentUpdateEvent(val equipment: Equipment) : EventAdapter()
