package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.attribute.FireImmunityMath;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityFireImmunityMixin {
    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    private void moreattribute$applyFireImmunityAttribute(CallbackInfoReturnable<Boolean> callback) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        AttributeInstance attribute = livingEntity.getAttribute(ModAttributes.FIRE_IMMUNITY.get());
        if (attribute != null) {
            callback.setReturnValue(FireImmunityMath.grantsFireImmunity(attribute.getValue()));
        }
    }
}
