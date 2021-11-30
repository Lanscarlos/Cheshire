# Cheshire

Cheshire 是 [Zaphkiel](https://github.com/Bkm016/Zaphkiel) 远征物品服务插件的**附属**<br>
旨在为 Zaphkiel 提供一系列可自定义的技能、粒子效果以及一些实用的功能

Cheshire 是免费的，但目前测试阶段暂不提供 jar 文件，你可以通过以下步骤自行构建插件。

**Windows 平台**
```shell
gradlew.bat clean build
```

**macOS/Linux 平台**
```shell
./gradlew clean build
```
<br>

***

<br>

# 计划

- [x] [自定义数据可视化](#data-display)
- [x] [事件强化](#event)
- [x] [自定义粒子效果](#effect) （基础部分）
- [ ] 自定义技能
- [ ] 灵魂绑定系统

<br>

***

<br>

# <strong id="data-display">自定义数据可视化</strong>
Cheshire 新增了数据显示功能，可将物品当中的 Data 定义的数据可视化
<details>
<summary>
详情
</summary>
<br>

```yaml
#./plugins/Zaphkiel/item/example.yml

example-Item:
  # 选择显示模板
  display: DISPLAY_ITEM_EXAMPLE
  icon: WOODEN_SWORD
  lore:
    TYPE: '&9Sword'
    LORE:
      - '&fCan be bought everywhere.'
  data:
    durability: 10
    example:
      mana: 4
      max-mana: 6
  meta:
    # 定义需要显示的数据
    data-display:
      MANA:
        # 定义数值
        key: 'example.mana'
        display: '&8[ &f%value% &8]'
```
```yaml
#./plugins/Zaphkiel/display/example.yml

# 定义显示模板
DISPLAY_ITEM_EXAMPLE:
  name: '&7<NAME>'
  lore:
    - '&9<TYPE>'
    - ''
    - '&7耐久 &7<DURABILITY>'
    - '&7灵力 &7<MANA>'
    - ''
    - '&7<Lore...>'
```
<br>
效果演示
<br>
<img src="https://z3.ax1x.com/2021/11/28/ouFNcQ.png" height="240">
<br><br><br>

除了直接显示数值，也支持像耐久度那样显示，可以自定义图标样式以及百分比<br>
具体请参考耐久度的格式
```yaml
  meta:
    # 定义需要显示的数据
    data-display:
      MANA:
        # 定义当前值以及最大值
        key:
          - 'example.mana'
          - 'example.max-mana'
        display: '&8[ &f%symbol% &8]'
        display-symbol:
          0: '✦'
          1: '✧'
```
<img src="https://z3.ax1x.com/2021/11/28/ouFt1g.png" height="240">
<img src="https://z3.ax1x.com/2021/11/28/ouFY9S.png" height="240">
<img src="https://z3.ax1x.com/2021/11/28/ouAAzD.png" height="240">
<br><br>
</details>

***

<br>

# <strong id="event">事件</strong>

Cheshire 提供了一系列**新增事件**以及**强化事件**供用户使用
<br>
强化事件是在原事件基础上内置了一些常用的实例对象
<br>
通过这些实例对象，可以让 Kether 发挥更强大的功能
<br>
只需要在普通事件后加上 * 即可对其强化，如

```yaml
example-Item:
  display: DISPLAY_ITEM
  icon: WOODEN_SWORD
  event:
    onAttack*: |-
      tell *attacking...
```
目前可用事件有：

| 脚本 | 描述 |
|:--|:--|
| **[onActive*](#onActive)** | 当玩家穿戴物品于装备栏（包括主副手、盔甲）时触发此事件 |
| onAttack* | 玩家攻击事件 |
| onAttacked* | 玩家被攻击事件 |
| onBlockBreak* | 玩家破坏方块事件 |
| onBlockPlace* | 玩家放置方块事件 |
| onDamaged* | 玩家受伤事件 |
| **[onInactive**](#onInactive)* | 当玩家从装备栏（包括主副手、盔甲）取下物品时触发此事件 |
| onTick* | 周期性事件（ 5秒触发一次 ） |

<br>

### <strong id="onActive">物品激活事件 ActiveEvent</strong>
<details>
<summary>
详情
</summary>
<br>

当玩家穿戴物品于装备栏（包括主副手、盔甲）时触发此事件
<br>

```yaml
example-Item:
  display: DISPLAY_ITEM
  icon: WOODEN_SWORD
  event:
    onActive*: |-
        tell *物品被穿戴
```
<br>

</details>

***

### <strong id="onActive">物品失活事件 InactiveEvent</strong>
<details>
<summary>
详情
</summary>
<br>
当玩家从装备栏（包括主副手、盔甲）取下物品时触发此事件
<br>

```yaml
example-Item:
  display: DISPLAY_ITEM
  icon: WOODEN_SWORD
  event:
    onInactive*: |-
        tell *物品被取下
```
<br>

</details>

***

<br>

# <strong id="effect">粒子效果</strong>

借助于 TabooLib 的粒子模块库<br>
Cheshire 提供了一些强大的粒子效果<br>
目前可用粒子效果有
| 粒子效果 | 描述 |
|:--|:--|
| Arrow-Trail | 箭矢尾迹粒子效果 |
| Circle | 水平圆形粒子效果 |
| Circle-Inclined | 倾斜圆形粒子效果 |
| Circle-Rotatable | 倾斜圆形粒子效果 |
| Point | 定点粒子效果 |
| Sword-Arc | 弧形剑气粒子效果 |

<br>

使用方式
```yaml
#./plugins/Zaphkiel/item/example.yml

example-Item:
  display: DISPLAY_ITEM
  icon: BOW
  lore:
    TYPE: '&9BOW'
    LORE:
      - '&fCan be bought everywhere.'
  meta:
    # 定义需要显示的数据
    effect:
      flame:
        # 选择粒子效果
        type: 'Circle-Rotatable'
        # 定义粒子
        particle:
          type: 'SOUL_FIRE_FLAME'
        # 每1Tick播放一次效果
        period: 1
        # 水平半径
        radius: 1.0
        # 高度范围
        height: 0.6
        # Y轴偏移
        offset-y: 1.0
        # 圆弧步长
        step: 13
        # 旋转角步长
        rotate-step: 4.0
      trail:
        # 选择Arrow-Trail粒子效果
        type: 'Arrow-Trail'
        particle:
          type: 'FLAME'
        period: 1
        # 持续10秒
        duration: 200
  event:
    # 物品穿戴事件
    # 装备物品时在穿戴者(&player) 身上播放 flame 效果
    onActive*: |-
        effect play *example on &player with always
    # 玩家射击事件
    # 射击时在射出的箭矢(&projectile) 上播放 trail 效果
    onShoot*: |-
        effect play *trail on &projectile
```
<br>
效果演示
<br>
<img src="https://z3.ax1x.com/2021/11/28/ouZneJ.gif" height="240">