package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.security.CurrentUser;
import gr.hua.dit.mycitygov.core.security.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackageClasses = { ProfileController.class })
public class CurrentUserControllerAdvice {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserControllerAdvice(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }
    @ModelAttribute("currentUser")
    public CurrentUser getCurrentUser() {
        return this.currentUserProvider.getCurrentUser().orElse(null);
    }
}