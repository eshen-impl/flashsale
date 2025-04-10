package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.RoleRepository;
import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.exception.DuplicateResourceException;
import com.chuwa.accountservice.model.CustomUserDetails;
import com.chuwa.accountservice.model.Role;
import com.chuwa.accountservice.model.User;
import com.chuwa.securitylib.UserSession;
import com.chuwa.accountservice.model.enumtype.RoleType;
import com.chuwa.accountservice.payload.SignInRequestDTO;
import com.chuwa.accountservice.payload.SignUpRequestDTO;
import com.chuwa.accountservice.service.AuthService;
import com.chuwa.securitylib.RedisUserSessionService;
import com.chuwa.securitylib.JwtUtil;
import com.chuwa.accountservice.util.UUIDUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements AuthService {
    private final RedisUserSessionService redisUserSessionService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(RedisUserSessionService redisUserSessionService, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.redisUserSessionService = redisUserSessionService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signUp(SignUpRequestDTO signUpRequestDTO) {
        String email = signUpRequestDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User with email '" + email + "' already exists.");
        }
        User newUser = new User();
        newUser.setEmail(signUpRequestDTO.getEmail());
        newUser.setUsername(signUpRequestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (signUpRequestDTO.getRoles() == null || signUpRequestDTO.getRoles().isEmpty()) {
            roles.add(roleRepository.findByType(RoleType.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Default role not found.")));
        } else {
            for (String role : signUpRequestDTO.getRoles()) {
                RoleType userRole = RoleType.valueOf(role);
                roles.add(roleRepository.findByType(userRole)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role)));
            }
        }

        newUser.setRoles(roles);
        userRepository.save(newUser); //create account

    }

    @Override
    public Map<String, String> signIn(SignInRequestDTO signInRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequestDTO.getEmail(), signInRequestDTO.getPassword())
            );
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();
            String token = JwtUtil.generateToken(UUIDUtil.encodeUUID(user.getId()));

            UserSession userSession = new UserSession(customUserDetails.getUsername(), (Set<SimpleGrantedAuthority>) customUserDetails.getAuthorities());
            redisUserSessionService.saveUserSession(UUIDUtil.encodeUUID(user.getId()), userSession, 24 * 60L); // 24-hour expiration

            HashMap<String, String> map = new HashMap<>();
            map.put("token", token);
            map.put("username", user.getUsername());
            map.put("roles", user.getRoles().stream()
                    .map(role -> role.getType().name())
                    .collect(Collectors.joining(", ")));
            return map;

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password!");
        }
    }

    @Override
    public void signOut(UUID userId) {
        redisUserSessionService.deleteUserSession(UUIDUtil.encodeUUID(userId));
    }


}
