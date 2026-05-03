package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private float moreattribute$scale = 1F;

    @Inject(method = "tick", at = @At("TAIL"))
    private void moreattribute$onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        AttributeInstance inst = player.getAttribute(ModAttributes.SIZE_SCALE.get());
        if (inst == null) {
            return;
        }

        float newScale = (float) inst.getValue();
        if (moreattribute$scale != newScale) {
            moreattribute$scale = newScale;
            this.refreshDimensions();
        }
    }

    @Inject(method = "getDimensions", at = @At("TAIL"), cancellable = true)
    private void moreattribute$getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (moreattribute$scale != 1F) {
            cir.setReturnValue(cir.getReturnValue().scale(moreattribute$scale));
        }
    }

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void moreattribute$getStandingEyeHeight(Pose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        if (moreattribute$scale != 1F && moreattribute$scale != 0F) {
            cir.setReturnValue(cir.getReturnValue() * moreattribute$scale);
        }
    }
}
