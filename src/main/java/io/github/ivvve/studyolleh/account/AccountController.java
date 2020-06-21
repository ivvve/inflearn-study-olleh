package io.github.ivvve.studyolleh.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    /**
     * SignUpForm이 들어오면 addValidator에 있는 것이 validation을 한다
     */
    @InitBinder("signUpForm")
    public void initBinder(final WebDataBinder webDataBinder) {
        webDataBinder.addValidators(this.signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpView(final Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid final SignUpForm signUpForm, final Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        this.accountService.processNewAccount(signUpForm);
        return "redirect:/";
    }
}
