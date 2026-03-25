package com.lingxi.isi.controller;

import com.lingxi.isi.service.IHrInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hr/intervention")
public class HrInterventionController {

    private final IHrInterventionService interventionService;

    public HrInterventionController(IHrInterventionService interventionService) {
        this.interventionService = interventionService;
    }
}
