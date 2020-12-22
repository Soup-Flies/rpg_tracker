package structure

interface IComponent : IResizable {
    val properties: List<IProperty>

    fun addProperty(prop: IProperty)
}

interface ISize

interface ILocation

interface IResizable {
    val horizontal: Int
    val vertical: Int

    fun size(): ISize

    fun resize(x: Int, y: Int): IResizable
}

interface IScreenView {

    val components: List<IComponent>

    fun addNewComponent(component: IComponent, location: ILocation)
}


sealed class Property : IProperty {
    class Title(val title: String) : Property()
    class Value<T> (val value: T) : Property()
    class Description(val description: String) : Property()
}

interface IProperty
