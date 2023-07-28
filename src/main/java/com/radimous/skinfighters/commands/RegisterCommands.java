package com.radimous.skinfighters.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber
public class RegisterCommands {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent e){
        new SFCommands(e.getDispatcher());
        ConfigCommand.register(e.getDispatcher());
    }
}
