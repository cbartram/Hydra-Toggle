package com.hydra.hydratoggle.model;

import lombok.Data;

@Data
public class HydraConfig {
    private ClientType clientType;
    private boolean darkModeEnabled;
}
