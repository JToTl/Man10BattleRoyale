package ltotj.minecraft.man10battleroyale.data

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.event.OnEditEvent
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

class EditField(val config:ConfigManager) {


    val event=OnEditEvent(this)
    val area= Bukkit.getWorld(config.getString("world") ?: "world")!!
    private val lootChestPos=HashMap<Int, ArrayList<Location>>()

    fun loadChestPos(){
        lootChestPos.clear()
        for(key in config.getKeys("chestPosition", false)){
            val tier=config.getInt("chestPosition.${key}.Tier")
            val locKey=key.split("|")
            val location=Location(area, locKey[0].toDoubleOrNull() ?: 0.0, locKey[1].toDoubleOrNull()
                    ?: 0.0, locKey[2].toDoubleOrNull() ?: 0.0)
            if(!lootChestPos.containsKey(tier)){
                lootChestPos[tier]= ArrayList()
            }
            lootChestPos[tier]?.add(location)
        }
    }

    fun setLootChest(location: Location, tier:Int){
        config.setValue("chestPosition.${location.blockX}|${location.blockY}|${location.blockZ}.Tier",tier)
        if(!lootChestPos.containsKey(tier)){
            lootChestPos[tier]= ArrayList()
        }
        lootChestPos[tier]?.add(location)
    }

    fun setAllLootChestOnArea(){
        for(key in lootChestPos.keys){
            setLootChestOnArea(key)
        }
    }

    fun setLootChestOnArea(tier:Int){
        config.load()
        loadChestPos()
        if(lootChestPos.containsKey(tier)){
            for(pos in lootChestPos[tier]!!){
                pos.block.type= Material.CHEST
            }
        }
    }

    fun removeLootChest(location: Location){
        config.setValue("chestPosition.${location.blockX}|${location.blockY}|${location.blockZ}",null)
    }

    fun removeAllLootChestOnArea(){
        for(key in lootChestPos.keys){
            removeLootChestOnArea(key)
        }
    }

    fun removeLootChestOnArea(tier:Int){
        if(lootChestPos.containsKey(tier)){
            for(pos in lootChestPos[tier]!!){
                pos.block.type= Material.AIR
            }
        }
    }

    fun countLootChest(tier:Int):Int{
        loadChestPos()
        return if(lootChestPos.containsKey(tier))lootChestPos[tier]!!.size else 0
    }

    fun countAllLootChest():Int{
        var count=0
        loadChestPos()
        for(pos in lootChestPos.values){
            count+=pos.size
        }
        return count
    }

    fun end(){
        removeAllLootChestOnArea()
        event.unregister()
        config.save()
        Main.editField =null
    }


}