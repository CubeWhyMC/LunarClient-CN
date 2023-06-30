# Lunar Client CN

> 这个是next-gen分支, 使用java编写, 支持多个操作系统, 使用Python实现的版本 -> [here](https://github.com/cubewhy/lunarclient-cn/tree/master)

[Official Website](https://lunarcn.top) | [Discord](https://discord.gg/rCqCepgWJc) | [QQ Group](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=mqmHg3kA1PKlJUjGpErCPehjOf5-a8Us&authKey=JuagP1HDFpWbbwGUQCMKRZQxDFDXlulIczRaJNCMOK96JGOWaaL%2Fam8%2BFRdSRud5&noverify=0&group_code=780154857)

## 开源软件使用声明

Google Gson

[Weave-Loader](https://github.com/Weave-MC/Weave-Loader)

[Kotlin](https://kotlinlang.org)

## 关于Attach (注入)

LunarClient必须运行在JDK上, 关于设置自定义Java -> [here](#如何使用自定义jre)

使用DLL注入的程序不受JAVA影响, LunarClient CN使用java attach实现运行时热修改class

## 支持的系统

离线启动功能暂时只支持Windows

## 关于全饰品

LunarCN只是一个启动器, 并不包含全饰品功能, 但是我们提供了加入JavaAgent的通道, 你可以编写你自己的JavaAgent并使用

## ModLoader

LunarCN ModLoader 基于weaveLoader制作, 仅支持LunarClient with Minecraft 1.8.9

示例Mod -> [Click here] (https://github.com/CubeWhy/LunarClient-CN/tree/mod)

## 常见问题

你可能想找[Wiki](https://github.com/CubeWhy/LunarClient-CN/wiki)

### 我发现了bug

> 请使用最新版本启动

在此处创建[issues](https://github.com/CubeWhy/LunarClient-CN/issues)

## 如何自己写一个LunarCN

1. 解压缩`app.asar`
2. 自己写代码

## 为LunarCN贡献

1. fork
2. clone本项目
3. 使用IDEA打开项目
4. 修改代码
5. push
6. 提交pr
7. 等待合并

## 配置文件

> 如果你不会改就不要去改了, 可能导致无法启动游戏

### 配置文件目录

Windows: `%userprofile%/.cubewhy/lunarcn`

Linux/MacOS: `~/.cubewhy/lunarcn`

## 如何添加JavaAgent

1. 打开[配置文件目录](#配置文件目录)
2. 打开文件夹`javaagents`
3. 在这个文件夹里边添加JavaAgents即可(jar拓展名)
4. 如果你想修改参数, 可以打开`config.json` (位于配置文件根目录) 然后修改`java-agent`这个键值, 格式为`"agent_name": "args"'`

## 如何使用自定义JRE

1. 打开[配置文件目录](#配置文件目录)
2. 打开文件`config.json`
3. 找到`jre`这个参数, 留空表示使用默认JRE, 你可以修改成一个指向Java可执行文件的值

## 如何设置JVM参数

> 内存请从启动器内设置, 而不是在此处JVM参数

1. 打开[配置文件目录](#配置文件目录)
2. 打开文件`config.json`
3. 找到`jvm-args`这个参数, 可以添加自己的JVM参数

## 如何设置游戏参数

1. 打开[配置文件目录](#配置文件目录)
2. 打开文件`config.json`
3. 找到`args`这个参数, 可以添加自己的游戏参数

