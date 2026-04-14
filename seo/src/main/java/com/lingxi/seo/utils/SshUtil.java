package com.lingxi.seo.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * SSH 工具类 - 用于连接 Ubuntu 服务器并操作 Nginx 配置
 */
@Slf4j
@Component
public class SshUtil {
    
    /**
     * 通过 SSH 上传文件到远程服务器
     * 
     * @param host 服务器 IP
     * @param port SSH 端口（默认 22）
     * @param username 用户名
     * @param password 密码
     * @param remotePath 远程路径
     * @param fileName 文件名
     * @param content 文件内容
     * @return 是否成功
     */
    public boolean uploadFile(String host, int port, String username, String password, 
                             String remotePath, String fileName, String content) {
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            
            // 配置 SSH 连接参数
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // 首次连接时自动接受主机密钥
            session.setConfig(config);
            session.setTimeout(30000); // 30 秒超时
            session.connect();
            
            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            // 上传文件
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
            channelSftp.put(inputStream, remotePath + "/" + fileName);
            
            log.info("文件上传成功：{}/{}", remotePath, fileName);
            return true;
            
        } catch (Exception e) {
            log.error("SSH 上传文件失败", e);
            return false;
        } finally {
            // 关闭连接
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 通过 SSH 执行远程命令
     * 
     * @param host 服务器 IP
     * @param port SSH 端口
     * @param username 用户名
     * @param password 密码
     * @param command 要执行的命令
     * @return 命令执行结果
     */
    public String executeCommand(String host, int port, String username, String password, String command) {
        Session session = null;
        ChannelExec channelExec = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(30000);
            session.connect();

            // 打开执行通道
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);

            // 获取输出流和错误流
            InputStream in = channelExec.getInputStream();
            InputStream err = channelExec.getErrStream();

            channelExec.connect();

            // 读取命令输出
            StringBuilder output = new StringBuilder();
            byte[] buffer = new byte[1024];
            int len;

            // 读取标准输出
            while ((len = in.read(buffer)) > 0) {
                output.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            // 读取错误输出（nginx -t 的输出通常在错误流中）
            while ((len = err.read(buffer)) > 0) {
                output.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            channelExec.disconnect();

            String result = output.toString();

            log.info("命令执行完成：{}, 结果：{}", command, result);
            return result;

        } catch (Exception e) {
            log.error("SSH 执行命令失败", e);
            return "执行失败：" + e.getMessage();
        } finally {
            if (channelExec != null && channelExec.isConnected()) {
                channelExec.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 测试 SSH 连接
     */
    public boolean testConnection(String host, int port, String username, String password) {
        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(10000);
            session.connect();

            log.info("SSH 连接测试成功");
            return true;
        } catch (Exception e) {
            log.error("SSH 连接测试失败", e);
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * 读取远程文件内容
     *
     * @param host 服务器 IP
     * @param port SSH 端口
     * @param username 用户名
     * @param password 密码
     * @param filePath 远程文件路径
     * @return 文件内容
     */
    public String readFile(String host, int port, String username, String password, String filePath) {
        Session session = null;
        ChannelExec channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(30000);
            session.connect();

            // 打开执行通道，使用 cat 命令读取文件
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("cat " + filePath);

            // 获取输出流
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();

            channel.connect();

            // 读取文件内容
            StringBuilder content = new StringBuilder();
            byte[] buffer = new byte[1024];
            int len;

            // 读取标准输出
            while ((len = in.read(buffer)) > 0) {
                content.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            // 检查错误输出
            StringBuilder errorOutput = new StringBuilder();
            while ((len = err.read(buffer)) > 0) {
                errorOutput.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            channel.disconnect();

            // 如果有错误输出，返回 null
            if (errorOutput.length() > 0) {
                log.error("读取文件失败：{} - {}", filePath, errorOutput.toString());
                return null;
            }

            log.info("文件读取成功：{}", filePath);
            return content.toString();

        } catch (Exception e) {
            log.error("读取文件失败：{}", filePath, e);
            return null;
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * 写入内容到远程文件
     *
     * @param host 服务器 IP
     * @param port SSH 端口
     * @param username 用户名
     * @param password 密码
     * @param filePath 远程文件路径
     * @param content 文件内容
     * @return 是否成功
     */
    public boolean writeFile(String host, int port, String username, String password, String filePath, String content) {
        Session session = null;
        ChannelExec channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(30000);
            session.connect();

            // 使用 tee 命令写入文件（需要 root 权限）
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("tee " + filePath + " > /dev/null");

            // 设置输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            channel.setInputStream(inputStream);

            // 获取错误流
            InputStream err = channel.getErrStream();

            channel.connect();

            // 等待命令执行完成
            while (!channel.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 检查错误输出
            StringBuilder errorOutput = new StringBuilder();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = err.read(buffer)) > 0) {
                errorOutput.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            channel.disconnect();

            // 检查退出状态
            if (channel.getExitStatus() == 0) {
                log.info("文件写入成功：{}", filePath);
                return true;
            } else {
                log.error("文件写入失败：{} - {}", filePath, errorOutput.toString());
                return false;
            }

        } catch (Exception e) {
            log.error("写入文件失败：{}", filePath, e);
            return false;
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

}
