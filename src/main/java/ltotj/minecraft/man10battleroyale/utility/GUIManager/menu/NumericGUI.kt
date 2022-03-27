package ltotj.minecraft.man10battleroyale.utility.GUIManager.menu

import ltotj.minecraft.man10battleroyale.utility.GUIManager.GUIItem
import ltotj.minecraft.man10battleroyale.utility.ItemManager.ItemStackPlus
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.util.function.Consumer

open class NumericGUI: MenuGUI {

    constructor(plugin: JavaPlugin,  title:String):super(plugin, 6, title)

    constructor(plugin: JavaPlugin, title:String, parent: MenuGUI, key:String, isInstant:Boolean):super(plugin, 6, title, parent, key,isInstant)

    var inputNum=""
    private var function:Consumer<Double>?=null

    init {

        //0から9まで
        setItem(47, GUIItem(getButtonHead("0"))
                .setEvent { _, _ ->
//                    if(inputNum.isNotEmpty()&&inputNum.length<9)
                    if(inputNum.length<9){
                        sucNum("0")
                        inputNum+=0
                    }
                })
        setItem(37, GUIItem(getButtonHead("1"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("1")
                        inputNum+=1
                    }
                })
        setItem(38, GUIItem(getButtonHead("2"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("2")
                        inputNum+=2
                    }
                })
        setItem(39, GUIItem(getButtonHead("3"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("3")
                        inputNum+=3
                    }
                })
        setItem(28, GUIItem(getButtonHead("4"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("4")
                        inputNum+=4
                    }
                })
        setItem(29, GUIItem(getButtonHead("5"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("5")
                        inputNum+=5
                    }
                })
        setItem(30, GUIItem(getButtonHead("6"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("6")
                        inputNum+=6
                    }
                })
        setItem(19, GUIItem(getButtonHead("7"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("7")
                        inputNum+=7
                    }
                })
        setItem(20, GUIItem(getButtonHead("8"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("8")
                        inputNum+=8
                    }
                })
        setItem(21, GUIItem(getButtonHead("9"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("9")
                        inputNum+=9
                    }
                })
        setItem(46, GUIItem(getButtonHead("back"))
                .setEvent { _, _ ->
                    if(inputNum.isNotEmpty()) {
                        backNum()
                        inputNum = inputNum.substring(0, inputNum.length - 1)
                    }
                })
        setItem(48, GUIItem(getButtonHead("enter"))
                .setEvent{ _,_->
                    function?.accept(inputNum.toDoubleOrNull()?:return@setEvent)
                })
        setItem(49, GUIItem(getButtonHead("point"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("point")
                        inputNum+="."
                    }
                })
        setItem(40, GUIItem(getButtonHead("minus"))
                .setEvent { _, _ ->
                    if(inputNum.length<9){
                        sucNum("minus")
                        inputNum+="-"
                    }
                })

        setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }
    }

    fun setOutput(action:Consumer<Double>): NumericGUI {
        function=action
        return this
    }

    //力技
    protected fun getButtonHead(str:String):ItemStack{
        return when(str){
            "0"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAJ3sidGV4dCI6IjAiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfXQBZEg0c0lBQUFBQUFBQS8rTmlZT0JpNEFuT0xzM0pDU2pLVDh2TVNlVmk0QUt5\n" +
                    "Q2xLTFNqSlRpemtaT0VwU0swcEtpMUtMdVJnWUdCZzVHRmpERW5OS1V4azJwRlo2R1VSRlpCaWtS\n" +
                    "SGpsSkZkNm1nSDVJY0VHT2Y2ZVdRWG1ubmxobFVuT25tYWV1VUI1RDBjem4wcExKTFdtSlluaHBq\n" +
                    "bVJ4bDRaVVhtQnBVbTVZUVkreGtFNXFSNUJoc201b1dXKzdtR1pVU0VwT1g0aG5oVitJYTVHL3VG\n" +
                    "QUhKS1RFV25rYXhScEZKWWJHWktTNlZmbGx4dmw3bG5oNjVLUkhabVZiaHlaNjJrVTVlNlc0NXVW\n" +
                    "YnVBZkhnWFU3Mm5nVitWcUVKbmxhZXlaWjJpWkZtaHJ5OERBemNEa21RTDBCSXZTalBPZkRpNTFx\n" +
                    "bGhVM3R1V3VQN2hNUVlHQUp0aUxzUUdBUUFB")?:ItemStack(Material.BARRIER)
            "1"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjEiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AWRINHNJQUFBQUFBQUEvMDJQTzA3RFFCUkZId2hRTUhSVTdNSWZiSlNDQXNX\n" +
                    "RXpDaGpFOGUvc1dnU2V5Si9aa0xrMkNnek5KUzBiSUJWc0FoV3dqcHdTWGVQem1tdUJxREI1Ykxw\n" +
                    "T1g5cVh6WVZaeHBvdzlxeHRxdlkvaHhHSFR0MGZjdjJHZ0FjamVBMFh2R2V3VGVUV00vU1VpOVN6\n" +
                    "SE9KbklIRHBjNTlWTzl1MFRhVzZ3bHlrQmo4N042WnkvRy8xdTVXaWMycGhjdHN1K2pYSXRiblZz\n" +
                    "RFpMREJ5RWIxNmFscFJoU3RxWmhVUnVDRXFOL3drTW1rOTViNmJDYXFLMGxPTjRic3hKK0dEOU14\n" +
                    "Z2FDSXJFMFFTRjkzNG9TZjhKT0NrWGh5b0lIWVdSb3BNOEhpVDZuY0FGM0NNaXVISUNaVDQ0L25u\n" +
                    "OGVycjgrMzY3TGQvQi9nRHNSUW1MZ29CQUFBPQ==")?:ItemStack(Material.BARRIER)
            "2"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjIiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AVhINHNJQUFBQUFBQUEvMDFQTzA3RE1CaitRU0MxaGt0d0ExZEpnN29nSVFL\n" +
                    "dG85cUdOaSt5dGFuYjJIRktsY1pBZW9KZW8ySmw0QUNJaGVOd0FGWThNbjN2NFVNQUNNNm5wZEg2\n" +
                    "dm41YVNpMFFJTXMyb202azJIYWgwNGpYeHRSaWl3RGdxQU9uOFV3YkFSK2lEWENXRm5pUkJqcHZp\n" +
                    "V2QxT01XYUU3VzVKT3U0bmQ4UWoxUTJIMTE3NDNid3I5dHZaa2xmUHpwQmthMGZ6THlLOGRpWmFE\n" +
                    "R2E5UElxZW1aRFZ2SWsxbGtTdWR5bm1LbVZ4YnZTY3BmNVJjSDhUR2FLdnRDUVlPN0hpaXBXc1Yz\n" +
                    "a3NKQkpQclJadUxKK3ZtUCtMYVpoYVRlNVhLYTlBY0FaSEpPRlBYSHllN0YzM24vbzFlSHp6ZjNx\n" +
                    "Zmh1QVAvdGRJWjBHQVFBQQ==")?:ItemStack(Material.BARRIER)
            "3"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjMiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AVxINHNJQUFBQUFBQUEvMDJQVDFMQ01CeEdmem82QTVVMTUyaHRVNGNsS2tv\n" +
                    "NkpKV1Mvc3NPU3BpbXBNaVUxcUc1QURmd0hHNVljZzF2b09ld1MzZmZtL2MybndGZ3dHQ3hiWlI2\n" +
                    "cTk0M1Vna0RqRzd0UlZWTGNlaERyeGJIdXFuRXdRQ0FxeDdjUmt2VkNQZ1NyV2Z5SkRmWGlhZXlG\n" +
                    "cnNkczRXcGZGenNIL0F1YWxkUDJNVmw1NmRqZDlhTy9yV29Yc1pJcGJhWDg5MjhXWldST2JNREph\n" +
                    "YUJsWlhoQjRubkpvOWZGR0VFRVUwY1dqem10QXhLR29jT1oxdUxzaFNSZTR4b1BIRlNUU1ZoVVVF\n" +
                    "MGwvNHJ0dnpuekNMYXkxTTlSajdqTXRYRTVpeVVtOFFhQWR6Qk5WNTNKMjVPQS9WN1BOUCthWEw1\n" +
                    "L0I3K0RBSCtBTEYrMU5vR0FRQUE=")?:ItemStack(Material.BARRIER)
            "4"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjQiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AVxINHNJQUFBQUFBQUEvMDJQUDA3RE1CeEdmeUFRYlVEaUttNmFCSFhvZ0lo\n" +
                    "b2JkVk9tNUIvbm1oVFY0bmpsQ3BORU00eHVBQVRPeXk5Q1F1bklTUGI5L1RlOGhrQUJ0d0VaYXZV\n" +
                    "c243WkZVb1lZUFRySU9xbUVNY2hEQnJ4MXJTMU9Cb0FjRGFBeTJpdFdnRmZRaFBFa3h4dEU2SXlq\n" +
                    "WjJlbndLa1BDd1BkM2dmNmMwRGRuRFYrL205czlDVGY2M2RyR05icFdPUzgvMnEzVlFSV294OUpl\n" +
                    "YitLS3ZDVis0U3hicThTaVhXek1XSVNxcjVqR3BtWW92R3ZFaE5qSmpKU3lwOVNlTkh5V1ZtVXJl\n" +
                    "MFdCZDJYSkxTaTZuMjNORDJYR3d4MHk5WVFDYTdCRTBCcnVFY2Ivc1RGeitqMDlYMGZmYjgvWEdp\n" +
                    "cTkvYlQ0QS8yWGU0Q1FZQkFBQT0=")?:ItemStack(Material.BARRIER)
            "5"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjUiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AVRINHNJQUFBQUFBQUEvMDJQVDA3Q1FCeUZmeG8xT0xMeElDYlRRbXRZRWlV\n" +
                    "d0RUTzEwTCt6Z3pMWTRneVMwcHAyam1UMEFoNkdlN2pDV2JwNjM4djNOZzhCSU9ndjN4b3BYNnIz\n" +
                    "YlNrRkFtVG9JS3E2Rk1kYjZOV2lyWnRLSEJFQVhQVGdPbDdKUnNDWDZEek0wd0p2VWsvbUhYRk5E\n" +
                    "NWRZK21SM2VDVDd1RnMvRVpjbzQyZGpkOTZOL20yZGVwVTRNaHQ0QmQ4SHpWckZlRDVZU0RGYldM\n" +
                    "bUtQcGdLTEdaSE9sTjA2RThuTmd0elRhZVJwc21rWlNiNUxtaDVPTmJHV1R4aGlqNi9Pc2JabVNh\n" +
                    "WTZneFRtd3g1R0dHbWVjRkRVbTVUYXdSd0I1ZGtZdzVjZVovbzkzenkrZmZOL2NQNTU5UUgrQU5G\n" +
                    "VUVJZkFnRUFBQT09")?:ItemStack(Material.BARRIER)
            "6"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjYiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AVxINHNJQUFBQUFBQUEvMDJQVFU2RFFCekYveG8xRlQySks1QVAwNld4aGc2\n" +
                    "QnFWSmdtTm0xZEJvWWh0cndvUXlKY1dXNGhrZndKcDdGRzhqUzNYdjV2ZC9pYVFBYVhLL0xUc3Fu\n" +
                    "K21WZlNLNkJOcVVqcjl1Q041Y3dhM25mZGpWdk5BQTRtY0Y1c3BFZGgyK3VQSjJsdWI1TFBaa3A1\n" +
                    "RXc5V3V0eWhjVHhEaDBTdFgxQURxb212cngzZkRYL3Q3WGJEYkVsTmIyY0haNjdiWlhvdmhsS3Zn\n" +
                    "eU5ySXBmZ3lIUXFRaHVtUnViYkVITkZVRVdyVHlCQjJUZ1JXbFRFdWFNVUlYZFVPRG8wV0lpeVZt\n" +
                    "RUJoWmxQUmFsRlpDNFp5NFd1RUpxY3Q0b1FjVStOZVlBVjNDS2R0T0pzL1FySGEzR0h6OHZibjdH\n" +
                    "OTQ5ZmdEK2g5RXd0QmdFQUFBPT0=")?:ItemStack(Material.BARRIER)
            "7"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjciLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AWBINHNJQUFBQUFBQUEvMDJQVDA2RFFCeUZmeHBOS25vREZ5N2N1RExUSWpR\n" +
                    "c3VxaWEyQ0dkcVZBWUNydlNUb1Zod0lZLzFXSGhCWXgzOFN6ZXdYczRTM2Z2NWZ2eWttY0FHSEN4\n" +
                    "TERvcG4rdlhYUzY1QVlaT2UxNjNPVy9PWU5EeTk3YXJlV01Bd05FQVR0bGFkaHkrdVhKUnNzclFk\n" +
                    "dVhLamNLMjdzRVN5UVVXK3pHdW1Fb2ZzSTFMeldkVGU2NmNmNjdWcmlOTHhxYWJKWlhYcFNWRGM5\n" +
                    "T1hmT1lQTjJWNG9LV2YwNUxsUkxDQ1JFa1dSNjRrL2IxSWhJZm9reStvSUgwc3dqdlNZMFI3cHRu\n" +
                    "TDJ5Skljam9LRlgyY3FqaWdNdTQ5VSs4VWRJUXRYQTJkblRlWkFKekRNZDdxRXlmWDQ4K2ZtMS95\n" +
                    "OGVXa2w4M3RGUWI0QTNqSVF5VUdBUUFB\n")?:ItemStack(Material.BARRIER)
            "8"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjgiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AWRINHNJQUFBQUFBQUEvMDJQVFU2RFFCaUdQLytTaWw3QVc0d1FNRjI0VUZs\n" +
                    "MEpwM0JBcVVNdTVaT3k4OVFDWVdHbWExbjhBWk52SUJMWStJdFBJc3JXYnA3bmp6djVqVUFETGdP\n" +
                    "eWs3SzUrWmxrMHRoZ0RGUUxabzJGL3RMR0xXaWI3dEc3QTBBT0JuQlJiU1VuWUFQb1FoSzRneXRZ\n" +
                    "eUpUaFozQnd3QkpEeGYxSGQ1RmF2V0VIVndOZmZMZ1ROWDQzOVp1bHd0YmNvdGt5VzdXcmFvSVRT\n" +
                    "MWZpb2wvbTFiekF3dkwzZ3RuTnRNc295YnZtVWxLSHBhRFp6Snh1V0thYXMrbHRoZW1paGJZWXRy\n" +
                    "UG1UdEhWRzlOcnJlYWhyS2dybC9SQlVkSmtTS3VTYzRDTXQ3RTZCN2dDazd4ZWpoeWZ2UHRmaDN0\n" +
                    "eCtQYjJldm56L3R2RGZBSCtzU3pKQW9CQUFBPQ==")?:ItemStack(Material.BARRIER)
            "9"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAKHsidGV4dCI6IjkiLCJpdGFsaWMiOmZhbHNlLCJi\n" +
                    "b2xkIjp0cnVlfX10AWxINHNJQUFBQUFBQUEvMDJQVDA2RFFCeEdmeHBOS25vU1YxQUNwb3N1cW9S\n" +
                    "MlNHRWNPa0NaWGY5TUEzUUdHd290Y0FkWFhzR05XK1BLQ3hqZGV3TlA0QWxrNmU1OWVkL21LUUFL\n" +
                    "WE0yMmxSRDN4Y01tRlZ3QnBhTWRMOHFVN3krZ1YvSzZyQXErVndEZ3BBZm40VUpVSE41NDQ2aHNu\n" +
                    "cWpydVNOV0RUSzdUV2Vxd0NqYjNhQThiSlozeUVTeTg1T1JPVzBHLzc1R3VZZ01FZXRPd25KU0xX\n" +
                    "V29UblZmOEltdnJXUndZRFRXNDc0dHZUWXczQWkxZUJ6VWJoWW0yQnJWWHA5b2VPekp1STFWdC9W\n" +
                    "U0ptMkpMVDlqRmpGd1JIUk1mZWxaOXRhbHQ1MkxHMGJERkZ2a2lISnRzQ0hESWNBbG5LSjFGM0wy\n" +
                    "OGZWTm4xNUhoK2YzeDUvcmw4OWZnRDh2RXg4UUNnRUFBQT09\n")?:ItemStack(Material.BARRIER)

            "enter"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAPXsidGV4dCI6IuaxuuWumiIsIml0YWxpYyI6ZmFs\n" +
                    "c2UsImNvbG9yIjoiZ3JlZW4iLCJib2xkIjp0cnVlfX10AWBINHNJQUFBQUFBQUEvMDJQeTFLRE1C\n" +
                    "aUZmMjh6RlIyZkpSYkJZZUVDdFdQRGxDQ1VVcEpkTDZra1RXcUhRbTFZTzdyekVmUUIzUG9JanM4\n" +
                    "bDd0eWQ4NTF2Y3l3QUMwNkh5MXFwKy9KeElSUzN3R3JUbXBlVjRKdGo2RlI4VjlVbDMxZ0FzTmVC\n" +
                    "bzJ5aWFnNWYzQVNJNVFXYTU0R2FHZXkyUFIwaUZXRzV2c1NyekV4dnNJdDF1L2Q5ZDJDOGY2NVRU\n" +
                    "Y2FPb25aUXNGVmNUM1dHQm5haWVEODVuK25SbHFaTHc5Snd4elFUSkZVeVNxOExLdjBMcGtPSDNm\n" +
                    "M3hrU0h5NFNtVWZrUEd2WWFtc1UyYjJEQ2RLU0pERkVvaWFEY1I1QlozUTUxSktwVWdJdkFXT2Jv\n" +
                    "Q09JRjlQRytQSEI0RVorK3Z2ZWpueFh2Ky9INzdLQUYrQVN0NmZmY0tBUUFB\n")?:ItemStack(Material.BARRIER)

            "back"->itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAC1BMQVlFUl9IRUFEc3EAfgAAc3EAfgAD\n" +
                    "dXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+\n" +
                    "AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQAOnsidGV4dCI6IuaIu+OCiyIsIml0YWxpYyI6ZmFs\n" +
                    "c2UsImNvbG9yIjoicmVkIiwiYm9sZCI6dHJ1ZX10AVxINHNJQUFBQUFBQUEvMDJQdTA3RE1CaEdm\n" +
                    "eENnRW5pWVhFaFF4NHBBYXlzWGtwWTQ4UUMwcVl1VDJLVktFOEI5RFZZZWdBa2hNZkphU0t4NFpQ\n" +
                    "dU96bGsrQThDQTAyblRDM0hkUHE0cXdRd3c5TnF3dHF2WTloZ0dIWHZwK3BadERRRFlHOEJoTmhj\n" +
                    "OWcwK21zRWx6Ymk1ekxFcUZQTTJ6cVNsaVZHL08wVHBUaXd2a0lhbjlaT1FGYXZpdmRiczVjVVho\n" +
                    "WUU3WFNiK1FtUms0cVdDVDFDcmx6Uk1scVl4SXFHajk0SVkyYmtLN09JdEp4Q04vNUlSKzFGQ1M3\n" +
                    "T2dzVVhTY1BGT0ptMEplMnJRdW5Lam1kZXlYdXIyU2RFeGxTQ2lQZHJ6U3JscmwxaERnQlBiUlVw\n" +
                    "ODQrUDY5LzdwNzh6L2U4ZEdQRmJ6ZUF2d0JBd0xsbGdZQkFBQT0=\n")?:ItemStack(Material.BARRIER)
            "minus"->itemFromBase64("")?: ItemStackPlus(Material.DIAMOND,1).setDisplay("§d- (仮置き)")
            "point" ->itemFromBase64("")?:ItemStackPlus(Material.EMERALD,1).setDisplay("§d. (仮置き)")
            else -> ItemStack(Material.BARRIER)
        }
    }

    private fun backNum(){
        for(i in 0 until inputNum.length-1){
            cloneItem(16-i,17-i)
        }
        removeItem(18-inputNum.length)
        renderGUI()
    }

    private fun sucNum(str:String){
        for(i in inputNum.length downTo 0){
            cloneItem(17-i,16-i)
        }
        setItem(17, GUIItem(getButtonHead(str)))
        renderGUI()
    }

    fun cloneItem(from:Int,to:Int){
        if(items[from]==null){
            return
        }
        setItem(to,items[from]!!)
    }

    protected fun itemFromBase64(data: String): ItemStack? = try {
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



}