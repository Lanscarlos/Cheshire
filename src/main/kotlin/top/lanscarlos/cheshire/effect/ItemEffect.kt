package top.lanscarlos.cheshire.effect

import ink.ptms.zaphkiel.api.ItemStream
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import top.lanscarlos.cheshire.effect.type.*

class ItemEffect(config: ConfigurationSection) {

    private val effects = mutableMapOf<String, Effect>()

    init {
        config.getConfigurationSection("meta.effect")?.let { section ->
            section.getKeys(false).forEach {
                val type = section.getString("${it}.type", "none")
                when(type.lowercase()) {
                    "arrow-trail" -> EffectArrowTrail(section.getConfigurationSection(it))
                    "circle" -> EffectCircle(section.getConfigurationSection(it))
                    "circle-inclined" -> EffectInclinedCircle(section.getConfigurationSection(it))
                    "circle-rotatable" -> EffectRotatableCircle(section.getConfigurationSection(it))
                    "point" -> EffectPoint(section.getConfigurationSection(it))
                    "sword-arc" -> EffectSwordArc(section.getConfigurationSection(it))
                    else -> return@forEach
                }.let { effect ->
                    effects[it] = effect
                }
            }
        }
    }

    fun hasEffects(): Boolean {
        return effects.isNotEmpty()
    }

    fun hasEffect(key: String): Boolean {
        return key in effects
    }

    fun getEffect(key: String): Effect? {
        return effects[key]
    }

    fun stop(key: String, entity: Entity? = null) {
        effects[key]?.let { effect ->
            entity?.let { effect.stop(it) } ?: effect.stop()
        }
    }

    fun stopAll(entity: Entity? = null) {
        effects.forEach { (_, v) ->
            entity?.let { v.stop(it) } ?: v.stop()
        }
    }

    fun release(key: String, entity: Entity? = null) {
        effects[key]?.let { effect ->
            entity?.let { effect.release(it) } ?: effect.release()
        }
    }

    fun releaseAll(entity: Entity? = null) {
        effects.forEach { (_, v) ->
            entity?.let { v.release(it) } ?: v.release()
        }
    }

    companion object {
        // 缓存对象
        val itemEffects = mutableMapOf<String, ItemEffect>()

        @SubscribeEvent
        fun e(e: ink.ptms.zaphkiel.api.event.ItemEvent.Drop) {
            // 判断是否有特效对象
            if (e.itemStream.isVanilla()) return
            // 释放所有特效对象
            e.itemStream.getItemEffect().releaseAll(e.bukkitEvent.player)
        }

        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            itemEffects.forEach { (_, v) ->
                v.releaseAll(e.player)
            }
        }

        @SubscribeEvent
        fun e(e: EntityDeathEvent) {
            itemEffects.forEach { (_, v) ->
                v.releaseAll(e.entity)
            }
        }

        @SubscribeEvent
        fun e(e: PluginReloadEvent.Item) {
            itemEffects.forEach { (_, itemEffect) ->
                itemEffect.stopAll()
            }
            itemEffects.clear()
        }

//        fun buildParticleSpawner(player: ProxyPlayer, particle: ProxyParticle, vector: Vector, count: Int, speed: Double): ParticleSpawner {
//            return object : ParticleSpawner {
//                override fun spawn(location: Location) {
//                    particle.sendTo(player, location, vector, count, speed)
//                }
//            }
//        }
//
//        fun buildEffect(player: ProxyPlayer, args: Map<*, *>): ParticleObj {
//            val particle = ProxyParticle.valueOf(args["particle"]?.toString()?.uppercase() ?: "FLAME")
//            val vecX = args["vec-x"]?.toString()?.toDouble() ?: 0.0
//            val vecY = args["vec-y"]?.toString()?.toDouble() ?: 0.0
//            val vecZ = args["vec-z"]?.toString()?.toDouble() ?: 0.0
//            val count = args["count"]?.toString()?.toInt() ?: 1
//            val speed = args["speed"]?.toString()?.toDouble() ?: 0.0
//            val spawner = buildParticleSpawner(player, particle, Vector(vecX, vecY, vecZ), count, speed)
//            val radius = args["radius"]?.toString()?.toDouble() ?: 1.0
//            val step = args["step"]?.toString()?.toDouble() ?: 5.0
//            val period = args["period"]?.toString()?.toLong() ?: 5L
//            val offsetX = args["offset-x"]?.toString()?.toDouble() ?: 0.0
//            val offsetY = args["offset-y"]?.toString()?.toDouble() ?: 1.0
//            val offsetZ = args["offset-z"]?.toString()?.toDouble() ?: 0.0
//            val effect = object : Circle(player.location, radius, step, period, spawner) {
//                override fun playNextPoint() {
//                    origin = player.location.add(offsetX, offsetY, offsetZ)
//                    super.playNextPoint()
//                }
//            }
//            return effect
//        }
    }
}

/**
 * 判断此物品有无特效对象
 * */
fun ItemStream.hasEffects(): Boolean {
    return getItemEffect().hasEffects()
}

/**
 * 获取此物品的效果
 * */
fun ItemStream.getItemEffect(): ItemEffect {
    return ItemEffect.itemEffects[this.getZaphkielName()] ?: let {
        ItemEffect.itemEffects[getZaphkielName()] = ItemEffect(getZaphkielItem().config)
        ItemEffect.itemEffects[getZaphkielName()]!!
    }
}
//
///**
// * 释放特效对象
// * */
//fun ItemStream.playEffect(player: Player) {
//    info("检测 - playerEffect")
//    if (!hasEffect()) return
//    info("检测 - playerEffect - play")
//    ItemEffect.cache[player]!![getZaphkielName()]?.let {
//        info("检测 - playerEffect - old playing")
//        it.forEach { obj -> obj.alwaysPlay() }
//    } ?: let {
//        val effects = mutableListOf<ParticleObj>()
//        getZaphkielItem().config.getMapList("meta.effect")?.forEach { map ->
//            info("检测 - playerEffect - loading...")
//            effects += ItemEffect.buildEffect(adaptPlayer(player), map)
//        }
//        effects.forEach {
//            info("检测 - playerEffect - playing...")
//            it.alwaysPlay()
//        }
//        ItemEffect.cache[player]!![getZaphkielName()] = effects
//    }
//}
//
///**
// * 结束特效
// * */
//fun ItemStream.stopEffect(player: Player) {
//    info("检测 - stopEffect")
//    if (!hasEffect()) return
//    info("检测 - stopEffect - stop")
//    val effects = ItemEffect.cache[player]!![getZaphkielName()]
//    effects?.forEach {
//        info("检测 - stopEffect - stopping...")
//        it.turnOffTask()
//    }
//}