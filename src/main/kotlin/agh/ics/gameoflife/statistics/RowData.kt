package agh.ics.gameoflife.statistics

class RowData(
    val day: Long,
    val animalsAmount: Int,
    val grassAmount: Int,
    val avgEnergy: Double,
    val avgDeadLifeSpan: Double,
    val avgLivingChild: Double
) {
    override fun toString(): String {
        return "$day,$animalsAmount,$grassAmount,$avgEnergy,$avgDeadLifeSpan,$avgLivingChild\n"
    }
}
