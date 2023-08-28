package com.radimous.skinfighters.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.radimous.skinfighters.Config;
import com.radimous.skinfighters.RemoteNames;
import com.radimous.skinfighters.SkinFighters;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

            .then(Commands.literal("info").executes(this::printInfo).build())

            .then(Commands.literal("source").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("source", StringArgumentType.word())
                    .suggests(((context, builder) ->
                        SharedSuggestionProvider.suggest(() -> Arrays.stream(SkinFighters.source.values()).map(Enum::name).iterator(), builder)
                    )).executes(this::setSource)))

            .then(Commands.literal("skinsFromURL").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("reload").executes(this::reloadRemoteNames).build())
                .then(Commands.literal("getURL").executes(this::getURL).build())
                .then(Commands.literal("removeAll").executes(this::removeURLSkins).build())
                .then(Commands.literal("setUrl")
                    .then(Commands.argument("url", StringArgumentType.string()).executes(this::setURL).build()))));

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

    private int removeURLSkins(CommandContext<CommandSourceStack> command) {
        RemoteNames.removeRemoteNames();
        command.getSource().sendSuccess(new TextComponent("All skins from URL were removed"), true);
        return 0;
    }

    private int printInfo(CommandContext<CommandSourceStack> command) {
        List<? extends String> names = SkinFighters.getNames();
        command.getSource().sendSuccess(new TextComponent("Getting skins from " + Config.SOURCE.get().name() + " \n" +
            "There is " + Config.SKIN_CHANCE.get() + "% chance to spawn fighter with one of those skins: \n" +
            (names.size() >= 10000 ? "[skin list is too long to display (" + names.size() + " skins)]" : names)).withStyle(ChatFormatting.GRAY), false);
        return 0;
    }

    private int changeChance(CommandContext<CommandSourceStack> command) {
        Integer chance = IntegerArgumentType.getInteger(command, "chance");
        Config.SKIN_CHANCE.set(chance);
        Config.SKIN_CHANCE.save();
        command.getSource().sendSuccess(new TextComponent("Chance to spawn fighter with skin changed to " + chance + "%"), true);
        return 0;
    }

    private int setSource(CommandContext<CommandSourceStack> command) {
        String strSource = StringArgumentType.getString(command, "source");
        SkinFighters.source source = SkinFighters.source.valueOf(strSource);
        Config.SOURCE.set(source);
        Config.SOURCE.save();
        command.getSource().sendSuccess(new TextComponent("Skin name source changed to " + strSource), true);
        return 0;
    }

    private int reloadRemoteNames(CommandContext<CommandSourceStack> command) {
        boolean invalidUsername;
        try {
            invalidUsername = RemoteNames.reloadRemoteNames();
        } catch (IOException e) {
            command.getSource().sendFailure(new TextComponent("Fetching names from url failed, more info in log"));
            SkinFighters.logger.error("Fetching names from \"" + Config.NAME_URL.get() + "\" failed. IOException: " + e);
            return 1;
        }
        command.getSource().sendSuccess(new TextComponent("Successfully reloaded skins from url, received " + RemoteNames.getRemoteNames().size() + " skin" + (RemoteNames.getRemoteNames().size() == 1 ? "" : "s")), true);
        if (invalidUsername) {
            command.getSource().sendSuccess(new TextComponent("At least 1 skin name was invalid, make sure your url is correct").withStyle(ChatFormatting.RED), true);

        }
        return 0;
    }

    private int getURL(CommandContext<CommandSourceStack> command) {
        command.getSource().sendSuccess(new TextComponent("Current url: \"" + Config.NAME_URL.get() + "\""), false);
        return 0;
    }

    private int setURL(CommandContext<CommandSourceStack> command) {
        String url = StringArgumentType.getString(command, "url");
        Config.NAME_URL.set(url);
        Config.NAME_URL.save();
        command.getSource().sendSuccess(new TextComponent("Successfully changed the url to \"" + url + "\""), true);
        reloadRemoteNames(command);
        return 0;
    }
}
