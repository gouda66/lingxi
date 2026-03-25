package com.lingxi.isi.controller;

import com.lingxi.isi.service.IEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email/template")
public class EmailTemplateController {

    private final IEmailTemplateService emailTemplateService;

    public EmailTemplateController(IEmailTemplateService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }
}
