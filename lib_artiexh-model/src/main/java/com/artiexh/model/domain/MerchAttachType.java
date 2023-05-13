package com.artiexh.model.domain;

public enum MerchAttachType {
    THUMBNAIL(1),
    OTHER(2);

    private final int value;

    MerchAttachType(int value) {
        this.value = value;
    }

    public static MerchAttachType fromValue(int value) {
        for (MerchAttachType type : MerchAttachType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No such value for MerchAttachType: " + value);
    }

    public int getValue() {
        return value;
    }
}
