package local.upya.sandbox.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/check")
    public String check() {
        return "OK";
    }
}
