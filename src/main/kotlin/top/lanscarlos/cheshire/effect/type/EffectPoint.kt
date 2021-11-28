package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.Playable
import top.lanscarlos.cheshire.effect.Effect

/**
 * 代表一个倾斜的园
 * */
class EffectPoint(config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection): Effect(config) {
    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        return Point(location, entity, viewRadius)
    }

    inner class Point(
        location: Location,
        entity: Entity?,
        viewRadius: Double,
    ) : EffectParticleObj(location, entity, buildParticleSpawner(viewRadius)), Playable {

        override fun finish(): Boolean {
            return true
        }

        override fun show() {
            if (timeUp()) return
            spawnParticle(getLocation())
        }

        override fun playNextPoint() {
            if (timeUp()) return
            spawnParticle(getLocation())
        }

    }
}