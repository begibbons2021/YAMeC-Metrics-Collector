package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Enum representing the different types of storage media
 */
public enum MediaType {
    UNSPECIFIED(0),
    HDD(3),
    SSD(4),
    SCM(5);

    private final int value;

    MediaType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MediaType fromValue(int value) {
        for (MediaType type : MediaType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNSPECIFIED;
    }
}
