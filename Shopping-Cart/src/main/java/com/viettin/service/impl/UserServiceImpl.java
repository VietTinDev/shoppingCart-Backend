package com.viettin.service.impl;

import com.viettin.dto.AddressDto;
import com.viettin.dto.UserDto;
import com.viettin.entity.Address;
import com.viettin.entity.User;
import com.viettin.enums.Role;
import com.viettin.exception.InvalidCredentialsException;
import com.viettin.exception.NotFoundException;
import com.viettin.mapper.MapperConfigs;
import com.viettin.repository.UserRepository;
import com.viettin.request.LoginRequest;
import com.viettin.response.Response;
import com.viettin.security.JwtUtils;
import com.viettin.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final MapperConfigs mapperConfigs;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Response registerUser(UserDto registrationRequest) {
        Role role = Role.USER;

        if(registrationRequest.getRole() != null && registrationRequest.getRole().equalsIgnoreCase("admin")){
            role = Role.ADMIN;
        }
        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .build();
        User savedUser = userRepository.save(user);
        UserDto userDto = mapperConfigs.mapUserToDtoBasic(savedUser);
        return Response.builder()
                .status(200)
                .message("Register Successfully")
                .userDto(userDto)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Password does not match");
        }
        String token = jwtUtils.generateToken(user);
        return Response.builder()
                .status(200)
                .message("User Successfully Logged In")
                .token(token)
                .expirationTime("1 Day")
                .role(user.getRole().name())
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = users.stream().map(mapperConfigs::mapUserToDtoBasic).collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .userDtoList(userDtoList)
                .build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        Address address = user.getAddress();
        UserDto userDto = mapperConfigs.mapUserToDtoPlusAddressAndOrderHistory(user);
        AddressDto addressDto = mapperConfigs.mapAddressToDtoBasic(address);
        return Response.builder()
                .status(200)
                .userDto(userDto)
                .addressDto(addressDto)
                .build();
    }
}
