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

# 自定义数据可视化
Cheshire 新增了数据显示功能，可将物品当中的 Data 的数据可视化
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
<img src="gugugu">

***

# 事件

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
| onBlockBreak* | 玩家破坏方块事件 |
| onBlockPlace* | 玩家放置方块事件 |
| onDamaged* | 玩家被攻击事件 |
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

# 粒子效果

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
      trail:
        # 选择粒子效果
        type: 'Arrow-Trail'
        # 定义粒子
        particle:
          type: 'FLAME'
        # 每1Tick播放一次效果
        period: 1
        # 持续10秒
        duration: 200
  event:
    # 玩家射击事件
    # 射击时在射出的箭矢(&projectile) 上播放 trail 效果
    onShoot*: |-
        effect play *trail on &projectile
```
<br>
效果演示
<br>
<img src="gugugu">