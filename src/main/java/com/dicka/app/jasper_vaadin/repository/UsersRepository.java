package com.dicka.app.jasper_vaadin.repository;

import com.dicka.app.jasper_vaadin.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
}
