package com.lingxi.isi.controller;

import com.lingxi.isi.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/operation-log")
public class OperationLogController {

    private final IOperationLogService operationLogService;

    public OperationLogController(IOperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }
}
