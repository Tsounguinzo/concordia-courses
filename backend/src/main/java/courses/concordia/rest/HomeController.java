package courses.concordia.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {return "Hello, Home!";}

    @GetMapping("/security")
    public String secured() {return "Well,Secured";}
}
