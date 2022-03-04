package architecture.dsl

import architecture.dsl.inter_process.process
import architecture.dsl.intra_process.layer
import common.*
import common.Diagram.Companion.ASSOCIATE

data class component(val element: Element) : DSL<component> {
    private var process: process? = null
    private var layer: layer? = null

    constructor(element: Element, process: process) : this(element) {
        this.process = process
        if (element.backgroundColor == null)
            element.backgroundColor = process.element.backgroundColor
    }

    constructor(element: Element, layer: layer) : this(element) {
        this.layer = layer
        if (element.backgroundColor == null)
            element.backgroundColor = layer.element.backgroundColor
    }

    /**
     * @param name The name of component
     * @param command The name of operation
     */
    fun call(name: String, command: String = "") {
        element.relate(name, ASSOCIATE, command)
    }

    override fun invoke(function: component.() -> Unit): component = apply { function() }

    override fun toString(): String = buildString {
        appendLine("$element {\n}")
        appendLine(element.generate())
        process?.let { appendLine(it.element.generate()) }
        layer?.let { appendLine(it.element.generate()) }
    }
}
