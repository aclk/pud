package contract.analysis.dsl

import common.Diagram
import common.Diagram.Color.PINK
import common.Diagram.Companion.ASSOCIATE
import contract.analysis.diagram_contract_analysis.contractToEvidences

// 契约(合同)
class contract(name: String) : evidence(name) {
    init {
        pertain(name)
    }
}

// 提议(提案)
class proposal(name: String) : evidence(name)

class rfp(name: String) : evidence(name)

// 证据(凭证)
open class evidence(val name: String) : Diagram.KeyInfo<evidence> {
    private var timestamps: Array<out String>? = null
    private var data: Array<out String>? = null
    private var preorderEvidence: evidence? = null
    private var postorderEvidences: MutableList<evidence> = mutableListOf()

    //当一个凭证属于2个协议的时候，表明该凭证存在凭证角色化
    private var contracts: MutableList<String> = mutableListOf()

    infix fun preorder(evidence: evidence) = evidence.let { preorderEvidence = it }

    infix fun postorder(evidence: evidence) = postorderEvidences.add(evidence)

    // 属于
    infix fun pertain(contract: String) {
        contracts.add(contract)
        val contractName = contracts.joinToString("+")
        if (contracts.size > 1) {
            val copy = HashMap(contractToEvidences)
            contractToEvidences.clear()
            contractToEvidences.computeIfAbsent(contractName) { mutableListOf() }
            contractToEvidences[contractName]?.add(this)
            contractToEvidences.putAll(copy)
        } else {
            contractToEvidences.computeIfAbsent(contractName) { mutableListOf() }
            contractToEvidences[contractName]?.add(this)
        }
    }

    override fun invoke(function: evidence.() -> Unit): evidence = apply { function() }

    override fun timestamp(vararg timestamps: String) = timestamps.let { this.timestamps = it }

    override fun data(vararg data: String) = data.let { this.data = it }

    override fun toString(): String = buildString {
        appendLine(
            """
            class $name<<${this@evidence.javaClass.simpleName}>> $PINK {
                ${timestamps?.joinToString() ?: ""}
                ${if (timestamps != null && data != null) ".." else ""}
                ${data?.joinToString() ?: ""}
            }
        """.trimIndent()
        )
        preorderEvidence?.let {
            appendLine("${name}$ASSOCIATE${it.name}:前序凭证")
        }
        postorderEvidences.forEach {
            appendLine("${name}$ASSOCIATE${it.name}:后序凭证")
        }
    }
}