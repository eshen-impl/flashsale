package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.model.CustomUserDetails;
import com.chuwa.accountservice.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));


            return new CustomUserDetails(user);

    }
}

