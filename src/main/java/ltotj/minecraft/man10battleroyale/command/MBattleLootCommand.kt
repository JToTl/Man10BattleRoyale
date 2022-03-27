package ltotj.minecraft.man10battleroyale.command

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandManager
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandObject
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class MBattleLootCommand(plugin: JavaPlugin, pluginTitle: String) : CommandManager(plugin, "mbrloot", pluginTitle) {

    fun exitEditedFile(sender: CommandSender):Boolean{
        if(Main.editLootTable==null){
            sender.sendMessage("${Main.pluginTitle}§4設定するファイルがセットされていません")
            return false
        }
        return true
    }

    init {

        setPermission("mbattle.op")

        addFirstArgument(
                CommandObject("end")
        )


    }

}