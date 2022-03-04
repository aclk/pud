import doxflow.diagram_8x_flow
import doxflow.diagram_8x_flow.Companion.currentLegend
import doxflow.diagram_8x_flow.LegendType.*
import doxflow.dsl.fulfillment
import doxflow.models.diagram.Relationship.Companion.ONE_TO_N
import org.junit.Test

internal class diagram_8x_flow_test {
    @Test
    fun hello_word_diagram() {
        diagram_8x_flow {
            context("商品销售上下文") {
                contract("商品订单合同") {
                    timestamp("签订时间")

                    fulfillment("fulfillment1") {
                        request {}
                        confirm {}
                    }

                    fulfillment("fulfillment2") {
                        request {}
                        confirm {}
                    }
                }
            }
        } exportDiagram "./diagrams/hello_word_diagram.png"
    }

    @Test
    fun test_relationship() {
        diagram_8x_flow {
            lateinit var fulfillment: fulfillment
            lateinit var fulfillment2: fulfillment
            context("context1") {
                val role = role_party("roleA").relate(role_domain("domainA"))
                val party = participant_party("partyB").relate(role_domain("domainB"))

                contract("contract1", role, party) {
                    fulfillment = fulfillment("fulfillment1") {
                        request {}
                        confirm {}
                    }

                    fulfillment2 = fulfillment("fulfillment2") {
                        request {}
                        confirm {}
                    }
                }
                role_context("xxxContext") play fulfillment.confirmation
            }

            context("context2") {
                val role = role_party("roleA")
                val party = participant_party("partyB")

                contract("contract2", role, party) {
                    fulfillment("fulfillmentA") {
                        request {}
                        confirm {
                            this play fulfillment2.confirmation
                        }
                    }
                }
            }

        } exportDiagram "./diagrams/test_relationship.png"
    }

    @Test
    fun create_relation_evidence_diagram() {
        diagram_8x_flow {
            currentLegend = StrategicLegend
            context("商品销售上下文") {
                val partyA = role_party("partyA")
                val partyB = role_party("partyB")
                rfp("RFP", partyA) {
                    evidence("EvidenceA")
                    proposal("Proposal", partyB) {
                        evidence("EvidenceB")
                        contract("Contract", partyA, partyB) {
                            evidence("EvidenceC")
                            fulfillment("Fulfillment") {
                                request {
                                    evidence("EvidenceD")
                                }
                                confirm {
                                    evidence("EvidenceE", ONE_TO_N)
                                }
                            }
                        }
                    }
                }
            }
        } exportDiagram "./diagrams/relation_evidence_diagram.png"
    }

    @Test
    fun create_reader_subscription_diagram() {
        diagram_8x_flow {
            lateinit var paymentInReaderSubscriptionContext: fulfillment

            context("读者订阅上下文") {
                val reader = participant_party("极客时间注册用户") play role_party("读者")
                val contentProvider = participant_party("极客时间平台") play role_party("内容提供商")

                contract("专栏订阅合同", reader, contentProvider) {
                    timestamp("订阅时间")
                    participant_place("专栏").relate(this)

                    paymentInReaderSubscriptionContext = fulfillment("专栏付款") {
                        request(contentProvider) {
                            timestamp("创建时间", "过期时间")
                            data("金额")
                        }

                        confirm(reader) {
                            timestamp("创建时间")
                            data("金额")
                        }
                    }

                    fulfillment("付费内容访问") {
                        request(reader) {
                            timestamp("创建时间", "订阅时间")
                            data("专栏")
                        }

                        confirm(contentProvider) {
                            timestamp("订阅时间")
                            data("专栏")
                        }
                    }

                    fulfillment("断更补偿") {
                        request(reader) {
                            timestamp("创建时间", "最后更新时间")
                            data("金额")
                        }

                        confirm(contentProvider) {
                            timestamp("创建时间")
                            data("金额")
                        }
                    }
                }
            }

            context("三方支付上下文") {
                contract("XXX支付协议") {
                    timestamp("签订时间")
                    fulfillment("代付") {
                        request {
                            timestamp("创建时间", "过期时间")
                            data("金额")
                        }

                        confirm {
                            timestamp("创建时间")
                            data("金额")

                            val evidence = evidence("支付凭证") {
                                timestamp("支付时间")
                                data("金额")
                            }
                            evidence play paymentInReaderSubscriptionContext.confirmation
                        }
                    }
                }
            }
        } exportDiagram "./diagrams/reader_subscription_diagram.png"
    }

    @Test
    fun create_editor_performance_diagram() {
        diagram_8x_flow {
            context("编辑绩效协议上下文") {
                val editor = role_party("编辑")
                val geekTimePlatform = role_party("极客时间平台")

                contract("绩效协议") {
                    timestamp("签订时间")
                    editor.relate(this, ONE_TO_N)
                    geekTimePlatform.relate(this)

                    fulfillment("目标设定") {
                        request(editor) {
                            timestamp("开始时间", "截止时间")
                            data("绩效指标")
                        }

                        confirm(geekTimePlatform) {
                            timestamp("确认时间")
                            data("目标完成率")
                        }
                    }

                    fulfillment("周进度检查") {
                        request(editor) {
                            timestamp("开始时间", "截止时间")
                            data("周进度")
                        }

                        confirm(geekTimePlatform) {
                            timestamp("确认时间")
                            data("周进度完成率")

                            this dependent confirmation("周进度检查确认条目") {
                                timestamp("确认时间")
                                data("专栏选题", "文章提交", "专栏立项", "交稿确认")
                            }
                        }
                    }
                }
            }
        } exportDiagram "./diagrams/editor_performance_diagram.png"
    }

    @Test
    fun create_contract_with_rfp_diagram() {
        diagram_8x_flow {
            context("商品销售上下文") {
                val seller = role_party("卖家")
                val buyer = role_party("买家")

                rfp("询问商品价格", buyer) {
                    timestamp("创建时间")

                    proposal("商品报价方案", seller) {
                        timestamp("创建时间")
                        data("报价金额")

                        participant_thing("商品").relate(this)

                        contract("商品订单合同", seller, buyer) {
                            timestamp("签订时间")
                        }
                    }
                }
            }
        } exportDiagram "./diagrams/contract_with_rfp_diagram.png"
    }

    @Test
    fun create_order_api_doc() {
        diagram_8x_flow {
            context("商品销售上下文") {
                val seller = role_party("卖家")
                val buyer = role_party("买家")

                rfp("询问商品价格", buyer) {
                    resource = "order-request"
                    timestamp("创建时间")

                    proposal("商品报价方案", seller, ONE_TO_N) {
                        timestamp("创建时间")
                        data("报价金额")

                        participant_thing("商品").relate(this)

                        contract("商品订单合同", seller, buyer) {
                            resource = "order"
                            relationship = ONE_TO_N
                            timestamp("签订时间")

                            fulfillment("订单支付") {
                                resource = "payment"
                                request(seller) {
                                    timestamp("创建时间", "过期时间")
                                    data("金额")
                                }

                                confirm(buyer) {
                                    timestamp("创建时间")
                                }
                            }
                        }
                    }
                }
            }
        } exportDoc "./docs/order_api_doc.md"
    }
}