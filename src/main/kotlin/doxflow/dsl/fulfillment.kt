package doxflow.dsl

import common.Element
import common.Element.Type.CLASS
import doxflow.models.ability.BusinessAbility
import doxflow.models.diagram.Party
import doxflow.models.diagram.Relationship
import doxflow.models.diagram.Relationship.Companion.DEFAULT

class fulfillment(
    val name: String, val contract: contract, override var resource: String = "",
    var relationship: String = DEFAULT
) : BusinessAbility<fulfillment>, Relationship {
    lateinit var request: request
    lateinit var confirmation: confirmation

    fun request(party: Party? = null, request: request.() -> Unit): request =
        request(Element("${name}申请", CLASS), this, party).apply {
            this@fulfillment.request = this
            request()
        }

    fun confirm(party: Party? = null, confirmation: confirmation.() -> Unit): confirmation =
        confirmation(Element("${name}确认", CLASS), this, party).apply {
            this@fulfillment.confirmation = this
            confirmation()
        }


    override fun invoke(function: fulfillment.() -> Unit): fulfillment = apply {
        function()
    }

    override fun toString(): String {
        return buildString {
            appendLine(request.toString())
            appendLine(confirmation.toString())
            request.element.relate(confirmation.element, relationship)
            appendLine(request.element.generate())
        }
    }
}