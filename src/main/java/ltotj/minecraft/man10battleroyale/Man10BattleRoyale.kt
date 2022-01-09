package ltotj.minecraft.man10battleroyale

import ltotj.minecraft.man10battleroyale.data.FieldData
import ltotj.minecraft.man10battleroyale.data.PlayerData
import ltotj.minecraft.man10battleroyale.event.OnBattleEvent
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import ltotj.minecraft.man10battleroyale.utility.TimeManager.TimerManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

class Man10BattleRoyale(fieldName: String, lootName: String){

    val playerList=HashMap<UUID,PlayerData>()
    val livingPlayers=HashMap<UUID, PlayerData>()
    val deadPlayers=HashMap<UUID, PlayerData>()
    val field=FieldData(ConfigManager(Main.plugin, fieldName),lootName)
    var isRunning=false
    var isEnding=false
    var canSpecMovement=false
    var keepInv=false
    val onBattleEvent = OnBattleEvent(this)

    var generatedChest=false

    private val startTimer=TimerManager()
    private val areaTimers=HashMap<Int, Pair<TimerManager, TimerManager>>()
    private val carePackageTimer=TimerManager()
    private val endTimer=TimerManager()

    private val bossBar= Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG)

    val restrictedItemsMax=hashMapOf(Pair(Material.IRON_HOE,2))//あとで色々できるようにしたい

    init {
        //タイマー作成
        for(phase in field.phases.keys){
            val phaseData=field.phases[phase]!!
            val phaseTimers=Pair(TimerManager(), TimerManager())
            areaTimers[phase]=phaseTimers
            val waitTimer=phaseTimers.first
            waitTimer
                    .setRemainingTime(phaseData.waitTime)
                    .addStartEvent{
                        field.currentPhase=phase
                        field.generateNewCenterAndWidth()
                    }
                    .addIntervalEvent(1) {
                        if(isEnding){
                            return@addIntervalEvent
                        }
                        setScoreboard()
                        setBossBar(phaseData.waitTime, waitTimer.getRemainingTime(), "§c§l[Phase - $phase] §rエリア収縮", BarColor.GREEN)
                    }
                    .addEndEvent{
                        if(!isEnding) {
                            phaseTimers.second.start()
                        }
                    }
            val executeTimer=phaseTimers.second
            executeTimer
                    .addStartEvent{
                        field.startNarrowingArea(phaseData.executeTime.toLong())
                    }
                    .setRemainingTime(phaseData.executeTime)
                    .addIntervalEvent(1){
                        if(isEnding){
                            return@addIntervalEvent
                        }
                        setScoreboard()
                        setBossBar(phaseData.executeTime, executeTimer.getRemainingTime(), "§c§l[Phase - $phase] §rエリア収縮終了", BarColor.RED)
                        field.setWBCenter(executeTimer.getRemainingTime().toLong(),phaseData.executeTime.toLong())
                    }
                    .addEndEvent {
                        if(!isEnding) {
                            field.setCurrentWidthAndCenter()
                        }
                    }
            if(phase!=field.phases.keys.last()) {
                executeTimer
                        .addEndEvent {
                            areaTimers[phase + 1]!!.first.start()
                        }
            }
            else{
                executeTimer.addEndEvent{
                    bossBar.setTitle("§c§l最終エリア")
                    bossBar.color=BarColor.RED
                    bossBar.progress=1.0
                }
            }
        }

        startTimer
                .addRemainingTime(field.firstAreaWaitTime)
                .addStartEvent{
                    field.setFirstWB()
                }
                .addEndEvent{
                    deleteShip()
                    areaTimers[1]!!.first.start()
                }

        carePackageTimer
                .setRemainingTime(360000)
                .addIntervalEvent(field.carePackage.frequency){
                    if(field.phases[field.currentPhase]!!.spawnableCarePackage){
                        field.spawnCarePackage()
                    }
                }
                .addIntervalEvent(1){
                    if(isEnding){
                        carePackageTimer.setRemainingTime(1)
                        return@addIntervalEvent
                    }
                }
        endTimer
                .setRemainingTime(1)
                .addEndEvent{
                    Thread.sleep(1000L)
                    if(livingPlayers.isNotEmpty()) {
                        field.broadcastMessage("${Main.pluginTitle}}§c§l${livingPlayers.values.elementAt(0).player.name}§dがバトルロワイヤルを制しました！")
                    }
                    else{
                        field.broadcastMessage("引き分け またはエラー")
                    }
                    Thread.sleep(2000L)
                    field.broadcastMessage("${Main.pluginTitle}順位を表示します")
                    Thread.sleep(1000L)
                    var d=0
                    if(livingPlayers.isNotEmpty()) {
                        field.broadcastMessage("${Main.pluginTitle}§61位： §c${livingPlayers.values.elementAt(0).player.name}")
                        d=1
                    }
                    for(i in 1 until 6-d){
                        if(deadPlayers.size-i<0)break
                        field.broadcastMessage("${Main.pluginTitle}§6${i}位： §c${deadPlayers.values.elementAt(deadPlayers.size-i).player.name}")
                    }
                }
    }

    fun checkEnd(){
        if(livingPlayers.size<2){
            end()
        }
    }

    fun end(){
        isEnding=true
        field.setWBSize(100000.0)
        bossBar.removeAll()
        field.removeLootChest()
        field.removeCarePackage()
        removeDeathBoxes()

        for(uuid in playerList.keys){
            if(!Main.participationHistory.containsKey(uuid)){
                Main.participationHistory[uuid]=1
            }
            else{
                Main.participationHistory[uuid]=Main.participationHistory[uuid]!!+1
            }
        }


        endTimer.start()
    }

    private fun setScoreboard(){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable{
        for(plData in playerList.values){
            val scoreboard=Bukkit.getScoreboardManager().newScoreboard
            var objective=scoreboard.getObjective("§d§Man10Battle")
            if(objective==null){
                objective=scoreboard.registerNewObjective("§d§Man10Battle","Dummy",Component.text(Main.pluginTitle))
            }
            val distances=plData.getDistanceToAreaAndCenter()
            objective.displaySlot=DisplaySlot.SIDEBAR
            objective.getScore("§6§l残り人数 : ${livingPlayers.size}人").score=0
            objective.getScore("§a§l中心座標まであと§b§l${distances.second}M").score=-1
            objective.getScore(if(distances.first==0.0)"§dエリア内に滞在中" else "§aエリア内まであと§b${distances.first}M").score=-2
//            objective.getScore("§b§lマップ ： ${field.area.name}").score=-3
            plData.player.scoreboard=scoreboard
        }
        })
    }

    private fun setBossBar(totalTime: Int, time: Int, str: String, color: BarColor) {
        bossBar.setTitle("${str}まで残り${time}秒")
        bossBar.color=color
        bossBar.progress = time.toDouble() / totalTime
    }

    fun getRandomLivPl(): PlayerData {
        return livingPlayers.values.random()
    }

    fun generateLootChest():Boolean{
        if(generatedChest){
            return false
        }
        field.setLootChest()
        generatedChest=true
        return true
    }

    fun generateShip(){
        val location=field.getShipRoute()
        val tpLoc=location.clone().add(0.0,-5.0,0.0)
        val dVec=location.direction.clone().crossProduct(Vector(0,1,0)).normalize()
        for((count, data) in livingPlayers.values.withIndex()){
            data.player.teleport(tpLoc)
            if(count%2==0){
                location.add(dVec)
                data.ship=field.area.spawnEntity(location, EntityType.WITHER_SKULL) as WitherSkull
                data.ship!!.customName="dropship"
                data.ship!!.isInvulnerable=true
            }
            else{
                location.add(-2*dVec.x,0.0,-2*dVec.z)
                data.ship=field.area.spawnEntity(location, EntityType.WITHER_SKULL) as WitherSkull
                data.ship!!.customName="dropship"
                data.ship!!.isInvulnerable=true
//                data.ship!!.addPassenger(data.player)
                location.add(location.direction.x/location.direction.length(),0.0,location.direction.z/location.direction.length())
                        .add(dVec)
            }
        }
        for(pData in livingPlayers.values){
            pData.ship!!.addPassenger(pData.player)
        }

    }

    fun joinPlayer(player: Player):Boolean{
        val uuid=player.uniqueId
        if(!isRunning&&!playerList.containsKey(uuid)){
            playerList[uuid]= PlayerData(player, this)
            if(!keepInv){
                player.inventory.clear()
            }
            return true
        }
        return false
    }

    fun removePlayer(player: Player){
        playerList.remove(player.uniqueId)
        livingPlayers.remove(player.uniqueId)
        deadPlayers.remove(player.uniqueId)
    }

    fun removeDeathBoxes(){
        for(plData in deadPlayers.values){
            plData.removeDeathBox()
        }
    }

    //メインから
    fun start():Boolean{
        if(isRunning)return false
        isRunning=true
        bossBar.removeFlag(BarFlag.CREATE_FOG)
        for(playerData in playerList.values){
            bossBar.addPlayer(playerData.player)
            livingPlayers[playerData.player.uniqueId]=playerData
            playerData.initializePlStatus()
        }
        for(item in field.area.entities){
            if(item.type==EntityType.DROPPED_ITEM){
                item.remove()
            }
        }
        generateShip()
        startTimer.start()
        carePackageTimer.start()
        return true
    }

    fun stop():Boolean{
        isEnding=true
        field.removeCarePackage()
        field.removeLootChest()
        removeDeathBoxes()
        bossBar.removeAll()
        return true
    }

    fun delete(){
        onBattleEvent.unregister()
        stop()
        isEnding=true
        Main.battleRoyale=null
    }

    fun deleteShip(){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            for (data in playerList.values) {
                data.ship?.remove()
            }
        })
    }

    fun inGame():Boolean{
        return isRunning&&!isEnding
    }


}