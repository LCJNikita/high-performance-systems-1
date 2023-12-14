package ru.itmo.hpsproject.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.exceptions.UserAlreadyExistsException;
import ru.itmo.hpsproject.model.dto.JwtResponseDto;
import ru.itmo.hpsproject.model.dto.UserRegisterRequestDto;
import ru.itmo.hpsproject.model.entity.UserEntity;
import ru.itmo.hpsproject.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto login(String username, String password) throws BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }

    public UserEntity register(UserRegisterRequestDto userRegisterRequest) throws UserAlreadyExistsException {
        return userService.register(userRegisterRequest);
    }





}
