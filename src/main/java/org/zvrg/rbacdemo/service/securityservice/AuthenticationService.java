package org.zvrg.rbacdemo.service.securityservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.dto.LoginDto;
import org.zvrg.rbacdemo.dto.RegisterDto;
import org.zvrg.rbacdemo.dto.SecurityUser;
import org.zvrg.rbacdemo.service.dataservice.UserService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public Mono<String> authenticate(LoginDto loginDto) {
        return reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword()))
                .mapNotNull(auth -> (SecurityUser) auth.getPrincipal())
                .flatMap(securityUser -> jwtService.generateToken(securityUser.getId()));
    }

    public Mono<Void> register(RegisterDto registerDto) {
        return Mono.just(registerDto)
                .flatMap(userService::createNewUser);
    }

}
