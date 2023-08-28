package com.radimous.skinfighters;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber
public class Events {
    @SubscribeEvent
    public static void commonSetupEvent(ServerAboutToStartEvent event) {
        try {
            if (RemoteNames.reloadRemoteNames()) {
                SkinFighters.logger.warn("At least 1 username was invalid, make sure your url is correct");
            }
        } catch (IOException e) {
            SkinFighters.logger.error("Fetching names from \"" + Config.NAME_URL.get() + "\" failed. IOException: " + e);
        }
    }
}
