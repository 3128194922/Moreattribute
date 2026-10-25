package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.Config;
import com.uniye.moreattribute.attribute.FireImmunityMath;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFireResistanceMixin {
    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"
            )
    )
    private boolean moreattribute$applyFireImmunityToFireResistance(LivingEntity entity, MobEffect effect) {
        boolean hasEffect = entity.hasEffect(effect);
        if (hasEffect && effect == MobEffects.FIRE_RESISTANCE) {
            AttributeInstance attribute = entity.getAttribute(ModAttributes.FIRE_IMMUNITY.get());
            if (attribute != null && FireImmunityMath.overridesFireResistance(
                    Config.fireImmunityOverridesFireResistance, attribute.getValue())) {
                return false;
            }
        }
        return hasEffect;
    }
}
