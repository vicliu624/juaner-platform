package indi.vicliu.juaner.id.controller;

import indi.vicliu.juaner.id.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/id")
public class IdController {

    @Autowired
    private SequenceService sequenceService;

    @GetMapping("/next")
    public long execute() {
        return sequenceService.nextId();
    }

}
