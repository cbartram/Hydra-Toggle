package com.hydra.hydratoggle.model;

import lombok.Getter;

import java.util.Objects;

public enum ClientType {
    RUNELITE("RUNELITE"),
    HYDRA("HYDRA"),
    UNKNOWN("UNKNOWN");

    @Getter
    private final String value;

    ClientType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if(Objects.equals(this.value, "RUNELITE")) {
            return "RuneLite Client";
        }
        return "Hydra Client";
    }
}
