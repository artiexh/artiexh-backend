package com.artiexh.authorization.client.service;

import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.User;

public interface RegistrationService {

	User createUser(User user);

	Artist registerArtist(Long id);

	Account createAdmin(Account account);

}
