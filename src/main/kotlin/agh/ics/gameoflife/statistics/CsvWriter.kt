package agh.ics.gameoflife.statistics

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.map.IWorldMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.io.FileOutputStream


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

    var sumOfDeadLifeSpan: Long = 0
    var amountOfDeadAnimals: Long = 0

    private var number_of_day_lame = 0L

    var animals: MutableList<Animal> = mutableListOf()
    var grassAmount: Int = 0

    var dataList = mutableListOf<RowData>()

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
    }

    fun recalculate() {
        this.number_of_day_lame++
        this.numberOfDay.value = this.number_of_day_lame
        this.grassAmountMS.value = this.grassAmount
        this.animalsAmountMS.value = this.animals.size

        this.amountOfDeadAnimalsMS.value = this.amountOfDeadAnimals


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
                this.animals.size,
                this.grassAmount,
                varLivingEnergy.toDouble(),
                varDeadAnimalLifeSpan.toDouble(),
                0.0
            )
        )
    }
}


data class RowData(
    val animalsAmount: Int,
    val grassAmount: Int,
    val varEnergy: Double,
    val varDeadLifeSpan: Double,
    val varLivingChild: Double
) {
    override fun toString(): String {
        return "$animalsAmount,$grassAmount,$varEnergy,$varDeadLifeSpan,$varLivingChild\n"
    }
}

class CsvWriter {
    fun flush(fileName: String, records: List<RowData>) {
        if (records.isNotEmpty())
            FileOutputStream(fileName, true).bufferedWriter().use { writer ->
                writer.append(
                    buildString {
                        records.forEach { this.append(it.toString()) }
                    }
                )
            }
    }
}