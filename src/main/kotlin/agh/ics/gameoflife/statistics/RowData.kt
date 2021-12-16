package agh.ics.gameoflife.statistics

data class RowData(
    val day: Long,
    val animalsAmount: Int,
    val grassAmount: Int,
    val varEnergy: Double,
    val varDeadLifeSpan: Double,
    val varLivingChild: Double
) {
    override fun toString(): String {
        return "$day,$animalsAmount,$grassAmount,$varEnergy,$varDeadLifeSpan,$varLivingChild\n"
    }
}
