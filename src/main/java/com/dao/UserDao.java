package com.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bean.User;

public interface UserDao extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

}
