package org.zvrg.rbacdemo.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class TestController {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/test"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('USER')")
    public Mono<Void> login(@NotNull @Parameter(name = "id", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "id", required = true) Integer id,
                            @Parameter(hidden = true) final ServerWebExchange exchange) {
        return Mono.just(id)
                .flatMap(x -> {
                    log.info("{}", x);
                    return exchange.getResponse().setComplete();
                });
    }

}
