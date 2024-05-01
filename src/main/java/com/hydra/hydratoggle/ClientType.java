package com.hydra.hydratoggle;

import lombok.Getter;

import java.util.Objects;

public enum ClientType {
    RUNELITE("0"),
    HYDRA("1"),
    UNKNOWN("2");

    @Getter
    private final String value;

    ClientType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if(Objects.equals(this.value, "0")) {
            return "RuneLite Client";
        }
        return "Hydra Client";
    }
}
