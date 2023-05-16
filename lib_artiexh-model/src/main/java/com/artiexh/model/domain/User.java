package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private Role role;
    private UserStatus status;
    private String avatarUrl;
    private String email;
    private String googleId;
    private String twitterId;
    private String facebookId;
    private Set<Subscription> subscriptionsTo;
}
