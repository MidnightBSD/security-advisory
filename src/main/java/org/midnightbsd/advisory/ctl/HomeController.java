package org.midnightbsd.advisory.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * @author Lucas Holt
 */
@Controller
@RequestMapping("/")
final public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {

        return "index";
    }
}
