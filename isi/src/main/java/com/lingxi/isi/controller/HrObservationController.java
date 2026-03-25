package com.lingxi.isi.controller;

import com.lingxi.isi.service.IHrObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hr/observation")
public class HrObservationController {

    private final IHrObservationService observationService;

    public HrObservationController(IHrObservationService observationService) {
        this.observationService = observationService;
    }
}
