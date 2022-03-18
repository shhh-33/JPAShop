package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //logger(lombok)
public class HomeController {

    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home"; //home.html 찾아간다.
    }
}
