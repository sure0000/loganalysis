package com.dashu.loganalysis.report.service;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import org.junit.Test;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午3:30
 **/
public class EmailServiceTest extends LoganalysisApplicationTests {
    @Resource
    private EmailService emailService;
    @Test
    public void sendEmail() throws AddressException {
        Address address = new InternetAddress("xuyouchang@treefintech.com");
        Address address1 = new InternetAddress("wujianyang@treefintech.com");
        Address[] addresses = new Address[]{address, address1};
        emailService.sendEmail("bigdata@treefintech.com",
                addresses,
                "smtp.exmail.qq.com",
                "Test",
                "<h1>this is a test</h1>",
                "HTML",
                "Dashu0701");
    }
}