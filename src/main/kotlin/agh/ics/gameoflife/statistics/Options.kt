package agh.ics.gameoflife.statistics

data class Options(
    val width: Int,
    val height: Int,
    val startEnergy: Int,
    val moveEnergy: Int,
    val plantEnergy: Int,
    val jungleRatio: Double,
    val isMagicEngine: Boolean
){
    val breedEnergy: Int = startEnergy / 2
    companion object {
        val Default = Options(10,10,50,3,100,0.5, false)
    }
}




//    Następujące parametry są danymi wejściowymi do aplikacji:
//    rozmiar mapy (width, height)
//    ilość energii początkowej zwierząt (startEnergy)
//    ilość energii traconej w każdym dniu (moveEnergy)
//    ilość energii zyskiwanej przy zjedzeniu rośliny (plantEnergy)
//    proporcje dżungli do sawanny (jungleRatio)

// „Oczywiście na początku symulacji na środku świata umieszczamy jedno lub kilka zwierząt (Adam/Ewa).” Czy to znaczy, że wszystkie zwierzęta są na początku na tym samym, środkowym polu, czy może każde z nich powinno zajmować losową pozycję?
// Zwierzęta na początku zajmują losowe pozycji i na jednej pozycji może być wtedy tylko jedno zwierze.

// Czy energia potrzebna do rozmnażania nie powinna być danymi wejściowymi? Jeżeli nie, to czy powinna wynosić jakiś % energii startowej, a jeśli tak, to jaki?
// Minimalna energia potrzebna do rozmnożenia to połowa energii początkowej zwierzęcia, tzn. 50% startEnergy, a nie 50% energii początkowej danego osobnika.

// Po ustaleniu poszukiwanego osobnika

// "określenie epoki, w której zmarło" - czyli mam pokazywać też zmarłe zwierzęta ??
// Ta informacja nie ma być pokazywana na mapie, tylko na panelu obok.

