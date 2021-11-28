package top.lanscarlos.cheshire.effect

import ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.ParticleSpawner
import taboolib.module.effect.Playable
import taboolib.module.nms.nmsProxy
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

abstract class Effect(
    val config: ConfigurationSection,
    val particle: ProxyParticle = ProxyParticle.valueOf(config.getString("particle.type", "FLAME").uppercase()),
    val particle_vector: Vector = Vector(config.getDouble("particle.vec-x", 0.0), config.getDouble("particle.vec-y", 0.0), config.getDouble("particle.vec-x", 0.0)),
    val particle_count: Int = config.getInt("particle.count", 1),
    val particle_speed: Double = config.getDouble("particle.speed", 0.0),
    val delay: Long = config.getInt("delay", 0).toLong(),
    val period: Long = config.getInt("period", 5).toLong(),
    val duration: Int = config.getInt("duration", -1),
    val offset: Vector = Vector(config.getDouble("offset-x", 0.0), config.getDouble("offset-y", 0.0), config.getDouble("offset-x", 0.0)),
) {

    val locEffects = mutableSetOf<ParticleObj>()
    val entityEffects = mutableMapOf<Entity, ParticleObj>()

    fun buildParticleData(): ProxyParticle.Data? {
        return when(particle) {
            ProxyParticle.SPELL_MOB, ProxyParticle.SPELL_MOB_AMBIENT, ProxyParticle.REDSTONE -> ProxyParticle.DustData(
                config.getString("particle.color", "0-0-0").split("-").toList().let {
                    when(it.size) {
                        1 -> java.awt.Color(it[0].toInt())
                        2 -> java.awt.Color(it[0].toInt(), it[1].toInt(), 0)
                        3 -> java.awt.Color(it[0].toInt(), it[1].toInt(), it[2].toInt())
                        else -> java.awt.Color(0, 0, 0)
                    }
                },
                config.getDouble("particle.size", 1.0).toFloat()
            )

            else -> null
        }
    }

    /**
     * 一次性释放效果
     * */
    fun show(location: Location, viewRadius: Double, always: Boolean = false) {
        if (always) {
            buildEffect(location, viewRadius).alwaysShow()
        }else {
            buildEffect(location, viewRadius).show()
        }
    }
    fun show(entity: Entity, viewRadius: Double, always: Boolean = false) {
        if (entity.isDead) return
        if (always) {
            buildEffect(entity, viewRadius).alwaysShow()
        }else {
            buildEffect(entity, viewRadius).show()
        }
    }

    /**
     * 释放一次动态效果
     * */
    fun play(location: Location, viewRadius: Double, always: Boolean = false) {
        if (always) {
            buildEffect(location, viewRadius).alwaysPlay()
        }else {
            (buildEffect(location, viewRadius) as Playable).play()
        }
    }

    fun play(entity: Entity, viewRadius: Double, always: Boolean = false) {
        if (entity.isDead) return
        if (always) {
            buildEffect(entity, viewRadius).alwaysPlay()
        }else {
            (buildEffect(entity, viewRadius) as Playable).play()
        }
    }

    fun stop() {
        locEffects.forEach { it.turnOffTask() }
        entityEffects.forEach { (_, v) -> v.turnOffTask() }
    }

    fun stop(entity: Entity) {
        entityEffects[entity]?.turnOffTask()
    }

    fun release(entity: Entity) {
        stop(entity)
        entityEffects.remove(entity)
    }

    fun release() {
        stop()
        locEffects.clear()
        entityEffects.clear()
    }

    fun buildEffect(location: Location, viewRadius: Double): ParticleObj {
        val effect = buildEffect(location, viewRadius)
        locEffects += effect
        return effect
    }

    fun buildEffect(entity: Entity, viewRadius: Double): ParticleObj {
        if (entity in entityEffects) {
            return entityEffects[entity]!!
        }
        val effect = buildEffect(entity.location.toProxyLocation(), entity, viewRadius)
        entityEffects[entity] = effect
        return effect
    }

    abstract fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj

    fun buildParticleSpawner(player: Player): ParticleSpawner {
        return object : ParticleSpawner {
            override fun spawn(location: Location) {
                particle.sendTo(adaptPlayer(player), location, particle_vector, particle_count, particle_speed, buildParticleData())
            }
        }
    }

    fun buildParticleSpawner(viewRadius: Double): ParticleSpawner {
        return object : ParticleSpawner {
            override fun spawn(location: Location) {
                location.toBukkitLocation().world?.let { world ->
                    world.getNearbyEntities(location.toBukkitLocation(), viewRadius, viewRadius, viewRadius).filterIsInstance<Player>().forEach {
                        particle.sendTo(it, location, particle_vector, particle_count, particle_speed, buildParticleData())
//                        NMSImpl.sendParticle(it, location)
//                        ProxyParticle.REDSTONE.sendTo(adaptPlayer(it), location, particle_vector, particle_count, particle_speed, buildParticleData())
//                        it.spawnParticle(Particle.REDSTONE, location.toBukkitLocation(), particle_count, particle_vector.x, particle_vector.y, particle_vector.z, particle_speed, Particle.DustOptions(
//                            org.bukkit.Color.fromRGB(255, 100, 100), 1.0F
//                        ))
                    }
                }
            }
        }
    }

    abstract inner class EffectParticleObj(
        location: Location,
        val entity: Entity? = null,
        spawner: ParticleSpawner
    ) : ParticleObj(spawner), Playable {

        private val endTime = if (this@Effect.duration < 0) -1L else System.currentTimeMillis() + this@Effect.duration * 50L

        init {
            super.origin = location
            super.period = this@Effect.period
        }

        fun timeUp(): Boolean {
            if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                turnOffTask()
                entity?.let { entityEffects.remove(it) }
                return true
            }
            return false
        }

        override fun play() {
            submit(delay =  this@Effect.delay, period = this@Effect.period) {
                if (timeUp()) {
                    this@submit.cancel()
                    return@submit
                }
                // 进行关闭
                if (finish()) {
                    this@submit.cancel()
                    return@submit
                }
                playNextPoint()
            }
        }

        /**
         * 判断效果是否已完成
         * 用于 play() 中止调度器
         * */
        abstract fun finish(): Boolean

        fun getLocation(): Location {
            if (entity?.isDead != false) {
                // 实体不为空且已死亡
                turnOffTask()
                entity?.let { entityEffects.remove(it) }
            }
            return (entity?.location?.toProxyLocation() ?: origin).clone().add(offset)
        }
    }

    companion object {
        val handle by lazy {
            nmsProxy<Effect>()
        }
    }
}

fun ProxyParticle.sendTo(player: Player, location: Location, offset: Vector, count: Int, speed: Double, data: ProxyParticle.Data? = null) {
    var color: Color? = null
    val particle = CraftParticle.toNMS(
        Particle.valueOf(this.name),
        when(data) {
            is ProxyParticle.DustData -> {
                color = Color.fromRGB(data.color.red, data.color.green, data.color.blue)
                Particle.DustOptions(color, data.size)
            }
            else -> null
        }
    )

    (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutWorldParticles(
        particle, false, location.x, location.y, location.z,
        color?.red?.div(255f) ?: offset.x.toFloat(),
        color?.green?.div(255f) ?: offset.y.toFloat(),
        color?.blue?.div(255f) ?: offset.z.toFloat(),
        color?.let { 1f } ?: speed.toFloat(),
        color?.let { 0 } ?: count
    ))
}