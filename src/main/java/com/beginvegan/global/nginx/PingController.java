package com.beginvegan.global.nginx;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RequiredArgsConstructor
@Slf4j
@RestController
public class PingController {

    @GetMapping("/ping")
    public String pingTest() {
        log.info("==== ping test result - pong ====");
        return "pong";
    }
}
