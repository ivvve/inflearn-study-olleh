package io.github.ivvve.studyolleh.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 요청 제한 설정
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 권한 없어도 요청 가능
                .mvcMatchers("/", "/login", "/sign-up", "/check-email",
                        "/check-email-token", "/email-login", "/check-email-login", "/login-link")
                    .permitAll()
                // 특정 Method 설정
                .mvcMatchers(HttpMethod.GET, "/profile/*")
                    .permitAll()
                .anyRequest()
                    .authenticated();
    }

    /**
     * static resource security 무시
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
