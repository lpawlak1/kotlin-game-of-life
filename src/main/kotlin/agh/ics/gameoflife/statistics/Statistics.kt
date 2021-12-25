package agh.ics.gameoflife.statistics

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.map.IWorldMap
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp

class Statistics {
    lateinit var isAnimalTracked: MutableState<Boolean>
    lateinit var map: IWorldMap

    lateinit var genotypeStringMS: MutableState<String>

    var tableList = mutableStateListOf<RowData>()
    var dataList = mutableListOf<RowData>()

    var sumOfDeadLifeSpan: Long = 0
    var amountOfDeadAnimals: Long = 0

    private var numberOfDayLame = 0L

    var animals: MutableList<Animal> = mutableListOf()
    var avgChildren: Double = 0.0

    var grassAmount: Int = 0


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

            this.isAnimalTracked.value = true
        }


    lateinit var trackedAnimalChildrenMS: MutableState<Long>
    lateinit var trackedAnimalAncestorsMS: MutableState<Long>
    lateinit var trackedAnimalDeathDateMS: MutableState<Long>
    lateinit var trackedAnimalLifeSpanMS: MutableState<Long>

    @Composable
    fun init() {
        this.trackedAnimalChildrenMS = remember { mutableStateOf(0L) }
        this.trackedAnimalAncestorsMS = remember { mutableStateOf(0L) }
        this.trackedAnimalDeathDateMS = remember { mutableStateOf(0L) }
        this.trackedAnimalLifeSpanMS = remember { mutableStateOf(0L) }
        this.genotypeStringMS = remember { mutableStateOf("") }

        this.isAnimalTracked = mutableStateOf(false)
    }

    fun recalculate() {
        this.numberOfDayLame++

        calcTrackedAnimal()

        calcAvgChildren()

        val (genotypeStr, _) = calcGenotypes()
        this.genotypeStringMS.value = genotypeStr

        val avgLivingEnergy = calcAvgLivingAnimalEnergy()

        val avgDeadAnimalLifeSpan = calcAvgDeadAnimalLifeSpan()

        val rowDataInst = RowData(
            this.numberOfDayLame,
            this.animals.size,
            this.grassAmount,
            avgLivingEnergy.toDouble(),
            avgDeadAnimalLifeSpan.toDouble(),
            avgChildren
        )

        dataList.add(rowDataInst)
        tableList.add(rowDataInst)

    }

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getStaticView(engine: IEngine) {

        if (engine is MagicEngine) {
            if (engine.rescueTimes != engine.counter.value)
                Text("Pozostało: ${engine.rescueTimes - engine.counter.value} ratunków")
            else
                Text("Wykorzystano wszystkie ratunki")
        }

        if (this.isAnimalTracked.value) {
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
        } else {
            Text("Żaden animal nie jets trackowany")
        }

        Text(this.genotypeStringMS.value)

        Text(
            "Legenda:\n" +
                    "- Numer epoki\n" +
                    "- Ilość żywych zwierząt\n" +
                    "- Ilość trawy na mapie\n" +
                    "- Średnia ilość żyjących zwierząt\n" +
                    "- Średnia długość życia do śmierci\n" +
                    "- Średnia ilość dzieci\n"
        )
    }

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getTableView() {
        val scrollbarState = rememberLazyListState()
        Box {

            val column2Weight = 1.0f / 6.0f // 100%/6
            LazyColumn(Modifier.fillMaxWidth().fillMaxHeight(), state = scrollbarState) {
                items(tableList.size) { index ->
                    val data = tableList[tableList.size - 1 - index]
                    Row(Modifier.fillMaxWidth().height(20.dp)) {
                        Text(data.day.toString(), modifier = Modifier.weight(column2Weight))
                        Text(data.animalsAmount.toString(), modifier = Modifier.weight(column2Weight))
                        Text(data.grassAmount.toString(), modifier = Modifier.weight(column2Weight))
                        Text(data.avgEnergy.round(2).toString(), modifier = Modifier.weight(column2Weight))
                        Text(data.avgDeadLifeSpan.round(2).toString(), modifier = Modifier.weight(column2Weight))
                        Text(data.avgLivingChild.round(2).toString(), modifier = Modifier.weight(column2Weight))
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollbarState)
            )
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
    }

    private fun calcGenotypes(): Pair<String, Int> {
        val genotypeMap = mutableMapOf<String, Pair<Array<Int>, Int>>()
        animals.forEach {
            val key = it.genes.contentDeepToString()
            genotypeMap.putIfAbsent(key, it.genes to 0)
            genotypeMap[key] = genotypeMap[key]!!.component1() to genotypeMap[key]!!.component2() + 1
        }
        var (maxGenotype, maxAmount) = Pair("-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-", 0)
        val mapEntry = genotypeMap.maxByOrNull { it.value.component2() }
        if (mapEntry != null && mapEntry.component2().component2() > 1) {
            maxGenotype = mapEntry.component1().replace(" ", "")
            maxGenotype = maxGenotype.replace("[", "").replace("]", "")
            maxAmount = mapEntry.component2().component2()
        }
        return maxGenotype to maxAmount
    }

    private fun calcAvgLivingAnimalEnergy(): Long {
        val avgLivingEnergy: Long = if (this.animals.isNotEmpty()) {
            this.animals.sumOf { it.energy.toLong() } / this.animals.size.toLong()
        } else {
            0L
        }
        return avgLivingEnergy
    }

    private fun calcAvgDeadAnimalLifeSpan(): Long {
        val avgDeadAnimalLifeSpan: Long =
            if (this.amountOfDeadAnimals != 0L) {
                this.sumOfDeadLifeSpan / amountOfDeadAnimals
            } else {
                0L
            }
        return avgDeadAnimalLifeSpan
    }
}

private fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
