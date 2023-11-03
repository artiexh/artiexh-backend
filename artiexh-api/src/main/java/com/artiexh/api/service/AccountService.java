package com.artiexh.api.service;

import com.artiexh.model.domain.Account;
import com.artiexh.model.rest.account.AccountProfile;

public interface AccountService {

	Account getUserById(Long id);

	AccountProfile getUserProfile(Long id);

	AccountProfile updateProfile(Long id, AccountProfile profile);
}
