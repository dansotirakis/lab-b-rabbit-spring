package com.rabbit.subscribe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbit.subscribe.domain.dto.SubscriptionDTO;
import com.rabbit.subscribe.domain.enumerated.SubscriptionType;
import com.rabbit.subscribe.service.SubscriptionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/subscription")
public class SubscriptionController {

    private final SubscriptionService service;


    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @ApiOperation(value = "Cria", response = SubscriptionType.class)
    @PostMapping(value = "{subscription}/assign")
    public SubscriptionDTO assign(@PathVariable String subscription) throws JsonProcessingException {
        return service.assign(subscription);
    }

    @ApiOperation(value = "Renova", response = SubscriptionType.class)
    @PostMapping(value = "{subscription}/renovation")
    public String renovation(@PathVariable String subscription) throws JsonProcessingException {
        return service.renovation(subscription);
    }

    @ApiOperation(value = "Cancela", response = SubscriptionType.class)
    @PostMapping(value = "{subscription}/cancel")
    public String cancel(@PathVariable String subscription) throws JsonProcessingException {
        return service.cancel(subscription);
    }

    @ApiOperation(value = "Consulta o status", response = String.class)
    @GetMapping(value = "{subscription}/status")
    public SubscriptionDTO findStatus(@PathVariable String subscription) {
        return service.findStatus(subscription);
    }

}
