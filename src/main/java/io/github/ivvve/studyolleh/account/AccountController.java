package io.github.ivvve.studyolleh.account;

import io.github.ivvve.studyolleh.domain.Account;
import io.github.ivvve.studyolleh.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

    @GetMapping("/check-email-token")
    public String checkEmailToken(final String token, final String email, final Model model) {
        final Optional<Account> account = this.accountRepository.findByEmail(email);

        if (account.isEmpty()) {
            model.addAttribute("error", "wrong.email");
            return "account/checked-email";
        }

        if (!account.get().getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return "account/checked-email";
        }

        account.get().completeSignUp();
        model.addAttribute("numberOfUser", this.accountRepository.count());
        model.addAttribute("nickname", account.get().getNickname());
        return "account/checked-email";
    }
}
