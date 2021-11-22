package top.lanscarlos.plugins

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object Cheshire : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}