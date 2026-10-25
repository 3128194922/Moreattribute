package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.event.ExperienceLevelAttributeHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerExperienceMixin {
    @ModifyVariable(method = "setExperienceLevels", at = @At("HEAD"), argsOnly = true)
    private int moreattribute$clampSetExperienceLevel(int requestedLevel) {
        return ExperienceLevelAttributeHandler.clampSetExperienceLevel((Player) (Object) this, requestedLevel);
    }

    @ModifyVariable(method = "setExperiencePoints", at = @At("HEAD"), argsOnly = true)
    private int moreattribute$clampSetExperiencePoints(int requestedPoints) {
        return ExperienceLevelAttributeHandler.clampSetExperiencePoints((Player) (Object) this, requestedPoints);
    }
}
