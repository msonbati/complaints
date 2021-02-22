package com.quality.complaints.controller;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
@RequestMapping("/")
public class AdminEndPoints  implements WebMvcConfigurer {

    @RequestMapping(value = "/login")
    public String index() {
        return "login";
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("customlogin");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
