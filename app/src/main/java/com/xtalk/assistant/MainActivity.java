package com.xtalk.assistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, PhraseCategoryFragment.PhraseEditListener {

    private static final String TAG = "MainActivity_TTS";
    private TextView sentenceTextView;
    private TabLayout categoryTabLayout;
    private ViewPager2 phraseViewPager;
    private TextToSpeech textToSpeech;
    private StringBuilder currentSentence;
    private DatabaseHelper dbHelper;
    private PhrasePagerAdapter phrasePagerAdapter;
   // 添加标志，防止无限循环初始化
    private boolean isInitializing = false;
    private boolean isLanguageSettingFailed = false;
    private boolean hasTriedOtherEngines = false;
    private int initializationAttempts = 0;
    private static final int MAX_INITIALIZATION_ATTEMPTS = 3;
    
    // 请求代码
    private static final int REQUEST_ADD_PHRASE = 1;
    private static final int REQUEST_EDIT_PHRASE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI组件
        sentenceTextView = findViewById(R.id.sentence_text_view);
        categoryTabLayout = findViewById(R.id.category_tab_layout);
        phraseViewPager = findViewById(R.id.phrase_view_pager);

        // 初始化句子构建器
        currentSentence = new StringBuilder();

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 设置ViewPager适配器
        phrasePagerAdapter = new PhrasePagerAdapter(this, this::addPhraseToSentence);
        phraseViewPager.setAdapter(phrasePagerAdapter);

        // 关联TabLayout和ViewPager
        new TabLayoutMediator(categoryTabLayout, phraseViewPager, (tab, position) -> {
            // 设置每个tab的文本
            switch (position) {
                case 0:
                    tab.setText(R.string.category_clothing_food_housing_transport);
                    break;
                case 1:
                    tab.setText(R.string.category_relationships);
                    break;
                case 2:
                    tab.setText(R.string.category_actions);
                    break;
                case 3:
                    tab.setText(R.string.category_emotions);
                    break;
                case 4:
                    tab.setText(R.string.category_requests);
                    break;
                case 5:
                    tab.setText(R.string.category_other);
                    break;
            }
        }).attach();

        // 设置播放按钮点击事件
        findViewById(R.id.play_button).setOnClickListener(v -> speakSentence());

        // 设置清空按钮点击事件
        findViewById(R.id.clear_button).setOnClickListener(v -> clearSentence());
        
        // 延迟初始化 TTS
        findViewById(R.id.play_button).postDelayed(() -> {
            Log.d(TAG, "开始初始化 TTS，Android 版本: " + Build.VERSION.SDK_INT);
            initializeTTS();
        }, 500);
    }
    
    /**
     * 将词组添加到当前句子中
     */
    public void addPhraseToSentence(String phrase) {
        if (currentSentence.length() > 0) {
            currentSentence.append(" ");
        }
        currentSentence.append(phrase);
        sentenceTextView.setText(currentSentence.toString());
    }
    
    /**
     * 初始化 TTS 引擎 - Android 16 兼容版本
     */
    private void initializeTTS() {
        if (isInitializing) {
            Log.w(TAG, "TTS 正在初始化中，跳过重复请求");
            return;
        }
        
        isInitializing = true;
        Log.d(TAG, "开始 TTS 初始化，尝试次数: " + initializationAttempts);
        
        try {
            // 关键修复：使用 Activity Context 而不是 ApplicationContext
            textToSpeech = new TextToSpeech(this, this);
            Log.d(TAG, "TTS 构造函数调用成功");
        } catch (Exception e) {
            Log.e(TAG, "TTS 初始化异常", e);
            isInitializing = false;
            Toast.makeText(this, "TTS 初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInit(int status) {
        isInitializing = false;
        Log.d(TAG, "TTS onInit 回调，状态码: " + status + ", 当前尝试次数: " + initializationAttempts + ", hasTriedOtherEngines: " + hasTriedOtherEngines);
        
        if (status == TextToSpeech.SUCCESS) {
            // 只有在首次尝试或成功时才重置尝试次数和失败标志
            if (!hasTriedOtherEngines) {
                initializationAttempts = 0;
                isLanguageSettingFailed = false;
            }
            
            // 尝试设置中文语言，优化isLanguageAvailable()的使用
            boolean isChineseAvailable = false;
            int langResult = TextToSpeech.LANG_MISSING_DATA;
            
            try {
                // 1. 优化isLanguageAvailable()检查，使用更多中文Locale，并记录详细结果
                Log.d(TAG, "开始检测中文支持状态...");
                
                // 检查多种中文Locale
                Locale[] chineseLocales = {
                    Locale.CHINESE,           // 中文（通用）
                    Locale.SIMPLIFIED_CHINESE, // 简体中文
                    Locale.CHINA,             // 中文（中国）
                    new Locale("zh"),           // zh语言代码
                    new Locale("zh", "CN"),       // zh-CN语言代码
                    new Locale("zh", "CHS")       // zh-CHS语言代码
                };
                
                for (Locale locale : chineseLocales) {
                    int availableResult = textToSpeech.isLanguageAvailable(locale);
                    Log.d(TAG, "isLanguageAvailable(" + locale + ") 返回: " + langResultToString(availableResult));
                    
                    // 只要返回值不是LANG_NOT_SUPPORTED或LANG_MISSING_DATA，就认为支持
                    if (availableResult != TextToSpeech.LANG_NOT_SUPPORTED && availableResult != TextToSpeech.LANG_MISSING_DATA) {
                        isChineseAvailable = true;
                        break;
                    }
                }
                
                Log.d(TAG, "中文支持状态检测结果: " + (isChineseAvailable ? "支持" : "不支持"));
                
                // 2. 无论isLanguageAvailable()结果如何，都尝试设置中文，因为某些TTS引擎的isLanguageAvailable()返回值不准确
                // 先尝试设置语言
                langResult = textToSpeech.setLanguage(Locale.CHINESE);
                Log.d(TAG, "设置 Locale.CHINESE 结果: " + langResultToString(langResult));
                
                if (langResult == TextToSpeech.LANG_MISSING_DATA || 
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 尝试简体中文
                    langResult = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    Log.d(TAG, "设置 Locale.SIMPLIFIED_CHINESE 结果: " + langResultToString(langResult));
                }
                
                if (langResult == TextToSpeech.LANG_MISSING_DATA || 
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 尝试 Locale.CHINA
                    langResult = textToSpeech.setLanguage(Locale.CHINA);
                    Log.d(TAG, "设置 Locale.CHINA 结果: " + langResultToString(langResult));
                }
                
                if (langResult == TextToSpeech.LANG_MISSING_DATA || 
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 尝试zh语言代码
                    langResult = textToSpeech.setLanguage(new Locale("zh"));
                    Log.d(TAG, "设置 zh 语言代码结果: " + langResultToString(langResult));
                }
                
                // 3. 无论setLanguage()结果如何，都尝试直接播放语音，因为某些TTS引擎的setLanguage()返回值不准确
                // 但仅在首次尝试时执行
                if (langResult == TextToSpeech.LANG_NOT_SUPPORTED && !isLanguageSettingFailed) {
                    Log.d(TAG, "setLanguage() 返回不支持，但尝试直接播放测试语音...");
                    String testText = "测试语音播放";
                    int speakResult = textToSpeech.speak(testText, TextToSpeech.QUEUE_FLUSH, null);
                    Log.d(TAG, "直接播放测试语音结果: " + (speakResult == TextToSpeech.SUCCESS ? "成功" : "失败"));
                    
                    if (speakResult == TextToSpeech.SUCCESS) {
                        // 播放成功，认为TTS引擎支持中文，忽略setLanguage()的返回值
                        langResult = TextToSpeech.LANG_AVAILABLE;
                        Log.d(TAG, "直接播放成功，覆盖setLanguage()结果为LANG_AVAILABLE");
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "检测或设置语言时发生异常", e);
            }
            
            // 检查最终结果
            if (langResult == TextToSpeech.LANG_MISSING_DATA) {
                Log.e(TAG, "TTS 语言数据缺失");
                // 重置标志，因为这是一个不同的问题（数据缺失而非引擎不支持）
                hasTriedOtherEngines = false;
                showDownloadLanguageDataDialog();
            } else if (langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS 引擎不支持中文");
                
                // 检查是否已经尝试过语言设置失败，防止无限循环
                if (isLanguageSettingFailed) {
                    Log.e(TAG, "语言设置已失败过，终止尝试");
                    // 重置标志
                    hasTriedOtherEngines = false;
                    showTTSNotSupportedDialog();
                    return;
                }
                
                // 设置语言设置失败标志
                isLanguageSettingFailed = true;
                
                // 列出可用引擎
                List<String> engines = getAvailableTtsEngines();
                Log.d(TAG, "可用 TTS 引擎: " + engines);
                
                // 只有当引擎数量大于1时才尝试其他引擎，并且确保不会无限循环
                if (engines != null && engines.size() > 1 && initializationAttempts < MAX_INITIALIZATION_ATTEMPTS) {
                    // 设置标志，表明我们正在尝试其他引擎
                    hasTriedOtherEngines = true;
                    // 尝试其他引擎
                    tryNextEngine(engines);
                } else {
                    // 没有更多引擎可以尝试，显示错误对话框
                    // 重置标志
                    hasTriedOtherEngines = false;
                    showTTSNotSupportedDialog();
                }
            } else {
                // 成功
                try {
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.setPitch(1.0f);
                } catch (Exception e) {
                    Log.e(TAG, "设置语速和音调时发生异常", e);
                }
                
                // 重置所有标志，因为初始化成功
                initializationAttempts = 0;
                isLanguageSettingFailed = false;
                hasTriedOtherEngines = false;
                
                Log.i(TAG, "TTS 初始化成功，语言设置完成");
                Toast.makeText(this, "语音功能已就绪", Toast.LENGTH_SHORT).show();
                
                // 可选：播放测试语音
                // testTTS();
            }
        } else {
            Log.e(TAG, "TTS 初始化失败，状态码: " + status);
            
            if (initializationAttempts < MAX_INITIALIZATION_ATTEMPTS) {
                // 增加尝试次数
                initializationAttempts++;
                
                Log.d(TAG, "准备重试 TTS 初始化 (第" + initializationAttempts + "/" + MAX_INITIALIZATION_ATTEMPTS + "次)");
                
                // 延迟重试，增加延迟时间，减少toast频率
                new android.os.Handler().postDelayed(() -> {
                    if (!isInitializing) {
                        Log.d(TAG, "重试 TTS 初始化");
                        initializeTTS();
                    } else {
                        Log.d(TAG, "TTS 正在初始化中，跳过此次重试");
                    }
                }, 2000); // 增加延迟到2秒，减少toast频率
            } else {
                // 已达到最大尝试次数，显示错误对话框
                Log.e(TAG, "已达到最大尝试次数（" + MAX_INITIALIZATION_ATTEMPTS + "次），终止重试");
                // 重置标志
                hasTriedOtherEngines = false;
                showTTSInitErrorDialog("TTS 初始化失败，错误码: " + status);
            }
        }
    }
    
    /**
     * 尝试使用下一个可用引擎
     */
    private void tryNextEngine(List<String> engines) {
        // 添加防护机制，防止无限循环
        if (engines == null || engines.isEmpty()) {
            Log.e(TAG, "引擎列表为空，终止尝试");
            showTTSNotSupportedDialog();
            return;
        }
        
        // 检查是否已达到最大尝试次数
        if (initializationAttempts >= MAX_INITIALIZATION_ATTEMPTS) {
            Log.e(TAG, "已达到最大尝试次数（" + MAX_INITIALIZATION_ATTEMPTS + "次），终止尝试");
            showTTSInitErrorDialog("TTS 初始化失败，已尝试 " + MAX_INITIALIZATION_ATTEMPTS + " 次");
            return;
        }
        
        // 检查是否已超过引擎列表大小
        if (initializationAttempts >= engines.size()) {
            Log.e(TAG, "已尝试所有可用引擎（" + engines.size() + "个），终止尝试");
            showTTSNotSupportedDialog();
            return;
        }
        
        String nextEngine = engines.get(initializationAttempts);
        int currentAttempt = initializationAttempts + 1;
        
        Log.d(TAG, "尝试使用引擎 (第" + currentAttempt + "/" + MAX_INITIALIZATION_ATTEMPTS + "次): " + nextEngine);
        Toast.makeText(this, "尝试使用引擎 (" + currentAttempt + "/" + MAX_INITIALIZATION_ATTEMPTS + "): " + nextEngine, Toast.LENGTH_SHORT).show();
        
        try {
            // 释放旧实例
            if (textToSpeech != null) {
                textToSpeech.shutdown();
                textToSpeech = null;
            }
            
            // 增加尝试次数，防止无限循环
            initializationAttempts++;
            
            // 使用指定引擎初始化，兼容 Android 16
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // API 21+ 支持指定引擎
                textToSpeech = new TextToSpeech(this, this, nextEngine);
            } else {
                // API 21- 不支持指定引擎，使用默认方式
                textToSpeech = new TextToSpeech(this, this);
            }
        } catch (Exception e) {
            Log.e(TAG, "使用引擎 " + nextEngine + " 初始化失败", e);
            // 继续尝试下一个，但添加延迟和防护
            new android.os.Handler().postDelayed(() -> {
                // 再次检查最大尝试次数
                if (initializationAttempts < MAX_INITIALIZATION_ATTEMPTS) {
                    tryNextEngine(engines);
                } else {
                    Log.e(TAG, "已达到最大尝试次数，终止尝试");
                    showTTSInitErrorDialog("TTS 初始化失败，已尝试 " + MAX_INITIALIZATION_ATTEMPTS + " 次");
                }
            }, 1000); // 增加延迟时间，减少toast频率
        }
    }
    
    /**
     * 获取可用的 TTS 引擎 - Android 16 兼容版本
     */
    private List<String> getAvailableTtsEngines() {
        List<String> engines = new ArrayList<>();
        PackageManager pm = getPackageManager();
        
        try {
            // 方法 1：查询 TTS_SERVICE Intent（获取 TTS 服务）
            // 使用 PackageManager.MATCH_ALL 标志，兼容 Android 16
            Intent ttsServiceIntent = new Intent();
            // ACTION_TTS_SERVICE 在 API 21+ 可用，但我们使用字符串常量兼容旧版
            ttsServiceIntent.setAction("android.speech.tts.engine.TTS_SERVICE");
            
            // 使用兼容旧版 Android 的标志
            int flags = PackageManager.MATCH_DEFAULT_ONLY;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags = PackageManager.MATCH_ALL;
            }
            
            List<ResolveInfo> services = pm.queryIntentServices(ttsServiceIntent, flags);
            
            Log.d(TAG, "查询到 " + services.size() + " 个 TTS 服务");
            
            for (ResolveInfo info : services) {
                if (info.serviceInfo != null) {
                    String pkg = info.serviceInfo.packageName;
                    if (pkg != null && !engines.contains(pkg)) {
                        engines.add(pkg);
                        Log.d(TAG, "找到 TTS 引擎（服务）: " + pkg);
                    }
                }
            }
            
            // 方法 2：查询 CHECK_TTS_DATA Intent（获取 TTS 活动）
            Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            List<ResolveInfo> activities = pm.queryIntentActivities(checkIntent, flags);
            
            Log.d(TAG, "查询到 " + activities.size() + " 个 TTS 配置活动");
            
            for (ResolveInfo info : activities) {
                if (info.activityInfo != null) {
                    String pkg = info.activityInfo.packageName;
                    if (pkg != null && !engines.contains(pkg)) {
                        engines.add(pkg);
                        Log.d(TAG, "找到 TTS 引擎（活动）: " + pkg);
                    }
                }
            }
            
            // 方法 3：使用 TextToSpeech.getEngines()
            // getEngines() 方法在 API 11 中添加，需要一个 TTS 实例来调用
            try {
                if (textToSpeech != null) {
                    List<TextToSpeech.EngineInfo> engineInfos = textToSpeech.getEngines();
                    Log.d(TAG, "getEngines() 返回 " + engineInfos.size() + " 个引擎");
                    
                    for (TextToSpeech.EngineInfo info : engineInfos) {
                        if (info.name != null && !engines.contains(info.name)) {
                            engines.add(info.name);
                            Log.d(TAG, "找到 TTS 引擎（getEngines）: " + info.name);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getEngines() 调用失败", e);
            }
            
            // 方法 4：添加常见的 TTS 引擎包名作为备选（兼容 Android 16）
            List<String> commonEngines = new ArrayList<>();
            commonEngines.add("com.google.android.tts"); // Google TTS
            commonEngines.add("com.svox.pico"); // Pico TTS（Android 内置）
            commonEngines.add("com.iflytek.speechcloud"); // 讯飞 TTS
            commonEngines.add("com.baidu.duersdk.opensdk"); // 百度 TTS
            commonEngines.add("com.oppo.tts"); // OPPO TTS（一加手机使用）
            commonEngines.add("com.coloros.speech"); // ColorOS TTS（一加手机使用）
            
            for (String engine : commonEngines) {
                if (!engines.contains(engine)) {
                    // 检查引擎是否真的安装在设备上
                    Intent engineCheckIntent = new Intent();
                    engineCheckIntent.setPackage(engine);
                    engineCheckIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(engineCheckIntent, flags);
                    if (!resolveInfos.isEmpty()) {
                        engines.add(engine);
                        Log.d(TAG, "找到 TTS 引擎（常见引擎）: " + engine);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "查询 TTS 引擎时发生异常", e);
        }
        
        return engines;
    }
    
    /**
     * 播放当前句子，优化朗读效果
     */
    private void speakSentence() {
        String sentence = currentSentence.toString().trim();
        if (sentence.isEmpty()) {
            Toast.makeText(this, "请先组成句子", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textToSpeech == null) {
            Toast.makeText(this, "语音功能未就绪，正在初始化...", Toast.LENGTH_SHORT).show();
            initializeTTS();
            return;
        }

        try {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
            
            // 优化1：调整TTS引擎参数，减少词组间停顿
            // 略微加快语速，减少停顿感（1.1f为略微加快）
            textToSpeech.setSpeechRate(1.1f);
            // 设置音调为正常水平
            textToSpeech.setPitch(1.0f);
            
            // 优化2：优化句子格式
            String optimizedSentence = optimizeSentenceForSpeech(sentence);
            
            Log.d(TAG, "开始播放: " + optimizedSentence);
            
            // 优化3：直接使用普通speak方法，不使用SSML，避免参数被读出来
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 使用现代API，但不使用SSML
                int result = textToSpeech.speak(optimizedSentence, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
                Log.d(TAG, "speak() 返回结果: " + result);
            } else {
                // 旧版 API
                int result = textToSpeech.speak(optimizedSentence, TextToSpeech.QUEUE_FLUSH, null);
                Log.d(TAG, "旧版speak() 返回结果: " + result);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "播放语音时发生异常", e);
            Toast.makeText(this, "语音播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 优化句子格式，减少词组间的停顿
     */
    private String optimizeSentenceForSpeech(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }
        
        // 优化1：移除所有空格，包括词组间的空格
        String optimized = sentence.replaceAll("\\s+", "");
        
        // 优化2：根据中文习惯，确保标点使用正确
        // 这里不再需要处理标点后的空格，因为已经移除了所有空格
        
        // 优化3：移除句子开头和结尾的空格（可选，因为已经移除了所有空格）
        optimized = optimized.trim();
        
        // 优化4：确保句子以中文标点结尾
        if (!optimized.isEmpty()) {
            char lastChar = optimized.charAt(optimized.length() - 1);
            if (lastChar != '。' && lastChar != '！' && lastChar != '？') {
                optimized += "。";
            }
        }
        
        Log.d(TAG, "优化前句子: " + sentence);
        Log.d(TAG, "优化后句子: " + optimized);
        
        return optimized;
    }
    
    /**
     * 测试 TTS（可选）
     */
    private void testTTS() {
        if (textToSpeech != null) {
            Log.d(TAG, "测试 TTS 播放");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak("语音测试成功", TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak("语音测试成功", TextToSpeech.QUEUE_FLUSH, null);
                }
            } catch (Exception e) {
                Log.e(TAG, "测试 TTS 失败", e);
            }
        }
    }
    
    /**
     * 清空当前句子
     */
    private void clearSentence() {
        currentSentence.setLength(0);
        sentenceTextView.setText("");
    }
    
    /**
     * 语言设置结果转字符串
     */
    private String langResultToString(int result) {
        switch (result) {
            case TextToSpeech.LANG_AVAILABLE: return "LANG_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_AVAILABLE: return "LANG_COUNTRY_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE: return "LANG_COUNTRY_VAR_AVAILABLE";
            case TextToSpeech.LANG_MISSING_DATA: return "LANG_MISSING_DATA";
            case TextToSpeech.LANG_NOT_SUPPORTED: return "LANG_NOT_SUPPORTED";
            default: return "UNKNOWN(" + result + ")";
        }
    }
    
    /**
     * 显示下载语言数据对话框
     */
    private void showDownloadLanguageDataDialog() {
        new AlertDialog.Builder(this)
            .setTitle("需要下载语言数据")
            .setMessage("您的 TTS 引擎需要下载中文语言数据。是否前往下载？")
            .setPositiveButton("前往下载", (dialog, which) -> {
                try {
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                } catch (Exception e) {
                    Log.e(TAG, "无法打开 TTS 数据下载页面", e);
                    Toast.makeText(this, "无法打开下载页面", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    /**
     * 显示 TTS 不支持对话框
     */
    private void showTTSNotSupportedDialog() {
        new AlertDialog.Builder(this)
            .setTitle("不支持中文语音")
            .setMessage("当前 TTS 引擎不支持中文。\n\n建议安装：\n• Google 文字转语音引擎\n• 其他支持中文的 TTS 应用")
            .setPositiveButton("前往设置", (dialog, which) -> {
                try {
                    Intent intent = new Intent();
                    intent.setAction("com.android.settings.TTS_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "无法打开 TTS 设置", e);
                    Toast.makeText(this, "无法打开设置", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    /**
     * 显示 TTS 初始化错误对话框
     */
    private void showTTSInitErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
            .setTitle("语音初始化失败")
            .setMessage(errorMessage + "\n\n请检查：\n1. 设备是否安装了 TTS 引擎\n2. TTS 设置是否正确\n3. 应用是否有必要的权限")
            .setPositiveButton("前往设置", (dialog, which) -> {
                try {
                    Intent intent = new Intent();
                    intent.setAction("com.android.settings.TTS_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "无法打开 TTS 设置", e);
                }
            })
            .setNegativeButton("重试", (dialog, which) -> {
                initializationAttempts = 0;
                initializeTTS();
            })
            .setNeutralButton("取消", null)
            .show();
    }

    @Override
    public void onAddPhrase(String category) {
        // 启动添加词组活动
        Intent intent = new Intent(this, PhraseEditActivity.class);
        intent.putExtra(PhraseEditActivity.EXTRA_CATEGORY, category);
        startActivityForResult(intent, REQUEST_ADD_PHRASE);
    }

    @Override
    public void onEditPhrase(String category, String phrase) {
        // 启动编辑词组活动
        Intent intent = new Intent(this, PhraseEditActivity.class);
        intent.putExtra(PhraseEditActivity.EXTRA_CATEGORY, category);
        intent.putExtra(PhraseEditActivity.EXTRA_PHRASE_CONTENT, phrase);
        startActivityForResult(intent, REQUEST_EDIT_PHRASE);
    }

    @Override
    public void onDeletePhrase(String category, String phrase) {
        // 从数据库中删除词组
        if (dbHelper != null) {
            dbHelper.deletePhraseByCategoryAndContent(category, phrase);
            // 刷新ViewPager
            refreshPhraseViewPager();
        }
    }

    /**
     * 刷新ViewPager，更新词组列表
     */
    private void refreshPhraseViewPager() {
        // 确保dbHelper不为null
        if (dbHelper != null) {
            phrasePagerAdapter = new PhrasePagerAdapter(this, this::addPhraseToSentence);
            phraseViewPager.setAdapter(phrasePagerAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            String category = data.getStringExtra(PhraseEditActivity.EXTRA_CATEGORY);
            String content = data.getStringExtra(PhraseEditActivity.EXTRA_PHRASE_CONTENT);
            
            if (category != null && content != null && dbHelper != null) {
                if (requestCode == REQUEST_ADD_PHRASE) {
                    // 添加新词组到数据库
                    Phrase phrase = new Phrase();
                    phrase.setCategory(category);
                    phrase.setContent(content);
                    dbHelper.addPhrase(phrase);
                    // 刷新ViewPager
                    refreshPhraseViewPager();
                } else if (requestCode == REQUEST_EDIT_PHRASE) {
                    // 更新数据库中的词组
                    String oldContent = data.getStringExtra(PhraseEditActivity.EXTRA_PHRASE_CONTENT);
                    if (oldContent != null) {
                        dbHelper.deletePhraseByCategoryAndContent(category, oldContent);
                        Phrase phrase = new Phrase();
                        phrase.setCategory(category);
                        phrase.setContent(content);
                        dbHelper.addPhrase(phrase);
                        // 刷新ViewPager
                        refreshPhraseViewPager();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            Log.d(TAG, "释放 TTS 资源");
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
}