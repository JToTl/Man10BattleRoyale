package ltotj.minecraft.man10battleroyale.utility.GUIManager

import ltotj.minecraft.man10battleroyale.utility.GUIManager.GUIInventory.Companion.guiExecutor
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer

open class GUIItem: ItemStack {

    constructor(material: Material, amount:Int):super(material,amount)

    constructor(item:ItemStack):super(item)

    private val clickEvents=ArrayList<Consumer<InventoryClickEvent>>()
    private val asyncClickEvent= ArrayList<Consumer<InventoryClickEvent>>()
    private var gui: GUIInventory?=null

    private var available=true

    fun addLore(lore:Array<String>): GUIItem {
        val meta=this.itemMeta
        val newLore=meta.lore()?:ArrayList()
        for(str in lore){
            newLore.add(Component.text(str))
        }
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun addLore(lore:String): GUIItem {
        val meta=this.itemMeta
        val newLore=meta.lore()?:ArrayList()
        newLore.add(Component.text(lore))
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun gui(): GUIInventory {
        return gui!!
    }

    fun setGUI(gui: GUIInventory): GUIItem {
        this.gui=gui
        return this
    }

    fun setLore(lore:Array<String>): GUIItem {
        val meta=this.itemMeta
        val newLore=ArrayList<Component>()
        for(str in lore){
            newLore.add(Component.text(str))
        }
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun setDisplay(name:String): GUIItem {
        val meta=this.itemMeta
        meta.displayName(Component.text(name))
        this.itemMeta=meta
        return this
    }

    fun setMaterial(material:Material): GUIItem {
        this.type=material
        return this
    }

    fun setEvent(action: (GUIItem, InventoryClickEvent) -> Unit): GUIItem {
        setEvent(event= {
            action(this, it)
        })
        return this
    }

    fun setEvent(event:Consumer<InventoryClickEvent>): GUIItem {
        clickEvents.add(event)
        return this
    }

    fun setAsyncEvent(action: (GUIItem, InventoryClickEvent) -> Unit): GUIItem {
        setAsyncEvent(event= {
            action(this, it)
        })
        return this
    }

    fun setAsyncEvent(event:Consumer<InventoryClickEvent>): GUIItem {
        asyncClickEvent.add(event)
        return this
    }

    fun setAvailable(boolean:Boolean): GUIItem {
        available=boolean
        return this
    }

    fun executeClickEvent(e:InventoryClickEvent){
        for(event in clickEvents){
            event.accept(e)
        }
        for(event in asyncClickEvent){
            guiExecutor.execute {
                event.accept(e)
            }
        }
    }

    fun setNBTInt(namespacedKey: String,value:Int,plugin:JavaPlugin): GUIItem {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER,value)
        itemMeta=meta
        return this
    }

    fun setNBTLong(namespacedKey: String,value:Long,plugin:JavaPlugin): GUIItem {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.LONG,value)
        itemMeta=meta
        return this
    }

    fun setNBTString(namespacedKey: String,value:String,plugin:JavaPlugin): GUIItem {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.STRING,value)
        itemMeta=meta
        return this
    }

    fun setNBTDouble(namespacedKey: String,value:Double,plugin:JavaPlugin): GUIItem {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.DOUBLE,value)
        itemMeta=meta
        return this
    }

    fun getNBTInt(namespacedKey:String,plugin:JavaPlugin):Int{
        val meta=itemMeta?:return -1
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER]?:-1
    }

    fun getNBTLong(namespacedKey:String,plugin:JavaPlugin):Long{
        val meta=itemMeta?:return -1L
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.LONG]?:-2L
    }

    fun getNBTString(namespacedKey:String,plugin:JavaPlugin):String{
        val meta=itemMeta?:return ""
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.STRING]?:""
    }

    fun getNBTDouble(namespacedKey: String,plugin:JavaPlugin):Double{
        val meta=itemMeta?:return 0.0
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.DOUBLE]?:0.0
    }

    fun reload(){
        if(gui==null)return
        for(slot in gui().items.keys){
            if(gui().items[slot]==this){
                gui().renderItem(slot)
            }
        }
    }

}