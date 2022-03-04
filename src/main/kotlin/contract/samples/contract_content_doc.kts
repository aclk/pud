package contract.samples

import contract.content.doc_contract_content

doc_contract_content {
    // 定义合约
    contract("预充值合约") {
        // 定义合约的签订方
        val twPlatform = person("甲方", "链家租房")
        val realtor = person("乙方", "房产经纪人")

        // 定义履约项并提供证明履约完成的凭证
        fulfillment("预充值", "预充值支付凭证", "10分钟内") {
            // 甲方请求(乙方)履约
            request(twPlatform)
            // 乙方必须在10分钟内完成履约
            confirm(realtor)
            failure(twPlatform, fulfillment("取消充值订单", "取消充值订单凭证"))
        }

        fulfillment("退款", "退款凭证", "3个工作日") {
            request(realtor)
            confirm(twPlatform)
            failure(realtor, prosecute())
        }

        fulfillment("余额提现", "余额提现凭证", "3个工作日") {
            request(realtor)
            confirm(twPlatform)
        }

        fulfillment("发票开具", "发票", "7个工作日") {
            request(realtor)
            confirm(twPlatform)
        }
    }
} exportDoc "./docs/contract_content_doc.md"
