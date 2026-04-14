package com.lingxi.mailmcpserver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.lingxi.mailmcpserver.model.*;
import com.lingxi.mailmcpserver.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

  private static final String CONTACTS_FILE = "contacts.json";
  
  private final Map<String, String> contacts = new ConcurrentHashMap<>();

  @Value("${spring.mail.username}")
  private String from;

  @Autowired
  private JavaMailSender mailSender;

  @PostConstruct
  public void init() {
    loadContacts();
    if (contacts.isEmpty()) {
      contacts.put("何龙", "2493576826@qq.com");
      contacts.put("刘显波", "liuxianbo@tf.cn");
      contacts.put("张仕东", "zhangshidong@tf.cn");
      contacts.put("杨帆", "yangfan@tf.cn");
      contacts.put("涂太松", "tutaisong@tf.cn");
      contacts.put("谭伟", "tanwei@tf.cn");
      contacts.put("张沛", "zhangpei1@tf.cn");
      contacts.put("段龙", "duanlong@tf.cn");
      contacts.put("赖健", "laijian@tf.cn");
      contacts.put("邱季", "qiuji@tf.cn");
      contacts.put("蔡玉成", "caiyucheng@tf.cn");
      saveContacts();
    }
    log.info("联系人列表已加载，共 {} 个联系人", contacts.size());
  }

  private void loadContacts() {
    try {
      File file = new File(CONTACTS_FILE);
      if (file.exists()) {
        String content = Files.readString(Paths.get(CONTACTS_FILE));
        Map<String, String> loadedContacts = JSON.parseObject(content, new TypeReference<Map<String, String>>() {});
        contacts.clear();
        contacts.putAll(loadedContacts);
      }
    } catch (Exception e) {
      log.error("加载联系人失败", e);
    }
  }

  private void saveContacts() {
    try (FileWriter writer = new FileWriter(CONTACTS_FILE)) {
      writer.write(JSON.toJSONString(contacts));
      writer.flush();
    } catch (IOException e) {
      log.error("保存联系人失败", e);
    }
  }

  @Tool(description = "Create and send a new email message.")
  @Override
  public SendMailOutput sendMail(@ToolParam(description = "发送邮件的请求参数") SendMailInput input) {
    log.info("====收到 MCP.sendMail 调用请求：{}", input);
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(from);
    simpleMailMessage.setTo(input.getTo());
    if (input.getCc() != null && !input.getCc().isEmpty()) {
        simpleMailMessage.setCc(input.getCc());
    }
    simpleMailMessage.setSubject(input.getSubject());
    simpleMailMessage.setText(input.getBody());
    try{
      mailSender.send(simpleMailMessage);
    }catch (Exception e){
      log.error("send mail failed:", e);
      throw e;
    }
    return SendMailOutput.builder()
            .resultCode("00")
            .resultMsg("发送成功")
            .build();
  }

  @Tool(description = "根据联系人姓名查找对应的邮件地址")
  @Override
  public FindContactOutput findContact(@ToolParam(description = "查找联系人请求参数") FindContactInput input){
    log.info("===收到 MCP.findContact 调用请求:{}", input);
    for (Map.Entry<String, String> contact : contacts.entrySet()){
      Pattern contactPattern = Pattern.compile(".*" + input.getName() + ".*");
      Matcher matcher = contactPattern.matcher(contact.getKey());
      if(matcher.matches()){
        return FindContactOutput.builder()
                .name(contact.getKey())
                .email(contact.getValue())
                .resultCode("00")
                .resultMsg("查找成功")
                .build();
      }
    }
    return FindContactOutput.builder()
            .resultCode("01")
            .resultMsg("未找到该联系人")
            .build();
  }

  @Tool(description = "添加新的联系人到通讯录")
  @Override
  public AddContactOutput addContact(@ToolParam(description = "添加联系人请求参数") AddContactInput input) {
    log.info("===收到 MCP.addContact 调用请求:{}", input);
    
    if (input.getName() == null || input.getName().trim().isEmpty()) {
      return AddContactOutput.builder()
              .resultCode("01")
              .resultMsg("联系人姓名不能为空")
              .build();
    }
    
    if (input.getEmail() == null || !input.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      return AddContactOutput.builder()
              .resultCode("01")
              .resultMsg("邮箱地址格式不正确")
              .build();
    }
    
    contacts.put(input.getName(), input.getEmail());
    saveContacts();
    log.info("联系人添加成功: {} -> {}", input.getName(), input.getEmail());
    
    return AddContactOutput.builder()
            .resultCode("00")
            .resultMsg("联系人添加成功")
            .build();
  }

  @Tool(description = "根据姓名删除联系人")
  @Override
  public DeleteContactOutput deleteContact(@ToolParam(description = "删除联系人请求参数") DeleteContactInput input) {
    log.info("===收到 MCP.deleteContact 调用请求:{}", input);
    
    String removedEmail = contacts.remove(input.getName());
    if (removedEmail != null) {
      saveContacts();
      log.info("联系人删除成功: {}", input.getName());
      return DeleteContactOutput.builder()
              .resultCode("00")
              .resultMsg("联系人删除成功")
              .build();
    } else {
      return DeleteContactOutput.builder()
              .resultCode("01")
              .resultMsg("未找到该联系人")
              .build();
    }
  }

  @Tool(description = "列出通讯录中的所有联系人")
  @Override
  public ListContactsOutput listContacts() {
    log.info("===收到 MCP.listContacts 调用请求");
    
    List<Map<String, String>> contactList = contacts.entrySet().stream()
            .map(entry -> {
              Map<String, String> map = new HashMap<>();
              map.put("name", entry.getKey());
              map.put("email", entry.getValue());
              return map;
            })
            .toList();
    
    return ListContactsOutput.builder()
            .resultCode("00")
            .resultMsg("查询成功")
            .contacts(contactList)
            .build();
  }
}