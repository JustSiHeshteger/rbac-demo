package org.zvrg.rbacdemo.service.dataservice;

import jakarta.validation.ClockProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zvrg.rbacdemo.dto.RegisterDto;
import org.zvrg.rbacdemo.entity.RoleEntity;
import org.zvrg.rbacdemo.entity.UserEntity;
import org.zvrg.rbacdemo.repository.RoleRepository;
import org.zvrg.rbacdemo.repository.UserRepository;
import org.zvrg.rbacdemo.repository.UserRoleRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

import static org.zvrg.rbacdemo.common.Constants.RoleConstants.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClockProvider clockProvider;

    @Transactional
    public Mono<Void> createNewUser(RegisterDto registerDto) {
        return userRepository.existsByLogin(registerDto.getLogin())
                .flatMap(exists -> {
                    //TODO
                    if (exists) return Mono.error(new RuntimeException("User with this login already exists"));
                    return saveUserProcess(registerDto);
                });
    }

    private Mono<Void> saveUserProcess(RegisterDto registerDto) {
        return Mono.just(registerDto)
                .flatMap(this::mapUserEntity)
                .flatMap(userRepository::save)
                .flatMap(this::addBaseRole)
                .then();
    }

    private Mono<UserEntity> mapUserEntity(RegisterDto registerDto) {
        return Mono.fromCallable(() -> passwordEncoder.encode(registerDto.getPassword()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(encodedPassword -> {
                    final var todayLocalDate = LocalDateTime.now(clockProvider.getClock());
                    return new UserEntity()
                            .setLogin(registerDto.getLogin())
                            .setPassword(encodedPassword)
                            .setEmail(registerDto.getEmail())
                            .setCreatedAt(todayLocalDate)
                            .setUpdatedAt(todayLocalDate);
                });
    }

    private Mono<Void> addBaseRole(UserEntity userEntity) {
        return Mono.just(userEntity)
                .map(UserEntity::getId)
                .flatMap(userId ->
                    roleRepository.findByName(ROLE_USER)
                            //TODO
                            .switchIfEmpty(Mono.error(new RuntimeException("Role not found")))
                            .map(RoleEntity::getId)
                            .flatMap(roleId -> userRoleRepository.insertRole(userId, roleId))
                );
    }

}
