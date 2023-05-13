package com.artiexh.model.domain;

public enum Role {
    ADMIN(0),
    USER(1),
    ARTIST(2);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("No such value for Role: " + value);
    }

    public int getValue() {
        return value;
    }
}
