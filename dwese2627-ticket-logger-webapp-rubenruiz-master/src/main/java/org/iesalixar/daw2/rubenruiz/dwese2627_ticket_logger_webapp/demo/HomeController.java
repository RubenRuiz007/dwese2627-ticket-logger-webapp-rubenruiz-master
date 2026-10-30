package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.demo;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
}