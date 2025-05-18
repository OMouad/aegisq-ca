package io.aegisq;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ca")
public class PingController {
    @GetMapping("/ping")
    public String ping() { return "CA up"; }
}
