package ltotj.minecraft.man10battleroyale.event

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent
import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.Man10BattleRoyale
import ltotj.minecraft.man10battleroyale.utility.ItemManager.ItemStackPlus
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.spigotmc.event.entity.EntityDismountEvent
import java.util.*
import kotlin.collections.ArrayList

class OnBattleEvent(private val battle: Man10BattleRoyale):Listener{

    init {
        Main.plugin.server.pluginManager.registerEvents(this, Main.plugin)
    }

    fun unregister(){
        HandlerList.unregisterAll(this)
    }

    private val rightClickList=listOf(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.ACACIA_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.JUNGLE_DOOR,
            Material.BIRCH_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
    )


    //////////////////////////////////
    //ドロップシップ.エリトラ関連のイベント群
    @EventHandler
    fun onInvClick(e:InventoryClickEvent){
        if(battle.inGame()&&e.slotType==InventoryType.SlotType.ARMOR){
            val item=ItemStackPlus(e.currentItem?:return)
            if(item.getNBTInt("ShipElytra",Main.plugin)==1){
                e.isCancelled=true
            }
        }
    }

    @EventHandler
    fun onDamageByPlayer(e:EntityDamageByEntityEvent){
        if(battle.inGame()) {
            val entity = e.damager
            if (entity is Player) {
                val item=ItemStackPlus(entity.inventory.chestplate?:return)
                if(item.getNBTInt("ShipElytra",Main.plugin)==1){
                    e.isCancelled=true
                }
            }
        }
    }

    @EventHandler
    fun onDamageByElytra(e:EntityDamageEvent){
        if(battle.inGame()){
            val entity=e.entity
            if(entity is Player&&ItemStackPlus(entity.inventory.chestplate?:return).getNBTInt("ShipElytra",Main.plugin)==1
                    &&e.cause==EntityDamageEvent.DamageCause.FLY_INTO_WALL){
                e.isCancelled=true
            }
        }
    }

