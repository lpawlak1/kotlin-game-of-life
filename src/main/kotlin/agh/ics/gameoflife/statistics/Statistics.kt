package agh.ics.gameoflife.statistics

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.map.IWorldMap
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

class Statistics {
    private lateinit var isAnimalTracked: MutableState<Boolean>
    lateinit var map: IWorldMap

    private lateinit var genotypeStringMS: MutableState<String>

    private var tableList = mutableStateListOf<RowData>()
    var dataList = mutableListOf<RowData>()

    var sumOfDeadLifeSpan: Long = 0
    var amountOfDeadAnimals: Long = 0

    private var numberOfDayLame = 0L

    var animals: MutableList<Animal> = mutableListOf()
    private var avgChildren: Double = 0.0

    var grassAmount: Int = 0


    private var deathDate: Long = 0L
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


    private lateinit var trackedAnimalChildrenMS: MutableState<Long>
    private lateinit var trackedAnimalAncestorsMS: MutableState<Long>
    private lateinit var trackedAnimalDeathDateMS: MutableState<Long>
    private lateinit var trackedAnimalLifeSpanMS: MutableState<Long>

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

        this.avgChildren = calcAvgChildren()

        val genotypeStr = calcGenotypes()
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
        if (this.isAnimalTracked.value) {
            Surface(color = MaterialTheme.colors.secondaryVariant) {
                Column {
                    Text("Tracked animal statistics")
                    Text("Children amount: ${trackedAnimalChildrenMS.value}")
                    Text("Descendant amount: ${trackedAnimalAncestorsMS.value}")
                    if (trackedAnimal!!.energy <= 0) {
                        Text("Death date: ${trackedAnimalDeathDateMS.value}")
                        Text("Lifespan: ${trackedAnimalLifeSpanMS.value}")
                    }else{
                        Text("")
                        Text("")
                    }
                }
            }
        } else {
            Text("No animal is being tracked\n\n\n\n", style = TextStyle(MaterialTheme.colors.error))
        }

        if (this.genotypeStringMS.value != "-") {
            Text("Dominant from the genotype: ")
            Text(this.genotypeStringMS.value)
        } else {
            Text("No dominant genotype found.\n", style = TextStyle(MaterialTheme.colors.error))
        }

        if (engine is MagicEngine) {
            if (engine.rescueTimes != engine.counter.value)
                Text("${engine.rescueTimes - engine.counter.value} rescues remaining")
            else
                Text("All rescues used")
        }
    }

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getTableView() {
        val scrollbarState = rememberLazyListState()
        Box {
            TooltipArea(tooltip = {
                Surface(color = MaterialTheme.colors.secondaryVariant, modifier = Modifier.padding(5.dp)) {
                    Text(
                        "Table legend:\n" +
                                "- Epoch number\n" +
                                "- Living animals count\n" +
                                "- Grass count\n" +
                                "- Average living animals\n" +
                                "- Average lifespan of dead animals\n" +
                                "- Average amount of children\n"
                    )
                }
            }){
                val column2Weight = 1.0f / 6.0f // 100%/6
                LazyColumn(Modifier.fillMaxWidth().height(350.dp), state = scrollbarState) {
                    items(kotlin.math.min(tableList.size, 30)) { index ->
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

    private fun calcAvgChildren(): Double =
        if (animals.size != 0) {
            (this.animals.sumOf { it.amountOfChildren }.toDouble() / this.animals.size.toDouble())
                .round(2)
        } else {
            0.0
        }

    private fun calcGenotypes(): String {
        val genotypeMap = mutableMapOf<String, Int>()
        animals.forEach {
            val key = it.genes.contentDeepToString()
            genotypeMap.putIfAbsent(key, 0)
            genotypeMap[key] = genotypeMap[key]!! + 1
        }
        val mapEntry = genotypeMap.maxByOrNull { it.value }
        if (mapEntry == null || mapEntry.component2() <= 1) {
            return "-"
        }
        return mapEntry
            .component1()
            .replace(" ", "")
            .replace("[", "")
            .replace("]", "")
    }

    private fun calcAvgLivingAnimalEnergy(): Long =
        if (this.animals.isNotEmpty()) {
            this.animals.sumOf { it.energy.toLong() } / this.animals.size.toLong()
        } else {
            0L
        }

    private fun calcAvgDeadAnimalLifeSpan(): Long =
        if (this.amountOfDeadAnimals != 0L) {
            this.sumOfDeadLifeSpan / amountOfDeadAnimals
        } else {
            0L
        }
}

private fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
