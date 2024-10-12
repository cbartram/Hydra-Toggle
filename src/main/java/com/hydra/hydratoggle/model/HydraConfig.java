package com.hydra.hydratoggle.model;

import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.FileSystems;

@Data
@Singleton
public class HydraConfig {

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private String sep = FileSystems.getDefault().getSeparator();

    @Setter(value = AccessLevel.NONE)
    private String runeLiteDirectory = System.getProperty("user.home") + sep + "AppData" + sep + "Local" + sep + "RuneLite";

    @Setter(value = AccessLevel.NONE)
    private String runeLiteJarPath = runeLiteDirectory + sep + "RuneLite.jar";

    @Setter(value = AccessLevel.NONE)
    private String runeLiteRealJarPath = runeLiteDirectory + sep + "RuneLite-real.jar";

    @Setter(value = AccessLevel.NONE)
    private String hydraJarPath = runeLiteDirectory + sep + "RuneLite-hydra.jar";

    @Setter(value = AccessLevel.NONE)
    private String hydraConfFilePath = runeLiteDirectory + sep + "hydra_toggle.json";

    @Setter(value = AccessLevel.NONE)
    private String runeLiteExePath = runeLiteDirectory + sep + "RuneLite.exe";

    private ClientType clientType;
    private boolean darkModeEnabled;
}
