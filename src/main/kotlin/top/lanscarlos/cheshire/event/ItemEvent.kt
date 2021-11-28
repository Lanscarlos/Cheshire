package top.lanscarlos.cheshire.event

import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author Lanscarlos
 * @since 2021-11-26 16:10
 */
object ItemEvent {

    /**
     * 当物品进入玩家装备栏时触发 (激活)
     * */
    class Active(val itemStream: ItemStream, val player: Player, val slot: EquipmentSlot) : BukkitProxyEvent() {

    }

    /**
     * 当物品离开玩家装备栏时触发 (失活)
     * */
    class Inactive(val itemStream: ItemStream, val player: Player, val slot: EquipmentSlot) : BukkitProxyEvent() {

    }
}