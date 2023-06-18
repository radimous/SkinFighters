package com.radimous.skinfighters.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract void setCustomNameVisible(boolean visible);
    @Shadow
    public abstract void setCustomName(Component name);
}
