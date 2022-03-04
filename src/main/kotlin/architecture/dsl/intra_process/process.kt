package architecture.dsl.intra_process

import common.Diagram.Companion.ASSOCIATE
import common.Element

class process(val element: Element) {

    fun call(name: String, command: String = "") {
        element.relate(name, ASSOCIATE, command)
    }

    override fun toString(): String = buildString {
        appendLine(element)
    }
}
