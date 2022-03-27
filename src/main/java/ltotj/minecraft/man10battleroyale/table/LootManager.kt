package ltotj.minecraft.man10battleroyale.table

import ltotj.minecraft.man10battleroyale.Man10BattleRoyale
import ltotj.minecraft.man10battleroyale.data.FieldData
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager

class LootManager(val lootFolderName:String,val fieldData: FieldData){


    val lootTables=HashMap<Int,LootTable>()
    val lootChests=ArrayList<LootChest>()
    var generatedChest=false

    init {
        for(tier in 1..fieldData.maxTier){
            lootTables[tier]=LootTable(this,tier)
            for(loc in fieldData.lootChestPos[tier]?:continue){
                lootChests.add(LootChest(loc,lootTables[tier]!!))
            }
        }
    }

    fun setLootChests(){
        for(lootChest in lootChests){
            lootChest.setChest()
        }
        generatedChest=true
    }

    fun generateContents():Boolean{
        if(!generatedChest)return false
        for(lootChest in lootChests){
            lootChest.generateContents()
        }
        return true
    }



}