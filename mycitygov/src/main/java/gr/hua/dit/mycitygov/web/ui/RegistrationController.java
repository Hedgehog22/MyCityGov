package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.port.GovIdentityPort;
import gr.hua.dit.mycitygov.core.service.UserService;
import gr.hua.dit.mycitygov.core.service.model.UserRegistrationDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final GovIdentityPort govIdentityPort;

    public RegistrationController(UserService userService, GovIdentityPort govIdentityPort) {
        this.userService = userService;
        this.govIdentityPort = govIdentityPort;
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            @RequestParam(required = false) String govToken,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (govToken != null && !govToken.isEmpty()) {
            try {
                var citizen = govIdentityPort.getCitizenIdentity(govToken)
                        .orElseThrow(() -> new RuntimeException("TaxisNet token expired or invalid"));

                UserRegistrationDto form = new UserRegistrationDto();
                form.setGovToken(govToken);
                form.setFirstName(citizen.firstName());
                form.setLastName(citizen.lastName());
                form.setAfm(citizen.afm());
                form.setAmka(citizen.amka());

                model.addAttribute("user", form);
                return "register";

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error verifying identity: " + e.getMessage());
                return "redirect:/login";
            }
        }

        if (!govIdentityPort.isServiceAvailable()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠Registration is currently unavailable due to maintenance. Please try again later.");
            return "redirect:/login";
        }

        return "redirect:http://localhost:8081/auth/login";
    }


    @PostMapping("/register")
    public String handleFormSubmission(
            @Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult bindingResult,
            Model model
    ) {
        System.out.println("Token: " + userDto.getGovToken());
        System.out.println("Email: " + userDto.getEmail());

        if (bindingResult.hasErrors()) {
            System.out.println("VALIDATION ERRORS");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("   -> " + error.getDefaultMessage());
            });
            return "register";
        }

        try {
            System.out.println("CALL THE SERVICE");
            userService.registerCitizen(userDto);
            System.out.println("SUCCESS...");
        } catch (RuntimeException e) {
            System.out.println("SERVICE ERROR: " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }

        return "redirect:/login?success";
    }

}