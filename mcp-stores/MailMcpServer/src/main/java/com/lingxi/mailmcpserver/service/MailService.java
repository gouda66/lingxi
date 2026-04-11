package com.lingxi.mailmcpserver.service;

import com.lingxi.mailmcpserver.model.*;

/**
 * @author zhangshdiong
 * @date 2025/5/9 10:46
 * @description 电子邮件服务接口定义
 **/
public interface MailService {

  /**
   * 发送电子邮件
   * @param input 请求
   * @return 响应
   */
  SendMailOutput sendMail(SendMailInput input);

  /**
   * 通过姓名查找收邮件地址
   * @param input 请求
   * @return 响应
   */
  FindContactOutput findContact(FindContactInput input);

  /**
   * 添加联系人
   * @param input 请求
   * @return 响应
   */
  AddContactOutput addContact(AddContactInput input);

  /**
   * 删除联系人
   * @param input 请求
   * @return 响应
   */
  DeleteContactOutput deleteContact(DeleteContactInput input);

  /**
   * 列出所有联系人
   * @return 响应
   */
  ListContactsOutput listContacts();

}