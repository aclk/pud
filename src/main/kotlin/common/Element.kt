package common

import doxflow.diagram_8x_flow.Companion.currentLegend
import doxflow.diagram_8x_flow.LegendType.*
import doxflow.models.diagram.Relationship

interface IElement {
    val element: Element
}

/**
 * 表示UML中的一个任意元素
 * type <size:14><b>name</b></size> color
 * package A #yellow
 * */
data class Element(
    var name: String,
    var type: String = RECTANGLE,
    var backgroundColor: String? = null,
) {
    companion object Type {
        const val RECTANGLE = "rectangle"
        const val CLASS = "class"
        const val CLOUD = "cloud"
    }

    var showName: String? = "<size:14><b>$name"
    var stereoType: String? = null

    // Relative element collection
    private var elements: MutableList<RelationshipWrapper> = mutableListOf()

    // Relative Element Name
    fun relate(name: String, relationship: String, command: String? = null) {
        relate(Element(name), relationship, command)
    }

    // Relative Element
    fun relate(element: Element, relationship: String, command: String? = null) =
        elements.add(RelationshipWrapper(element, relationship, command))

    // Generate relationships
    fun generate(): String = buildString {
        elements.forEach {
            append("${name.fixBlank()}${it.relationship}${it.relativeElement.name.fixBlank()}")
            appendLine(with(it.command) { return@with if (!isNullOrBlank()) ":${it.command}" else "" })
        }
        elements.clear()
    }

    override fun toString(): String =
        "$type \"$showName\" as ${name.fixBlank()} ${if (stereoType != null && currentLegend == TacticalLegend) "<<$stereoType>>" else ""} ${backgroundColor ?: ""}"

    inner class RelationshipWrapper(val relativeElement: Element, var relationship: String, val command: String?) {
        init {
            relationship = if (currentLegend == StrategicLegend) Relationship.NONE else relationship
        }
    }

    private fun String.fixBlank(): String {
        return this.replace(" ", "_")
    }

}