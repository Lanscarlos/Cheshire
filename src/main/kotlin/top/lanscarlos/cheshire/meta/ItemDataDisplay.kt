package top.lanscarlos.cheshire.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import top.lanscarlos.cheshire.Cheshire

/**
 * @author Lanscarlos
 * @since 2021-11-26 14:52
 */
internal object ItemDataDisplay {

    private var dataDisplay: String? = null
    private var dataSymbol: List<String>? = null

    fun createBar(current: Int, max: Int, display: String = dataDisplay!!, symbol: List<String> = dataSymbol!!, scale: Int = -1): String {
        val percent = Coerce.format((current / max.toDouble()) * 100).toString()
        return if (scale == -1) {
            display.replace("%symbol%", (1..max).joinToString("") { i ->
                if (current >= i) "§f${symbol.getOrElse(0) { "" }}" else "§7${symbol.getOrElse(1) { "" }}"
            })
        } else {
            display.replace("%symbol%", taboolib.common5.util.createBar(
                "§7${symbol.getOrElse(1) { "" }}",
                "§f${symbol.getOrElse(0) { "" }}",
                scale,
                current / max.toDouble()
            ))
        }.replace("%current%", current.toString()).replace("%max%", max.toString()).replace("%percent%", percent)
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        e.itemStream.getZaphkielItem().config.getMapList("meta.data-display")?.forEach { map ->
            val name = map["name"]?.toString() ?: return@forEach
            val current = e.itemStream.getZaphkielData().getDeep(map["key-current"]?.toString() ?: map["key"]?.toString() ?: return@forEach) ?: return@forEach
            val display = map["display"]?.toString() ?: dataDisplay!!
            if (display == "none") {
                return@forEach
            }

            val max = map["key-max"]?.let { e.itemStream.getZaphkielData().getDeep(it.toString()) ?: current }
            val displaySymbol = if (map["display-symbol"] != null) {
                listOf((map["display-symbol"] as Map<*, *>).let { it["0"]?.toString() ?: it[0].toString() }, (map["display-symbol"] as Map<*, *>).let { it["1"]?.toString() ?: it[1].toString() })
            }else {
                dataSymbol!!
            }
            val scale = map["scale"]?.toString()?.toInt() ?: -1

            val value = if (max != null) {
                // bar类型
                createBar(current.asInt(), max.asInt(), display, displaySymbol, scale)
            }else {
                display.replace("%value%", current.asString())
            }

            e.addName(name, value)
            e.addLore(name, value)

        }
    }


    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        dataDisplay = Cheshire.conf.getString("Data.display")
        dataSymbol = arrayListOf(Cheshire.conf.getString("Data.display-symbol.0"), Cheshire.conf.getString("Data.display-symbol.1"))
    }
}