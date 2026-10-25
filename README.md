# Moreattribute

`Moreattribute` 是一个基于 Minecraft Forge 1.20.1 的属性扩展模组。本mod由AI编写。

它主要做两件事：

- 给玩家和生物注册额外属性
- 通过事件和 Mixin 让这些属性直接影响原版行为

当前项目更偏向“给整合包和开发者提供属性接口”，而不是新增一批独立物品或方块。

# 参考MOD：
吃/喝 速度属性和饱食可吃相关Mixin
https://github.com/Minecraft-LightLand/FruitsDelight

碰撞体积和碰撞箱Mixin
https://github.com/JrDemiurg/Demis-Enigmatic-Dice

潜行速度
https://github.com/Glyceryl6/NoSlowDown.git
## 环境

- Minecraft: `1.20.1`
- Forge: `47.4.6`
- Java: `17`
- Mod ID: `moreattribute`

## 提供的属性

本模组当前注册了 10 个属性：

- `moreattribute:eat_speed`
- `moreattribute:drink_speed`
- `moreattribute:can_always_eat`
- `moreattribute:size_scale`
- `moreattribute:no_collision`
- `moreattribute:cooldown_reduction`
- `moreattribute:sneak_speed`
- `moreattribute:charge_speed`
- `moreattribute:fire_immunity`
- `moreattribute:max_experience_level`

其中：

- `eat_speed`、`drink_speed`、`can_always_eat`、`cooldown_reduction`、`sneak_speed`、`charge_speed` 只加给玩家
- `max_experience_level` 只加给玩家
- `size_scale`、`no_collision` 会加给所有 `LivingEntity`
- `fire_immunity` 会加给所有 `LivingEntity`

## 属性说明

### 1. `moreattribute:eat_speed`

控制玩家吃东西所需时间的倍率。

- 默认值：`1.0`
- 范围：`0.1` 到 `10.0`
- 生效对象：玩家

该属性现在按“耗时倍率”工作：

- `1.0`：原版所需时间
- `0.5`：使用时长变为原来的 `1/2`
- `2.0`：使用时长变为原来的 `2` 倍
- `0.25`：使用时长变为原来的 `1/4`

实现公式：

- `实际使用时长 = 原版使用时长 * eat_speed`

只有 `UseAnim.EAT` 的物品会受影响。

### 2. `moreattribute:drink_speed`

控制玩家喝东西所需时间的倍率。

- 默认值：`1.0`
- 范围：`0.1` 到 `10.0`
- 生效对象：玩家

缩短规则和 `eat_speed` 相同，也是按耗时倍率计算使用时长。

会影响 `UseAnim.DRINK` 的物品，例如常见饮用类物品。

### 3. `moreattribute:can_always_eat`

允许玩家在满饱食度时仍然开始食用可食用物品。

- 默认值：`0`
- 范围：`0.0` 到 `1.0`
- 生效对象：玩家

判定方式：

- 属性值大于 `0` 时视为启用
- 仅对 `isEdible()` 的食物生效

这意味着它主要影响“食物”，不等于“所有可右键入口中的可饮用物品都能无条件使用”。

### 4. `moreattribute:size_scale`

控制实体体型缩放。

- 默认值：`1`
- 范围：`0.1` 到 `20.0`
- 生效对象：所有生物实体

这个属性会同时影响：

- 实体碰撞箱尺寸
- 玩家站立视线高度
- 客户端渲染缩放
- 阴影半径

适合做：

- 巨化
- 缩小
- 特殊怪物体型变化

### 5. `moreattribute:no_collision`

控制实体是否可被推动。

- 默认值：`0`
- 范围：`0.0` 到 `1.0`
- 生效对象：所有生物实体

判定方式：

- 属性值大于等于 `1` 时，实体 `isPushable()` 返回 `false`

注意：

- 这更接近"无实体推挤"
- 不是完整的穿墙
- 不等于关闭方块碰撞

### 6. `moreattribute:cooldown_reduction`

控制玩家物品冷却缩减百分比。

- 默认值：`0`
- 范围：`0.0` 到 `1.0`
- 生效对象：玩家

效果：

- `0.0`：无缩减，原版冷却时长
- `0.5`：冷却时长缩减 50%（减半）
- `1.0`：冷却时长缩减 100%（无冷却）

实现方式：

- 拦截 `ItemCooldowns.addCooldown()`，按百分比缩减 `duration`
- 影响所有原版物品冷却，例如末影珍珠、紫颂果、盾牌等 `cooldown` 机制

### 7. `moreattribute:sneak_speed`

控制玩家潜行时的移动速度倍率（移植自 NoSlowDown）。

- 默认值：`0.3`（原版潜行速度倍率）
- 范围：`0.0` 到 `1.0`
- 生效对象：玩家

效果：

