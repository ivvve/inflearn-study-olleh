package io.github.ivvve.studyolleh.account;

import io.github.ivvve.studyolleh.domain.Account;
import io.github.ivvve.studyolleh.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final PasswordHashEncoder passwordHashEncoder;

    public void processNewAccount(final SignUpForm signUpForm) {
        final Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(final @Valid SignUpForm signUpForm) {
        final Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(this.passwordHashEncoder.encode(signUpForm.getPassword()))
                .studyEnrollmentResultByWeb(true)
                .studyCreatedByWeb(true)
                .studyUpdatedByWeb(true)
                .build();

        return this.accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(final Account newAccount) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올레, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        this.mailSender.send(mailMessage);
    }
}
