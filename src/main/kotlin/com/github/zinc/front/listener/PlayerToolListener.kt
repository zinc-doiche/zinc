package com.github.zinc.front.listener

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import com.github.zinc.container.EquipmentContainer
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.equipment.STATUS_KEY
import com.github.zinc.front.event.EquipmentUpdateEvent
import com.github.zinc.front.event.PlayerEquipEvent
import com.github.zinc.util.PassedBy
import com.github.zinc.util.*
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

/**
 * pdc를 추가해야 되는 상황:
 * 1. 상자서 꺼냄
 * 2. 조합
 * 3. 땅에떨어진거 먹음
 *
 * 검사해야 되는 상황:
 * 1. 인벤에서 입음
 * 2. 우클릭으로 입음
 * 3. 칼같은 거는 사용하는 이벤트 캔슬 필요
 *
 * 검사 후 실패시
 * 1. 인벤으로 리턴
 * 2. 꽉 차면 뱉는다.
 * 3. 캔슬
 */
class PlayerToolListener: Listener {

    @EventHandler
    @PassedBy(PlayerListener::class, PlayerInventorySlotChangeEvent::class)
    fun onEquip(e: PlayerEquipEvent) {
        val playerData = PlayerContainer[e.player.name] ?: return

        if (!e.equipment.isDeserved(playerData)) {
            e.player.giveItem(e.equipment.item)
            e.player.sendMessage("아직 사용하기엔 이르다.")
            e.player.removeSlot(e.equipSlot)
        }
    }

    //Not Called by launching arrows, but called by launching the projectiles like Trident
    @EventHandler
    fun onCharge(e: PlayerLaunchProjectileEvent) {
        if(e.projectile !is Trident) return
        val playerData = PlayerContainer[e.player.name] ?: return

        if (e.itemStack.hasPersistent(STATUS_KEY)) {
            val equipment = EquipmentContainer[e.itemStack.getPersistent(STATUS_KEY)!!] ?: return
            if (!equipment.isDeserved(playerData)) {
                e.player.sendMessage("아직 사용하기엔 이르다.")

                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onShootArrow(e: EntityShootBowEvent) {
        if(e.entity !is Player) return
        val playerData = PlayerContainer[e.entity.name] ?: return

        e.bow?.let { item ->
            if(item.hasPersistent(STATUS_KEY)) {
                val uuid = item.getPersistent(STATUS_KEY) ?: return
                val equipment = EquipmentContainer[uuid] ?: return

                if(!equipment.isDeserved(playerData)) {
                    val player = playerData.manager?.playerEntity ?: return
                    val isCrossbow = item.type == Material.CROSSBOW

                    player.sendMessage("아직 사용하기엔 이르다.")
                    e.setConsumeItem(isCrossbow)
                    if(isCrossbow) {
                        e.consumable?.let { consumable ->
                            val arrow = consumable.clone()
                            arrow.amount = 1
                            player.giveItem(arrow)
                        }
                    }
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    @PassedBy(PlayerListener::class, PlayerInventorySlotChangeEvent::class)
    fun onEquipmentUpdated(e: EquipmentUpdateEvent) {
        e.equipment.run {
            setStatus()
            setPDC()
            setLore()
        }
    }
}