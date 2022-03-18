package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello") //hello url로 이동하면 컨트롤러를 실행하겠다.
    public String hello(Model model) {
        model.addAttribute("data", "hello!!");
        //model에 데이터를 컨트롤러에 실어서 view로 넘긴다.
        //name이 data인 value 값이 hello 인거 넘긴다
        return "hello"; //스프링부트의 타임리프가 설정을 걸면서 자동으로 hello.html로 매핑해서 이동

        /*
         스프링 부트 thymeleaf viewName 매핑
         resources:templates/ +{ViewName}+ .html
         */
    }
}