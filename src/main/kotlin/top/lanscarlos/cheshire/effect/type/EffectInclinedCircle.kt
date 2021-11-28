package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.Playable
import top.lanscarlos.cheshire.effect.Effect
import kotlin.math.cos
import kotlin.math.sin

/**
 * 代表一个倾斜的园
 * */
class EffectInclinedCircle(config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection): Effect(config) {
    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        return RotatableCircle(location, entity, viewRadius)
    }

    /**
     * @param radius 半径
     * @param step 步长
     * @param startAngle 初始角度
     * @param reversal 是否反转
     * */
    inner class RotatableCircle(
        location: Location,
        entity: Entity?,
        viewRadius: Double,
        val radius: Double = config.getDouble("radius", 1.0),
        val step: Double = config.getDouble("step", 10.0),
        val startAngle: Double = config.getDouble("start-angle", 0.0),
        val reversal: Boolean = config.getBoolean("reversal", false)
    ) : EffectParticleObj(location, entity, buildParticleSpawner(viewRadius)), Playable {

        var angle = startAngle

        override fun finish(): Boolean {
            if (angle >= 360.0) {
                angle = startAngle
                return true
            }
            return timeUp()
        }

        override fun show() {
            if (timeUp()) return
            val yaw = entity?.location?.yaw?.toDouble() ?: 0.0
            for (angle in 0 until 360) {
                val radians = Math.toRadians(angle.toDouble())
                val x = sin(radians)
                val y = sin(Math.toRadians(yaw + angle)) * if (!reversal) -1 else 1
                val z = cos(radians)
                spawnParticle(getLocation().add(x, y, z))
            }
        }

        override fun playNextPoint() {
            if (timeUp()) return
            val yaw = entity?.location?.yaw?.toDouble() ?: 0.0
            val radians = Math.toRadians(angle)
            val x = radius * sin(radians)
            val y = sin(Math.toRadians(yaw + angle)) * if (!reversal) -1 else 1
            val z = radius * cos(radians)
            angle += step
            if (angle >= startAngle + 720) angle = startAngle
            spawnParticle(getLocation().add(x, y, z))
        }

    }
}