package top.lanscarlos.cheshire.item

import ink.ptms.zaphkiel.api.Item
import ink.ptms.zaphkiel.api.ItemEvent
import ink.ptms.zaphkiel.api.ItemStream
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture

/**
 * 触发脚本
 *
 * 传入事件的各种数据对象
 * 以便 Kether 能够使用
 * @param data 数据对象集合
 * */
fun Item.invokeScript(key: String, player: Player?, event: Event, itemStream: ItemStream, namespace: String = "zaphkiel-internal", data: Map<String, Any> = mapOf()): CompletableFuture<ItemEvent.ItemResult?>? {
    val itemEvent = eventMap[key] ?: return null
    if (itemEvent.isCancelled && event is Cancellable) {
        event.isCancelled = true
    }
    return itemEvent.invoke(player, event, itemStream, mapOf(*eventData.toList().toTypedArray(), *data.toList().toTypedArray()), namespace)
}