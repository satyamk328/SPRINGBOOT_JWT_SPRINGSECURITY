package com.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.bean.User;

public interface UserDao extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

}
