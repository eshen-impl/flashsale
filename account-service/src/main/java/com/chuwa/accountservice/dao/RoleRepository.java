package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.Role;
import com.chuwa.accountservice.model.enumtype.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByType(RoleType type);
}
