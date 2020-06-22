package io.github.ivvve.studyolleh.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(final String email);
    boolean existsByNickname(final String nickname);
    Optional<Account> findByEmail(final String email);
}
