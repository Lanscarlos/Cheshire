package top.lanscarlos.cheshire.event

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import taboolib.common.platform.Schedule
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author Lanscarlos
 * @since 2021-11-25 15:14
 */
object ArrowEvent {

    // 存储玩家射击事件
    private val data = mutableMapOf<Projectile, EntityShootBowEvent>()

    // 追踪箭矢
    @Schedule(period = 10, async = true)
    fun track() {
        val iterator = data.iterator()
        while (iterator.hasNext()) {
            val entry =  iterator.next()
            if (entry.key.isDead) {
                iterator.remove()
                continue
            }
            if (entry.key.isOnGround) {
                val event = Land(entry.value.entity as Player, entry.key, ZaphkielAPI.read(entry.value.bow!!), entry.value)
                if (!event.call()) {
                    entry.key.remove()
                }
                iterator.remove()
                continue
            }
        }
    }

    fun onHit(damageEvent: EntityDamageByEntityEvent, projectile: Projectile, entity: LivingEntity) {
        if (!contains(projectile)) return
        val player = data[projectile]!!.entity as Player
        val itemStream = ZaphkielAPI.read(data[projectile]!!.bow!!)
        val event = Hit(player, projectile, entity, itemStream, data[projectile]!!, damageEvent)
        if (!event.call()) {
            damageEvent.isCancelled = true
        }
    }

    fun contains(projectile: Projectile): Boolean {
        return data.containsKey(projectile)
    }

    fun addShootEvent(e: EntityShootBowEvent) {
        if (e.isCancelled || e.projectile.isDead) return
        data[e.projectile as Projectile] = e
    }

    /**
     * 当箭矢着陆时
     * @param player 射击者
     * @param arrow 射出的箭矢
     * @param itemStream 射击所使用的弓
     * */
    class Land(val player: Player, val arrow: Projectile, val itemStream: ItemStream, val shootEvent: EntityShootBowEvent) : BukkitProxyEvent() {}

    /**
     * 当箭矢着陆时
     * @param player 射击者
     * @param arrow 射出的箭矢
     * @param entity 被射中的实体
     * @param itemStream 射击所使用的弓
     * */
    class Hit(val player: Player, val arrow: Projectile, val entity: LivingEntity, val itemStream: ItemStream, val shootEvent: EntityShootBowEvent, val damageEvent: EntityDamageByEntityEvent) : BukkitProxyEvent() {}

}