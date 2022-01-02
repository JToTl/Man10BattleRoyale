package ltotj.minecraft.man10battleroyale.utility.GUIManager.menu

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

open class AnimationGUI: MenuGUI {


    constructor(plugin: JavaPlugin,row:Int,title:String):super(plugin, row, title)

    constructor(plugin: JavaPlugin, row:Int, title:String, parent: MenuGUI, key:String, isInstant:Boolean):super(plugin, row, title, parent, key,isInstant)


    fun moveItem(to:Int,from:Int): AnimationGUI {
        if(items.containsKey(from)){
            items[to]=items[from]!!
            items.remove(from)
            renderItem(to)
        }
        inv.setItem(from, ItemStack(Material.STONE,0))
        return this
    }

    fun moveItem(list:MutableList<Int>): AnimationGUI {
        for(index in 0 until list.size-1){
            moveItem(list[index],list[index+1])
        }
        return this
    }

    fun exchangeItem(to:Int,from: Int): AnimationGUI {
        val item=items[to]
        moveItem(to,from)
        if(item!=null){
            items[from]=item
            renderItem(from)
        }
        return this
    }



}
