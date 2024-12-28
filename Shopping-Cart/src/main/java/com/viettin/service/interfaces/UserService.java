package com.viettin.service.interfaces;

import com.viettin.dto.UserDto;
import com.viettin.entity.User;
import com.viettin.request.LoginRequest;
import com.viettin.response.Response;

public interface UserService {
    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
}
