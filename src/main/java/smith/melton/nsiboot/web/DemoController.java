package smith.melton.nsiboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smith.melton.nsiboot.KStream.KStreamService;

/**
 * @author Melton Smith
 * @since 09.11.2020
 */
@Controller
@RequestMapping("/")
public class DemoController {

    final KStreamService kStreamService;

    public DemoController(KStreamService kStreamService) {
        this.kStreamService = kStreamService;
    }


    @GetMapping
    public String showDemoForm(Model model){
        return "demo";
    }

    @PostMapping
    public String action(@RequestParam(value = "action") String action){
        if (action.equals("start"))
            kStreamService.start();
        else
            kStreamService.stop();
        return "redirect:/";
    }
}
