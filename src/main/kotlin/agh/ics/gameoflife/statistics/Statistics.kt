package agh.ics.gameoflife.statistics

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.map.IWorldMap
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi

class Statistics {
    lateinit var map: IWorldMap

    private lateinit var amountOfDeadAnimalsMS: MutableState<Long>
    private lateinit var grassAmountMS: MutableState<Int>
    private lateinit var animalsAmountMS: MutableState<Int>
    private lateinit var avgDeadLifeSpanMS: MutableState<Long>
    private lateinit var avgLivingEnergyMS: MutableState<Long>
    private lateinit var numberOfDay: MutableState<Long>
    private lateinit var maxGenotypeMS: MutableState<String>
    private lateinit var maxGenotypeAmountMS: MutableState<Int>
    private lateinit var avgNumberOfChildrenMS: MutableState<Double>

    var sumOfDeadLifeSpan: Long = 0
    var amountOfDeadAnimals: Long = 0

    private var numberOfDayLame = 0L

    var animals: MutableList<Animal> = mutableListOf()
    var avgChildren: Double = 0.0

    var grassAmount: Int = 0

    var dataList = mutableListOf<RowData>()

    var deathDate: Long = 0L
    var trackedAnimal: Animal? = null
        set(value) {
            field?.isTracked = false

            animals.forEach {
                it.isAncestorFromTracked = false
                it.trackedParent = null
            }

            deathDate = 0L

            value?.amountOfChildren = 0
            value?.isTracked = true

            trackedAnimalChildrenMS.value = 0L
            trackedAnimalAncestorsMS.value = 0L
            trackedAnimalDeathDateMS.value = 0L
            trackedAnimalLifeSpanMS.value = 0L

            field = value
        }


    lateinit var trackedAnimalChildrenMS: MutableState<Long>
    lateinit var trackedAnimalAncestorsMS: MutableState<Long>
    lateinit var trackedAnimalDeathDateMS: MutableState<Long>
    lateinit var trackedAnimalLifeSpanMS: MutableState<Long>

    @Composable
    fun init() {
        this.numberOfDay = remember { mutableStateOf(0) }
        this.grassAmountMS = remember { mutableStateOf(0) }
        this.animalsAmountMS = remember { mutableStateOf(0) }
        this.avgDeadLifeSpanMS = remember { mutableStateOf(0L) }
        this.avgLivingEnergyMS = remember { mutableStateOf(0L) }
        this.amountOfDeadAnimalsMS = remember { mutableStateOf(0L) }
        this.maxGenotypeMS = remember { mutableStateOf("") }
        this.maxGenotypeAmountMS = remember { mutableStateOf(0) }
        this.avgNumberOfChildrenMS = remember { mutableStateOf(0.0) }

        this.trackedAnimalChildrenMS = remember { mutableStateOf(0L) }
        this.trackedAnimalAncestorsMS = remember { mutableStateOf(0L) }
        this.trackedAnimalDeathDateMS = remember { mutableStateOf(0L) }
        this.trackedAnimalLifeSpanMS = remember { mutableStateOf(0L) }
    }

