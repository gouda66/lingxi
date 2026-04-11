package com.lingxi.mailmcpclient;

import com.lingxi.mailmcpclient.client.MailClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.Scanner;

@SpringBootApplication
public class MailMcpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailMcpClientApplication.class, args);
    }


//    /**
//     * 创建一个命令行用户交互
//     * @param mailClient 邮件智能体
//     * @return 命令行输出
//     */
//    @Bean
//    CommandLineRunner cli(MailClient mailClient){
//        return args -> {
//            var scanner = new Scanner(System.in);
//            System.out.println("Agent: 我是一个非常聪明的邮件助手，能够帮您编辑并发送邮件。");
//            while (true){
//                System.out.print("User: ");
//                String userCommand = scanner.nextLine();
//                if(userCommand == null || userCommand.trim().isEmpty()){
//                    System.out.println("System: 请输入您的指令");
//                    continue;
//                }
//                if(userCommand.contains("exit") || userCommand.contains("退出")){
//                    break;
//                }
//                Flux<String> sendResponse = mailClient.sendMail(userCommand);
//                System.out.print("Agent: ");
//                sendResponse
//                        .doOnNext(System.out::print)
//                        .blockLast();
//                System.out.println();
//            }
//        };
//    }

}
