package ltotj.minecraft.man10battleroyale.utility.GUIManager

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

//Shoさんのやつの猿真似
abstract class GUIInventory(private val plugin: JavaPlugin,val row:Int,val title:String):Listener{

    companion object{
        val changingInvPlayer=ArrayList<UUID>()
        val guiExecutor: Executor =Executors.newCachedThreadPool()
    }

    protected val inv=Bukkit.createInventory(null,row*9, Component.text(title))

    protected val items=HashMap<Int, GUIItem>()

    private val onCloseEvents=ArrayList<Consumer<InventoryCloseEvent>>()
    private val onForcedCloseEvents=ArrayList<Consumer<InventoryCloseEvent>>()
    private val onClickEvents=ArrayList<Consumer<InventoryClickEvent>>()

    private val asyncOnCloseEvents=ArrayList<Consumer<InventoryCloseEvent>>()
    private val asyncOnForcedCloseEvents=ArrayList<Consumer<InventoryCloseEvent>>()
    private val asyncOnClickEvents=ArrayList<Consumer<InventoryClickEvent>>()

    val invPlayerList=ArrayList<UUID>()

    fun getInvItem(slot:Int):ItemStack{
        return inv.getItem(slot)?: ItemStack(Material.AIR)
    }

    fun getItem(slot:Int): GUIItem?{
        return items[slot]
    }

    fun setItem(slot:Int,item: GUIItem): GUIInventory {
        items[slot]=item
        item.setGUI(this)
        return this
    }

    fun setItems(slots:Array<Int>,item: GUIItem): GUIInventory {
        for(i in slots){
            items[i]=item
        }
        item.setGUI(this)
        return this
    }

    fun fillItem(item: GUIItem): GUIInventory {
        for(i in 0 until row*9){
            items[i]=item
        }
        item.setGUI(this)
        return this
    }

    fun removeInvItem(slot:Int): GUIInventory {
        inv.setItem(slot,ItemStack(Material.STONE,0))
        return this
    }

    fun removeInvItem(item:ItemStack): GUIInventory {
        inv.remove(item)
        return this
    }

    fun removeItem(slot:Int): GUIInventory {
        items.remove(slot)
        return this
    }

    fun removeItems(slots:Array<Int>): GUIInventory {
        for(i in slots){
            items.remove(i)
        }
        return this
    }

    fun renderGUI(): GUIInventory {
        inv.clear()
        for(i in items.keys){
            inv.setItem(i,items[i])
        }
        return this
    }

    fun renderItem(slot: Int): GUIInventory {
        inv.setItem(slot,items[slot])
        return this
    }

    fun clear(): GUIInventory {
        items.clear()
        return this
    }

    fun setClickEvent(action: (GUIInventory, InventoryClickEvent) -> Unit): GUIInventory {
        setClickEvent(event = {
            action(this,it)
        })
        return this
    }

    fun setClickEvent(event:Consumer<InventoryClickEvent>): GUIInventory {
        onClickEvents.add(event)
        return this
    }

    fun setAsyncClickEvent(action: (GUIInventory, InventoryClickEvent) -> Unit): GUIInventory {
        setAsyncClickEvent(event = {
            action(this,it)
        })
        return this
    }

    fun setAsyncClickEvent(event:Consumer<InventoryClickEvent>): GUIInventory {
        asyncOnClickEvents.add(event)
        return this
    }

    fun setCloseEvent(action: (GUIInventory, InventoryCloseEvent) -> Unit): GUIInventory {
        setCloseEvent(event = {
            action(this,it)
        })
        return this
    }

    fun setCloseEvent(event:Consumer<InventoryCloseEvent>): GUIInventory {
        onCloseEvents.add(event)
        return this
    }

    fun setAsyncCloseEvent(action: (GUIInventory, InventoryCloseEvent) -> Unit): GUIInventory {
        setAsyncCloseEvent(event = {
            action(this,it)
        })
        return this
    }

    fun setAsyncCloseEvent(event:Consumer<InventoryCloseEvent>): GUIInventory {
        asyncOnCloseEvents.add(event)
        return this
    }

    fun setForcedCloseEvent(event:Consumer<InventoryCloseEvent>): GUIInventory {
        onForcedCloseEvents.add(event)
        return this
    }

    fun setForcedCloseEvent(action: (GUIInventory, InventoryCloseEvent) -> Unit): GUIInventory {
        setForcedCloseEvent(event = {
            action(this,it)
        })
        return this
    }

    fun open(player:Player){
        println("${title}のviewerは${inv.viewers.size}")
        if(inv.viewers.isEmpty()) {
            renderGUI()
            plugin.server.pluginManager.registerEvents(this,plugin)
        }
        plugin.server.scheduler.runTask(plugin, Runnable {
            player.openInventory(inv)
            invPlayerList.add(player.uniqueId)
        })
    }

    fun close(player:Player){
        plugin.server.scheduler.runTask(plugin, Runnable {
            player.closeInventory()
            invPlayerList.remove(player.uniqueId)
        })
    }

    fun forceChangeGUI(player:Player,gui: GUIInventory){
        invPlayerList.remove(player.uniqueId)
        if(invPlayerList.isEmpty()){
            HandlerList.unregisterAll(this)
        }
        changingInvPlayer.add(player.uniqueId)
        gui.open(player)
        changingInvPlayer.remove(player.uniqueId)
    }

    fun changeGUI(player:Player,gui: GUIInventory){
        plugin.server.scheduler.runTask(plugin, Runnable {
            changingInvPlayer.add(player.uniqueId)
            gui.open(player)
            changingInvPlayer.remove(player.uniqueId)
        })
    }

    @EventHandler
    fun onClose(e:InventoryCloseEvent){
        if(!invPlayerList.contains(e.player.uniqueId))return
        invPlayerList.remove(e.player.uniqueId)
        if(inv.viewers.size==1){
            HandlerList.unregisterAll(this)
        }
        if(changingInvPlayer.contains(e.player.uniqueId)){
            changingInvPlayer.remove(e.player.uniqueId)
        }
        else{
            for(event in onCloseEvents){
                event.accept(e)
            }
            for(event in asyncOnCloseEvents){
                guiExecutor.execute {
                    event.accept(e)
                }
            }
        }
        for(event in onForcedCloseEvents){
            event.accept(e)
        }
        for(event in asyncOnForcedCloseEvents){
            event.accept(e)
        }

    }

    @EventHandler
    fun onClick(e: InventoryClickEvent){
        if(!invPlayerList.contains(e.whoClicked.uniqueId))return
        for(event in onClickEvents){
            event.accept(e)
        }
        for(event in asyncOnClickEvents){
            guiExecutor.execute {
                event.accept(e)
            }
        }
        if(items.containsKey(e.rawSlot)){
            items[e.rawSlot]!!.executeClickEvent(e)
        }
    }

}