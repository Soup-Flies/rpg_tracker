import screen.Component
import screen.ComponentType
import screen.screen
import kotlin.test.Test

class ScreenTest {

    @Test
    fun test() {
        val mScreen = screen {
            name = "Sheet"

            listComponent {
                name = "Stats"
                statComponent("Strength")
                statComponent("Dexterity")
                statComponent("Constitution")
                statComponent("Intelligence")
                statComponent("Wisdom")
                statComponent("Charisma")
            }

            numberComponent {
                name = "Passive perception"
            }

            listComponent(ComponentType.STRING, 10) {
                name = "yo"
            }
        }

        println(mScreen)

        println("RECURSE:\n")
        Component.recursChildren(mScreen)
    }
}