    fun recalculate() {
        this.numberOfDayLame++

        this.numberOfDay.value = this.numberOfDayLame

        this.grassAmountMS.value = this.grassAmount
        this.animalsAmountMS.value = this.animals.size
        this.amountOfDeadAnimalsMS.value = this.amountOfDeadAnimals

        calcTrackedAnimal()

        calcAvgChildren()

        calcGenotypes()

        val avgLivingEnergy = calcAvgLivingAnimalEnergy()

        val avgDeadAnimalLifeSpan = calcAvgDeadAnimalLifeSpan()

        dataList.add(
            RowData(
                this.numberOfDayLame,
                this.animals.size,
                this.grassAmount,
                avgLivingEnergy.toDouble(),
                avgDeadAnimalLifeSpan.toDouble(),
                avgChildren
            )
        )

    }

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getView(engine: IEngine) {
        Text("Grass:  ${grassAmountMS.value}")
        Text("Animals: ${animalsAmountMS.value}")
        Text("Avg of dead animal lifespan: ${avgDeadLifeSpanMS.value}")
        Text("Avg of living animals' energy: ${avgLivingEnergyMS.value}")
        Text("Avg of living animals' children: ${avgNumberOfChildrenMS.value}")
        Text("Dead by today: ${amountOfDeadAnimalsMS.value}")
        Text("Today: ${numberOfDay.value}")
        if (maxGenotypeMS.value != "") {
            Text("Max genotype: \n ${maxGenotypeMS.value}")
            Text("Max genotype amount: ${maxGenotypeAmountMS.value}")
        }
        if (engine is MagicEngine) {
            if (engine.rescueTimes != engine.counter)
                Text("Pozostało: ${engine.rescueTimes - engine.counter} ratunków")
            else
                Text("Wykorzystano wszystkie ratunki")
        }
        if (trackedAnimal != null) {
            Surface(color = Color.hsl(250.0F, 0.37F, 0.5F, 1.0F)) {
                Column {
                    Text("Tracked animal statistics")
                    Text("Children amount ${trackedAnimalChildrenMS.value}")
                    Text("Ancestors amount ${trackedAnimalAncestorsMS.value}")
                    if (trackedAnimal!!.energy <= 0) {
                        Text("Death date: ${trackedAnimalDeathDateMS.value}")
                        Text("Lifespan: ${trackedAnimalLifeSpanMS.value}")
                    }
                }
            }
        }
    }

    private fun calcTrackedAnimal() {
        if (trackedAnimal != null) {
            trackedAnimalChildrenMS.value = trackedAnimal!!.amountOfChildren.toLong()
            trackedAnimalAncestorsMS.value = trackedAnimal!!.amountOfAncestors
            if (deathDate == 0L && (trackedAnimal!!.energy) <= 0) {
                deathDate = this.numberOfDayLame - 1
                trackedAnimalDeathDateMS.value = deathDate
                trackedAnimalLifeSpanMS.value = trackedAnimal!!.lifeSpan.toLong()
            }
        }
    }

    private fun calcAvgChildren() {
        this.avgChildren =
            if (animals.size != 0) {
                (this.animals.sumOf { it.amountOfChildren }.toDouble() / this.animals.size.toDouble())
                    .round(2)
            } else {
                0.0
            }
        this.avgNumberOfChildrenMS.value = this.avgChildren
    }

    private fun calcGenotypes() {
        val genotypeMap = mutableMapOf<Array<Int>, Int>()
        animals.forEach {
            genotypeMap.putIfAbsent(it.genes, 0)
            genotypeMap[it.genes] = genotypeMap[it.genes]!! + 1
        }
        var (maxGenotype, maxAmount) = Pair("", 0)
        val mapEntry = genotypeMap.maxByOrNull { it.value }
        if (mapEntry != null && mapEntry.component2() > 1) {
            maxGenotype = mapEntry.component1().toString()
            maxAmount = mapEntry.component2()
        }
        this.maxGenotypeMS.value = maxGenotype
        this.maxGenotypeAmountMS.value = maxAmount
    }

    private fun calcAvgLivingAnimalEnergy(): Long {
        val avgLivingEnergy: Long = if (this.animals.isNotEmpty()) {
            this.animals.sumOf { it.energy.toLong() } / this.animals.size.toLong()
        } else {
            0L
        }
        this.avgLivingEnergyMS.value = avgLivingEnergy
        return avgLivingEnergy
    }

    private fun calcAvgDeadAnimalLifeSpan(): Long {
        val avgDeadAnimalLifeSpan: Long =
            if (this.amountOfDeadAnimals != 0L) {
                this.sumOfDeadLifeSpan / amountOfDeadAnimals
            } else {
                0L
            }
        this.avgDeadLifeSpanMS.value = avgDeadAnimalLifeSpan
        return avgDeadAnimalLifeSpan
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
