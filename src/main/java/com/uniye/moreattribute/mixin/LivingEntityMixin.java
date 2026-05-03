package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Unique
    private float moreattribute$scale = 1F;

    @Inject(method = "tick", at = @At("TAIL"))
    private void moreattribute$onTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        AttributeInstance inst = livingEntity.getAttribute(ModAttributes.SIZE_SCALE.get());
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

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void moreattribute$isPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getAttributeValue(ModAttributes.NO_COLLISION.get()) >= 1D) {
            cir.setReturnValue(false);
        }
    }
}
