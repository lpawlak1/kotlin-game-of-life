package agh.ics.gameoflife.statistics

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.map.IWorldMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class Statistics {
    lateinit var map: IWorldMap

    lateinit var amountOfDeadAnimalsMS: MutableState<Long>
    lateinit var grassAmountMS: MutableState<Int>
    lateinit var animalsAmountMS: MutableState<Int>
    lateinit var varDeadLifeSpanMS: MutableState<Long>
    lateinit var varLivingEnergyMS: MutableState<Long>
    lateinit var numberOfDay: MutableState<Long>
    lateinit var maxGenotypeMS: MutableState<String>
    lateinit var maxGenotypeAmountMS: MutableState<Int>
    lateinit var avgNumberOfChildsMS: MutableState<Double>

    var sumOfDeadLifeSpan: Long = 0
    var amountOfDeadAnimals: Long = 0

    private var number_of_day_lame = 0L

    var animals: MutableList<Animal> = mutableListOf()
    var grassAmount: Int = 0

    var dataList = mutableListOf<RowData>()

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
    var deathDate: Long = 0L
    var avgChildren: Double = 0.0
    lateinit var trackedAnimalChildrenMS: MutableState<Long>
    lateinit var trackedAnimalAncestorsMS: MutableState<Long>
    lateinit var trackedAnimalDeathDateMS: MutableState<Long>
    lateinit var trackedAnimalLifeSpanMS: MutableState<Long>

    @Composable
    fun init() {
        this.numberOfDay = remember { mutableStateOf(0) }
        this.grassAmountMS = remember { mutableStateOf(0) }
        this.animalsAmountMS = remember { mutableStateOf(0) }
        this.varDeadLifeSpanMS = remember { mutableStateOf(0L) }
        this.varLivingEnergyMS = remember { mutableStateOf(0L) }
        this.amountOfDeadAnimalsMS = remember { mutableStateOf(0L) }
        this.maxGenotypeMS = remember { mutableStateOf("") }
        this.maxGenotypeAmountMS = remember { mutableStateOf(0) }
        this.avgNumberOfChildsMS = remember { mutableStateOf(0.0) }

        this.trackedAnimalChildrenMS = remember { mutableStateOf(0L) }
        this.trackedAnimalAncestorsMS = remember { mutableStateOf(0L) }
        this.trackedAnimalDeathDateMS = remember { mutableStateOf(0L) }
        this.trackedAnimalLifeSpanMS = remember { mutableStateOf(0L) }
    }

    fun recalculate() {
        this.number_of_day_lame++
        this.numberOfDay.value = this.number_of_day_lame
        this.grassAmountMS.value = this.grassAmount
        this.animalsAmountMS.value = this.animals.size


        if (deathDate == 0L && (trackedAnimal?.energy ?: 20) <= 0) {
            deathDate = this.number_of_day_lame - 1
            trackedAnimalDeathDateMS.value = deathDate
            trackedAnimalLifeSpanMS.value = trackedAnimal!!.lifeSpan.toLong()
        }
        if (trackedAnimal != null) {
            trackedAnimalChildrenMS.value = trackedAnimal!!.amountOfChildren.toLong()
            trackedAnimalAncestorsMS.value = trackedAnimal!!.amountOfAncestors.toLong()
        }

        this.amountOfDeadAnimalsMS.value = this.amountOfDeadAnimals

        if (animals.size != 0) {
            this.avgChildren =
                (this.animals.sumOf { it.amountOfChildren }.toDouble() / this.animals.size.toDouble()).round(2)
            this.avgNumberOfChildsMS.value = this.avgChildren
        } else {
            this.avgNumberOfChildsMS.value = 0.0
            this.avgChildren = 0.0
        }

        val genotypeMap = mutableMapOf<Array<Int>, Int>()

        animals.forEach {
            genotypeMap.getOrPut(it.genes) { 0 }
            genotypeMap[it.genes] = genotypeMap[it.genes]!! + 1
        }
        val value = genotypeMap.maxByOrNull { it.value }
        if (value != null && value.component2() > 1) {
            val (maxGenotype, maxValue) = value
            this.maxGenotypeMS.value = maxGenotype.toString()
            this.maxGenotypeAmountMS.value = maxValue
        } else {
            this.maxGenotypeMS.value = ""
            this.maxGenotypeAmountMS.value = 0
        }


        val varLivingEnergy: Long
        val varDeadAnimalLifeSpan: Long

        if (this.animals.isNotEmpty()) {
            varLivingEnergy = this.animals.sumOf { it.energy.toLong() } / this.animals.size.toLong()
            this.varLivingEnergyMS.value = varLivingEnergy
        } else {
            varLivingEnergy = 0L
            this.varLivingEnergyMS.value = 0L
        }

        if (this.amountOfDeadAnimals != 0L) {
            varDeadAnimalLifeSpan = this.sumOfDeadLifeSpan / amountOfDeadAnimals
            this.varDeadLifeSpanMS.value = varDeadAnimalLifeSpan
        } else {
            varDeadAnimalLifeSpan = 0L
            this.varDeadLifeSpanMS.value = 0L
        }
        dataList.add(
            RowData(
                this.number_of_day_lame,
                this.animals.size,
                this.grassAmount,
                varLivingEnergy.toDouble(),
                varDeadAnimalLifeSpan.toDouble(),
                avgChildren
            )
        )
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
