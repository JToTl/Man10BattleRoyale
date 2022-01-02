package ltotj.minecraft.man10battleroyale.utility.TimeManager

import java.lang.Thread.sleep
import java.util.*
import java.util.function.Consumer


class TimerManager {

    private var unitTime=1000
    private val variables=HashMap<String,Any>()
    private var remainingTime=0
    private var totalTime=0
    private val startEvents= ArrayList<Runnable>()
    private val intervalEvents=HashMap<Int,ArrayList<Consumer<Int>>>()
    private val endEvents=ArrayList<Runnable>()
    private var available=true
    private var isRunning=false

    fun isRunning():Boolean{
        return isRunning
    }

    fun addVariable(key:String,any:Any):TimerManager{
        variables[key]=any
        return this
    }

    fun addIntervalEvent(interval:Int,event:Consumer<Int>): TimerManager {
        if(!intervalEvents.containsKey(interval)){
            intervalEvents[interval]= ArrayList()
        }
        intervalEvents[interval]!!.add(event)
        return this
    }

    fun addEndEvent(event:Runnable): TimerManager {
        endEvents.add(event)
        return this
    }

    fun addStartEvent(event:Runnable): TimerManager {
        startEvents.add(event)
        return this
    }

    fun addRemainingTime(time:Int): TimerManager {
        remainingTime+=time
        return this
    }

    fun addTotalTime(time:Int): TimerManager {
        totalTime+=time
        return this
    }

    fun forcedStart(){
        Thread{
            for (event in startEvents) {
                event.run()
            }
            while (remainingTime >0) {
                if (!available) {
                    isRunning = false
                    return@Thread
                }

                for (interval in intervalEvents.keys) {
                    if (totalTime % interval == 0) {
                        for (event in intervalEvents[interval]!!) {
                            event.accept(remainingTime)
                        }
                    }
                }
                sleep(1000)
                remainingTime--
                totalTime++
            }
            for (event in endEvents) {
                event.run()
            }
        }
                .start()
    }

    fun getRemainingTime():Int{
        return remainingTime
    }

    fun getTotalTime():Int{
        return totalTime
    }

    fun getVarInt(key:String):Int{
        return (variables[key]?:0 as Int) as Int
    }

    fun reduceTime(time:Int): TimerManager {
        remainingTime-=time
        return this
    }

    fun reduceTotalTime(time:Int): TimerManager {
        totalTime-=time
        return this
    }

    fun setRemainingTime(time:Int): TimerManager {
        remainingTime=time
        return this
    }

    fun setTotalTime(time:Int): TimerManager {
        totalTime=time
        return this
    }

    fun setUnitTime(unit:Int):TimerManager{
        unitTime=unit
        return this
    }

    fun stop(){
        available=false
    }

    fun start(){
        println("通った")
        if(isRunning){
            println("通っちゃった")
            return
        }
        isRunning=true
        Thread{
            for (event in startEvents) {
                event.run()
            }
            while (remainingTime >0) {
                if (!available) {
                    isRunning = false
                    return@Thread
                }

                for (interval in intervalEvents.keys) {
                    if (totalTime % interval == 0) {
                        for (event in intervalEvents[interval]!!) {
                            event.accept(remainingTime)
                        }
                    }
                }
                sleep(unitTime.toLong())
                remainingTime--
                totalTime++
            }
            for (event in endEvents) {
                event.run()
            }
        }.start()
    }

}