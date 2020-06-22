package io.github.ivvve.studyolleh.account;

import io.github.ivvve.studyolleh.domain.Account;
import io.github.ivvve.studyolleh.domain.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JavaMailSender mailSender;

    @DisplayName("회원 가입 화면이 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        this.mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmitWithWrongInput() throws Exception {
        this.mockMvc.perform(
                post("/sign-up")
                        .param("nickname", "keesun")
                        .param("email", "email..")
                        .param("password", "12345")
                        .with(csrf())
        )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("/account/sign-up"));
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpSubmitWithCorrectInput() throws Exception {
        final String email = "keesun@email.com";
        final String password = "12345";

        this.mockMvc.perform(
                post("/sign-up")
                        .param("nickname", "keesun")
                        .param("email", email)
                        .param("password", password)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertThat(this.accountRepository.existsByEmail(email)).isTrue();
        then(this.mailSender).should().send(any(SimpleMailMessage.class));

        final Account account = this.accountRepository.findByEmail(email).get();
        assertThat(account.getPassword()).isNotEqualTo(password);
        assertThat(account.getEmailCheckToken()).isNotNull();
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailTokenWithWrongInput() throws Exception {
        this.mockMvc.perform(
                get("/check-email-token")
                        .param("token", "asdfasdaf")
                        .param("email", "email@email.com")
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"));
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    void checkEmailTokenWithCorrentInput() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .password("12344567")
                .nickname("keesun")
                .build();
        account.generateEmailCheckToken();
        account = this.accountRepository.save(account);

        this.mockMvc.perform(
                get("/check-email-token")
                        .param("token", account.getEmailCheckToken())
                        .param("email", account.getEmail())
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"));
    }
}