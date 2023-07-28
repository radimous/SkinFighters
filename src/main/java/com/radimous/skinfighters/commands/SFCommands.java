package com.radimous.skinfighters.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.radimous.skinfighters.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SFCommands {
    public SFCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skinfighters")
            .then(Commands.literal("add").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("name", StringArgumentType.word()).executes(this::addSkin).build()))
            .then(Commands.literal("remove").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(this::removeSkin)
                    .suggests((context, builder) ->
                        SharedSuggestionProvider.suggest(Objects.requireNonNull(getSkinArr()), builder))
                    .build()))
            .then(Commands.literal("removeAll").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(this::removeAllSkins).build())
            .then(Commands.literal("chance").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("chance", IntegerArgumentType.integer(0, 100)).executes(this::changeChance).build()))
            .then(Commands.literal("list").executes(this::listSkins).build()));

    }

    private String[] getSkinArr() {
        if (Config.NAMES.get() != null) {
            return Config.NAMES.get().toArray(new String[0]);
        }
        return new String[0];
    }

    @SuppressWarnings("unchecked")
    private int addSkin(CommandContext<CommandSourceStack> command) {
        String name = StringArgumentType.getString(command, "name");
        if (name.length() > 32) {
            command.getSource().sendFailure(new TextComponent("Skin name is too long"));
            return 1;
        }
        List<? extends String> newSkinList = Config.NAMES.get();
        ((List<String>) newSkinList).add(name);
        Config.NAMES.set(newSkinList);
        Config.NAMES.save();
        command.getSource().sendSuccess(new TextComponent("Skin \"" + name + "\" added successfully"), true);
        return 0;
    }

    private int removeSkin(CommandContext<CommandSourceStack> command) {
        String name = StringArgumentType.getString(command, "name");
        List<? extends String> newSkinList = Config.NAMES.get();
        System.out.println(name);
        System.out.println(newSkinList.contains(name));
        if (!newSkinList.remove(name)) {
            command.getSource().sendFailure(new TextComponent("Skin \"" + name + "\" couldn't be removed"));
            return 1;
        }
        Config.NAMES.set(newSkinList);
        Config.NAMES.save();
        command.getSource().sendSuccess(new TextComponent("Skin \"" + name + "\" removed successfully"), true);
        return 0;
    }

    private int removeAllSkins(CommandContext<CommandSourceStack> command) {
        Config.NAMES.set(new ArrayList<>());
        Config.NAMES.save();
        command.getSource().sendSuccess(new TextComponent("All skins were removed"), true);
        return 0;
    }

    private int listSkins(CommandContext<CommandSourceStack> command) {
        Entity pl = command.getSource().getEntity();
        if (pl != null) pl.sendMessage(
            new TextComponent("There is " + Config.SKIN_CHANCE.get() + "% chance to spawn fighter with one of those skins: \n" + Config.NAMES.get().toString()).withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
        return 0;
    }

    private int changeChance(CommandContext<CommandSourceStack> command) {
        Integer chance = IntegerArgumentType.getInteger(command, "chance");
        Config.SKIN_CHANCE.set(chance);
        Config.SKIN_CHANCE.save();
        command.getSource().sendSuccess(new TextComponent("Chance to spawn fighter with skin changed to " + chance + "%"), true);
        return 0;
    }
}
