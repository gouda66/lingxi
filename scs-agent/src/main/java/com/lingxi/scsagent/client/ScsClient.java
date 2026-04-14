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
                    6. 如果缺少必要参数，主动向用户询问。""")
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
