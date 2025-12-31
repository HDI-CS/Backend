package kr.co.hdi.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.domain.user.entity.Role;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.user.entity.QUserEntity.userEntity;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserEntity> searchExperts(UserType type, String keyword) {

        return queryFactory
                .selectFrom(userEntity)
                .where(
                        userEntity.userType.eq(type),
                        userEntity.role.eq(Role.USER),
                        userEntity.deletedAt.isNull(),
                        keywordContains(keyword)
                )
                .orderBy(userEntity.id.desc())
                .fetch();
    }

    private BooleanExpression keywordContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        return userEntity.name.containsIgnoreCase(q)
                .or(userEntity.email.containsIgnoreCase(q))
                .or(userEntity.phoneNumber.containsIgnoreCase(q))
                .or(userEntity.gender.containsIgnoreCase(q))
                .or(userEntity.age.containsIgnoreCase(q))
                .or(userEntity.career.containsIgnoreCase(q))
                .or(userEntity.academic.containsIgnoreCase(q))
                .or(userEntity.expertise.containsIgnoreCase(q))
                .or(userEntity.company.containsIgnoreCase(q))
                .or(userEntity.note.containsIgnoreCase(q));
    }
}
