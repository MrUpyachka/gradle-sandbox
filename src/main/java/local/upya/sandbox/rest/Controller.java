package local.upya.sandbox.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.RequestHandledEvent;

@RestController
public class Controller {

    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @EventListener
    public void handleEvent (RequestHandledEvent e) {
        LOG.info("Event: {}", e);
    }

    @GetMapping("/check")
    public String check() {
        LOG.info("Someone accessed /check endpoint");
        return "OK";
    }
}
