package io.github.ivvve.studyolleh.account;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashEncoder {
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public String encode(final String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
