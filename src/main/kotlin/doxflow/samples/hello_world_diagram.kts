package doxflow.samples

import doxflow.diagram_8x_flow

diagram_8x_flow {
    context("商品销售上下文") {
        contract("商品订单合同") {
            timestamp("签订时间")
        }
    }
} exportDiagram "./diagrams/hello_word_diagram.png"