- `0.3`：原版潜行减速（保留 30% 移速）
- `1.0`：潜行不减速
- `0.0`：潜行时完全停下

实现说明：

- 属性值 = 潜行时的移速倍率（`1.0` = 与普通移速一致，`0.5` = 普通移速的一半）
- 属性值直接替换原版减速倍率，不受“迅捷潜行”附魔影响

### 8. `moreattribute:charge_speed`

控制玩家使用物品时的移动速度倍率（移植自 NoSlowDown）。

- 默认值：`0.2`（原版使用物品速度倍率）
- 范围：`0.0` 到 `1.0`
- 生效对象：玩家

效果：

- `0.2`：原版使用物品减速（保留 20% 移速）
- `1.0`：使用物品不减速
- `0.0`：使用物品时完全停下

实现说明：

- 覆盖所有“使用物品”状态：弓/弩/三叉戟蓄力、吃、喝、盾牌格挡、望远镜等
- 只在本地玩家客户端生效

### 9. `moreattribute:fire_immunity`

控制实体受到火焰伤害时的免疫比例。

- 默认值：可着火实体为 `0.0`，原生 `fireImmune() == true` 的实体为 `1.0`
- 范围：`0.0` 到 `1.0`
- 生效对象：所有 `LivingEntity`

效果：

- `0.0`：不额外减少火焰伤害
- `0.5`：火焰伤害降低 50%
- `1.0`：完全免疫火焰，且 `fireImmune()` 返回 `true`

计算公式：

```text
实际火焰伤害 = 原始火焰伤害 × (1 - fire_immunity)
```

所有 `LivingEntity` 都会注册该属性：默认能够着火的实体初始值为 `0.0`，原生已经完全免疫火焰的实体初始值为 `1.0`。原生火焰免疫实体默认保持原版行为；当属性被改为低于 `1.0` 时，原生免疫会被属性覆盖，实体可以重新受到火焰伤害。只有属性达到 `1.0` 时，`fireImmune()` 才返回 `true`；部分免疫不会改变这个布尔结果，避免其他模组将部分免疫误判为完全免疫。本属性只衰减带有原版 `minecraft:is_fire` 标签的伤害，不修改着火状态持续时间。

默认情况下，`fire_immunity` 还会覆盖原版抗火药水：属性为 `0.0` 时抗火药水失效，属性为 `0.5` 时仍受到 50% 火焰伤害。可以在配置中关闭此覆盖，让抗火药水恢复原版的完全免疫效果。

其他模组或数据包自定义的火焰伤害类型，需要加入 `minecraft:is_fire` damage type 标签后才会自动适用本属性。带有 `bypasses_invulnerability` 标签的伤害保留绕过无敌判定的原行为。

Boss 兼容（可选模组存在时自动启用）：

部分 mod 的 boss 并不依赖 `fireImmune()`，而是在自己的 `hurt()` 里硬编码拦截 `minecraft:is_fire` 火焰伤害，导致本属性对其无效。本模组通过条件 Mixin 兼容以下 boss（对应 mod 未安装时 Mixin 自动禁用，无任何影响）：

- 安卡顽敌 `anka_mobs`：远古幽魂 `anka_mobs:ancient_wraith`、驱魔骷髅 `anka_mobs:demagic_skull`、炼狱花 `anka_mobs:purgatorite_flower`
- Yazz's Dungeons: The Sequel `ydtwo`：监守者 `ydtwo:twisted_warden`

这些 boss 的 `fire_immunity` 默认值按"原生火焰免疫"处理为 `1.0`（属性未降低时行为与原 mod 完全一致）；当属性被降低到 `1.0` 以下时，其 `hurt()` 内的火焰伤害拦截会被跳过，火焰伤害继续走 boss 自身的出生免伤/伤害上限等规则与原版伤害管线，并按上方公式缩放。

### 10. `moreattribute:max_experience_level`

控制玩家可达到的最高经验等级。

- 默认值：`0`
- 范围：`0` 到 `2147483647`（整型最大值）
- 生效对象：玩家

属性值为 `0` 时不限制经验等级，保持原版经验行为。属性值大于 `0` 时向下取整作为等级上限；例如 `100` 表示玩家不能升到 101 级，`0.5` 会限制在 0 级。经验点或直接等级增加都不能越过启用的上限，跨越上限的经验溢出会被舍弃。

玩家达到上限后不会拾取经验球，经验球会留在世界中。使玩家刚好达到上限的经验球仍可正常吸收。

原版 `/experience`（别名 `/xp`）的 `add` 和 `set` 子命令也遵循该规则：启用上限时，`add` 不会使等级越界，`set levels` 会被限制到上限，玩家已达上限时 `set points` 会清空经验条进度。属性值为 `0` 时，这些命令和经验球判定都保持原版无限制行为。

## 最常见用法

