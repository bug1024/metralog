package com.bug1024.metralog.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Application
 * @author bug1024
 * @date 2019-06-29
 */
@SpringBootApplication(scanBasePackages = {"com.bug1024.metralog"})
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/pubCheck")
    public String pubCheck() {
        return "ok";
    }
}
