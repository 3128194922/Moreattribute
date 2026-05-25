# Moreattribute

`Moreattribute` 是一个基于 Minecraft Forge 1.20.1 的属性扩展模组。

它主要做两件事：

- 给玩家和生物注册额外属性
- 通过事件和 Mixin 让这些属性直接影响原版行为

当前项目更偏向“给整合包和开发者提供属性接口”，而不是新增一批独立物品或方块。

# 参考MOD：
吃/喝 速度属性和饱食可吃相关Mixin
https://github.com/Minecraft-LightLand/FruitsDelight?tab=LGPL-2.1-1-ov-file

碰撞体积和碰撞箱Mixin
https://github.com/JrDemiurg/Demis-Enigmatic-Dice

## 环境

- Minecraft: `1.20.1`
- Forge: `47.4.6`
- Java: `17`
- Mod ID: `moreattribute`

## 提供的属性

本模组当前注册了 5 个属性：

- `moreattribute:eat_speed`
- `moreattribute:drink_speed`
- `moreattribute:can_always_eat`
- `moreattribute:size_scale`
- `moreattribute:no_collision`

其中：

- `eat_speed`、`drink_speed`、`can_always_eat` 只加给玩家
- `size_scale`、`no_collision` 会加给所有 `LivingEntity`

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

- 这更接近“无实体推挤”
- 不是完整的穿墙
- 不等于关闭方块碰撞

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

## 实现概要

项目当前主要由以下部分组成：

- `ModAttributes`：注册 5 个属性，并把属性挂到玩家或全部生物上
- `FoodSpeedAttributeHandler`：处理进食、饮用速度和“满饱食度也能吃”的逻辑
- `LivingEntityMixin`：处理生物体型变化与不可推动
- `PlayerMixin`：额外处理玩家尺寸与视线高度
- `LivingEntityRendererMixin`：处理客户端模型缩放和阴影半径同步

## 配置说明

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
- `size_scale` 影响的不只是渲染，还会改碰撞箱和玩家视角高度
- `no_collision` 当前只处理“是否可被推动”，不是完整无碰撞系统
- 若其他模组也修改实体尺寸、渲染缩放或 `isPushable()`，需要做兼容测试

## License

项目配置里声明的许可证为 `MIT`。如果后续补充正式 `LICENSE` 文件，请以仓库实际文件为准。
