package contract.content

import common.DSL

class fulfillment(
    val name: String,
    val evidence: String? = null,
    private val periodOfFulfil: String? = null,
    val parties: List<person>? = null
) : DSL<fulfillment> {
    private var requestPerson: person? = null
    private var confirmPerson: person? = null
    private val failureFulfillment: MutableList<Pair<person, fulfillment>> = mutableListOf()

    fun request(person: person) = person.apply { requestPerson = person }

    fun confirm(person: person) = person.apply {
        confirmPerson = person
    }

    fun failure(person: person, fulfillment: fulfillment) = person.apply {
        failureFulfillment.add(Pair(person, fulfillment))
    }

    fun isExistParties(): Boolean {
        return requestPerson != null && confirmPerson != null
    }

    override fun invoke(function: fulfillment.() -> Unit) = apply {
        function()
    }

    override fun toString(): String = buildString {
        if (isExistParties()) {
            append(" **${requestPerson!!.party}**申请**${name}**，")
            append("**${confirmPerson!!.party}**必须在**${periodOfFulfil}**完成**${name}**")
            append("并提供**$evidence**。")
        }
        failureFulfillment.forEach {
            append("否则，")
            append("**${it.first.party}**有权在**${it.second.periodOfFulfil}**发起**${it.second.name}**请求。")
        }
    }
}