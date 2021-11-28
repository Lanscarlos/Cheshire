package top.lanscarlos.cheshire.item

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStream
import top.lanscarlos.cheshire.event.ItemEvent
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import top.lanscarlos.cheshire.effect.getItemEffect
import top.lanscarlos.cheshire.event.ArrowEvent

object ItemListener {
    val data = mutableMapOf<Player, Array<ItemStream?>>()

    // 追踪玩家装备栏的变化
    @Schedule(period = 10, async = true)
    fun track() {
        Bukkit.getOnlinePlayers().forEach {
            if (!data.containsKey(it)) {
                data[it] = arrayOfNulls(6)
            }
            val equipment = data[it]!!
            fun track(index: Int, item: ItemStack?, slot: EquipmentSlot) {
                val itemStream = item?.let { if (item.isNotAir()) ZaphkielAPI.read(item) else null }
                if (itemStream == null || itemStream.isVanilla()) {
                    if (equipment[index] != null) {
                        // 触发事件
                        ItemEvent.Inactive(equipment[index]!!, it, slot).call()
                        equipment[index] = null
                    }
                    return
                }
                if(equipment[index] != null) {
                    if (equipment[index]!!.getZaphkielName() == itemStream.getZaphkielName()) return
                    ItemEvent.Inactive(equipment[index]!!, it, slot).call()
                }
                ItemEvent.Active(itemStream, it, slot).call()
                equipment[index] = itemStream
            }
            track(0, it.inventory.itemInMainHand, EquipmentSlot.HAND)
            track(1, it.inventory.itemInOffHand, EquipmentSlot.OFF_HAND)
            track(2, it.inventory.helmet, EquipmentSlot.HEAD)
            track(3, it.inventory.chestplate, EquipmentSlot.CHEST)
            track(4, it.inventory.leggings, EquipmentSlot.LEGS)
            track(5, it.inventory.boots, EquipmentSlot.FEET)
        }
    }

    @SubscribeEvent
    fun e(e: ink.ptms.zaphkiel.api.event.ItemEvent.AsyncTick) {
        if (e.itemStream.isExtension()) {
            e.itemStream.getZaphkielItem().invokeScript("onTick*", e.player, e, e.itemStream)
        }
    }

    /**
     * 当物品被穿戴在装备栏时
     * 触发脚本
     * */
    @SubscribeEvent
    fun e(e: ItemEvent.Active) {
        e.itemStream.getZaphkielItem().invokeScript("onActive*", e.player, e, e.itemStream, data = mapOf(
            "event" to e,
            "player" to e.player,
            "itemStream" to e.itemStream,
            "effect" to e.itemStream.getItemEffect(),
            "slot" to e.slot,
            "isCancelled" to e.isCancelled
        ))
    }

    /**
     * 当物品从装备栏取下时
     * 触发脚本
     * */
    @SubscribeEvent
    fun e(e: ItemEvent.Inactive) {
        e.itemStream.getZaphkielItem().invokeScript("onInactive*", e.player, e, e.itemStream, data = mapOf(
            "event" to e,
            "player" to e.player,
            "itemStream" to e.itemStream,
            "effect" to e.itemStream.getItemEffect(),
            "slot" to e.slot,
            "isCancelled" to e.isCancelled
        ))
    }

    /**
     * 当实体受到伤害时
     * 触发脚本
     * */
    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {
        val attacker = e.damager
        if (attacker is Player) {
            invokeScript("onAttack*", attacker, e, mapOf(
                "event" to e,
                "player" to attacker,
                "attacker" to attacker,
                "entity" to e.entity,
                "entityType" to e.entityType,
                "damage" to e.damage,
                "cause" to e.cause,
                "isCancelled" to e.isCancelled
            ))
        }else if (attacker is Projectile) {
            val entity = e.entity
            // 触发事件
            if (entity is LivingEntity) ArrowEvent.onHit(e, attacker, entity)
        }

        val entity = e.entity
        if (entity is Player) {
            invokeScript("onDamaged*", entity, e, mapOf(
                "event" to e,
                "player" to entity,
                "entity" to entity,
                "attacker" to e.damager,
                "entityType" to e.entityType,
                "damage" to e.damage,
                "cause" to e.cause,
                "isCancelled" to e.isCancelled
            ))
        }
    }

    @SubscribeEvent
    fun e(e: EntityShootBowEvent) {
        val shooter = e.entity
        if (shooter is Player) {
            val bow = e.bow
            if (bow == null || bow.isAir()) return
            val itemStream = ZaphkielAPI.read(bow)
            if (itemStream.isExtension()) {
                itemStream.getZaphkielItem().invokeScript("onShoot*", shooter, e, itemStream, data = mapOf(
                    "event" to e,
                    "player" to shooter,
                    "itemStream" to itemStream,
                    "effect" to itemStream.getItemEffect(),
                    "bow" to bow,
                    "hand" to e.hand,
                    "force" to e.force,
                    "projectile" to e.projectile,
                    "shouldConsumeItem" to e.shouldConsumeItem(),
                    "isCancelled" to e.isCancelled
                ))
                ArrowEvent.addShootEvent(e)
            }
        }
    }

    @SubscribeEvent
    fun e(e: ArrowEvent.Land) {
        val itemStream = e.itemStream
        itemStream.getZaphkielItem().invokeScript("onArrowLand*", e.player, e, itemStream, data = mapOf(
            "event" to e,
            "player" to e.player,
            "itemStream" to itemStream,
            "effect" to itemStream.getItemEffect(),
            "arrow" to e.arrow,
            "projectile" to e.arrow,
            "isCancelled" to e.isCancelled
        ))
    }

