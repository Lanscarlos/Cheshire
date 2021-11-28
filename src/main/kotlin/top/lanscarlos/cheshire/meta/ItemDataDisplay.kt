package top.lanscarlos.cheshire.meta

import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import ink.ptms.zaphkiel.taboolib.library.configuration.ConfigurationSection
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.module.chat.colored
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
        e.itemStream.getZaphkielItem().config.getConfigurationSection("meta.data-display")?.let { section ->
            section.getKeys(false).forEach { key ->
                val data = when(val it = section.get("$key.key") ?: "null") {
                    is List<*> -> {
                        if (it.size > 0) Pair(
                            e.itemStream.getZaphkielData().getDeep(it[0].toString()) ?: null,
                            if (it.size >= 2) e.itemStream.getZaphkielData().getDeep(it[1].toString()) ?: null else null
                        ) else Pair(e.itemStream.getZaphkielData().getDeep(it.toString()), null)
                    }
                    else -> Pair(e.itemStream.getZaphkielData().getDeep(it.toString()), null)
                }
                val display = section.getString("$key.display", dataDisplay!!).also {
                    if (it == "none") return@forEach
                }
                val displaySymbol = section.getConfigurationSection("$key.display-symbol")?.let {
                    listOf(it.getString("0", dataSymbol!![0]), it.getString("1", dataSymbol!![1]))
                } ?: dataSymbol!!
                val scale = section.getInt("scale", -1)

                // 显示的数据
                if (data.first == null) return@forEach
                val value = if (data.second == null) {
                    display.replace("%value%", data.first!!.asString())
                }else {
                    createBar(data.first!!.asInt(), data.second!!.asInt(), display, displaySymbol, scale)
                }

                e.addName(key, value)
                e.addLore(key, value)
            }
        }
    }


    @SubscribeEvent
    fun e(e: PluginReloadEvent.Item) {
        dataDisplay = Cheshire.conf.getString("Data.display")
        dataSymbol = arrayListOf(Cheshire.conf.getString("Data.display-symbol.0"), Cheshire.conf.getString("Data.display-symbol.1"))
    }
}
