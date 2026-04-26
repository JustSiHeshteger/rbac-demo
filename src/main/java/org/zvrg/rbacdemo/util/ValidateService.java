package org.zvrg.rbacdemo.util;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final Validator validator;

    public <T> Mono<T> validate(T obj) {
        final var violations = validator.validate(obj);

        if (!violations.isEmpty())
            return Mono.error(new ConstraintViolationException(violations));

        return Mono.just(obj);
    }

}
