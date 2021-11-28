package top.lanscarlos.cheshire.effect.type

import org.bukkit.entity.Entity
import taboolib.common.util.Location
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.Playable
import top.lanscarlos.cheshire.effect.Effect

class EffectArrowTrail(config: ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection): Effect(config) {

    override fun buildEffect(location: Location, entity: Entity?, viewRadius: Double): ParticleObj {
        return ArrowTrail(location, entity, viewRadius)
    }

    inner class ArrowTrail(
        location: Location,
        entity: Entity?,
        viewRadius: Double
    ) : EffectParticleObj(location, entity, buildParticleSpawner(viewRadius)), Playable {

        override fun finish(): Boolean {
            if (entity?.isDead != false) {
                entity?.let { entityEffects.remove(it) }
                return true
            }
            return timeUp()
        }

        override fun show() {
            if (timeUp()) return
            spawnParticle()
        }

        override fun playNextPoint() {
            if (timeUp()) return
            spawnParticle()
        }

        private fun spawnParticle() {
            if (entity?.isDead != false) {
                turnOffTask()
                entity?.let { entityEffects.remove(it) }
            }
            spawnParticle(getLocation())
        }
    }

}