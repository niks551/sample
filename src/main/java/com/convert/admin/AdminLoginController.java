package com.convert.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class AdminLoginController {

    AdminLoginController() {
    }

    @RequestMapping("/login")
    public ModelAndView newLogin() {
        System.out.println("here");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("jaffa");
        return mav;
    }

}
