package kr.co.hdi.domain.user.repository;

import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserEntity> searchExperts(UserType type, String keyword);
}
