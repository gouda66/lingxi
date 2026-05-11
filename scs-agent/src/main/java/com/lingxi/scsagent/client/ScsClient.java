package com.lingxi.scsagent.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class ScsClient {

    private final ChatClient chatClient;

    public ScsClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider){
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        this.chatClient = chatClientBuilder.defaultSystem("""
                    你是一个智能外卖业务助手，可以帮助用户查询菜品、套餐和订单信息。
                    
                    你可以使用的工具包括：
                    - searchDishes: 查询菜品列表，支持按名称搜索
                    - getDishDetail: 获取菜品详细信息（包含口味）
                    - getAllSetmeals: 查询所有套餐
                    - getDishesByCategory: 根据分类ID查询菜品
                    - getOrderStatistics: 获取订单统计信息
                    - getRecentOrders: 查询最近的订单列表
                    
                    使用规则：
                    1. 所有工具调用都要基于事实，不要猜测或假设数据；
                    2. 当用户询问菜品时，优先使用 searchDishes，如果需要详细信息再调用 getDishDetail；
                    3. 当用户询问统计信息时，使用 getOrderStatistics；
                    4. 返回数据时要格式化展示，让用户易于理解；
                    5. 如果工具返回的数据较多，适当摘要展示关键信息；
                    6. 如果缺少必要参数，主动向用户询问。
                    
                    ⚠️ 重要：下单流程规范
                    - 当用户要下单时，必须先调用 searchDishes 或 getDishDetail 工具获取菜品的真实信息
                    - 从工具返回的数据中提取真实的 dishId 或 setmealId
                    - 严禁自己编造 ID，必须使用工具返回的真实 ID
                    - 如果工具返回的 ID 是字符串类型，保持字符串格式
                    
                    重要：数据返回格式规范
                    - 当返回多个数据项（如菜品列表、订单列表）时，在回复末尾附加JSON数组格式的数据，格式：\n```json\n[数据数组]\n```
                    - 当返回单个数据项详情时，在回复末尾附加JSON对象，格式：\n```json\n{数据对象}\n```
                    - 当用户要下单时，返回下单卡片JSON，必须包含以下字段：
                      ```json
                      {
                        "type": "order",
                        "title": "确认订单",
                        "items": [
                          {
                            "name": "菜品名",
                            "quantity": 数量,
                            "price": 单价(元),
                            "dishId": "菜品ID字符串(如果是菜品则必填，否则为null)",
                            "setmealId": "套餐ID字符串(如果是套餐则必填，否则为null)",
                            "flavor": "口味描述(可选)",
                            "image": "图片路径(可选)"
                          }
                        ],
                        "totalAmount": 总金额(元)
                      }
                      ```
                    - ⚠️ 重要：dishId 和 setmealId 必须是字符串类型（用引号包裹），不能是数字
                    - ⚠️ 重要：每个商品必须有 dishId 或 setmealId 其中之一，不能同时为 null
                    - JSON数据前要有简短的说明文字
                    - 确保JSON格式正确，可以被解析
                    - 价格和金额单位统一使用元（不是分），保留两位小数""")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    public Flux<String> chat(String command){
        return chatClient.prompt(command)
                .stream()
                .content();
    }
}
