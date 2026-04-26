package org.zvrg.rbacdemo.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.zvrg.rbacdemo.dto.LoginDto;
import org.zvrg.rbacdemo.dto.RegisterDto;
import org.zvrg.rbacdemo.service.securityservice.AuthenticationService;
import org.zvrg.rbacdemo.util.ValidateService;
import reactor.core.publisher.Mono;

import static org.zvrg.rbacdemo.common.Constants.WebConstants.APPLICATION_JSON;
import static org.zvrg.rbacdemo.common.Constants.WebConstants.BEARER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final ValidateService validateService;
    private final AuthenticationService authenticationService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login",
            consumes = { "application/json" }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> login(@Valid @RequestBody Mono<LoginDto> loginDto, @Parameter(hidden = true) final ServerWebExchange exchange) {
        return loginDto
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body is missing")))
                .flatMap(validateService::validate)
                .flatMap(authenticationService::authenticate)
                .flatMap(token -> {
                    exchange.getResponse().getHeaders().set(HttpHeaders.AUTHORIZATION, BEARER + token);
                    exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
                    exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);

                    return exchange.getResponse().setComplete();
                });
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/register",
            consumes = { "application/json" }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> register(@Valid @RequestBody Mono<RegisterDto> registerDto, @Parameter(hidden = true) final ServerWebExchange exchange) {
        return registerDto
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body is missing")))
                .flatMap(validateService::validate)
                .flatMap(authenticationService::register)
                .then(Mono.defer(() -> exchange.getResponse().setComplete()));
    }

}
