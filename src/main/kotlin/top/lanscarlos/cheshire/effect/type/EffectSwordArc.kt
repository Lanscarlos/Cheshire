package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.common.util.random
import taboolib.module.effect.*
import top.lanscarlos.cheshire.effect.Effect
import kotlin.math.cos
import kotlin.math.sin

/**
 * 代表一个可旋转的园
 * */
class EffectSwordArc(config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection): Effect(config) {
    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        return SwordArc(location, entity, viewRadius)
    }

    /**
     * @param radius 半径
     * @param step 弧形步长
     * @param angle 弧形角度 [ 0~180 ]
     * @param rotate 旋转角度
     * @param speed 前进速度
     * */
    inner class SwordArc(
        location: Location,
        entity: Entity?,
        viewRadius: Double,
        val radius: Double = config.getDouble("radius", 1.0),
        val step: Int = config.getInt("step", 10), // 弧形步长
        val angle: Int = config.getInt("angle", 30), // 弧形角度 [ 0~180 ]
        val rotate: String = config.getString("rotate"), // 旋转角度
        val speed: Double = config.getDouble("speed", 0.2), // 前进速度
    ) : EffectParticleObj(location, entity, buildParticleSpawner(viewRadius)), Playable {

        val loc = getLocation()
        val coordinate = PlayerFixedCoordinate(loc)
        val pitch = Math.toRadians(-loc.pitch.toDouble())
        val axisZ = coordinate.newLocation(0.0, 0.0, 1.0).subtract(coordinate.originDot).toVector()
        val axisX = coordinate.newLocation(0.1, 0.0, 0.0).subtract(coordinate.originDot).toVector()
        val direction = loc.direction.normalize()
        var position = 0.0
        val rotateAngle = rotate.split("..").let {
            Math.toRadians(
                if (it.size == 2) {
                    random(it[0].toDouble(), it[1].toDouble())
                }else {
                    it[0].toDouble()
                }
            )
        }

        override fun finish(): Boolean {
            return timeUp()
        }

        override fun show() {
            if (timeUp()) return
        }

        override fun playNextPoint() {
            if (timeUp()) return
            val vec = direction.clone().multiply(position)
            for (i in -angle .. angle step step) {
                val radians = Math.toRadians(i.toDouble())
                val y = radius * sin(radians)
                val z = radius * cos(radians)
                spawnParticle(rotate(rotate(coordinate.newLocation(0.0, y, z), coordinate.originDot, rotateAngle, axisZ), coordinate.originDot, pitch, axisX).add(vec))
            }
            position += speed
        }

        fun rotate(location: Location, origin: Location, radians: Double, axis: Vector): Location {
            return LocationUtils.rotateLocationAboutVector(location, origin, radians, axis)
        }

    }
}