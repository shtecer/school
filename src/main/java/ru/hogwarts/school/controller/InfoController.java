package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")

public class InfoController {

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/port")
    public ResponseEntity<Integer> getPort() {
        return ResponseEntity.ok(serverPort);
    }

    @GetMapping("/sum")
    public ResponseEntity<Integer> getSum() {
        int limit = 1_000_000;

        long sum = (long) limit * (1 + limit) / 2;

        return ResponseEntity.ok((int) sum);
    }

}