package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.security.CurrentUserProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final CurrentUserProvider currentUserProvider;

    public ProfileController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        var user = currentUserProvider.requireCurrentUser();
        model.addAttribute("fullName", user.fullName());
        model.addAttribute("email", user.email());
        model.addAttribute("role", user.role());
        return "profile";
    }
}