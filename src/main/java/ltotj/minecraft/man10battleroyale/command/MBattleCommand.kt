package ltotj.minecraft.man10battleroyale.command

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandManager
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandObject
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MBattleCommand(plugin: JavaPlugin,pluginTitle:String) :CommandManager(plugin,"mbat",pluginTitle){


    init {

        setPermission("mbattle.player")

        addFirstArgument(
                CommandObject("join")
                        .setFunction{
                            val sender=it.first
                            if(Main.battleRoyale==null){
                                sender.sendMessage("${Main.pluginTitle}§4現在バトロワは開催されていません")
                                return@setFunction
                            }
                            if(sender !is Player){
                                sender.sendMessage("${Main.pluginTitle}§4このコマンドはプレイヤーのみ実行可能です")
                                return@setFunction
                            }
                            if(!Main.battleRoyale!!.joinPlayer(sender as Player)){
                                sender.sendMessage("${Main.pluginTitle}§4既に参加しているか、参加可能期間を過ぎています")
                                return@setFunction
                            }
                            if(Main.participationHistory[sender.uniqueId]?:0>=Main.maxParticipationTimes){
                                sender.sendMessage("${Main.pluginTitle}§4参加可能な回数を超過しています")
                                return@setFunction
                            }
                            sender.sendMessage("${Main.pluginTitle}§a参加登録完了")
                        }
        )
    }
}