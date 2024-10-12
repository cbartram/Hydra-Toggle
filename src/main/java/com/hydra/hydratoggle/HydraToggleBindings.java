package com.hydra.hydratoggle;

import com.google.inject.AbstractModule;
import com.hydra.hydratoggle.controller.MainController;
import com.hydra.hydratoggle.model.HydraConfig;
import com.hydra.hydratoggle.model.HydraConfigProvider;

public class HydraToggleBindings extends AbstractModule {
    @Override
    protected void configure() {
        bind(HydraConfig.class).toProvider(HydraConfigProvider.class).asEagerSingleton();
        bind(ClientToggle.class);
        bind(HydraToggleApplication.class);
        bind(MainController.class);
    }
}