package indi.vicliu.juaner.id.id.controller;

import indi.vicliu.juaner.id.id.service.SequenceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/id")
public class IdController {

    @Autowired
    private SequenceService sequenceService;

    @ApiOperation(value = "分布式发号器")
    @GetMapping("/next")
    public long execute() {
        return sequenceService.nextId();
    }

}
