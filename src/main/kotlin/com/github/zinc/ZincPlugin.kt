package com.github.zinc;

import com.github.zinc.mybatis.MybatisConfig
import com.github.zinc.command.StatusCommand
import com.github.zinc.core.player.PlayerDAO
import com.github.zinc.command.QuestCommand
import com.github.zinc.container.PlayerContainer
import com.github.zinc.core.quest.QuestDAO
import com.github.zinc.command.TestCommand
import com.github.zinc.core.player.PlayerData
import com.github.zinc.core.recipe.Recipes
import com.github.zinc.front.listener.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin;

class ZincPlugin: JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MybatisConfig.init()

        //QuestManager.registerAllQuestList()

        registerAll(
            ServerListener(),
            PlayerExpListener(),
            PlayerListener(),
            QuestListener(),
            PlayerToolListener(),
            PlayerWorldListener()
        )
        executors(
            "status" to StatusCommand(),
            "test" to TestCommand(),
            "quest" to QuestCommand()
        )
        ServerListener.add("updateAll") {
            if(PlayerContainer.container.isEmpty()) return@add
            info("saving...")
            PlayerDAO().use { PlayerContainer.container.values.map(PlayerData::playerVO).forEach(it::update) }
        }

        QuestDAO().use(QuestDAO::questTimer)

        Recipes.registerAll()
    }

    override fun onDisable() {
        if(PlayerContainer.container.isNotEmpty()) {
            info("saving...")
            PlayerDAO().use { PlayerContainer.container.values.map(PlayerData::playerVO).forEach(it::update) }
        }
    }

    private fun registerAll(vararg listener: Listener) {
        listener.forEach { plugin.server.pluginManager.registerEvents(it, this) }
    }

    private fun executors(vararg executors: Pair<String, CommandExecutor>) {
        executors.forEach { plugin.getCommand(it.first)!!.setExecutor(it.second) }
    }
}

internal lateinit var plugin: JavaPlugin
internal fun info(msg: Any) { plugin.logger.info(msg.toString()) }
internal fun warn(msg: Any) { plugin.logger.warning(msg.toString()) }