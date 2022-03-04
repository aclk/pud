package doxflow.samples

import doxflow.diagram_8x_flow
import doxflow.models.diagram.Relationship

diagram_8x_flow {
    context("商品销售上下文") {
        val seller = role_party("卖家")
        val buyer = role_party("买家")

        rfp("询问商品价格", buyer) {
            resource = "order-request"
            timestamp("创建时间")

            proposal("商品报价方案", seller, Relationship.ONE_TO_N) {
                timestamp("创建时间")
                data("报价金额")

                participant_thing("商品").relate(this)

                contract("商品订单合同", seller, buyer) {
                    resource = "order"
                    relationship = Relationship.ONE_TO_N
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