    @EventHandler
    fun onToggleGlide(e:EntityToggleGlideEvent){
        val entity=e.entity
        if(entity is Player){
            if(entity.isGliding&&ItemStackPlus(entity.inventory.chestplate?:return).getNBTInt("ShipElytra",Main.plugin)==1){
                entity.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,1,200))
                entity.inventory.clear()

                //ここ可変にしたい
                entity.inventory.addItem(ItemStack(Material.COMPASS,1))
                entity.inventory.addItem(ItemStack(Material.STONE_SWORD,1))
            }
        }
    }

    @EventHandler
    fun onDropFromShip(e:EntityDismountEvent){
        if(battle.inGame()) {

            //開始直後は降りられないように
            if(!battle.canDrop){
                e.isCancelled=true
                return
            }

            val entity = e.entity
            if (entity is Player && ItemStackPlus(entity.inventory.chestplate
                            ?: return).getNBTInt("ShipElytra", Main.plugin) == 1) {
                entity.location.add(0.0, -4.0, 0.0)
                entity.isGliding = true
                if (e.dismounted.name == "dropship") {
                    e.dismounted.remove()
                }
            }
        }
    }

    @EventHandler
    fun onConflictShip(e:ProjectileCollideEvent){
        if(e.entity.name=="dropship"){
            e.isCancelled=true
        }
    }
    //ドロップシップ.エリトラ関連のイベント群
    //////////////////////////////////


    ////////////////////////////
    //ケアパケ関連のイベント
    @EventHandler
    fun onRemoveSkull(e:EntityRemoveFromWorldEvent){
        if(battle.inGame()&&e.entity.name=="carepackage"){
            battle.field.setCarePackage(e.entity.location)
        }
    }
    //ケアパケ関連のイベント
    ////////////////////////////


    ////////////////////////////
    //コンパスのイベント
    @EventHandler
    fun onRightClickCompass(e:PlayerInteractEvent){
        if(battle.inGame()&&!battle.isLastArea&&(e.action==Action.RIGHT_CLICK_BLOCK||e.action==Action.RIGHT_CLICK_AIR)&&e.hand==EquipmentSlot.HAND
                &&e.item?.type==Material.COMPASS&&battle.livingPlayers.containsKey(e.player.uniqueId)){
            val distance=battle.livingPlayers[e.player.uniqueId]!!.getDistanceToAreaAndCenter()
            e.player.compassTarget= Location(battle.field.area,battle.field.nextCenter[0],1.0,battle.field.nextCenter[1])
            val item=ItemStackPlus(e.item!!)
                    .setDisplay("§a中心座標まであと${distance.second}§bM " +
                            if(distance.first==0.0)"§dエリア内に滞在中" else "§aエリア内まであと§b${distance.first}")
            e.player.inventory.setItemInMainHand(item)
        }
    }
    //コンパスのイベント
    ////////////////////////////

    ////////////////////////////
    //デスボックスのイベント
    @EventHandler
    fun onClickDeathBox(e:PlayerInteractEvent){
        val state=e.clickedBlock?.state
        if(battle.inGame()&&state is Chest){
            //customNameでやるべきじゃない 要変更
            if(battle.deadPlayers.containsKey(UUID.fromString(state.customName?:return))){
                e.player.openInventory(battle.deadPlayers[UUID.fromString(state.customName!!)]!!.deathBox)
                e.isCancelled=true
            }
        }
    }
    //デスボックスのイベント
    ////////////////////////////

    ////////////////////////////
    //スペクテイター関連のイベント群
    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(!battle.isEnding&&battle.deadPlayers.containsKey(e.player.uniqueId)){
            //殺した人に乗り移る
            e.player.gameMode=GameMode.SPECTATOR
            battle.deadPlayers[e.player.uniqueId]!!.setSpecTarget()
        }
    }

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent){
        //殺した人に乗り移る
        if(!battle.isEnding&&battle.deadPlayers.containsKey(e.player.uniqueId)){
            e.player.gameMode=GameMode.SPECTATOR
            Bukkit.getScheduler().runTaskLater(Main.plugin,Runnable{
                battle.deadPlayers[e.player.uniqueId]!!.setSpecTarget()
            },1)
        }
    }

    @EventHandler
    fun onStopSpec(e:PlayerStopSpectatingEntityEvent){
        if(battle.inGame()&&!battle.canSpecMovement&&e.player.gameMode==GameMode.SPECTATOR){
            battle.deadPlayers[e.player.uniqueId]?.setSpecTarget()
            if(battle.livingPlayers.containsKey(e.player.spectatorTarget?.uniqueId)){
                e.isCancelled=true
            }
        }
    }
    //スペクテイター関連のイベント群
    ///////////////////////////


    //////////////////////////
    //死亡関連のイベント群
    @EventHandler
    fun onMoveWorld(e: PlayerChangedWorldEvent) {
        if(battle.inGame()){
            battle.livingPlayers[e.player.uniqueId]?.death(null)
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        if(battle.inGame()&&battle.livingPlayers.containsKey(e.entity.uniqueId)) {
            battle.livingPlayers[e.entity.uniqueId]!!.death(e.entity.killer)
            if (e.entity.lastDamageCause?.cause== EntityDamageEvent.DamageCause.SUFFOCATION) {
                e.deathMessage(Component.text("${e.entity.name}はエリアダメージを耐えることができなかった"))
            }
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent){
        if(battle.inGame()){
            battle.livingPlayers[e.player.uniqueId]?.death(null)?:return
            battle.field.broadcastMessage("${Main.pluginTitle}${e.player.name}はログアウトしたため死亡扱いとなります}")
        }
        else if(!battle.isEnding){
            battle.removePlayer(e.player)
        }
    }
    //死亡関連のイベント群
    /////////////////////////


    ///////////////////////////////////
    //制限アイテムの計算イベント群
    @EventHandler
    fun onPickUp(e:EntityPickupItemEvent){
        val entity=e.entity
        if(entity !is Player)return
        if(battle.inGame()&&!e.isCancelled){
            val pData=battle.playerList[entity.uniqueId]?:return
            if(!pData.canPickUp(e.item.itemStack))e.isCancelled=true
        }
    }

    @EventHandler(priority= EventPriority.MONITOR)
    fun onDrop(e:PlayerDropItemEvent){
        if(battle.inGame()&&!e.isCancelled){
            val pData=battle.playerList[e.player.uniqueId]?:return
            if(battle.restrictedItemsMax.containsKey(e.itemDrop.itemStack.type)){
                pData.restrictedItems[e.itemDrop.itemStack.type]=
                        if(pData.restrictedItems[e.itemDrop.itemStack.type]!!>0)pData.restrictedItems[e.itemDrop.itemStack.type]!!-1
                else 0
            }
        }
    }

    @EventHandler
    fun onInvClose(e:InventoryCloseEvent){
        if(battle.inGame()&&battle.livingPlayers.containsKey(e.player.uniqueId)){
            val contents=e.player.inventory.contents
            for(material in battle.restrictedItemsMax.keys){
                val itemList=ArrayList<ItemStack>()
                if(e.player.inventory.itemInOffHand.type==material){
                    itemList.add(e.player.inventory.itemInOffHand)
                }
                for(item in contents){
                    if(item==null)continue
                    if(item.type==material){
                        itemList.add(item)
                    }
                }
                if(itemList.size>battle.restrictedItemsMax[material]!!){
                    for(item in itemList){
                        e.player.inventory.remove(item)
                        e.player.world.dropItem(e.player.location,item)
                    }
                    battle.livingPlayers[e.player.uniqueId]!!.restrictedItems[material]=0
                    e.player.sendMessage(Component.text("${battle.restrictedItemsMax[material]!!+1}個以上の銃は持てません"))
                }
                else{
                    battle.livingPlayers[e.player.uniqueId]!!.restrictedItems[material]=itemList.size
                }
            }
        }
    }
    //制限アイテムの計算イベント群
    ///////////////////////////////////

    /////////////////////////////////////////
    //バトロワ中のスニーク・ブロック破壊制限イベント群
    @EventHandler
    fun onSprint(e:PlayerToggleSprintEvent){
        if(battle.inGame()&&battle.livingPlayers.containsKey(e.player.uniqueId)){
            if(e.player.isSneaking&&e.isSprinting){
                e.player.isSneaking=false
            }
            if(!e.player.isSneaking&&!e.isSprinting){
                e.player.isSneaking=true
            }
        }
    }

    @EventHandler
    fun onSneak(e:PlayerToggleSneakEvent){
        if(battle.inGame()&&battle.livingPlayers.containsKey(e.player.uniqueId)){
            e.isCancelled=true
            if (e.player.isSneaking && e.player.isSprinting){
                e.player.isSneaking = true
            }
        }
    }

    @EventHandler
    fun onRightClick(e:PlayerInteractEvent){
        if(battle.inGame()&&rightClickList.contains(e.clickedBlock?.type)&&e.action==Action.RIGHT_CLICK_BLOCK){
            e.player.isSneaking=false
            Bukkit.getScheduler().runTaskLater(Main.plugin,Runnable{
                e.player.isSneaking=true
            },1)
        }
    }

    @EventHandler
    fun onBreakCrop(e:PlayerInteractEvent){
        if(e.action==Action.PHYSICAL&&e.clickedBlock?.type==Material.FARMLAND){
            e.isCancelled=true
        }
    }
    //バトロワ中のスニーク・ブロック破壊制限イベント群
    /////////////////////////////////////////


}