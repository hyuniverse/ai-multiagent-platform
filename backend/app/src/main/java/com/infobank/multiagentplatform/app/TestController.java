// src/main/java/com/infobank/multiagentplatform/app/TestController.java

package com.infobank.multiagentplatform.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
