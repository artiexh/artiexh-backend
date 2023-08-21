package com.artiexh.api.service;

import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.domain.User;

public interface RegistrationService {

	User createUser(User user);

	Artist registerArtist(Long id, Shop shop);

	Account createAdmin(Account account);

}
