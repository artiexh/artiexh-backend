package com.artiexh.api.service.auth;

import com.artiexh.model.domain.User;

import java.util.Optional;

public interface VerifyUserService {

    Optional<User> verifyFromUsernamePassword(String username, String password);

    Optional<User> verifyFromGoogleProvider(String sub);

    Optional<User> verifyFromFacebookProvider(String sub);

    Optional<User> verifyFromTwitterProvider(String sub);


}
