package com.uniye.moreattribute.mixin.compat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 安卡顽敌（anka_mobs）的三个 boss 在自身 hurt() 开头硬编码拦截
 * is_fire / is_fall / is_freezing / is_explosion 四类环境伤害，
 * 火焰伤害根本不会进入原版伤害管线，fire_immunity 属性因此失效。
 *
 * 这里在 fire_immunity 被降低到 1.0 以下时让 hurt() 内的
 * DamageSource#is(is_fire) 判定返回 false，火焰伤害即可继续走
 * boss 自身的出生免伤/伤害上限逻辑与原版伤害管线（由
 * FireImmunityAttributeHandler 按 (1 - fire_immunity) 缩放）。
 *
 * 目标 mod 缺席时由 CompatMixinPlugin 禁用本 Mixin。
 * 生产运行时为 SRG 名（hurt=m_6469_，DamageSource.is=m_269533_，已从目标 jar 字节码确认），
 * 因此注解直接使用 SRG 名并 remap=false，按字面量匹配。
 */
@Mixin(targets = {
        "com.ankamobs.mod.entity.boss.AncientWraithEntity",
        "com.ankamobs.mod.entity.boss.DemagicSkullEntity",
        "com.ankamobs.mod.entity.boss.PurgatoriteFlowerEntity"
})
public abstract class AnkaBossFireCompatMixin {
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
