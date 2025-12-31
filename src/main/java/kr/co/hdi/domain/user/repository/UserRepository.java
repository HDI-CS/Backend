package kr.co.hdi.domain.user.repository;

import kr.co.hdi.admin.user.dto.response.ExpertNameResponse;
import kr.co.hdi.domain.user.entity.Role;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByName(String name);

    boolean existsByEmail(String email);

    @Query("""
    SELECT u
    FROM UserEntity u
    WHERE u.userType = :userType
      AND u.role = :role
      AND u.deletedAt IS NULL
    ORDER BY u.id ASC
""")
    List<UserEntity> findExpertByType(@Param("userType") UserType userType, @Param("role")Role role);

    List<UserEntity> findByUserTypeAndDeletedAtIsNull(UserType type);

    @Query("""
    SELECT new kr.co.hdi.admin.user.dto.response.ExpertNameResponse(
        u.id,
        u.name
    )
    FROM UserEntity u
    WHERE u.userType = :userType
      AND u.role = :role
      AND u.deletedAt IS NULL
      AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<ExpertNameResponse> findExpertNamesByUserTypeAndName(
            @Param("userType") UserType userType,
            @Param("name") String name,
            @Param("role") Role role
    );

    Optional<UserEntity> findByIdAndUserTypeAndDeletedAtIsNull(Long id, UserType userType);

    @Query("""
    select u
    from UserEntity u
    where u.userType = :userType
      and u.deletedAt is null
      and u.name like concat('%', :q, '%')
    """)
    List<UserEntity> findByUserTypeAndSearch(
            @Param("userType") UserType userType,
            @Param("q") String q
    );
}
