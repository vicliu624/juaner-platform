package indi.vicliu.juaner.upms.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    @GetMapping("metrics")
    public void metrics(HttpServletResponse response) throws IOException {
      response.sendRedirect("/prometheus");
    }
    
}
