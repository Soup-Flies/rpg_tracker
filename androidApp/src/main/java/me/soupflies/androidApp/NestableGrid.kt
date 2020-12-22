package me.soupflies.androidApp

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import screen.Component

class NestableGrid @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr), IComponentView {

    override val name: String
        get() {
            components.firstOrNull()?.let {
                val parent = it.parent.name
                val curr = it.name
                return "${parent}_$curr"
            }
            return "TOP_LEVEL"
        }

    private val _components = mutableListOf<Component>()
    override val components: List<Component> get() = _components

    private val parentSize = (parent as? NestableGrid)?.components?.size ?: 0

    override fun addComponent(component: Component) {

        val (width, height) = component.size
        Log.d("XXX addComp", "parent: ${component.parent.name} rows: $rowCount columns: $columnCount name: ${component.name} size: ${component.size}")
        Log.d("XXX addComp", "parentSize: $parentSize start: ${components.size} span: $height sum: ${components.size + height}")
        val layout = NestableGrid(context).apply {
            rowCount = height
            columnCount = width

            setBackgroundColor(ColorList.getNext())

            val rows = spec(0, height)
            val columns = spec(0, width)
            val params = LayoutParams(rows, columns)
            layoutParams = params
        }

        val text = TextView(context).apply {
            text = component.name
            val rows = spec(0, height)
            val columns = spec(0, width)
            val params = LayoutParams(rows, columns)
            layoutParams = params
        }

        val value = TextView(context).apply {
            this.text = component.value.toString()
            val rows = spec(0,  height)
            val columns = spec(0, width, RIGHT)
            val params = LayoutParams(rows, columns)
            layoutParams = params
        }

        layout.addView(text)
//        layout.addView(value)

        addView(layout)

//        component.allChildren().forEach {
//           layout.addComponent(it)
//        }

        _components.add(component)
    }
}

interface IComponentView {
    fun addComponent(component: Component)

    val components: List<Component>
    val name: String
}