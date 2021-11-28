package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.Playable
import top.lanscarlos.cheshire.effect.Effect
import kotlin.math.cos
import kotlin.math.sin

/**
 * 代表一个可旋转的园
 * */
class EffectRotatableCircle(config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection): Effect(config) {
    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        return RotatableCircle(location, entity, viewRadius)
    }

    /**
     * @param radius 半径
     * @param step 步长
     * @param startAngle 初始角度
     * @param rotateStep 旋转步长
     * */
    inner class RotatableCircle(
        location: Location,
        entity: Entity?,
        viewRadius: Double,
        val radius: Double = config.getDouble("radius", 1.0),
        val height: Double = config.getDouble("height", 1.0),
        val step: Double = config.getDouble("step", 10.0),
        val startAngle: Double = config.getDouble("start-angle", 0.0),
        val rotateStep: Double = config.getDouble("rotate-step", 1.0)
    ) : EffectParticleObj(location, entity, buildParticleSpawner(viewRadius)), Playable {

        val axis = Vector(0.0, 1.0, 0.0).normalize()
        var angle = startAngle
        var rotate = 0.0

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
                val y = sin(Math.toRadians(yaw + angle))
                val z = cos(radians)
                spawnParticle(getLocation().add(x, y, z))
            }
        }

        override fun playNextPoint() {
            if (timeUp()) return
            val loc = getLocation()
            val radians = Math.toRadians(angle)
            val x = radius * sin(radians)
            val y = height * sin(Math.toRadians(rotate))
            val z = radius * cos(radians)
            spawnParticle(loc.add(x, y, z))
            angle += step
            rotate += rotateStep
            if (angle >= startAngle + 720) angle = startAngle
            if (rotate >= 360) rotate = 0.0
        }

    }
}