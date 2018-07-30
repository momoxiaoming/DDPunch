package com.al.ddpunch.email;

import com.sun.mail.util.MailSSLSocketFactory;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by zhangxiaoming on 2018/7/30.
 */

public class EmaiUtil {


    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "465";
    private static final String FROM_ADD = "2742372881@qq.com"; //发送方邮箱
    private static final String FROM_PSW = "pmzlyeggchwjdcfg";//发送方邮箱授权码


    public static void sendMsg(final String msg, final String toAdress) {



        new Thread(new Runnable() {
            @Override
            public void run() {
                sendText(msg,toAdress);
            }
        }).start();


    }



    private static void sendText(String msg,String toAdress){
        try {
            // 获取系统属性
            Properties properties = System.getProperties();

            // 设置邮件服务器
            properties.setProperty("mail.smtp.host", HOST); //设置邮件服务器
            properties.setProperty("mail.smtp.port", PORT);//设置服务器断开
            properties.put("mail.smtp.auth", "true"); //设置auth验证

            //QQ邮箱需要加入ssl加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);


            Session session = Session.getDefaultInstance(properties,new Authenticator(){
                public PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(FROM_ADD, FROM_PSW); //发件人邮件用户名、密码
                }
            });

            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(FROM_ADD));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toAdress));

            // Set Subject: 头部头字段
            message.setSubject("通知邮件");

            // 设置消息体
            message.setText(msg);

            // 发送消息
            Transport.send(message);
            System.out.println("邮件发送成功");
        } catch (Exception mex) {
            mex.printStackTrace();
        }
    }


}
