package ltotj.minecraft.man10battleroyale.table

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream

class LootItem(base64s:List<String>,val weight:Long) {

    val items=ArrayList<ItemStack>()
    var fillSlot=0

    fun itemFromBase64(data: String): ItemStack? = try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            items[i] = dataInput.readObject() as ItemStack
        }

        dataInput.close()
        items[0]
    } catch (e: Exception) {
        null
    }

    init {
        for(str in base64s){
            items.add(itemFromBase64(str)?:continue)
            fillSlot++
        }
    }

}