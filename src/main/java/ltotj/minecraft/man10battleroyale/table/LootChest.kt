package ltotj.minecraft.man10battleroyale.table

import ltotj.minecraft.man10battleroyale.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Chest
import org.bukkit.persistence.PersistentDataType
import java.util.*

class LootChest(val location:Location,val table:LootTable){

    val chestUUID=UUID.randomUUID()
    var chest:Chest?=null


    fun generateContents(){
        if(chest==null)return
        val amount=Random().nextInt(table.maxItems-table.minItems)+table.minItems
        var itemCount=0

        for(i in 0 until amount){
            val lootItem=table.randomLootItem()?:continue
            itemCount+=lootItem.fillSlot
            if(itemCount>27) break
            for(item in lootItem.items){
                chest!!.inventory.addItem(item)
            }
        }
        chest!!.inventory.contents.random()
        chest!!.update()
    }

    fun setChest(){
        location.block.type= Material.CHEST
        chest=location.block.state as Chest
        chest!!.persistentDataContainer.set(NamespacedKey(Main.plugin,"chestUUID"), PersistentDataType.STRING,chestUUID.toString())
        chest!!.update()
    }

}