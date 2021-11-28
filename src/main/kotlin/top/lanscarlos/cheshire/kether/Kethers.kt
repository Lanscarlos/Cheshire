package top.lanscarlos.cheshire.kether

import ink.ptms.zaphkiel.api.ItemStream
import taboolib.module.kether.ScriptFrame

fun ScriptFrame.itemStream(): ItemStream {
    return variables().get<Any?>("@ItemStream").orElse(null) as? ItemStream ?: error("No item-stream selected.")
}