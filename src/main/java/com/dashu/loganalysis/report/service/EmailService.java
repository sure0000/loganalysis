package com.dashu.loganalysis.report.service;

import java.util.Map;

import javax.mail.Address;

/**
 * @Description 邮件服务
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:18
 **/
public interface EmailService {

    /**
     * 邮件发送
     * @param emailConf 邮件配置
     * @param subject 主题
     * @param content 邮件内容
     * @param emailType 邮件类型
     */
    void sendEmail(Map emailConf,
                   String subject,
                   String content,
                   String emailType);
}
