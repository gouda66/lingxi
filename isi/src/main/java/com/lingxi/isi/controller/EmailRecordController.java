package com.lingxi.isi.controller;

import com.lingxi.isi.service.IEmailRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email/record")
public class EmailRecordController {

    private final IEmailRecordService emailRecordService;

    public EmailRecordController(IEmailRecordService emailRecordService) {
        this.emailRecordService = emailRecordService;
    }
}
