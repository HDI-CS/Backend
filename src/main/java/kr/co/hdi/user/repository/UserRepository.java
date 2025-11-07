package kr.co.hdi.user.repository;

import kr.co.hdi.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByName(String name);

    boolean existsByEmail(String email);
}
