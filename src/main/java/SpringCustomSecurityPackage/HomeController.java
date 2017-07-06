package SpringCustomSecurityPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;

@Controller
public class HomeController {

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;
    @RequestMapping("/")
    public String index(){
        return "home";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/loginSuccess", method = RequestMethod.GET)
    public String getLogin(Model model, User user, Principal principal){
        user.setEmail(principal.getName());
        String emailSession = user.getEmail();
        user = userRepository.findByUsername(emailSession);
        String typeRole = user.getUserType();
        if(typeRole.equalsIgnoreCase("seeker")){return "seekerlogin";}
        else return "recruiterlogin";
    }

    @RequestMapping(value="/newjob", method = RequestMethod.GET)
    public String jobPostGet(Model model){
        model.addAttribute(new Job());
        return "redirect:/newjob";
    }
    @RequestMapping(value="/newjob", method = RequestMethod.POST)
    public String potJob(@ModelAttribute Job job, Model model, Principal principal){
        job.setDate(new Date());
        job.setPostedBy(principal.getName());
        jobRepository.save(job);
        return "recruiterlogin";
    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "index";
    }
    public UserValidator getUserValidator() {
        return userValidator;
    }
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }
}