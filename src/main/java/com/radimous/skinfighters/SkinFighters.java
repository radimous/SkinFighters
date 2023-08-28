package com.radimous.skinfighters;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod("skinfighters")
public class SkinFighters {
    public static Logger logger = LogManager.getLogger("skinfighters");

    public enum source {
        CONFIG,
        URL,
        CONFIG_AND_URL,
    }

    public static List<? extends String> getNames() {
        List<? extends String> names;
        switch (Config.SOURCE.get()) {
            case CONFIG -> names = Config.NAMES.get();
            case URL -> names = RemoteNames.getRemoteNames();
            case CONFIG_AND_URL -> {
                List<String> tempNameList = new ArrayList<>(Config.NAMES.get());
                tempNameList.addAll(RemoteNames.getRemoteNames());
                names = tempNameList;
            }
            default -> names = Config.NAMES.get(); // shouldn't ever get here
        }
        return names;
    }

    public SkinFighters() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}