这个模组最直接的用法，是通过原版 `/attribute` 指令给玩家或生物设置属性值。

### 设置吃东西速度

```mcfunction
attribute @p moreattribute:eat_speed base set 0.5
```

效果：吃东西所需时间减半。

### 设置喝东西速度

```mcfunction
attribute @p moreattribute:drink_speed base set 0.5
```

效果：喝东西所需时间减半。

### 允许满饱食度继续吃

```mcfunction
attribute @p moreattribute:can_always_eat base set 1
```

### 缩小玩家体型

```mcfunction
attribute @p moreattribute:size_scale base set 0.5
```

### 放大生物体型

```mcfunction
attribute @e[type=minecraft:zombie,limit=1,sort=nearest] moreattribute:size_scale base set 2
```

### 关闭实体推挤

```mcfunction
attribute @e[type=minecraft:villager,limit=1,sort=nearest] moreattribute:no_collision base set 1
```

### 缩减物品冷却

```mcfunction
attribute @p moreattribute:cooldown_reduction base set 0.5
```

效果：所有物品冷却时长减半。

```mcfunction
attribute @p moreattribute:cooldown_reduction base set 1
```

效果：所有物品无冷却。

### 潜行/使用物品不减速

```mcfunction
attribute @p moreattribute:sneak_speed base set 1
attribute @p moreattribute:charge_speed base set 1
```

效果：潜行和使用物品（蓄力/吃/喝等）时都保持正常移速。

### 设置火焰免疫

```mcfunction
attribute @e[type=minecraft:zombie,limit=1,sort=nearest] moreattribute:fire_immunity base set 0.5
```

效果：目标受到的火焰伤害降低 50%。

设置完全免疫：

```mcfunction
attribute @e[type=minecraft:zombie,limit=1,sort=nearest] moreattribute:fire_immunity base set 1
```

效果：目标完全免疫火焰，并且其他模组调用目标的 `fireImmune()` 时会得到 `true`。对于原本自带火焰免疫的实体，将属性设置为 `0` 后即可取消其火焰免疫。

### 设置玩家经验等级上限

```mcfunction
attribute @p moreattribute:max_experience_level base set 100
```

效果：玩家最高升至 100 级；达到 100 级后，经验球不会被拾取。

恢复原版无限制经验：

```mcfunction
attribute @p moreattribute:max_experience_level base set 0
```

命令行为检查（将示例上限设为 `5`）：

```mcfunction
attribute @p moreattribute:max_experience_level base set 5
experience add @p 100 levels
experience set @p 100 levels
experience set @p 50 points
attribute @p moreattribute:max_experience_level base set 0
experience set @p 100 levels
```

启用上限时，前两条经验命令都不会让等级超过 5；玩家已到 5 级时，设置经验点会将进度归零。将属性改为 `0` 后，最后一条命令可按原版行为设置等级，不受上限限制。

## 推荐测试流程

进入游戏后，可以按下面顺序快速验证：

1. 给玩家设置 `eat_speed` 或 `drink_speed`
2. 手持食物或饮品测试使用时间是否变化
3. 给玩家设置 `can_always_eat`
4. 在满饱食度下尝试继续吃食物
5. 给玩家或怪物设置 `size_scale`
6. 观察模型大小、碰撞箱和视角高度是否一起变化
7. 给目标实体设置 `no_collision`
8. 用实体碰撞测试是否还能被推动
9. 给玩家设置 `cooldown_reduction`
10. 使用末影珍珠测试冷却时长是否按比例缩减
11. 给玩家设置 `sneak_speed` 为 1，潜行测试是否不再减速
12. 给玩家设置 `charge_speed` 为 1，吃食物/蓄弓测试是否不再减速
13. 给玩家或怪物设置 `fire_immunity` 为 0.5，观察火焰伤害是否降低一半
14. 将 `fire_immunity` 设置为 1，确认实体完全免疫火焰
15. 确认 `fire_immunity` 为 0.5 时，其他模组的 `fireImmune()` 判定仍不会被误判为完全免疫
16. 将 `max_experience_level` 设为 `2`，确认玩家能升到 2 级但不能超过上限
17. 达到上限后触碰经验球，确认经验球仍留在世界中
18. 将 `max_experience_level` 设为 `0`，确认经验获取恢复原版行为
19. 使用 `/experience add`（或 `/xp add`）增加等级，确认不会越过启用的上限
20. 使用 `/experience set <玩家> <等级> levels`，确认设置值会受启用的上限约束
21. 玩家达到上限后使用 `/experience set <玩家> <经验点> points`，确认经验条进度归零
22. 将属性设为 `0` 后重复 `/xp set`，确认命令恢复原版无限制行为

## 适合的接入方式

这个项目很适合配合以下内容使用：

- 指令系统
- 数据包
- KubeJS
- 其他可以修改实体属性的模组
- 装备词条或饰品系统

