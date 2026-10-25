package com.uniye.moreattribute.mixin.compat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Yazz's Dungeons: The Sequel（ydtwo）的监守者 boss 在自身 hurt() 中
 * 硬编码拦截 is_fire / is_fall 环境伤害，fire_immunity 属性因此失效。
 *
 * 处理方式与 {@link AnkaBossFireCompatMixin} 相同：fire_immunity 被降低到
 * 1.0 以下时让 hurt() 内的 is_fire 判定返回 false，火焰伤害继续走
 * boss 自身的出场免伤/伤害预算逻辑与原版伤害管线。
 */
@Mixin(targets = "com.yd2.entity.boss.TwistedWardenEntity")
public abstract class YazzWardenFireCompatMixin {
    @Redirect(
            method = "m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;m_269533_(Lnet/minecraft/tags/TagKey;)Z",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean moreattribute$letFireThroughWhenImmunityReduced(DamageSource source, TagKey<DamageType> tag) {
        if (tag == DamageTypeTags.IS_FIRE && CompatFireLogic.shouldLetFireDamageThrough(this)) {
            return false;
        }
        return source.is(tag);
    }
}
