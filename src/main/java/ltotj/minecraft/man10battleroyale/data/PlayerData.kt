package ltotj.minecraft.man10battleroyale.data

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.Man10BattleRoyale
import ltotj.minecraft.man10battleroyale.utility.ItemManager.ItemStackPlus
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.block.Skull
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.inventory.ItemStack
import kotlin.math.*

class PlayerData(val player:Player, private val battle:Man10BattleRoyale){

    var killCounter=0
    var killer:PlayerData?=null
    var alive=true
    var firstSpecTarget:PlayerData?=null
    val restrictedItems=hashMapOf(Pair(Material.IRON_HOE,0))//あとで色々できるようにしたい

    var ship:WitherSkull?=null

    var deathBox=Bukkit.createInventory(null,45, Component.text("§l${player.name}のインベントリ"))
    lateinit var deadLoc:Location
    var blocks:Pair<BlockData,BlockData>?=null

    private fun getSpecTargetData():PlayerData?{
        if(battle.livingPlayers.isEmpty())return null
        if(!alive&&killer==null&&firstSpecTarget==null)firstSpecTarget=battle.getRandomLivPl()
        return if (alive) this else firstSpecTarget!!.getSpecTargetData()
    }

    fun getDistanceToAreaAndCenter():Pair<Double,Double>{
        val pLoc=player.location
        val toCenter=round(10* sqrt((pLoc.x - battle.field.nextCenter[0]).pow(2)+(pLoc.z - battle.field.nextCenter[1]).pow(2)))/10
        val l= max(abs(pLoc.x-battle.field.nextCenter[0]), abs(pLoc.z-battle.field.nextCenter[1]))
        return Pair(if(l<=battle.field.nextWidth)0.0 else round(10*toCenter*(l-battle.field.nextWidth)/l)/10,toCenter)
    }

    fun death(killer:Player?){
        battle.deadPlayers[player.uniqueId]=battle.playerList[player.uniqueId]!!
        battle.livingPlayers.remove(player.uniqueId)
        alive=false
        this.killer=battle.playerList[killer?.uniqueId]
        if(this.killer!=null){
            this.killer!!.killCounter++
        }
        if(battle.livingPlayers.containsKey(killer?.uniqueId)){
            firstSpecTarget=this.killer
        }
        deadLoc=player.location.clone()
        setDeathBox()
        battle.checkEnd()
    }

    fun setSpecTarget(){
        Bukkit.getPlayer(player.uniqueId)?.spectatorTarget=getSpecTargetData()?.player
    }

    fun setDeathBox(){
        val deadLoc2=deadLoc.clone().add(0.0,1.0,0.0)
        blocks=Pair(deadLoc.block.blockData,deadLoc2.block.blockData)
        deathBox.contents=player.inventory.contents
        player.inventory.clear()
        deadLoc.block.type=Material.CHEST
        val chest=deadLoc.block.state as Chest
        chest.customName=player.uniqueId.toString()
        chest.update()
        deadLoc2.block.type=Material.PLAYER_HEAD
        val skull=deadLoc2.block.state as Skull
        skull.setOwningPlayer(player)
        skull.update()
    }

    fun removeDeathBox(){
        if(blocks==null)return
        deadLoc.block.blockData=blocks!!.first
        deadLoc.clone().add(0.0,1.0,0.0).block.blockData=blocks!!.second
    }

    fun canPickUp(item:ItemStack):Boolean{
        if(restrictedItems.keys.contains(item.type)){
            return if(restrictedItems[item.type]!!+1<=battle.restrictedItemsMax[item.type]!!){
                restrictedItems[item.type]= restrictedItems[item.type]!! +1
                true
            } else false
        }
        return true
    }

    fun initializePlStatus(){
        player.inventory.clear()
        player.gameMode=GameMode.ADVENTURE
        player.activePotionEffects.clear()
        player.health=20.0
        player.saturation=20F
        player.foodLevel=20
        player.inventory.chestplate=ItemStackPlus(Material.ELYTRA,1)
                .setNBTInt("ShipElytra",1, Main.plugin)
    }


}