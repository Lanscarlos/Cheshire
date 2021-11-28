package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.module.effect.Circle
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation
import top.lanscarlos.cheshire.effect.Effect

/**
 * 代表一个水平的的园
 * */
class EffectCircle(
    config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection,
    val radius: Double = config.getDouble("radius", 1.0),
    val step: Double = config.getDouble("step", 10.0),
    val startAngle: Double = config.getDouble("startAngle", 0.0)
): Effect(config) {

    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        val effect = object : Circle(location, radius, step, period, buildParticleSpawner(viewRadius)) {

            val endTime = if (this@EffectCircle.duration < 0) -1L else System.currentTimeMillis() + this@EffectCircle.duration * 50L

            fun timeUp(): Boolean {
                if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                    turnOffTask()
                    entity?.let { entityEffects.remove(it) }
                    return true
                }
                return false
            }

            override fun show() {
                if (timeUp()) return
                entity?.let {
                    if (it.isDead) {
                        entityEffects.remove(it)
                        turnOffTask()
                    }
                    origin = entity.location.toProxyLocation().add(offset)
                } ?: let {
                    origin = origin.clone().add(offset)
                }
                super.show()
            }

            override fun playNextPoint() {
                if (timeUp()) return
                entity?.let {
                    if (it.isDead) {
                        entityEffects.remove(it)
                        turnOffTask()
                    }
                    origin = entity.location.toProxyLocation().add(offset)
                } ?: let {
                    origin = origin.clone().add(offset)
                }
                super.playNextPoint()
            }
        }
        effect.startAngle = startAngle
        return effect
    }

}