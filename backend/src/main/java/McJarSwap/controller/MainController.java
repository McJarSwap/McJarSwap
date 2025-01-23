package McJarSwap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String showMainPage(Model model){
        //model.addAttribute("servers", serverService.getServers());
        model.addAttribute("servers", "hello!!");
        return "main";
    }

}