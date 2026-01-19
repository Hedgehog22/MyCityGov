package gr.hua.dit.govidentifymock.web.ui;

import org.springframework.stereotype.Controller; // Зверніть увагу: не RestController!
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {


    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "taxis-login";
    }

    @PostMapping("/auth/login")
    public RedirectView handleLogin(@RequestParam String username, @RequestParam String password) {

        if ("nikoletta".equals(username) && "123".equals(password)) {
            String token = "valid-token-111";

            return new RedirectView("http://localhost:8080/register?govToken=" + token);
        }

        return new RedirectView("/auth/login?error");
    }
}