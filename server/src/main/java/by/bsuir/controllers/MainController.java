package by.bsuir.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/main")
public class MainController {

    @GetMapping
    public String filePage() {
        return "creation_page";
    }

}
