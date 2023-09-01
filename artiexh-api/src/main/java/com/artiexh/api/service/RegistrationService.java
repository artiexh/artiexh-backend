package com.artiexh.api.service;

import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.User;
import com.artiexh.model.rest.artist.RegistrationShopRequest;

public interface RegistrationService {

	User createUser(User user);

	Artist registerArtist(Long id, RegistrationShopRequest request);

	Account createAdmin(Account account);

}
