package ltotj.minecraft.man10battleroyale.command

import ltotj.minecraft.man10battleroyale.data.EditField
import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandArgumentType
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandManager
import ltotj.minecraft.man10battleroyale.utility.CommandManager.CommandObject
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import ltotj.minecraft.man10battleroyale.utility.ItemManager.ItemStackPlus
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MBattleEditCommand(plugin: JavaPlugin, pluginTitle: String) : CommandManager(plugin, "mbredit", pluginTitle) {


    fun exitEditedFile(sender: CommandSender):Boolean{
        if(Main.editField==null){
            sender.sendMessage("${Main.pluginTitle}§4設定するファイルがセットされていません")
            return false
        }
        return true
    }

    init {
        setPermission("mbattle.op")

        addFirstArgument(
                CommandObject("chest")
                        .addNextArgument(
                                CommandObject("visible")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.INT)
                                                        .setComment("tier")
                                                        .setExplanation("指定Tierチェストを可視化")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                Main.editField!!.setLootChestOnArea(it.second[2].toInt())
                                                                it.first.sendMessage("${Main.pluginTitle}§cTier${it.second[2]}§aチェストを表示しました")
                                                            }
                                                        }
                                        )
                                        .addNextArgument(
                                                CommandObject("all")
                                                        .setExplanation("全てのチェストを可視化")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                Main.editField!!.setAllLootChestOnArea()
                                                                it.first.sendMessage("${Main.pluginTitle}§a全てのTierチェストを表示しました")
                                                            }
                                                        }
                                        )
                        )
                        .addNextArgument(
                                CommandObject("invisible")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.INT)
                                                        .setComment("tier")
                                                        .setExplanation("指定Tierチェストを非可視化")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                Main.editField!!.removeLootChestOnArea(it.second[2].toInt())
                                                                it.first.sendMessage("${Main.pluginTitle}§cTier${it.second[2]}§aチェストを非表示にしました")
                                                            }
                                                        }
                                        )
                                        .addNextArgument(
                                                CommandObject("all")
                                                        .setExplanation("全てのTierチェストを非可視化")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                Main.editField!!.removeAllLootChestOnArea()
                                                                it.first.sendMessage("${Main.pluginTitle}§a全てのTierチェストを非表示にしました")
                                                            }
                                                        }
                                        )
                        )
                        .addNextArgument(
                                CommandObject("get")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.INT)
                                                        .setComment("tier")
                                                        .setExplanation("指定tierチェスト設置用のアイテムを取得")
                                                        .setFunction{
                                                            val sender=it.first
                                                            if(sender is Player){
                                                                if(!sender.inventory.itemInMainHand.type.isBlock||sender.inventory.itemInMainHand.type==Material.AIR){
                                                                    sender.inventory.setItemInMainHand(
                                                                            ItemStackPlus(Material.CHEST,1)
                                                                                    .setNBTInt("EditTier",it.second[2].toInt(),Main.plugin)
                                                                                    .setDisplay("§cTier${it.second[2]}チェスト")
                                                                    )
                                                                }
                                                                else{
                                                                    sender.inventory.setItemInMainHand(
                                                                            ItemStackPlus(sender.inventory.itemInMainHand)
                                                                                    .setNBTInt("EditTier",it.second[2].toInt(),Main.plugin)
                                                                                    .setDisplay("§cTier${it.second[2]}チェスト")
                                                                    )
                                                                }
                                                            }
                                                        }
                                        )
                        )
                        .addNextArgument(
                                CommandObject("count")
                                        .addNextArgument(
                                                CommandObject(CommandArgumentType.INT)
                                                        .setComment("tier")
                                                        .setExplanation("指定tierチェストの数を表示")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                it.first.sendMessage("${Main.pluginTitle}§cTier${it.second[2]}§aのチェストの個数は§b${Main.editField!!.countLootChest(it.second[2].toInt())}個§aです")
                                                            }
                                                        }
                                        )
                                        .addNextArgument(
                                                CommandObject("all")
                                                        .setExplanation("tierチェストの総数を表示")
                                                        .setFunction{
                                                            if(exitEditedFile(it.first)){
                                                                it.first.sendMessage("${Main.pluginTitle}Tierチェストの総数は§b${Main.editField!!.countAllLootChest()}個§aです")
                                                            }
                                                        }
                                        )
                        )
                        .addNextArgument(
                                CommandObject("delete")
                                        .setFunction {
                                            val sender = it.first
                                            if (sender is Player) {
                                                if (sender.inventory.itemInMainHand.type == Material.AIR) {
                                                    sender.inventory.setItemInMainHand(
                                                            ItemStackPlus(Material.IRON_INGOT, 1)
                                                                    .setNBTInt("DeleteItem", 1, Main.plugin)
                                                                    .setDisplay("§cTierチェスト削除用アイテム")
                                                                    .setItemLore(mutableListOf("これを持ってチェストを破壊することで削除できます"))
                                                    )
                                                } else {
                                                    sender.inventory.setItemInMainHand(
                                                            ItemStackPlus(sender.inventory.itemInMainHand)
                                                                    .setNBTInt("DeleteItem", 1, Main.plugin)
                                                                    .setDisplay("§cTierチェスト削除用アイテム")
                                                                    .setItemLore(mutableListOf("これを持ってチェストを破壊することで削除できます"))
                                                    )
                                                }
                                            }
                                        }
                        )
        )

        addFirstArgument(
                CommandObject("set")
                        .addNextArgument(
                                CommandObject(CommandArgumentType.STRING)
                                        .setComment("バトロワファイル名")
                                        .setExplanation("ルートチェストを位置を変更するファイルをセット")
                                        .setFunction{
                                            if(Main.battleRoyale!=null){
                                                it.first.sendMessage("${Main.pluginTitle}§aバトルロワイヤルがセットされています")
                                                it.first.sendMessage("${Main.pluginTitle}§ambrop delete を実行してバトルロワイヤルを削除した後に実行してください")
                                                return@setFunction
                                            }
                                            Main.editField= EditField(ConfigManager(Main.plugin,it.second[1]))
                                            it.first.sendMessage("${Main.pluginTitle}§a設定するファイルをセットしました")
                                        }
                        )
        )

        addFirstArgument(
                CommandObject("end")
                        .setExplanation("設定を終える")
                        .setFunction{
                            if(exitEditedFile(it.first)){
                                Main.editField!!.end()
                                it.first.sendMessage("${Main.pluginTitle}§a設定終了")
                            }
                        }
        )
    }


}