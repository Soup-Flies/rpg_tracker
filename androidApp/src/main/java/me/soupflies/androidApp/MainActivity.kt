package me.soupflies.androidApp

import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import screen.Grid
import screen.screen


object ColorList {

    val colorList = listOf<Int>(
        parseColor("red"),
        parseColor("green"),
        parseColor("blue"),
        parseColor("yellow"),
        parseColor("cyan"),
        parseColor("magenta")
    )

    fun getNext(): Int {
        return colorList.random()
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val parent = findViewById<ConstraintLayout>(R.id.main_view)
        parent.setBackgroundColor(parseColor("lightgrey"))

        val testComp = screen {
            name = "Sheet"

            listComponent {
                name = "Stats"

                statComponent("Strength")
                statComponent("Dexterity")
//                statComponent("Constitution")
//                statComponent("Intelligence")
//                statComponent("Wisdom")
//                statComponent("Charisma")

                size = Grid(9, 9)
            }
        }

        val nestableGrid = NestableGrid(this).apply {
            val layoutParams = GridLayout.LayoutParams()

            rowCount = testComp.size.vertical
            columnCount = testComp.size.horizontal

            setBackgroundColor(parseColor("black"))

            Log.d("XXX", "screen size: $rowCount $columnCount")

            layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = GridLayout.LayoutParams.MATCH_PARENT
        }

        val one = buildTextView("HELLO ONE", 1, 0, 1, 1)
        val two = buildTextView("HELLO TWO", 1, 2, 2, 2)
        val three = buildTextView("HELLO THREE", 1, 7, 3, 3)
        val four = buildTextView("OTHER Y", 1, 3, 8, 8)
        with(nestableGrid) {
            addView(one)
            addView(two)
            addView(three)
            addView(four)
        }

//        nestableGrid.addComponent(testComp)
        parent.addView(nestableGrid)


    }

    fun buildTextView(text: String, x: Int, y: Int, xSize: Int, ySize: Int): GridLayout {
        val layout = GridLayout(this).apply {
            rowCount = ySize
            columnCount = xSize

            setBackgroundColor(ColorList.getNext())

//            val columns = GridLayout.spec(x, xSize)
//            val rows = GridLayout.spec(y, ySize)
            val columns = GridLayout.spec(x, xSize)
            val rows = GridLayout.spec(y, ySize)

            layoutParams = GridLayout.LayoutParams(rows, columns).apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.MATCH_PARENT
            }
        }
        val inner = LinearLayout(this).apply {

//            val columns = GridLayout.spec(0, GridLayout.CENTER)
//            val rows = GridLayout.spec(0, GridLayout.CENTER)
            val columns = GridLayout.spec(0, xSize)
            val rows = GridLayout.spec(0, ySize)


            val params = GridLayout.LayoutParams(rows, columns).apply {
                width = 200
                height = 200
            }
            layoutParams = params
        }

        val textV = TextView(this)
        textV.text = text

        inner.addView(textV)
        layout.addView(inner)
        return layout
    }
}
