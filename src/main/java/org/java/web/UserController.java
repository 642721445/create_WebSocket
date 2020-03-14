package org.java.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String name, HttpSession session){
        //将用户名，存储在session
        session.setAttribute("name",name);

        return "/main";
    }
}
