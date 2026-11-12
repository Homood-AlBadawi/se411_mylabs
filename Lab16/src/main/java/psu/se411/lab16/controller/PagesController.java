package psu.se411.lab16.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PagesController {

    @RequestMapping("/")
    public String home() { 
        return "index";
    }

	
    @RequestMapping("/dashboard")
    public String dashboard() { 
        return "dashboard";
    }
    
    @RequestMapping("/admindashboard")
    public String admindashboard() { 
        return "adminDashboard";
    }
    
}
