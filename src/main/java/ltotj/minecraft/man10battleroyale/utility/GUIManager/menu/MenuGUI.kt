package ltotj.minecraft.man10battleroyale.utility.GUIManager.menu

import ltotj.minecraft.man10battleroyale.utility.GUIManager.GUIInventory
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

open class MenuGUI: GUIInventory {

    private val children= HashMap<String, MenuGUI>()
    lateinit var siblings:HashMap<String, MenuGUI>
    private var turnBack=true
    private var parent: MenuGUI?=null

    constructor(plugin: JavaPlugin,row:Int,title:String):super(plugin, row, title)

    constructor(plugin: JavaPlugin, row:Int, title:String, parent: MenuGUI, key:String, isInstant:Boolean):super(plugin, row, title){
        parent.addChildGUI(key,this)
        siblings=parent.children
        setCloseEvent(event={
            if(turnBack){
                back(it.player as Player)
            }
        })
        if(isInstant){
            setCloseEvent { guiInventory, _ ->
                if(guiInventory.invPlayerList.isEmpty()){
                    (guiInventory as MenuGUI).parent?.removeChildGUI(key)
                }
            }
        }
    }

    fun parent(): MenuGUI?{
        return parent
    }

    fun children():HashMap<String, MenuGUI>{
        return children
    }

    fun addChildGUI(key:String,childMenu: MenuGUI): MenuGUI {
        childMenu.parent=this
        children[key]=childMenu
        return this
    }

    fun removeChildGUI(key: String): MenuGUI {
        children.remove(key)
        return this
    }

    fun openChildGUI(key:String,player: Player): MenuGUI {
        if(children.containsKey(key)){
            forceChangeGUI(player,children[key]!!)
        }
        else{
            player.sendMessage("§4${key}は設定されていません")
        }
        return this
    }

    fun openSiblingGUI(key:String,player:Player):Boolean{
        if(siblings.containsKey(key)){
            forceChangeGUI(player,siblings[key]!!)
            return true
        }
        return false
    }

    fun deleteChild(key:String): MenuGUI {
        children.remove(key)
        return this
    }

    fun back(player:Player) {
        if(parent!=null){
            changeGUI(player, parent!!)
        }
    }

}