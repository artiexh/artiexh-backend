package com.artiexh.api.service;

import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.User;
import com.artiexh.model.rest.artist.request.RegistrationShopRequest;

public interface RegistrationService {

	User createUser(User user);

	Artist registerArtist(Long id);

	Account createAdmin(Account account);

}
