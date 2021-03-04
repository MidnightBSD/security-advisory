package org.midnightbsd.advisory.ctl;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author Lucas Holt
 */
@Controller
@RequestMapping("/")
public final class HomeController {

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String home(Model model, HttpSession session) {

        return "index";
    }
}