    @SubscribeEvent
    fun e(e: ArrowEvent.Hit) {
        val itemStream = e.itemStream
        e.player
        itemStream.getZaphkielItem().invokeScript("onArrowHit*", e.player, e, itemStream, data = mapOf(
            "event" to e,
            "player" to e.player,
            "entity" to e.entity,
            "itemStream" to itemStream,
            "effect" to itemStream.getItemEffect(),
            "arrow" to e.arrow,
            "projectile" to e.arrow,
            "hand" to e.shootEvent.hand,
            "force" to e.shootEvent.force,
            "shouldConsumeItem" to e.shootEvent.shouldConsumeItem(),
            "isCancelled" to e.isCancelled
        ))
    }


    /**
     * 当玩家消耗物品时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun e(e: ink.ptms.zaphkiel.api.event.ItemEvent.Consume) {
        e.itemStream.getZaphkielItem().invokeScript("onConsume*", e.bukkitEvent.player, e.bukkitEvent, e.itemStream, data = mapOf(
            "event" to e.bukkitEvent,
            "player" to e.bukkitEvent.player,
            "itemStream" to e.itemStream,
            "effect" to e.itemStream.getItemEffect(),
            "isCancelled" to e.isCancelled
        ))
    }

    /**
     * 当玩家与空气或方块发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun e(e: ink.ptms.zaphkiel.api.event.ItemEvent.Interact) {
        val data = mapOf(
            "event" to e.bukkitEvent,
            "player" to e.bukkitEvent.player,
            "itemStream" to e.itemStream,
            "effect" to e.itemStream.getItemEffect(),
            "hand" to (e.bukkitEvent.hand ?: "null"),
            "action" to e.bukkitEvent.action,
            "blockFace" to e.bukkitEvent.blockFace,
            "clickedBlock" to (e.bukkitEvent.clickedBlock ?: "null"),
            "isBlockInHand" to e.bukkitEvent.isBlockInHand,
            "material" to e.bukkitEvent.material
        )
        when (e.bukkitEvent.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                e.itemStream.getZaphkielItem().invokeScript("onLeftClick*", e.bukkitEvent.player, e.bukkitEvent, e.itemStream, data = data)
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                e.itemStream.getZaphkielItem().invokeScript("onRightClick*", e.bukkitEvent.player, e.bukkitEvent, e.itemStream, data = data)
            }
            else -> {}
        }
    }

    /**
     * 当玩家与实体发生交互时
     * 触发事件及脚本
     */
    @SubscribeEvent
    fun e(e: ink.ptms.zaphkiel.api.event.ItemEvent.InteractEntity) {
        e.itemStream.getZaphkielItem().invokeScript("onRightClickEntity*", e.bukkitEvent.player, e.bukkitEvent, e.itemStream, data = mapOf(
            "event" to e.bukkitEvent,
            "player" to e.bukkitEvent.player,
            "itemStream" to e.itemStream,
            "effect" to e.itemStream.getItemEffect(),
            "hand" to e.bukkitEvent.hand,
            "entity" to e.bukkitEvent.rightClicked,
            "isCancelled" to e.bukkitEvent.isCancelled
        ))
    }

    /**
     * 当玩家破坏方块时
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: BlockBreakEvent) {
        invokeScript("onBlockBreak*", e.player, e, mapOf(
            "event" to e,
            "player" to e.player,
            "block" to e.block,
            "isDropItems" to e.isDropItems,
            "expToDrop" to e.expToDrop,
            "isCancelled" to e.isCancelled
        ))
    }

    /**
     * 当玩家放置方块时
     * 触发脚本
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: BlockPlaceEvent) {
        invokeScript("onBlockPlace*", e.player, e, mapOf(
            "event" to e,
            "player" to e.player,
            "block" to e.block,
            "hand" to e.hand,
            "itemInHand" to e.itemInHand,
            "blockPlaced" to e.blockPlaced,
            "canBuild" to e.canBuild(),
            "isCancelled" to e.isCancelled
        ))
    }

    private fun invokeScript(key: String, player: Player?, event: Event, data: Map<String, Any>) {
        invokeScript(key, player, event, player?.inventory?.itemInMainHand, mapOf("slot" to -1, *data.toList().toTypedArray()))
        invokeScript(key, player, event, player?.inventory?.itemInOffHand, mapOf("slot" to 40, *data.toList().toTypedArray()))
        invokeScript(key, player, event, player?.inventory?.helmet, mapOf("slot" to 39, *data.toList().toTypedArray()))
        invokeScript(key, player, event, player?.inventory?.chestplate, mapOf("slot" to 38, *data.toList().toTypedArray()))
        invokeScript(key, player, event, player?.inventory?.leggings, mapOf("slot" to 37, *data.toList().toTypedArray()))
        invokeScript(key, player, event, player?.inventory?.boots, mapOf("slot" to 36, *data.toList().toTypedArray()))
    }

    private fun invokeScript(key: String, player: Player?, event: Event, item: ItemStack?, data: Map<String, Any>) {
        if (item == null || item.isAir()) return
        val itemStream = ZaphkielAPI.read(item)
        if (itemStream.isExtension()) {
            itemStream.getZaphkielItem().invokeScript(key, player, event, itemStream, data = mapOf("itemStream" to itemStream, "effect" to itemStream.getItemEffect(), *data.toList().toTypedArray()))
        }
    }

}