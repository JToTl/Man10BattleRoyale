package ltotj.minecraft.man10battleroyale.event

import ltotj.minecraft.man10battleroyale.data.EditField
import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.ItemManager.ItemStackPlus
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class OnEditEvent(val editField: EditField):Listener{

    init {
        Main.plugin.server.pluginManager.registerEvents(this, Main.plugin)
    }

    fun unregister(){
        HandlerList.unregisterAll(this)
    }

    /////////////////////////
    //tierチェスト設置
    @EventHandler
    fun onPlace(e:BlockPlaceEvent){
        val tier=ItemStackPlus(e.itemInHand).getNBTInt("EditTier",Main.plugin)
        if(tier>-1){
            editField.setLootChest(e.block.location,tier)
            e.block.type= Material.CHEST
        }
    }
    //tierチェスト設置
    /////////////////////////

    /////////////////////////
    //tierチェスト削除
    @EventHandler
    fun onBreak(e:BlockBreakEvent){
        if(e.block.type==Material.CHEST&&ItemStackPlus(e.player.inventory.itemInMainHand).getNBTInt("DeleteItem",Main.plugin)==1){
            editField.removeLootChest(e.block.location)
        }
    }
    //tierチェスト削除
    /////////////////////////


}