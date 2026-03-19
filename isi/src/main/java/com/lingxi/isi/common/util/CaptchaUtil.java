package com.lingxi.isi.common.util;

import com.lingxi.isi.infrastructure.properties.CaptchaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaUtil {

    private final CaptchaProperties captchaProperties;
    
    private static final int WIDTH = 100;
    private static final int HEIGHT = 36;
    
    private final Random random = new Random();

    /**
     * 生成验证码图片
     * @return CaptchaImageResult 包含验证码图片和对应的验证码字符串
     */
    public CaptchaImageResult generateCaptchaImageWithCode() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 绘制边框
        g.setColor(new Color(210, 210, 210));
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        
        // 绘制干扰线
        drawLines(g, 15);
        
        // 生成并绘制验证码字符
        StringBuilder code = new StringBuilder();
        int length = captchaProperties.getLength();
        String chars = captchaProperties.getChars();
        
        for (int i = 0; i < length; i++) {
            String ch = String.valueOf(chars.charAt(random.nextInt(chars.length())));
            code.append(ch);
            
            g.setFont(generateRandomFont());
            g.setColor(getRandomColor(50, 150));
            
            // 随机旋转
            double rotation = random.nextDouble() * 0.4 - 0.2;
            g.rotate(rotation, WIDTH / (length * 2.0) * (i + 0.5), HEIGHT / 2.0);
            g.drawString(ch, (float)(WIDTH / (length * 2.0) * (i + 0.5) - 5), (float)(HEIGHT / 2.0 + 5));
            g.rotate(-rotation, WIDTH / (length * 2.0) * (i + 0.5), HEIGHT / 2.0);
        }
        
        // 绘制干扰点
        drawPoints(g, 50);
        
        g.dispose();
        
        return new CaptchaImageResult(image, code.toString());
    }

    public record CaptchaImageResult(BufferedImage image, String code) {}

    /**
     * 生成验证码字符串
     * @return 验证码
     */
    public String generateCaptchaCode() {
        StringBuilder code = new StringBuilder();
        int length = captchaProperties.getLength();
        String chars = captchaProperties.getChars();
        
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * 生成随机颜色
     */
    private Color getRandomColor(int minColor, int maxColor) {
        if (minColor > 255) minColor = 255;
        if (maxColor > 255) maxColor = 255;
        
        int red = minColor + random.nextInt(maxColor - minColor);
        int green = minColor + random.nextInt(maxColor - minColor);
        int blue = minColor + random.nextInt(maxColor - minColor);
        
        return new Color(red, green, blue);
    }

    /**
     * 生成随机字体
     */
    private Font generateRandomFont() {
        String[] fonts = {"微软雅黑", "宋体", "楷体", "Arial", "Verdana"};
        String fontName = fonts[random.nextInt(fonts.length)];
        int fontSize = 20 + random.nextInt(6);
        int fontStyle = random.nextInt(3);
        
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * 绘制干扰线
     */
    private void drawLines(Graphics2D g, int count) {
        for (int i = 0; i < count; i++) {
            g.setColor(getRandomColor(100, 200));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制干扰点
     */
    private void drawPoints(Graphics2D g, int count) {
        for (int i = 0; i < count; i++) {
            g.setColor(getRandomColor(100, 200));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillOval(x, y, 2, 2);
        }
    }
}
