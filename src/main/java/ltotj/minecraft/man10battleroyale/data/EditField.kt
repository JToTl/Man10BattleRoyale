package ltotj.minecraft.man10battleroyale.data

import ltotj.minecraft.man10battleroyale.Main
import ltotj.minecraft.man10battleroyale.event.OnEditEvent
import ltotj.minecraft.man10battleroyale.utility.ConfigManager.ConfigManager
import ltotj.minecraft.man10battleroyale.utility.GUIManager.GUIItem
import ltotj.minecraft.man10battleroyale.utility.GUIManager.menu.MenuGUI
import ltotj.minecraft.man10battleroyale.utility.GUIManager.menu.NumericGUI
import ltotj.minecraft.man10battleroyale.utility.GUIManager.menu.TrueOrFalseGUI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class EditField(val config:ConfigManager) {


    val event=OnEditEvent(this)
    var area:World
    private val lootChestPos=HashMap<Int, ArrayList<Location>>()
    private val mainMenu=MenuGUI(Main.plugin,6,"${config.filename}の設定")
    private val phaseMenu=MenuGUI(Main.plugin,6,"フェーズの設定",mainMenu,"phaseMenu",false)
    private val carePackageMenu=MenuGUI(Main.plugin,6,"ケアパッケージの設定",mainMenu,"carePackageMenu",false)
    private val chestMenu=MenuGUI(Main.plugin,6,"ルートチェストの設定",mainMenu,"chestMenu",false)

    private val phaseDataMap=HashMap<Int,PhaseData>()



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

    fun openMenu(player:Player){
        mainMenu.open(player)
    }

    fun end(){
        removeAllLootChestOnArea()
        event.unregister()
        config.save()
        Main.editField =null
    }

    init {

        try{area= Bukkit.getWorld(config.getString("world") ?: "world")!!}catch (e:NullPointerException){
            area= Bukkit.getWorlds()[0]!!
            Bukkit.getLogger().info("指定されたワールドを検出できませんでした")
        }

        //////////////
        //メインメニュー

        mainMenu.setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }

        //初期中心設定
        val firstCenter= GUIItem(Material.COMPASS,1)
        firstCenter.setDisplay("§a§l初期エリアの中心を設定")
                .setLore(arrayOf("§d現在の初期エリアの中心 X: §e${config.getDouble("firstCenter.X")}§d,Z: §e${config.getDouble("firstCenter.Z")}"))
                .setEvent { _, inventoryClickEvent ->

                    val player=inventoryClickEvent.whoClicked as Player
                    var newX: Double
                    var newZ: Double
                    val xMenu=NumericGUI(Main.plugin,"X座標を設定",mainMenu,"xMenu",true)

                    xMenu.setOutput{ x ->
                        newX=x

                        val yMenu=NumericGUI(Main.plugin,"Z座標を設定",mainMenu,"zMenu",true)
                        yMenu.setOutput { z ->
                            newZ = z
                            config.setValue("firstCenter.X",newX)
                            config.setValue("firstCenter.Z",newZ)
                            firstCenter.setLore(arrayOf("§d現在の初期エリアの中心 X: §e${newX}§d,Z: §e${newZ}"))
                                    .reload()
                            yMenu.close(player)
                        }

                        xMenu.openSiblingGUI("zMenu",player)
                    }
                    mainMenu.openChildGUI("xMenu",player)
                }

        //初期エリアサイズの設定
        val firstWidth=GUIItem(Material.DIRT,1)
        firstWidth.setDisplay("§a§l初期エリアのサイズを設定")
                .setLore(arrayOf("§d現在のエリアサイズ： §e${2*config.getDouble("firstWidth")}×${2*config.getDouble("firstWidth")}"))
                .setEvent { _, inventoryClickEvent ->

                    val player=inventoryClickEvent.whoClicked as Player
                    val fWidthMenu=NumericGUI(Main.plugin,"エリアの一辺の長さを指定",mainMenu,"fWidthMenu",true)
                    fWidthMenu.setOutput{ width->
                        config.setValue("firstWidth",width*0.5)
                        firstWidth.setLore(arrayOf("§d現在のエリアサイズ： §e${width}×${width}"))
                                .reload()
                        fWidthMenu.close(player)
                    }
                    mainMenu.openChildGUI("fWidthMenu",player)
                }

        //開催ワールド設定
        val worldName=GUIItem(Material.GRASS_BLOCK,1)
        worldName.setDisplay("§a§l開催するワールドを設定")
                .setLore(arrayOf("§d現在の開催ワールド：§e${config.getString("world")}","§dクリックで現在居るワールドに変更"))
                .setEvent { _, inventoryClickEvent ->

                    config.setValue("world",inventoryClickEvent.whoClicked.world.name)
                    worldName.setLore(arrayOf("§d現在の開催ワールド：§e${inventoryClickEvent.whoClicked.world.name}","§dクリックで現在居るワールドに変更"))
                            .reload()
                }

        //チェスト生成率設定
        val generatingRate=GUIItem(Material.DIAMOND,1)
        generatingRate.setDisplay("§a§lチェスト生成率を設定")
                .setLore(arrayOf("§d現在の生成率：§e${config.getDouble("generatingRate")*100}%"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val rateMenu=NumericGUI(Main.plugin,"生成率を百分率で指定",mainMenu,"rateMenu",true)
                    rateMenu.setOutput{
                        val rate=if(it>100) 1.0 else it*0.01
                        config.setValue("generatingRate",rate)
                        generatingRate.setLore(arrayOf("§d現在の生成率：§e${it}%"))
                                .reload()
                        rateMenu.close(player)
                    }
                    mainMenu.openChildGUI("rateMenu",player)
                }


        //チェストのTier設定
        val tierChest=GUIItem(Material.CHEST,1)
        tierChest.setDisplay("§a§lルートチェストの設定")
                .setEvent { _, inventoryClickEvent ->

                    mainMenu.openChildGUI("chestMenu",inventoryClickEvent.whoClicked as Player)
                }

        //ケアパッケージの設定
        val carePackage=GUIItem(Material.GOLD_BLOCK,1)
        carePackage.setDisplay("§e§lケアパッケージの設定")
                .setEvent { _, inventoryClickEvent ->

                    mainMenu.openChildGUI("carePackageMenu",inventoryClickEvent.whoClicked as Player)
                }

        //ドロップシップの高さ設定
        val dropShip=GUIItem(Material.WITHER_SKELETON_SKULL,1)
        dropShip.setDisplay("§a§lドロップシップの高さ設定")
                .setLore(arrayOf("§d現在の高さ： §e${config.getDouble("dropShipAltitude")}"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player


                    val altitude=NumericGUI(Main.plugin,"ドロップシップのY座標を入力",mainMenu,"dropShipAltitude",true)
                    altitude.setOutput{
                        val y=if(it>0)it else 0
                        config.setValue("dropShipAltitude",y)
                        dropShip.setLore(arrayOf("§d現在の高さ： §e$y"))
                                .reload()
                        altitude.close(player)
                    }
                    mainMenu.openChildGUI("dropShipAltitude",player)
                }

        //フェーズ開始までの時間を設定
        val firstAreaWait=GUIItem(Material.CLOCK,1)
        firstAreaWait.setDisplay("§a§lフェーズ１開始までの時間を設定")
                .setLore(arrayOf("§d現在の設定時間： §e${config.getInt("firstAreaWaitTime")}"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player


                    val areaWait=NumericGUI(Main.plugin,"フェーズ１までの時間を秒数で指定",mainMenu,"firstAreaWaitTime",true)
                    areaWait.setOutput{
                        val time=if(it>0)it.toInt() else 1
                        config.setValue("firstAreaWaitTime",time)
                        firstAreaWait.setLore(arrayOf("§d現在の設定時間： $time"))
                        areaWait.close(player)
                    }
                    mainMenu.openChildGUI("firstAreaWaitTime",player)
                }

        //フェーズの設定
        val areaReduction=GUIItem(Material.COBBLESTONE,1)
                .setDisplay("§a§lフェーズごとの設定")
                .setEvent { _, inventoryClickEvent ->

                    mainMenu.openChildGUI("phaseMenu",inventoryClickEvent.whoClicked as Player)
                }

        mainMenu.setItem(0,firstCenter)
                .setItem(1,firstWidth)
                .setItem(2,worldName)
                .setItem(3,generatingRate)
                .setItem(4,tierChest)
                .setItem(5,carePackage)
                .setItem(6,dropShip)
                .setItem(7,firstAreaWait)
                .setItem(8,areaReduction)
        //メインメニュー
        //////////////


        ///////////////
        //フェーズメニュー

        phaseMenu.setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }
        //呼び出しが多そうなので関数化
        loadPhaseMenu()

        //フェーズメニュー
        ///////////////

    }

    private fun loadPhaseMenu(){
        phaseMenu.clear()
        phaseMenu.removeAllChild()


        for(key in config.getKeys("areaReduction", false)){
            val phase=key.toIntOrNull() ?:continue
            phaseDataMap[phase]= PhaseData(phase
                    , config.getInt("areaReduction.${key}.waitTime"), config.getInt("areaReduction.${key}.executeTime"), config.getDouble("areaReduction.${key}.reductionRate"), config.getBoolean("areaReduction.${key}.spawnableCarePackage"), config.getDouble("areaReduction.${key}.areaDamage"), config.getInt("areaReduction.${key}.areaDamageBuffer"))
            val phaseButton=GUIItem(Material.EMERALD_BLOCK,1)
                    .setEvent { _, inventoryClickEvent ->
                        phaseMenu.openChildGUI("phase${phase}",inventoryClickEvent.whoClicked as Player)
                    }
            writePhaseData(phaseDataMap[phase]!!,phaseButton)
            createPhaseDataMenu(phaseDataMap[phase]!!,phaseButton)
            phaseMenu.setItem(phase-1,phaseButton)
        }
        val addPhase=GUIItem(Material.REDSTONE,1)
        addPhase.setDisplay("§a§lフェーズを追加する")
                .setEvent { _, _ ->

                    config.setValue("areaReduction.${phaseDataMap.size+1}.waitTime",10)
                    config.setValue("areaReduction.${phaseDataMap.size+1}.executeTime",10)
                    config.setValue("areaReduction.${phaseDataMap.size+1}.reductionRate",1)
                    config.setValue("areaReduction.${phaseDataMap.size+1}.spawnableCarePackage",false)
                    config.setValue("areaReduction.${phaseDataMap.size+1}.areaDamage",0.1)
                    config.setValue("areaReduction.${phaseDataMap.size+1}.areaDamageBuffer",0)
                    loadPhaseMenu()
                }

        phaseMenu.setItem(config.getKeys("areaReduction", false).size,addPhase)
                .renderGUI()
    }

    private fun createPhaseDataMenu(phaseData: PhaseData,button:GUIItem) {
        val phaseDataMenu = MenuGUI(Main.plugin, 3, "Phase${phaseData.phaseNum}の設定", phaseMenu, "phase${phaseData.phaseNum}", false)
        phaseDataMenu
                .setClickEvent { _, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled = true
                }
                .setCloseEvent { _, _ ->
                    loadPhaseMenu()
                }

        val waitTime = GUIItem(Material.LODESTONE, 1)
        waitTime.setDisplay("§a§l縮小までの時間を設定")
                .setLore(arrayOf("§d現在の設定: §e${phaseData.waitTime}秒"))
                .setEvent { _, inventoryClickEvent ->
                    val player = inventoryClickEvent.whoClicked as Player
                    val waitTimeMenu = NumericGUI(Main.plugin, "縮小までの時間を設定", phaseDataMenu, "waitTimeMenu", true)
                    waitTimeMenu.setOutput {
                        val time = it.roundToInt()
                        if (time < 1) return@setOutput
                        config.setValue("areaReduction.${phaseData.phaseNum}.waitTime", time)
                        waitTime.setLore(arrayOf("§d現在の設定: §e${time}秒"))
                                .reload()
                        waitTimeMenu.close(player)
                        writePhaseData(phaseData,button)
                    }
                    phaseDataMenu.openChildGUI("waitTimeMenu", player)
                }

        val executeTime = GUIItem(Material.CLOCK, 1)
        executeTime.setDisplay("§a§l縮小時間を設定")
                .setLore(arrayOf("§d現在の設定: §e${phaseData.executeTime}秒"))
                .setEvent { _, inventoryClickEvent ->
                    val player = inventoryClickEvent.whoClicked as Player
                    val executeTimeMenu = NumericGUI(Main.plugin, "縮小までの時間を設定", phaseDataMenu, "executeTimeMenu", true)
                    executeTimeMenu.setOutput {
                        val time = it.roundToInt()
                        if (time < 1) return@setOutput
                        config.setValue("areaReduction.${phaseData.phaseNum}.executeTime", time)
                        executeTime.setLore(arrayOf("§d現在の設定: §e${time}秒"))
                                .reload()
                        executeTimeMenu.close(player)
                        writePhaseData(phaseData,button)
                    }
                    phaseDataMenu.openChildGUI("executeTimeMenu", player)
                }

        val reductionRate=GUIItem(Material.OBSERVER,1)
        reductionRate.setDisplay("§a§l縮小率を設定")
                .setLore(arrayOf("§d現在の設定: §e${phaseData.reductionRate*100}%"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val redRateMenu=NumericGUI(Main.plugin,"縮小率を百分率で設定",phaseDataMenu,"redRateMenu",true)
                    redRateMenu.setOutput{
                        if(it<0)return@setOutput
                        config.setValue("areaReduction.${phaseData.phaseNum}.reductionRate",it*0.01)
                        reductionRate.setLore(arrayOf("§d現在の設定: §e${it}%"))
                                .reload()
                        redRateMenu.close(player)
                        writePhaseData(phaseData,button)
                    }
                    phaseDataMenu.openChildGUI("redRateMenu",player)
                }

        val carePackage=GUIItem(Material.CHEST,1)
        carePackage.setDisplay("§a§lケアパッケージの出現設定")
                .setLore(arrayOf("§d現在の設定: §e${if(phaseData.spawnableCarePackage)"出現する" else "出現しない"}"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val carePackageMenu=TrueOrFalseGUI(Main.plugin,"ケアパッケージの出現設定",phaseDataMenu,"carePackageMenu",true)
                    carePackageMenu.setFalseItem(
                            GUIItem(Material.RED_WOOL,1)
                                    .setDisplay("§4ケアパッケージを生成しない"))
                            .setTrueItem(
                                    GUIItem(Material.LIME_WOOL,1)
                                            .setDisplay("§aケアパッケージを生成する"))
                            .setOutput{
                                config.setValue("areaReduction.${phaseData.phaseNum}.spawnableCarePackage",it.boolean)
                                carePackage.setLore(arrayOf("§d現在の設定: §e${if(it.boolean)"出現する" else "出現しない"}"))
                                        .reload()
                                carePackageMenu.close(player)
                                writePhaseData(phaseData,button)
                            }
                    phaseDataMenu.openChildGUI("carePackageMenu",player)
                }

        val areaDamage=GUIItem(Material.PURPLE_GLAZED_TERRACOTTA,1)
        areaDamage.setDisplay("§a§lエリアダメージの設定")
                .setLore(arrayOf("§d現在の設定: §e${phaseData.areaDamage}"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val areaDamageMenu=NumericGUI(Main.plugin,"エリアダメージの設定",phaseDataMenu,"areaDamageMenu",true)
                    areaDamageMenu.setOutput{
                        if(it<0)return@setOutput
                        config.setValue("areaReduction.${phaseData.phaseNum}.areaDamage",it)
                        areaDamage.setLore(arrayOf("§d現在の設定: §e${it}"))
                                .reload()
                        areaDamageMenu.close(player)
                        writePhaseData(phaseData,button)
                    }
                    phaseDataMenu.openChildGUI("areaDamageMenu",player)
                }

        val areaBuffer=GUIItem(Material.LIME_GLAZED_TERRACOTTA,1)
        areaBuffer.setDisplay("§a§lエリアバッファーの設定")
                .setLore(arrayOf("§d現在の設定: §e${phaseData.areaDamageBuffer}"))
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val areaBufferMenu=NumericGUI(Main.plugin,"エリアバッファーの設定",phaseDataMenu,"areaBufferMenu",true)
                    areaBufferMenu.setOutput{
                        if(it<0)return@setOutput
                        config.setValue("areaReduction.${phaseData.phaseNum}.areaDamageBuffer",it)
                        areaBuffer.setLore(arrayOf("§d現在の設定: §e${it}"))
                                .reload()
                        areaBufferMenu.close(player)
                        writePhaseData(phaseData,button)
                    }
                    phaseDataMenu.openChildGUI("areaBufferMenu",player)
                }

        val deleteButton=GUIItem(Material.BARRIER,1)
                .setDisplay("§a§lこのフェーズを削除")
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    val deleteMenu=TrueOrFalseGUI(Main.plugin,"本当に削除しますか？",phaseDataMenu,"delete",true)
                    deleteMenu.setFalseItem(
                            GUIItem(Material.RED_WOOL,1)
                                    .setDisplay("§4削除する"))
                            .setTrueItem(
                                    GUIItem(Material.LIME_WOOL,1)
                                            .setDisplay("§a削除しない"))
                            .setOutput{
                                if(it.boolean){
                                    deleteMenu.close(player)
                                    return@setOutput
                                }

                                //フェーズの削除処理
                                for(oldData in phaseDataMap.values) {
                                    config.setValue("areaReduction.${oldData.phaseNum}", null)
                                }
                                val cloneMap=HashMap<Int,PhaseData>()
                                cloneMap.putAll(phaseDataMap)
                                cloneMap.remove(phaseData.phaseNum)
                                phaseDataMap.clear()
                                for((newPhase,oldPhase) in cloneMap.keys.sorted().withIndex()){
                                    phaseDataMap[newPhase+1]=cloneMap[oldPhase]!!
                                    phaseDataMap[newPhase+1]!!.phaseNum=newPhase+1
                                }
                                for(newData in phaseDataMap.values){
                                    config.setValue("areaReduction.${newData.phaseNum}.waitTime",newData.waitTime)
                                    config.setValue("areaReduction.${newData.phaseNum}.executeTime",newData.executeTime)
                                    config.setValue("areaReduction.${newData.phaseNum}.reductionRate",newData.reductionRate)
                                    config.setValue("areaReduction.${newData.phaseNum}.spawnableCarePackage",newData.spawnableCarePackage)
                                    config.setValue("areaReduction.${newData.phaseNum}.areaDamage",newData.areaDamage)
                                    config.setValue("areaReduction.${newData.phaseNum}.areaDamageBuffer",newData.areaDamageBuffer)
                                }
                                loadPhaseMenu()
                                //

                                deleteMenu.changeGUI(player,phaseMenu)
                            }
                    phaseDataMenu.openChildGUI("delete",player)
                }

        phaseDataMenu
                .setItem(0,waitTime)
                .setItem(1,executeTime)
                .setItem(2,reductionRate)
                .setItem(3,carePackage)
                .setItem(4,areaDamage)
                .setItem(5,areaBuffer)
                .setItem(6,deleteButton)
    }

    private fun writePhaseData(phaseData: PhaseData,button:GUIItem){
        button.setDisplay("§c§l[${phaseData.phaseNum}]§a§lの設定")
                .setLore(arrayOf("§d縮小待機時間: §e${phaseData.waitTime}"
                        ,"§d縮小時間: §e${phaseData.executeTime}"
                        ,"§d縮小率: §e${phaseData.reductionRate*100}%"
                        ,"§dケアパッケージ: §e${if(phaseData.spawnableCarePackage)"出現する" else "出現しない"}"
                        ,"§dエリアダメージ: §e${phaseData.areaDamage}"
                        ,"§dエリアバッファー: §e${phaseData.areaDamageBuffer}§bM"))
                .reload()
    }
}