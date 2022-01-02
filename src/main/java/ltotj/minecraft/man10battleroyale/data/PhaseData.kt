package ltotj.minecraft.man10battleroyale.data

data class PhaseData(    var phaseNum:Int,
                         var waitTime:Int,
                         var executeTime:Int,
                         var reductionRate:Double,
                         var spawnableCarePackage:Boolean,
                         var areaDamage:Double,
                         var areaDamageBuffer:Int
)
