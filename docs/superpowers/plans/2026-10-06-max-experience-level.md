# 最大经验等级属性实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为每名玩家增加范围为 0 至 `Integer.MAX_VALUE`、默认值为 0 的最大经验等级属性；0 表示无限制，正值阻止原版经验使等级超过该上限。

**Architecture:** 将可同步的 `max_experience_level` 属性注册到玩家，并使用 Forge 的 `PlayerXpEvent` 限制经验点、等级变化和经验球拾取；使用服务端玩家经验设置入口的 Mixin 覆盖原版 `/experience set`（`/xp set`）绕过事件的路径。属性值为 0 时保留原版经验行为；正值作为上限。经验点阈值用任意精度整数计算，以支持最高至 `Integer.MAX_VALUE` 的上限。经验球使玩家刚好达到上限时可以被吸收；玩家已达到上限后，拾取事件取消，经验球保留在世界中。

**Tech Stack:** Minecraft 1.20.1、Forge 47.4.6、Java 17、Forge Event Bus。

**Spec:** `docs/superpowers/specs/2026-10-06-max-experience-level-design.md`

## Global Constraints

- 只修改 `E:\Server_mod\Moreattribute` 主项目。
- 仅使用 Minecraft 1.20.1 / Forge 47.4.6 现有 API，不添加第三方依赖。
- 属性 ID 为 `moreattribute:max_experience_level`，显示名为“最大经验等级”，默认值为 0，范围为 0 至 2147483647；0 表示无上限。
- 保留工作区已有未提交改动；中文语言文件只追加新键。
- 不新增或运行测试；完成后编译并检查改动格式。

## Review Focus

- 玩家属性注册只加给玩家且启用同步；构造注册对象时不提前调用 `.get()`。
- 属性值 0 保持原版无限制经验行为；正数小数值向下取整后作为等级上限（例如 0.5 限制在 0 级），范围包含 0 和 2147483647。
- XP 点增长刚好跨上限时，经验等级停在上限，额外经验不留下可继续升级的进度。
- 直接等级增加不能越过上限；负向经验和等级变化仍正常工作。
- 原版 `/experience` 与 `/xp` 的 `set levels` 不能越过上限；已达上限时 `set points` 不会留下经验条进度；0 属性不限制这些命令。
- 玩家已达到或超过上限时，经验球拾取被取消，经验球保留在世界中；到达上限的那颗经验球仍可正常吸收。

---

### Task 1: 注册最大经验等级玩家属性和中文名称

**Files:**
- Modify: `src/main/java/com/uniye/moreattribute/attribute/ModAttributes.java`
- Modify: `src/main/resources/assets/moreattribute/lang/zh_cn.json`

**Interfaces:**
- Produces: `ModAttributes.MAX_EXPERIENCE_LEVEL`，类型为 `RegistryObject<Attribute>`，默认值 0，最小值 0，最大值 `Integer.MAX_VALUE`，可同步。

- [x] **Step 1: 在现有属性定义中新增属性注册**

使用项目已采用的 `DeferredRegister` 和 `RangedAttribute` 模式：

```java
public static final RegistryObject<Attribute> MAX_EXPERIENCE_LEVEL = ATTRIBUTES.register("max_experience_level",
        () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".max_experience_level",
                0.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));
```

- [x] **Step 2: 将该属性加入玩家属性修改事件**

在 `onEntityAttributeModification` 现有玩家属性注册区添加：

```java
event.add(EntityType.PLAYER, MAX_EXPERIENCE_LEVEL.get());
```

不要将该属性加到通用 LivingEntity 循环。

- [x] **Step 3: 追加中文属性名称**

在现有 `zh_cn.json` 保留其他键和值，追加：

```json
"attribute.moreattribute.max_experience_level": "最大经验等级"
```

---

### Task 2: 限制玩家经验增长和经验球拾取

**Files:**
- Create: `src/main/java/com/uniye/moreattribute/event/ExperienceLevelAttributeHandler.java`
- Depends on: Task 1 的 `ModAttributes.MAX_EXPERIENCE_LEVEL`

**Interfaces:**
- Consumes: Forge 47.4.6 的 `PlayerXpEvent.XpChange`、`PlayerXpEvent.LevelChange` 和 `PlayerXpEvent.PickupXp`。
- Produces: Forge 游戏总线静态订阅处理器；所有上限判断在服务端执行。

- [x] **Step 1: 添加与其他事件处理器一致的订阅类**

使用 `@Mod.EventBusSubscriber(modid = MoreattributeMod.MODID)` 和静态 `@SubscribeEvent` 方法。实现一个私有上限读取方法：若属性值小于或等于 0，返回 -1 作为内部无限制标记，否则向下取整为整数等级。

- [x] **Step 2: 限制正向经验点变化**

在 `PlayerXpEvent.XpChange` 处理器中，先忽略已取消事件、客户端事件、非正数经验，以及内部无限制标记 -1（属性值 0）。用闭式计算方法避免每次经验变化都遍历每一级；目标经验阈值用 `BigInteger` 计算，避免接近整型最大等级时溢出：

```java
private static long xpForNextLevel(int level) {
    if (level >= 30) return 9L * level - 158L;
    if (level >= 15) return 5L * level - 38L;
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
```

计算到上限还需要的经验点数时，求 `xpToReachLevel(maxLevel) - xpToReachLevel(player.experienceLevel) - currentProgressXp`，并把结果限制为不小于 0。将结果与事件经验量取较小值，再安全转换为 `int` 写回。若玩家已达到上限，正向经验量设为 0。不得用 `totalExperience` 单独推算当前进度，因为直接等级变化不会保证该字段等于等级经验阈值。

- [x] **Step 3: 限制正向等级变化**

在 `PlayerXpEvent.LevelChange` 处理器中：忽略已取消事件、客户端事件、负向等级变化和内部无限制标记 -1（属性值 0）；对其余正向变化计算 `max(0, 上限 - 当前等级)`，将等级增量限制为不超过此值。负向等级变化照常保留。

- [x] **Step 4: 阻止达到上限后的经验球拾取**

在 `PlayerXpEvent.PickupXp` 处理器中，先忽略客户端事件和内部无限制标记 -1（属性值 0）；当玩家当前等级大于或等于属性上限时取消事件。不要移除经验球或修改其数量。玩家低于上限时保持事件默认行为，让经验点处理器将其经验限制到上限。

---

### Task 3: 编译和静态检查

**Files:**
- Review: 上述三个实现文件，以及设计文档中列出的验收标准。

- [x] **Step 1: 检查补丁格式**

运行：

```powershell
git diff --check
```

Expected: 无空白错误。

- [x] **Step 2: 编译主项目**

运行：

```powershell
.\gradlew.bat --no-daemon --console=plain compileJava
```

Expected: `BUILD SUCCESSFUL`。

- [x] **Step 3: 复核工作区边界**

检查 `git status --short` 和最终差异，确认本次只新增计划所列代码与语言键，没有覆盖、暂存、清理或提交已有工作区改动。

## Verification Limits

编译和静态检查不证明实际游戏拾取、经验条边界、属性修饰器同步或多人服务器行为；本任务按要求不新增或运行测试，也不声称已完成游戏内验证。
