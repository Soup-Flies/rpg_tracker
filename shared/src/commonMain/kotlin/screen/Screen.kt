package screen

import kotlin.math.max
import kotlin.native.concurrent.ThreadLocal

typealias Dsl<T> = T.() -> Unit

data class Grid(
    val horizontal: Int,
    val vertical: Int
)

abstract class Component {
    open lateinit var name: String
    open var size: Grid = Grid(1, 1)

    var parent: Component = NoComponent
        protected set

    abstract val type: ValueType<*>
    val value get() = type.value

    val components = mutableMapOf<String, Component>()

    fun allChildren(): List<Component> = components.values.toList()

    fun Component.component(type: ComponentType, properties: Dsl<Component>): Component {
        val component = ComponentFactory.create(type)
        component.parent = this@Component
        component.properties()
        components[component.name] = component
        return component
    }

    fun Component.numberComponent(properties: Dsl<Component>) {
        component(ComponentType.NUMBER, properties)
    }

    fun Component.stringComponent(properties: Dsl<Component>) {
        component(ComponentType.STRING, properties)
    }

    fun Component.listComponent(properties: Dsl<Component>) {
        component(ComponentType.LIST, properties)
    }

    fun Component.listComponent(type: ComponentType, number: Int, properties: Dsl<Component>) {
        component(ComponentType.LIST) {
            properties()
            val typeName = type.name.toLowerCase()
            name = "${this.name}_${typeName}_list"
            repeat(number) {
                component(type) {
                    properties()
                    name = "${this.name}_$it"
                }
            }
        }
    }

    fun Component.statComponent(title: String) {
        numberComponent {
            name = title

            numberComponent {
                name = "Mod"
                val mod = parent.value as Int
                (type as ValueType<Int>).value = (mod - 10) / 2

            }
        }
    }

    private fun parentCount(): Int {
        var count = 0
        var parent = this.parent

        while (parent !is NoComponent) {
            count += 1
            parent = parent.parent
        }

        return count
    }

    override fun toString(): String {
        val preSpace = " ".repeat(parentCount())
        val out = if (value == Unit) "" else value
        val strB = StringBuilder("$preSpace$name: $out\n")
        val count = components.count()
        var iter = 0

        components.forEach { (_, value) ->
            iter += 1
            strB.append("$preSpace $value")
            if (iter == count) strB.append("\n")
        }

        return strB.toString()
    }

    companion object {
        fun recursChildren(comp: Component, count: Int = 0) {
            if (count == 0) println(comp.name)
            comp.allChildren().forEach {
                val spaces = " ".repeat(count)
                println("$spaces ${it.name}")
                recursChildren(it, count + 2)
            }
        }
    }
}

fun screen(components: Dsl<Component>): ScreenComponent {
    val new = ComponentFactory.create(ComponentType.SCREEN) as ScreenComponent
    new.components()
    return new
}

sealed class ValueType<T: Any> : IValueType<T> {
    class Number : ValueType<Int>() {
        override var value: Int = 0
    }

    class Text : ValueType<String>() {
        override var value: String = ""
    }

    class Tick : ValueType<Boolean>() {
        override var value: Boolean = false
    }

    class Empty : ValueType<Unit>() {
        override var value: Unit = Unit
    }
}

interface IValueType<T: Any> {
    var value: T
}

class ScreenComponent : Component() {
    override var size: Grid = Grid(18, 18)
    override val type = ValueType.Empty()

    init {
        parent = NoComponent
    }
}

@ThreadLocal
object NoComponent : Component() {
    override val type = ValueType.Empty()
    override var name: String = "NoComponent"
}

class StatComponent : Component() {
    override var size: Grid = Grid(3, 3)
    override val type = ValueType.Number()
}

class NumberComponent : Component() {
    override var size: Grid = Grid(1, 1)
    override val type = ValueType.Number()
}

class StringComponent : Component() {
    override var size: Grid = Grid(1, 1)
    override val type = ValueType.Text()
}

class ListComponent : Component() {
    override var size: Grid = Grid(0, 0)
//        get() = updateSize()

    override val type = ValueType.Empty()

    private fun updateSize(): Grid {
        val children = allChildren()

        var x = 0
        var y = 0

        children.forEach {
            x += it.size.horizontal
            y += it.size.vertical
        }

        return Grid(x, y)
    }
}

class BooleanComponent : Component() {
    override var size: Grid = Grid(1, 1)
    override val type = ValueType.Tick()
}

enum class ComponentType {
    NUMBER, STRING, TICK, LIST, SCREEN
}

class ComponentFactory {
    companion object {
        fun create(type: ComponentType) : Component {
           return when (type) {
               ComponentType.SCREEN -> ScreenComponent()
               ComponentType.STRING -> StringComponent()
               ComponentType.TICK -> BooleanComponent()
               ComponentType.NUMBER -> StatComponent()
               ComponentType.LIST -> ListComponent()
           }
        }
    }
}
