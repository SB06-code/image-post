package codeit.sb06.imagepost.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

    @RequestMapping(value = {"/app", "/app/{path:^(?!api|static|uploads)[^.]*}/**"})
    public String forwardApp() {
        return "forward:/app/index.html";
    }
}
