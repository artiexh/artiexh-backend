package com.artiexh.authorization.client.service;

import com.artiexh.model.domain.User;

import java.util.Optional;

public interface AuthenticationService {

	Optional<User> login(String username, String password);

}
