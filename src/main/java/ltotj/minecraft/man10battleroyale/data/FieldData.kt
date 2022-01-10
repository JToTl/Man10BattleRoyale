package ltotj.minecraft.man10battleroyale.data

import com.google.common.math.IntMath.pow
import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.WitherSkull
import org.bukkit.loot.LootTable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.lang.Math.signum
import java.security.SecureRandom
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

class FieldData(configManager: ConfigManager,val lootChestKey:String){

    //コンフィグからのデータ
    var area= Bukkit.getWorld(configManager.getString("world") ?: "world")!!
    var worldBorder=area.worldBorder
    var centerX=configManager.getDouble("firstCenter.X")
    var centerZ=configManager.getDouble("firstCenter.Z")
    var firstWidth=configManager.getDouble("firstWidth")
    var generationRate=configManager.getDouble("generationRate")
    val randomTierWeight=HashMap<Int, Int>()
    var carePackage:CarePackageData
    var dropShipAltitude=250.0
    var playerHealth=20
    var firstAreaWaitTime=10
    val phases=HashMap<Int, PhaseData>()


    val currentCenter= arrayOf(centerX, centerZ)
    val nextCenter= arrayOf(0.0, 0.0)
    var currentWidth=firstWidth
    var nextWidth=firstWidth

    var currentPhase=1

    private val lootChestPos=HashMap<Int, ArrayList<Location>>()
    private val carePackagePos=ArrayList<Location>()
    private val carePackEntity=ArrayList<Entity>()


    init {
        //Tierチェスト定義のための重みロードと設定
        lootChestPos[0]=ArrayList()
        for(i in 1..configManager.getKeys("randomTierWeight", false).size){
            randomTierWeight[i]=configManager.getInt("randomTierWeight.Tier${i}")
            lootChestPos[i]=ArrayList()
        }
        carePackage= CarePackageData(configManager.getInt("carePackage.frequency"), configManager.getInt("carePackage.Tier"), configManager.getDouble("carePackage.rate"))
        dropShipAltitude=configManager.getDouble("dropShipAltitude")
        playerHealth=configManager.getInt("playerHealth")
        firstAreaWaitTime=configManager.getInt("firstAreaWaitTime")
        //フェーズ登録
        for(key in configManager.getKeys("areaReduction", false)){
            phases[key.toIntOrNull() ?: 0]= PhaseData(key.toIntOrNull()
                    ?: 0, configManager.getInt("areaReduction.${key}.waitTime"), configManager.getInt("areaReduction.${key}.executeTime"), configManager.getDouble("areaReduction.${key}.reductionRate"), configManager.getBoolean("areaReduction.${key}.spawnableCarePackage"), configManager.getDouble("areaReduction.${key}.areaDamage"), configManager.getInt("areaReduction.${key}.areaDamageBuffer"))
        }
        //ルートチェストの位置読み込み
        for(key in configManager.getKeys("chestPosition", false)){
            val tier=configManager.getInt("chestPosition.${key}.Tier")
            val locKey=key.split("|")
            val location=Location(area, locKey[0].toDoubleOrNull() ?: 0.0, locKey[1].toDoubleOrNull()
                    ?: 0.0, locKey[2].toDoubleOrNull() ?: 0.0)
            lootChestPos[tier]?.add(location)
        }
    }

