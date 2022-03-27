package ltotj.minecraft.man10battleroyale.table

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import kotlin.random.Random

class LootTable(val lootManager: LootManager,val tier:Int){


    val lootItems=ArrayList<LootItem>()
    var totalWeight=0L
    val config=ConfigManager(Main.plugin,"tier$tier","LootTables/${lootManager.lootFolderName}").getConfig()
    val minItems=config.getInt("minItems")
    val maxItems=config.getInt("maxItems")

    init {
        for(map in config.get("items") as List<Map<String,*>>){
            lootItems.add(LootItem((map["item"] as String).split(","),map["weight"] as Long))
            totalWeight+=map["weight"] as Long
        }
    }

    fun randomLootItem():LootItem?{
        var random= Random.nextLong(totalWeight)
        for(item in lootItems){
            random-=item.weight
            if(random<0L)return item
        }
        return null
    }




}