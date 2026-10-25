package com.uniye.moreattribute.mixin.compat;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * 按目标 mod 是否安装来启用对应的兼容 Mixin，
 * 避免 mod 缺席时 Mixin 因目标类不存在而报错。
 */
public final class CompatMixinPlugin implements IMixinConfigPlugin {
    private static final String ANKA_MODID = "anka_mobs";
    private static final String YAZZ_MODID = "ydtwo";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ModList modList = ModList.get();
        if (modList == null) {
            return false;
        }
        if (mixinClassName.endsWith(".AnkaBossFireCompatMixin")) {
            return modList.isLoaded(ANKA_MODID);
        }
        if (mixinClassName.endsWith(".YazzWardenFireCompatMixin")) {
            return modList.isLoaded(YAZZ_MODID);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