常见思路：

- 戒指提升 `eat_speed`
- 种族系统修改 `size_scale`
- 特殊状态让单位临时获得 `no_collision`
- 某类职业永久启用 `can_always_eat`
- 饰品或附魔提供 `cooldown_reduction` 减少技能等待
- Boss 或装备词条系统提供 `fire_immunity`

## 实现概要

项目当前主要由以下部分组成：

- `ModAttributes`：注册 10 个属性，并把属性挂到玩家或全部生物上
- `FoodSpeedAttributeHandler`：处理进食、饮用速度和"满饱食度也能吃"的逻辑
- `ItemCooldownsMixin`：拦截原版物品冷却，实现冷却缩减
- `LivingEntityMixin`：处理生物体型变化与不可推动
- `PlayerMixin`：额外处理玩家尺寸、视线高度和冷却缩减 Player 关联
- `LivingEntityRendererMixin`：处理客户端模型缩放和阴影半径同步
- `LocalPlayerMixin`：客户端替换潜行/使用物品的减速系数（移植自 NoSlowDown）
- `EntityFireImmunityMixin` 与 `FireImmunityAttributeHandler`：保留原始 `fireImmune()` 语义，并按属性衰减火焰伤害
- `LivingEntityFireResistanceMixin`：按配置决定 `fire_immunity` 是否覆盖抗火药水
- `FireImmunityMath`：统一处理免疫比例范围、完全免疫判定和伤害计算
- `ExperienceLevelAttributeHandler`：按玩家的 `max_experience_level` 属性限制经验增长和经验球拾取

## 配置说明

### `fireImmunityOverridesFireResistance`

控制 `fire_immunity` 是否覆盖原版抗火药水。

- 默认值：`true`
- `true`：属性优先，属性不为 `1.0` 时抗火药水不会阻止火焰伤害
- `false`：抗火药水优先，具有抗火效果时仍完全免疫火焰

项目内存在一个 Forge `COMMON` 配置类，但当前这些配置项基本还是模板内容：

- `logDirtBlock`
- `magicNumber`
- `magicNumberIntroduction`
- `items`

它们目前不参与核心属性功能判定。

因此这个模组当前的实际玩法重点是“额外属性本身”，不是配置文件。

## 开发与运行

### 运行客户端

```powershell
.\gradlew runClient
```

### 运行服务端

```powershell
.\gradlew runServer
```

### 构建 Jar

```powershell
.\gradlew build
```

构建产物通常位于：

```text
build/libs/
```

## 注意事项

- `eat_speed` 和 `drink_speed` 只会对玩家生效
- `eat_speed` 和 `drink_speed` 现在使用耗时倍率语义，`1.0` 为原时长
- `can_always_eat` 只处理可食用物品，不是通用右键白名单
- `cooldown_reduction` 影响所有原版基于 `ItemCooldowns` 的物品冷却，包括其他模组通过原版冷却系统的物品
- `size_scale` 影响的不只是渲染，还会改碰撞箱和玩家视角高度
- `no_collision` 当前只处理“是否可被推动”，不是完整无碰撞系统
- `sneak_speed` / `charge_speed` 只影响本地玩家的移动输入（客户端行为），属性值本身会从服务端同步
- `sneak_speed` / `charge_speed` 的数值即移速倍率（`1.0` = 正常移速，`0.5` = 一半移速），默认 `0.3` / `0.2` 时与原版一致
- `sneak_speed` 不受“迅捷潜行”附魔影响；`charge_speed` 覆盖所有使用物品状态
- `fire_immunity` 的 `0.5` 只代表火焰伤害降低 50%，不会让 `fireImmune()` 返回 `true`
- `fire_immunity` 会注册给所有生物实体；默认可着火实体为 `0.0`，原生火焰免疫实体为 `1.0`
- 原生火焰免疫实体的免疫状态由 `fire_immunity` 接管；将其设置为低于 `1.0` 后会允许受到火焰伤害
- `fireImmunityOverridesFireResistance` 默认为 `true`；设为 `false` 时，抗火药水优先并保持原版完全免疫
- `fire_immunity` 只识别带 `minecraft:is_fire` 标签的伤害类型，不会按其他模组 ID 硬编码判断
- `fire_immunity` 不改变实体着火时间；若需要同时缩短着火时间，需要额外的状态或事件逻辑
- `max_experience_level` 范围为 `0` 至 `2147483647`；默认值 `0` 表示不限制经验等级，正数才启用上限
- `max_experience_level` 达到上限后会阻止经验球拾取，经验球保留在世界中
- 若其他模组也修改实体尺寸、渲染缩放或 `isPushable()`，需要做兼容测试

## License

项目配置里声明的许可证为 `MIT`。如果后续补充正式 `LICENSE` 文件，请以仓库实际文件为准。
