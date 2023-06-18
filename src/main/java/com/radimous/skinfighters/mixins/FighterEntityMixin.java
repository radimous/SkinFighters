package com.radimous.skinfighters.mixins;

import com.radimous.skinfighters.Config;
import iskallia.vault.entity.entity.FighterEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(value = FighterEntity.class)
public abstract class FighterEntityMixin extends EntityMixin {
    private final Random random = new Random();

    @Inject(method = "finalizeSpawn", at = @At(value = "RETURN", target = "Liskallia/vault/entity/entity/FighterEntity;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"))
    public void customName(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        List<? extends String> names = Config.NAMES.get();
        if (!names.isEmpty() && random.nextInt(100) < Config.SKIN_CHANCE.get()) {
            String name = names.get(random.nextInt(0, names.size()));
            this.setCustomName(new TextComponent(name));
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Liskallia/vault/util/SkinProfile;updateSkin(Ljava/lang/String;)V"))
    public void makeNametagsVisible(CallbackInfo ci) {
        this.setCustomNameVisible(true);
    }
}