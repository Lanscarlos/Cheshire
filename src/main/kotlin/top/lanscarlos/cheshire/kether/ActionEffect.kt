package top.lanscarlos.cheshire.kether

import top.lanscarlos.cheshire.effect.getItemEffect
import org.bukkit.entity.Entity
import taboolib.common.platform.function.warning
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.type.BukkitPlayer
import taboolib.platform.util.toProxyLocation
import java.lang.Exception
import java.util.concurrent.CompletableFuture

object ActionEffect {

    class ShowEffect(val key: ParsedAction<*>, val target: ParsedAction<*>, val viewRadius: Double, val always: Boolean) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(key).run<Any>().thenApply { key ->
                frame.newFrame(target).run<Any>().thenApply { target ->
                    frame.itemStream().getItemEffect().getEffect(key.toString())?.let { effect ->
                        when (target) {
                            is Entity -> {
                                effect.show(target, viewRadius, always)
                            }
                            is BukkitPlayer -> {
                                effect.show(target.player, viewRadius, always)
                            }
                            is org.bukkit.Location -> {
                                effect.show(target.toProxyLocation(), viewRadius, always)
                            }
                            is taboolib.common.util.Location -> {
                                effect.show(target, viewRadius, always)
                            }
                            else -> {
                                warning("Cannot found effect target!")
                            }
                        }
                    } ?: warning("Cannot found ${frame.itemStream().getZaphkielName()}'s effect $key ")
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class PlayEffect(val key: ParsedAction<*>, val target: ParsedAction<*>, val viewRadius: Double, val always: Boolean) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(key).run<Any>().thenApply { key ->
                frame.newFrame(target).run<Any>().thenApply { target ->
                    frame.itemStream().getItemEffect().getEffect(key.toString())?.let { effect ->
                        when (target) {
                            is Entity -> {
                                effect.play(target, viewRadius, always)
                            }
                            is BukkitPlayer -> {
                                effect.play(target.player, viewRadius, always)
                            }
                            is org.bukkit.Location -> {
                                effect.play(target.toProxyLocation(), viewRadius, always)
                            }
                            is taboolib.common.util.Location -> {
                                effect.play(target, viewRadius, always)
                            }
                            else -> {
                                warning("Unknown type of effect target!")
                            }
                        }
                    } ?: warning("Cannot found ${frame.itemStream().getZaphkielName()}'s effect $key ")
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class StopEffect(val key: ParsedAction<*>, val target: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(key).run<Any>().thenApply { key ->
                frame.newFrame(target).run<Any>().thenApply { target ->
                    when (target) {
                        is Entity -> {
                            frame.itemStream().getItemEffect().stop(key.toString(), target)
                        }
                        is BukkitPlayer -> {
                            frame.itemStream().getItemEffect().stop(key.toString(), target.player)
                        }
                        else -> {
                            warning("Unknown type of effect target!")
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ReleaseEffect(val key: ParsedAction<*>, val target: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(key).run<Any>().thenApply { key ->
                frame.newFrame(target).run<Any>().thenApply { target ->
                    when (target) {
                        is Entity -> {
                            frame.itemStream().getItemEffect().release(key.toString(), target)
                        }
                        is BukkitPlayer -> {
                            frame.itemStream().getItemEffect().release(key.toString(), target.player)
                        }
                        else -> {
                            warning("Unknown type of effect target!")
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    /**
     * effect show *key on entity/location [ by viewRadius with always ]
     * effect play *key on entity/location [ by viewRadius with always ]
     * effect stop *key on entity/location
     * effect release *key on entity/location
     * effect stop-all entity
     * effect release-all entity
     * */
    @KetherParser(["effect"], namespace = "zaphkiel", shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("show") {
                val key = it.next(ArgTypes.ACTION)
                it.mark()
                it.expect("on")
                val target = it.next(ArgTypes.ACTION)
                var viewRadius = 100.0
                var always = false
                try {
                    it.mark()
                    it.expect("by")
                    viewRadius = it.nextDouble()
                } catch (e: Exception) {
                    it.reset()
                }
                try {
                    it.mark()
                    it.expect("with")
                    always = it.nextToken() == "always"
                } catch (ex: Exception) {
                    it.reset()
                }
                ShowEffect(key, target, viewRadius, always)
            }
            case("play") {
                val key = it.next(ArgTypes.ACTION)
                it.mark()
                it.expect("on")
                val target = it.next(ArgTypes.ACTION)
                var viewRadius = 100.0
                var always = false
                try {
                    it.mark()
                    it.expect("by")
                    viewRadius = it.nextDouble()
                } catch (e: Exception) {
                    it.reset()
                }
                try {
                    it.mark()
                    it.expect("with")
                    always = it.nextToken() == "always"
                } catch (ex: Exception) {
                    it.reset()
                }
                PlayEffect(key, target, viewRadius, always)
            }
            case("stop") {
                val key = it.next(ArgTypes.ACTION)
                it.mark()
                it.expect("on")
                val target = it.next(ArgTypes.ACTION)
                StopEffect(key, target)
            }
            case("release") {
                val key = it.next(ArgTypes.ACTION)
                it.mark()
                it.expect("on")
                val target = it.next(ArgTypes.ACTION)
                ReleaseEffect(key, target)
            }
            case("stop-all") {
                try {
                    it.mark()
                    it.expect("on")
                    val target = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(target).run<Any>().thenApply { target ->
                            if (target is Entity) {
                                itemStream().getItemEffect().stopAll(target)
                            }else {
                                warning("Unknown type of effect target!")
                            }
                        }
                    }
                } catch (e: Exception) {
                    it.reset()
                    actionNow {
                        itemStream().getItemEffect().stopAll()
                    }
                }
            }
            case("release-all") {
                try {
                    it.mark()
                    it.expect("on")
                    val target = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(target).run<Any>().thenApply { target ->
                            if (target is Entity) {
                                itemStream().getItemEffect().releaseAll(target)
                            }else {
                                warning("Unknown type of effect target!")
                            }
                        }
                    }
                } catch (e: Exception) {
                    it.reset()
                    actionNow {
                        itemStream().getItemEffect().releaseAll()
                    }
                }
            }
        }
    }
}