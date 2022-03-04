package doxflow.dsl

import common.Element
import common.Element.Type.CLASS
import doxflow.models.ability.BusinessAbilityCreator
import doxflow.models.diagram.*
import doxflow.models.diagram.Relationship.Companion.DEFAULT

class proposal(element: Element, party: Party?, note: String? = null) :
    Evidence<proposal>(element, proposal::class, party, note) {
    private var context: context? = null
    private var rfp: rfp? = null

    constructor(element: Element, context: context, party: Party?, note: String? = null) : this(element, party, note) {
        this.context = context
    }

    constructor(element: Element, rfp: rfp, party: Party?, note: String? = null) : this(element, party, note) {
        this.rfp = rfp
    }

    private lateinit var contract: contract

    init {
        resource = proposal::class.java.simpleName
    }

    fun contract(name: String, vararg parties: Party, function: contract.() -> Unit) {
        this.contract = contract(Element(name, CLASS), this, *parties).apply {
            relationship = DEFAULT
            function()
        }
        element.relate(this.contract.element, DEFAULT)
    }

    override fun addBusinessAbility(abilityCreator: BusinessAbilityCreator) {
        super.addBusinessAbility(abilityCreator)
        contract.addBusinessAbility(abilityCreator)
    }

    override fun invoke(function: proposal.() -> Unit): proposal = apply { function() }

    override fun getUriPrefix(): String {
        rfp?.let {
            return BusinessAbilityCreator.getUri(it.resource, it.relationship, it.getUriPrefix())
        }
        return super.getUriPrefix()
    }

    override fun toString(): String {
        return buildString {
            appendLine(super.toString())
            appendLine(contract.toString())
            rfp?.let { appendLine(it.element.generate()) }
        }
    }
}