package ltotj.minecraft.man10battleroyale.utility.GUIManager.menu

import ltotj.minecraft.man10battleroyale.utility.GUIManager.GUIItem
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

open class TrueOrFalseGUI: MenuGUI {

    constructor(plugin: JavaPlugin, title:String):super(plugin, 3, title)

    constructor(plugin: JavaPlugin, title:String, parent: MenuGUI, key:String, isInstant:Boolean):super(plugin, 3, title, parent, key,isInstant)

    private var function:Consumer<ClickEventData>?=null

    inner class ClickEventData(val boolean: Boolean,val event:InventoryClickEvent)

    init {

        setItems(arrayOf(0,1,2,9,10,11,18,19,20), GUIItem(Material.RED_WOOL,1)
                .setDisplay("§c§lfalse"))
        setItems(arrayOf(3,4,5,12,14,21,22,23), GUIItem(Material.BLUE_STAINED_GLASS_PANE,1))
        setItems(arrayOf(6,7,8,15,16,17,24,25,26), GUIItem(Material.LIME_WOOL,1)
                .setDisplay("§a§ltrue"))

        setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
            if(function!=null) {
                when (inventoryClickEvent.slot) {
                    0, 1, 2, 9, 10, 11, 18, 19, 20 ->function!!.accept(ClickEventData(false,inventoryClickEvent))
                    6,7,8,15,16,17,24,25,26 ->function!!.accept(ClickEventData(true,inventoryClickEvent))
                }
            }
        }
    }

    fun setTrueItem(item: GUIItem): TrueOrFalseGUI {
        setItems(arrayOf(6,7,8,15,16,17,24,25,26),item)
        return this
    }

    fun setFalseItem(item: GUIItem): TrueOrFalseGUI {
        setItems(arrayOf(0,1,2,9,10,11,18,19,20),item)
        return this
    }

    fun setOutput(action:Consumer<ClickEventData>): TrueOrFalseGUI {
        function=action
        return this
    }

}