    fun setWBCenter(remTime:Long,totalTime:Long){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            setWBCenter(currentCenter[0] + (totalTime - remTime) * (nextCenter[0] - currentCenter[0]) / totalTime, currentCenter[1] + (totalTime - remTime) * (nextCenter[1] - currentCenter[1]) / totalTime)
        })
    }

    private fun setWBCenter(X: Double, Y: Double){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable{
            worldBorder.setCenter(X, Y)
        })
    }

    private fun setWBBuffer(buffer: Double){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            worldBorder.damageBuffer = buffer
        })
    }

    private fun setWBDamage(damage: Double){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            worldBorder.damageAmount = damage
        })
    }

    private fun setWBSize(width: Double, time: Long){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable{
            worldBorder.setSize(width, time)
        })
    }

    fun setWBSize(width:Double){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            worldBorder.size = width
        })
    }

    fun setFirstWB(){
        setWBSize(2*firstWidth)
        setWBDamage(0.1)
        setWBBuffer(1.0)
        setWBCenter(centerX,centerZ)
    }

    fun setCurrentWidthAndCenter(){
        currentCenter[0]=nextCenter[0]
        currentCenter[1]=nextCenter[1]
        currentWidth=nextWidth
    }

    fun startNarrowingArea(time:Long){
        broadcastMessage("§4==================")
        broadcastMessage(" ");
        broadcastMessage("§4エリア収縮が始まります！")
        broadcastMessage("§4エリア収縮が始まります！")
        broadcastMessage("§4エリア収縮が始まります！")
        broadcastMessage(" ");
        broadcastMessage("§4==================")
        broadcastSound(Sound.ENTITY_WITHER_SPAWN,1F,1F)
        setWBSize(2*nextWidth,time)
    }

    fun generateNewCenterAndWidth(){
        val redRate=phases[currentPhase]!!.reductionRate
        nextCenter[0]=currentCenter[0]+(Random.nextDouble()-0.5)*currentWidth*(1-redRate)
        nextCenter[1]=currentCenter[1]+(Random.nextDouble()-0.5)*currentWidth*(1-redRate)
        nextWidth=if(redRate*currentWidth>1)redRate*currentWidth else 1.0
    }

    //現在のエリアの辺上のうちの一点から中心に向かって１進めた場所をlocationとして返す
    fun getShipRoute():Location{
        val random=java.util.Random()
        val location=Location(area,currentCenter[0],dropShipAltitude,currentCenter[1])
        val l=nextWidth*((sqrt(2.0)-1)*random.nextDouble() +1)
        val randomInt=random.nextInt(8)
        val ponx=pow(-1,randomInt)
        val ponz=if(randomInt<=3) -1 else 1
        if((randomInt+3)%4<=1){
            location.add(ponx*nextWidth,0.0,ponz*sqrt(l*l-nextWidth*nextWidth))
        }
        else {
            location.add(ponx*sqrt(l*l-nextWidth*nextWidth),0.0,ponz*nextWidth)
        }
        location.direction = Vector(currentCenter[0]-location.x,0.0,currentCenter[1]-location.z)
        location.add(sign(location.direction.x),0.0,sign(location.direction.z))
        return location
    }

    fun broadcastMessage(message: String){
        area.sendMessage(Component.text(message))
    }

    fun broadcastTitle(title: String,subTitle:String){
        area.showTitle(Title.title(Component.text(title),Component.text(subTitle),Title.DEFAULT_TIMES))
    }

    fun broadcastSound(sound: Sound, volume:Float, pitch:Float){
        for(player in area.players){
            player.playSound(player.location,sound,volume, pitch)
        }
    }

    fun setLootChest(){
        for(tier in lootChestPos.keys){
            if(generationRate>java.util.Random().nextDouble())continue
            if(tier==0) {
                var totalWeight=0
                for(weight in randomTierWeight.values){
                    totalWeight+=weight
                }
                for (location in lootChestPos[tier]!!) {
                    var randomInt=java.util.Random().nextInt(totalWeight)
                    for(weightTier in randomTierWeight.keys){
                        randomInt-=randomTierWeight[weightTier]!!
                        if(randomInt<0){
                            location.block.type=Material.CHEST
                            val chest=location.block.state as Chest
                            val lootTable=Bukkit.getServer().getLootTable(NamespacedKey.fromString("battleroyalepack:${lootChestKey}/tier${weightTier}")!!)
                            chest.lootTable = lootTable
                            chest.update()
                            break
                        }
                    }
                }
            }
            else{
                for(location in lootChestPos[tier]!!){
                    location.block.type=Material.CHEST
                    val chest=location.block.state as Chest
                    val lootTable=Bukkit.getServer().getLootTable(NamespacedKey.fromString("battleroyalepack:${lootChestKey}/tier${tier}")!!)
                    chest.lootTable = lootTable
                    chest.update()
                }
            }
        }
    }

    //メインからくるからこのままでOK
    fun setCarePackage(location:Location){
        var tier=carePackage.tier
        if(tier==0) {
            var totalWeight=0
            for(weight in randomTierWeight.values){
                totalWeight+=weight
            }
            var randomInt=java.util.Random().nextInt(totalWeight)
            for(ti in randomTierWeight.keys){
                randomInt-=ti
                if(randomInt<0){
                    tier=ti
                }
            }
        }
        carePackagePos.add(location)
        location.block.type=Material.CHEST
        val chest=location.block.state as Chest
        val lootTable=Bukkit.getServer().getLootTable(NamespacedKey.fromString("battleroyalepack:${lootChestKey}/tier${tier}")!!)
        chest.lootTable = lootTable
        chest.update()
    }

    fun spawnCarePackage(){
        val random=java.util.Random()
        if(random.nextDouble()<carePackage.rate) {
            Bukkit.getScheduler().runTask(Main.plugin, Runnable {
                val location = Location(area, nextCenter[0] + nextWidth * (0.5 - Random.nextDouble()), dropShipAltitude, nextCenter[1] + nextWidth * (0.5 - Random.nextDouble()))
                location.direction = Vector(0, -1, 0)
                val skull = area.spawnEntity(location, EntityType.WITHER_SKULL) as WitherSkull
                val armorS = area.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
                skull.isInvulnerable = true
                skull.isCharged = true
                skull.customName = "carepackage"
                skull.velocity = Vector(0.0, -0.03, 0.0)
                skull.addPassenger(armorS)

                armorS.isInvulnerable = true
                armorS.isInvisible = true
                armorS.isGlowing=true
                armorS.customName = "subcarepackage"
                carePackEntity.add(armorS)
            })
        }
    }

    fun removeLootChest(){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable{
        for( tier in lootChestPos.keys){
            for(location in lootChestPos[tier]!!){
                val blockState=location.block.state
                if(blockState is Chest){
                    blockState.inventory.clear()
                    location.block.type= Material.AIR
                }
            }
        }
        })
    }

    fun removeCarePackage(){
        Bukkit.getScheduler().runTask(Main.plugin,Runnable {
            for (location in carePackagePos) {
                location.block.type = Material.AIR
            }
            for (entity in carePackEntity) {
                entity.remove()
            }
        })
    }

}