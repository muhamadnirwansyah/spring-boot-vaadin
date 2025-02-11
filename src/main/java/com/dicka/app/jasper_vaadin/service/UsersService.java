package com.dicka.app.jasper_vaadin.service;

import com.dicka.app.jasper_vaadin.entity.Users;
import com.dicka.app.jasper_vaadin.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public Users handleSaveOrUpdate(Users users){
        return usersRepository.save(users);
    }

    public Users findUsersById(Long id){
        return usersRepository.findById(id)
                .orElse(null);
    }

    public List<Users> findAllUsers(){
        return usersRepository.findAll();
    }

    public void deleteUserById(Long id){
        Users users = findUsersById(id);
        usersRepository.delete(users);
    }
}
