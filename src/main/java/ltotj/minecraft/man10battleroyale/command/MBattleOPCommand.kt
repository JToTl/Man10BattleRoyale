package ltotj.minecraft.man10battleroyale.command

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.Man10BattleRoyale
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandArgumentType
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandManager
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandObject
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class MBattleOPCommand(plugin: JavaPlugin, pluginTitle: String) :CommandManager(plugin, "mbrop", pluginTitle) {

    fun exitBattleRoyale(sender:CommandSender):Boolean{
        if(Main.battleRoyale==null){
            sender.sendMessage("${Main.pluginTitle}§4バトロワがセットされていません")
            return false
        }
        return true
    }

    init {

        setPermission("mbattle.op")

        addFirstArgument(
                CommandObject("set")
                        .addNextArgument(
                                CommandObject(CommandArgumentType.STRING)
                                        .setComment("バトロワファイル名")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.STRING)
                                                        .setComment("ルートチェスト名")
                                                        .setExplanation("バトロワをセット")
                                                        .setFunction{
                                                            if(Main.battleRoyale==null){
                                                                Main.battleRoyale= Man10BattleRoyale(it.second[1],it.second[2])
                                                                it.first.sendMessage("${Main.pluginTitle}§aバトロワをセットしました")
                                                            }
                                                            else{
                                                                it.first.sendMessage("${Main.pluginTitle}§4既にセットされています")
                                                            }
                                                            return@setFunction
                                                        }
                                        )
                        )
        )

        addFirstArgument(
                CommandObject("start")
                        .setExplanation("バトルロワイヤルを開始")
                        .setFunction{
                            if (exitBattleRoyale(it.first)) {
                                if (!Main.battleRoyale!!.preStart()) {
                                    it.first.sendMessage("${Main.pluginTitle}§4既に開始されています")
                                    return@setFunction
                                }
                                Main.battleRoyale!!.generateLootChest()
                            }
                        }
        )

        addFirstArgument(
                CommandObject("forceStart")
                        .setExplanation("バトルロワイヤルを即座に開始")
                        .setFunction {
                            if (exitBattleRoyale(it.first)) {
                                if(Main.battleRoyale!!.preStart){
                                    it.first.sendMessage("${Main.pluginTitle}§4既に開始されています")
                                    return@setFunction
                                }
                                if (!Main.battleRoyale!!.start()) {
                                    it.first.sendMessage("${Main.pluginTitle}§4既に開始されています")
                                    return@setFunction
                                }
                                it.first.sendMessage("${Main.pluginTitle}§aバトロワを開始しました")
                            }
                        }
        )

        addFirstArgument(
                CommandObject("generateChest")
                        .setExplanation("チェストを生成")
                        .setFunction {
                            if (exitBattleRoyale(it.first)) {
                                if (Main.battleRoyale!!.generateLootChest()) {
                                    it.first.sendMessage("${Main.pluginTitle}§aチェストを生成しました")
                                    return@setFunction
                                }
                                it.first.sendMessage("${Main.pluginTitle}§a生成済みです")
                            }
                        }
        )

        addFirstArgument(
                CommandObject("delete")
                        .setExplanation("現在のバトロワを削除")
                        .setFunction {
                            if (exitBattleRoyale(it.first)) {
                                Main.battleRoyale!!.delete()
                                it.first.sendMessage("${Main.pluginTitle}§a削除しました")
                            }
                        }
        )

        addFirstArgument(
                CommandObject("cancel")
                        .setExplanation("バトロワを強制終了")
                        .setFunction {
                            if (exitBattleRoyale(it.first)) {
                                Main.battleRoyale!!.stop()
                                it.first.sendMessage("${Main.pluginTitle}§a強制終了しました")
                            }
                        }
        )

        addFirstArgument(
                CommandObject("joinAll")
                        .setExplanation("OP以外強制参加")
                        .setFunction{
                            if(exitBattleRoyale(it.first)) {
                                if (!Main.battleRoyale!!.isRunning) {
                                    for (player in Bukkit.getOnlinePlayers()) {
                                        if (!player.hasPermission("mbattle.op")&&Main.participationHistory[player.uniqueId]?:0<Main.maxParticipationTimes) {
                                            Main.battleRoyale!!.joinPlayer(player)
                                            player.sendMessage("${Main.pluginTitle}§a参加登録完了")
                                        }
                                    }
                                }
                                else{
                                    it.first.sendMessage("${Main.pluginTitle}§a既に開始されています")
                                }
                            }
                        }
        )

        addFirstArgument(
                CommandObject("freeSpec")
                        .addNextArgument(
                                CommandObject(CommandArgumentType.BOOLEAN)
                                        .setExplanation("自由に観戦できるかどうかを設定")
                                        .setFunction{
                                            if(Main.battleRoyale==null){
                                                it.first.sendMessage("${Main.pluginTitle}§4バトロワがセットされていません")
                                                return@setFunction
                                            }
                                            val boolean=it.second[1].toBoolean()
                                            if(boolean){
                                                if(Main.battleRoyale!!.canSpecMovement){
                                                    it.first.sendMessage("${Main.pluginTitle}§a既にtrueです")
                                                }
                                                else{
                                                    it.first.sendMessage("${Main.pluginTitle}§atrueに設定しました")
                                                    Main.battleRoyale!!.canSpecMovement=true
                                                }
                                            }
                                            else{
                                                if(Main.battleRoyale!!.canSpecMovement){
                                                    it.first.sendMessage("${Main.pluginTitle}§afalseに設定しました")
                                                    Main.battleRoyale!!.canSpecMovement=false
                                                }
                                                else{
                                                    it.first.sendMessage("${Main.pluginTitle}§a既にfalseです")
                                                }
                                            }
                                        }
                        )
        )
        addFirstArgument(
                CommandObject("max")
                        .setNullable(true)
                        .setFunction{
                            it.first.sendMessage("${Main.pluginTitle}§a現在の最大参加可能回数は§b${Main.maxParticipationTimes}回§aです")
                        }
                        .addNextArgument(
                                CommandObject(CommandArgumentType.INT)
                                        .setExplanation("参加可能回数の確認・変更")
                                        .setComment("参加可能回数")
                                        .setFunction{
                                            val times=it.second[1].toInt()
                                            if(times<1){
                                                it.first.sendMessage("${Main.pluginTitle}§a1以上のを入力してください")
                                                return@setFunction
                                            }
                                            Main.maxParticipationTimes=times
                                            it.first.sendMessage("${Main.pluginTitle}§a最大参加可能回数を§b${Main.maxParticipationTimes}回§aに設定しました")
                                        }
                        )
        )

        addFirstArgument(
                CommandObject("list")
                        .setExplanation("参加者の一覧を表示")
                        .setFunction{
                            if(exitBattleRoyale(it.first)){
                                it.first.sendMessage("${Main.pluginTitle}§a参加者は以下の通りです")
                                for(data in Main.battleRoyale!!.playerList.values){
                                    it.first.sendMessage(data.player.name)
                                }
                                it.first.sendMessage("${Main.pluginTitle}§a計§c${Main.battleRoyale!!.playerList.size}名")
                            }
                        }
        )

        addFirstArgument(
                CommandObject("debug")
                        .setExplanation("デバッグ用")
                        .addNextArgument(
                                CommandObject("setSpecTarget")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.ONLINE_PlAYER)
                                                        .setComment("プレイヤー名")
                                                        .setFunction{
                                                            if(exitBattleRoyale(it.first)) {
                                                                if (Main.battleRoyale!!.deadPlayers.containsKey(Bukkit.getPlayerUniqueId(it.second[2])?:return@setFunction)) {
                                                                    Main.battleRoyale!!.deadPlayers[Bukkit.getPlayerUniqueId(it.second[2])!!]!!.setSpecTarget()
                                                                    it.first.sendMessage("${Main.pluginTitle}§aセットしました")
                                                                }
                                                                else{
                                                                    it.first.sendMessage("${Main.pluginTitle}§aプレイヤーが見つかりません")
                                                                }
                                                            }
                                                        }
                                        )
                        )
        )
    }
}