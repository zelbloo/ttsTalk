# 语音辅助App

## 项目简介

本应用专为说话及打字障碍的人群设计的语音辅助工具，帮助其通过选择常用语句组成完整句子，并转换为语音播放，实现与他人的沟通。

## 功能特点

- **词组选择与句子组成**：从分类列表中选择常用词组，自动组成完整句子
- **文字转语音（TTS）**：支持将组成的句子转换为中文语音播放
- **词组分类管理**：根据使用场景和类型对词组进行分类

## 技术栈

- **开发语言**：Java
- **开发框架**：Android SDK
- **TTS引擎**：Android自带TextToSpeech引擎
- **UI组件**：Material Components

## 项目结构

```
├── app
│   ├── build.gradle          # 应用模块构建配置
│   └── src
│       └── main
│           ├── AndroidManifest.xml  # 应用清单文件
│           ├── java
│           │   └── com
│           │       └── xtalk
│           │           └── assistant
│           │               ├── MainActivity.java          # 主活动类
│           │               ├── PhraseCategoryFragment.java # 分类片段类
│           │               └── PhrasePagerAdapter.java     # ViewPager适配器
│           └── res
│               ├── drawable     # 图片资源
│               ├── layout       # 布局文件
│               │   ├── activity_main.xml           # 主布局
│               │   ├── fragment_phrase_category.xml # 分类片段布局
│               │   └── item_phrase_button.xml      # 词组按钮布局
│               └── values       # 资源文件
│                   ├── colors.xml   # 颜色资源
│                   ├── strings.xml  # 字符串资源
│                   └── styles.xml   # 样式资源
├── build.gradle          # 项目构建配置
├── gradle.properties     # Gradle属性配置
└── settings.gradle       # 项目设置
```

## 安装与运行

1. **环境要求**
   - Android Studio 4.0+ 
   - Android SDK 23+ 
   - JDK 8+ 

2. **安装步骤**
   - 克隆或下载项目代码
   - 使用Android Studio打开项目
   - 连接Android设备或启动模拟器
   - 点击运行按钮编译并安装应用

3. **运行应用**
   - 打开应用后，在分类标签栏选择不同的分类
   - 点击分类下的词组按钮，将词组添加到句子组成区域
   - 点击底部的播放按钮，将组成的句子转换为语音播放
   - 点击清空按钮，清空当前组成的句子

## 使用说明

1. **组成句子**
   - 在顶部的分类标签栏选择一个分类
   - 点击分类下的词组按钮，词组会自动添加到句子组成区域
   - 可以从不同分类中选择词组，组成完整的句子

2. **播放语音**
   - 点击底部中央的大型播放按钮，应用会将当前组成的句子转换为语音播放
   - 如果句子为空，会弹出提示信息

3. **清空句子**
   - 点击底部左侧的清空按钮，可以清空当前组成的句子

## 主要功能模块

### 1. 句子组成模块
- 负责显示当前已组成的句子
- 支持滚动显示长句子
- 显示每个词组之间的分隔

### 2. 分类词组模块
- 提供多个分类标签，包括衣食住行、人际关系、行为动作等
- 每个分类下显示常用词组，采用网格布局
- 支持左右滑动切换分类

### 3. 语音播放模块
- 集成Android TextToSpeech引擎
- 支持中文语音播放
- 提供清晰的播放按钮

## 扩展与定制

### 添加新的词组
1. 在 `strings.xml` 文件中添加新的词组字符串
2. 在 `PhrasePagerAdapter.java` 的 `initCategoryPhrases()` 方法中，将新词组添加到对应的分类列表中

### 添加新的分类
1. 在 `strings.xml` 文件中添加新的分类名称字符串
2. 在 `activity_main.xml` 文件中添加新的 `TabItem`
3. 在 `PhrasePagerAdapter.java` 的 `initCategoryPhrases()` 方法中，添加新的分类词组列表

### 自定义主题颜色
在 `colors.xml` 文件中修改对应的颜色值：
- `primary_color`：主题主色
- `secondary_color`：主题辅色
- `background_color`：背景色
- `button_color`：按钮颜色

## 注意事项

1. 首次运行应用时，可能需要等待TTS引擎初始化完成
2. 确保设备已安装中文语音包，否则可能无法正常播放中文语音
3. 应用支持Android 6.0及以上版本
4. 在部分设备上，TTS语音质量可能有所差异

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

- 项目负责人：[负责人姓名]
- 联系邮箱：[邮箱地址]
- 联系电话：[电话号码]