package agh.ics.gameoflife.statistics

data class Options(
    val width: Int,
    val height: Int,
    val startEnergy: Int,
    val moveEnergy: Int,
    val plantEnergy: Int,
    val jungleRatio: Double,
    val isMagicEngine: Boolean
) {
    val breedEnergy: Int = startEnergy / 2

    companion object {
        val Default = Options(10, 10, 50, 3, 100, 0.5, false)
    }
}