package top.lanscarlos.cheshire

import ink.ptms.zaphkiel.Zaphkiel
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

/**
 * @author Lanscarlos
 * @since 2021-11-15 20:09
 */
object Cheshire : Plugin() {

    @Config
    lateinit var conf: SecuredFile
        private set

    override fun onEnable() {
        if (Zaphkiel.conf.getBoolean("Database.enable")) {
            setupPlayerDatabase(conf.getConfigurationSection("Database"), "${conf.getString("prefix")}_2")
        } else {
            setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        }
        info("Successfully enable Cheshire!")
    }
}