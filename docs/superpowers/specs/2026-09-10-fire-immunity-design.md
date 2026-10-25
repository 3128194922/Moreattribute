# 火焰免疫属性设计

## 目标

为 Moreattribute 添加 `moreattribute:fire_immunity` 属性，数值范围为 `0.0` 到 `1.0`：

- `0.0`：不额外免疫火焰。
- `0.5`：火焰伤害降低 50%。
- `1.0`：完全免疫火焰，并让 `Entity.fireImmune()` 返回 `true`。

## 兼容性方案

采用方案 B：所有 `LivingEntity` 都挂载该属性；默认 `EntityType.fireImmune() == true` 的实体初始值为 1.0，其他实体初始值为 0.0。在 `Entity.fireImmune()` 返回值之后追加判断，只把属性达到 1.0 的实体提升为完全免疫；原实体实现返回 `true` 时保持不变。低于 1.0 的部分不修改 `fireImmune()` 的布尔语义，而是在 `LivingHurtEvent` 中仅对带有 `minecraft:is_fire` 标签的伤害进行衰减。

这样可以避免其他模组把 `fireImmune()` 当成严格的“完全免疫”开关时，将 50% 误判为完全免疫；对于原本 `fireImmune() == true` 的生物，将属性调低后也能解除原生火焰免疫。带有 `bypasses_invulnerability` 的伤害不被属性拦截，以保留绕过无敌判定的原语义。

## 边界

本次只处理火焰伤害数值，不修改着火状态持续时间；自定义火焰伤害类型应通过原版 `minecraft:is_fire` damage type tag 接入。
