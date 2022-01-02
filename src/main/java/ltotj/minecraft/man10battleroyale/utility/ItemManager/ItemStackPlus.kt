package ltotj.minecraft.man10battleroyale.utility.ItemManager

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

open class ItemStackPlus:ItemStack{

    constructor(material: Material, amount:Int):super(material,amount)

    constructor(item: ItemStack):super(item)


    fun addLore(lore:MutableList<String>): ItemStackPlus {
        val meta=this.itemMeta
        val newLore=meta.lore()?: ArrayList()
        for(str in lore){
            newLore.add(Component.text(str))
        }
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun addLore(lore:String): ItemStackPlus {
        val meta=this.itemMeta
        val newLore=meta.lore()?: ArrayList()
        newLore.add(Component.text(lore))
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun setItemAmount(amount:Int): ItemStackPlus {
        this.amount=amount
        return this
    }

    fun setCustomModelData(id:Int): ItemStackPlus {
        val meta=this.itemMeta
        meta.setCustomModelData(id)
        this.itemMeta=meta
        return this
    }

    fun setItemLore(lore:MutableList<String>): ItemStackPlus {
        val meta=this.itemMeta
        val newLore=ArrayList<Component>()
        for(str in lore){
            newLore.add(Component.text(str))
        }
        meta.lore(newLore)
        this.itemMeta=meta
        return this
    }

    fun setDisplay(name:String): ItemStackPlus {
        val meta=this.itemMeta
        meta.displayName(Component.text(name))
        this.itemMeta=meta
        return this
    }

    fun setMaterial(material:Material): ItemStackPlus {
        this.type=material
        return this
    }

    fun setNBTInt(namespacedKey: String,value:Int,plugin: JavaPlugin): ItemStackPlus {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER,value)
        itemMeta=meta
        return this
    }

    fun setNBTLong(namespacedKey: String,value:Long,plugin: JavaPlugin): ItemStackPlus {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.LONG,value)
        itemMeta=meta
        return this
    }

    fun setNBTString(namespacedKey: String,value:String,plugin: JavaPlugin): ItemStackPlus {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.STRING,value)
        itemMeta=meta
        return this
    }

    fun setNBTDouble(namespacedKey: String,value:Double,plugin: JavaPlugin): ItemStackPlus {
        val meta=itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.DOUBLE,value)
        itemMeta=meta
        return this
    }

    fun getNBTInt(namespacedKey:String,plugin: JavaPlugin):Int{
        val meta=itemMeta?:return -1
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER]?:-1
    }

    fun getNBTLong(namespacedKey:String,plugin: JavaPlugin):Long{
        val meta=itemMeta?:return -1L
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.LONG]?:-2L
    }

    fun getNBTString(namespacedKey:String,plugin: JavaPlugin):String{
        val meta=itemMeta?:return ""
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.STRING]?:""
    }

    fun getNBTDouble(namespacedKey: String,plugin: JavaPlugin):Double{
        val meta=itemMeta?:return 0.0
        return meta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.DOUBLE]?:0.0
    }

}