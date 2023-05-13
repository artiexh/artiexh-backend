package com.artiexh.api.controller.auth;

import com.artiexh.api.common.Endpoint;
import com.artiexh.api.service.auth.VerifyUserService;
import com.artiexh.model.domain.User;
import com.artiexh.model.request.LoginRequest;
import com.artiexh.model.request.OAuth2LoginRequest;
import com.artiexh.model.request.UsernamePasswordLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(Endpoint.Auth.ROOT)
@RequiredArgsConstructor
public class AuthController {

    private final VerifyUserService verifyUserService;

    @PostMapping(value = Endpoint.Auth.VERIFY, params = "type=username-password")
    public User verify(@RequestBody @Valid UsernamePasswordLoginRequest request) {
        return verifyUserService.verifyFromUsernamePassword(request.username(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
    }

    @PostMapping(value = Endpoint.Auth.VERIFY, params = "type=oauth2")
    public User verify(@RequestBody @Valid OAuth2LoginRequest request) {
        return switch (request.provider()) {
            case GOOGLE -> verifyUserService.verifyFromGoogleProvider(request.sub())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token"));
            case FACEBOOK -> verifyUserService.verifyFromFacebookProvider(request.sub())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Facebook token"));
            case TWITTER -> verifyUserService.verifyFromTwitterProvider(request.sub())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Twitter token"));
        };
    }

    @PostMapping(Endpoint.Auth.LOGIN)
    public void login(@RequestBody @Valid LoginRequest request) {

    }

}
