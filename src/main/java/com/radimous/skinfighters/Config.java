package com.radimous.skinfighters;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static ForgeConfigSpec.IntValue SKIN_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> NAMES;
    public static ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        NAMES = BUILDER
                .comment("List of player names used for skins")
                .defineList("names", Arrays.asList(
                        "HellFirePvP",
                        "iskall85",
                        "BrryHrry",
                        "HBomb94",
                        "CaptainSparklez",
                        "ChosenArchitect",
                        "PeteZahHutt",
                        "BaboAbe",
                        "tangofrags",
                        "KaraCorvus",
                        "X33N",
                        "rhiga",
                        "falsesymmetry",
                        "Stressmonster101",
                        "RagingSkittles",
                        "Seapeekay",
                        "neentarts",
                        "Douwsky",
                        "5uppps",
                        "CaptainPuffy",
                        "Tubbo_",
                        "KaptainWutax",
                        "jmilthedude"), entry -> true);
        SKIN_CHANCE = BUILDER
                .comment("Chance for fighter to have skin")
                .defineInRange("skinChance", 20, 0, 100);
        SPEC = BUILDER.build();
    }
}
