package doxflow

import common.Element
import common.*
import doxflow.dsl.context

data class diagram_8x_flow(
    val layoutDirection: Diagram.LayoutDirection = Diagram.LayoutDirection.Vertical,
    val function: diagram_8x_flow.() -> Unit
) : Diagram, Document {

    private var contexts: MutableList<context> = mutableListOf()

    init {
        this.function()
    }

    companion object {
        var currentLegend = LegendType.TacticalLegend
    }

    enum class LegendType {
        StrategicLegend,
        TacticalLegend
    }

    fun context(name: String, context: (context.() -> Unit)? = null): context =
        context(Element(name, "rectangle")).apply {
            contexts.add(this)
            context?.let { it() }
        }

    fun exports(diagram: String, doc: String) {
        exportDiagram(diagram)
        exportDoc(doc)
    }


    override fun buildDocContent(): String = buildString {
        appendLine("# 服务与业务能力")
        appendLine(buildApiDocContent())
    }

    override fun buildPlantUmlString(): String = """
        |@startuml
        ${getClassStyle(layoutDirection)}
        ${buildPlantUmlContent()}
        |@enduml
        """.trimMargin()

    override fun exportResult(isSuccess: Boolean) {
        if (isSuccess) contexts.clear()
    }

    private fun buildPlantUmlContent(): String = buildString {
        contexts.forEach {
            appendLine(it.toString())
        }
    }

    override fun exportDocCompleted() {
        contexts.clear()
    }

    private fun buildApiDocContent(): String = buildString {
        contexts.forEach {
            appendLine(it.toApiString())
        }
    }
}



