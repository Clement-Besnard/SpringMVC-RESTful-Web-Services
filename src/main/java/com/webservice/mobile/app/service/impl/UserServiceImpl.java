package com.webservice.mobile.app.service.impl;

import com.webservice.mobile.app.UserRepository;
import com.webservice.mobile.app.io.entity.UserEntity;
import com.webservice.mobile.app.service.UserService;
import com.webservice.mobile.app.shared.Utils;
import com.webservice.mobile.app.shared.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) {


        if (userRepository.findUserByEmail(userDTO.getEmail()) !=null)
            throw new RuntimeException("Record Already Exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO,userEntity);

        String autoGeneratedPublicUserID = utils.generateUserId(30);
        userEntity.setUserId(autoGeneratedPublicUserID);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        UserEntity storedUSerDeatils =userRepository.save(userEntity);
        UserDTO returnValue = new UserDTO();
        BeanUtils.copyProperties(storedUSerDeatils,returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity= userRepository.findUserByEmail(email);
        if (userEntity ==null)throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }
}
