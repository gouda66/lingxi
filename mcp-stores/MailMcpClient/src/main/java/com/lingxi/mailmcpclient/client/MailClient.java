package com.lingxi.mailmcpclient.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class MailClient {

  private final ChatClient chatClient;

  /**
   * 构造方法
   * @param chatClientBuilder spring ai根据yml中的spring.ai.model配置自动装配的对话客户端builder
   * @param toolCallbackProvider spring ai根据yml中的spring.ai.mcp.client.sse.connections配置自动装配的mcp工具provider
   */
  public MailClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider){
    //创建会话记忆窗口，最大10轮
    ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
    //创建与大模型的交互客户端
    this.chatClient = chatClientBuilder.defaultSystem("""
                    你是一个聪明的邮件助手，根据用户指令发送电子邮件，步骤如下：
                    1、先获取工具列表，找到需要的工具和对应的参数，所有的工具调用都要基于事实，不要猜测，不要假设；
                    2、根据用户的要求生成合理的邮件内容，根据邮件内容生成合适的邮件主题；邮件内容需要包含称呼、问候语、正文、祝福语；发件人是四川天府银行科技部的张仕东；
                    3、如果用户没有明确给出收件地址，而是姓名，请调用工具查找联系人的邮件地址；如果未查找到联系人，请提示用户提供准确的姓名或直接给出邮件地址；
                    4、在发送前请向用户确认即将发送的邮件标题、内容、收件地址等信息，得到用户肯定确认后调用对应的工具发送邮件；
                    5、在调用工具时，如果缺失参数请向用户询问，直到收集完全部的调用参数后调用工具完成发送，并将发送结果反馈给用户。""")
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) //添加会话记忆
            .defaultToolCallbacks(toolCallbackProvider) //添加工具列表
            .build();
  }

  /**
   * 发送邮件
   * @param command 用户指令
   * @return 运行结果
   */
  public Flux<String> sendMail(String command){
    return chatClient.prompt(command)
            .stream()
            .content();
  }
}