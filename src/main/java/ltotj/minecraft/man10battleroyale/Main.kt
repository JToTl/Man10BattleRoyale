package ltotj.minecraft.man10battleroyale

import ltotj.minecraft.man10battleroyale.command.MBattleCommand
import ltotj.minecraft.man10battleroyale.command.MBattleEditCommand
import ltotj.minecraft.man10battleroyale.command.MBattleOPCommand
import ltotj.minecraft.man10battleroyale.data.EditField
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class Main : JavaPlugin() {


    companion object{
        var battleRoyale:Man10BattleRoyale?=null
        lateinit var plugin: JavaPlugin
        const val pluginTitle="§7§l[§6Man10Battle§7§l]§r";
        var editField: EditField?=null
        val participationHistory=HashMap<UUID,Int>()
        var maxParticipationTimes=99
    }

    override fun onEnable() {
        plugin=this
        saveDefaultConfig()
        MBattleCommand(this, pluginTitle)
        MBattleOPCommand(this, pluginTitle)
        MBattleEditCommand(this, pluginTitle)
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}