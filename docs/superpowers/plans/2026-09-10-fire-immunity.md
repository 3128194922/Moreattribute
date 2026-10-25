# 火焰免疫属性实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 Moreattribute 中实现兼容原始 `Entity.fireImmune()` 的火焰免疫属性。
**Architecture:** 原始 `fireImmune()` 保持优先；属性达到 1.0 时通过 Mixin 增加完全免疫；部分免疫通过最低优先级 `LivingHurtEvent` 按 `DamageTypeTags.IS_FIRE` 衰减。
**Tech Stack:** Minecraft 1.20.1、Forge 47.4.6、Java 17、现有 Sponge Mixin 配置；不新增第三方依赖。
**Spec:** `docs/superpowers/specs/2026-09-10-fire-immunity-design.md`

## 任务

- [x] 先添加无外部测试框架的纯 Java 数学测试，并确认测试在实现缺失时失败。
- [x] 添加火焰免疫属性、数值计算辅助类、`Entity.fireImmune()` 兼容 Mixin 和火焰伤害事件处理器。
- [x] 添加中文属性名、README 使用说明和 Mixin 配置登记。
- [x] 运行纯 Java 测试、Gradle 构建和差异检查，确认不覆盖已有 `mods.toml` 修改。
