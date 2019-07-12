package com.dashu.loganalysis.report.service;

import javax.mail.Address;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:18
 **/
public interface EmailService {

    /**
     * 邮件发送
     * @param from 发送方
     * @param to 接收方
     * @param host 邮件服务器
     * @param subject 主题
     * @param content 邮件内容
     * @param emailType 邮件类型
     * @param password 发送方密码
     */
    void sendEmail(String from,
                   Address[] to,
                   String host,
                   String subject,
                   String content,
                   String emailType,
                   String password);
}
