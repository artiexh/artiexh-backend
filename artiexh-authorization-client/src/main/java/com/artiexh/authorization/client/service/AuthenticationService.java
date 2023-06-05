package com.artiexh.authorization.client.service;

import com.artiexh.model.domain.Account;

import java.util.Optional;

public interface AuthenticationService {

	Optional<Account> login(String username, String password);

}
