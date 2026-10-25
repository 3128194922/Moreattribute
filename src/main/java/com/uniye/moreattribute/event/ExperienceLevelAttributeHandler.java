package com.uniye.moreattribute.event;

import com.uniye.moreattribute.MoreattributeMod;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigInteger;

@Mod.EventBusSubscriber(modid = MoreattributeMod.MODID)
public final class ExperienceLevelAttributeHandler {
    private ExperienceLevelAttributeHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperiencePointChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || event.getAmount() <= 0) {
            return;
        }

        int maxLevel = getMaxExperienceLevel(player);
        if (maxLevel < 0) {
            return;
        }

        BigInteger currentProgressXp = BigInteger.valueOf(Math.round(
                (double) player.experienceProgress * xpForNextLevel(player.experienceLevel)));
        BigInteger remainingXp = xpToReachLevel(maxLevel)
                .subtract(xpToReachLevel(player.experienceLevel))
                .subtract(currentProgressXp)
                .max(BigInteger.ZERO);

        int allowedAmount = remainingXp.min(BigInteger.valueOf(event.getAmount())).intValue();
        if (allowedAmount != event.getAmount()) {
            event.setAmount(allowedAmount);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceLevelChange(PlayerXpEvent.LevelChange event) {
        Player player = event.getEntity();
        int requestedLevels = event.getLevels();
        if (player.level().isClientSide || requestedLevels <= 0) {
            return;
        }

        int maxLevel = getMaxExperienceLevel(player);
        if (maxLevel < 0) {
            return;
        }

        long allowedLevels = Math.max(0L, (long) maxLevel - player.experienceLevel);
        if ((long) requestedLevels > allowedLevels) {
            event.setLevels((int) allowedLevels);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceOrbPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        int maxLevel = getMaxExperienceLevel(player);
        if (maxLevel >= 0 && player.experienceLevel >= maxLevel) {
            event.setCanceled(true);
        }
    }

    private static int getMaxExperienceLevel(Player player) {
        double value = player.getAttributeValue(ModAttributes.MAX_EXPERIENCE_LEVEL.get());
        return value <= 0.0D ? -1 : Mth.floor(value);
    }

    public static int clampSetExperienceLevel(Player player, int requestedLevel) {
        if (player.level().isClientSide) {
            return requestedLevel;
        }

        int maxLevel = getMaxExperienceLevel(player);
        return maxLevel < 0 ? requestedLevel : Math.min(requestedLevel, maxLevel);
    }

    public static int clampSetExperiencePoints(Player player, int requestedPoints) {
        if (player.level().isClientSide) {
            return requestedPoints;
        }

        int maxLevel = getMaxExperienceLevel(player);
        return maxLevel >= 0 && player.experienceLevel >= maxLevel ? 0 : requestedPoints;
    }

    private static long xpForNextLevel(int level) {
        if (level >= 30) {
            return 9L * level - 158L;
        }
        if (level >= 15) {
            return 5L * level - 38L;
        }
        return 2L * level + 7L;
    }

    private static BigInteger xpToReachLevel(int level) {
        BigInteger levelValue = BigInteger.valueOf(level);
        if (level <= 15) {
            return levelValue.multiply(BigInteger.valueOf((long) level + 6L));
        }
        if (level <= 30) {
            BigInteger levelsAbove15 = BigInteger.valueOf((long) level - 15L);
            return BigInteger.valueOf(315L)
                    .add(BigInteger.valueOf(5L).multiply(levelValue.add(BigInteger.valueOf(14L)))
                            .multiply(levelsAbove15).divide(BigInteger.TWO))
                    .subtract(BigInteger.valueOf(38L).multiply(levelsAbove15));
        }
        BigInteger levelsAbove30 = BigInteger.valueOf((long) level - 30L);
        return BigInteger.valueOf(1395L)
                .add(BigInteger.valueOf(9L).multiply(levelValue.add(BigInteger.valueOf(29L)))
                        .multiply(levelsAbove30).divide(BigInteger.TWO))
                .subtract(BigInteger.valueOf(158L).multiply(levelsAbove30));
    }
}
