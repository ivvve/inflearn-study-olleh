package io.github.ivvve.studyolleh.account;

import io.github.ivvve.studyolleh.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class SignUpFormValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(final Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(final Object o, final Errors errors) {
        final SignUpForm signUpForm = (SignUpForm) o;

        if (this.accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail(), "이메일"}, "이미 사용 중인 이메일입니다");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname(), "닉네임"}, "이미 사용 중인 닉네임입니다");
        }
    }
}
