package com.xtalk.assistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
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

    private TextView sentenceTextView;
    private TabLayout categoryTabLayout;
    private ViewPager2 phraseViewPager;
    private TextToSpeech textToSpeech;
    private StringBuilder currentSentence;
    private DatabaseHelper dbHelper;
    private PhrasePagerAdapter phrasePagerAdapter;
    
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
        
        // 延迟初始化TTS，确保应用启动后再初始化，兼容旧版Android
        findViewById(R.id.play_button).postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeTTS();
            }
        }, 500);
    }
    
    /**
     * 初始化TTS引擎，兼容Android 16
     */
    private void initializeTTS() {
        try {
            // 直接初始化TTS，不指定引擎，让系统自动选择
            textToSpeech = new TextToSpeech(getApplicationContext(), this);
            
            // 显示调试信息
            Toast.makeText(this, "TTS初始化请求已发送", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // 捕获初始化异常
            e.printStackTrace();
            Toast.makeText(this, "TTS初始化异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // 显示更详细的错误信息
            showTTSInitErrorDialog(e.getMessage());
        }
    }
    
    /**
     * 显示TTS初始化错误对话框
     */
    private void showTTSInitErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("TTS初始化失败")
               .setMessage("语音合成初始化失败: " + errorMessage + "\n\n请确保您的设备已安装并启用了支持中文的TTS引擎。")
               .setPositiveButton("确定", null)
               .show();
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
     * 播放当前句子，兼容Android 16
     */
    private void speakSentence() {
        String sentence = currentSentence.toString();
        if (sentence.isEmpty()) {
            Toast.makeText(this, "请先组成句子", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textToSpeech == null) {
            Toast.makeText(this, "TTS未初始化，正在尝试重新初始化...", Toast.LENGTH_SHORT).show();
            // 重新初始化TTS
            initializeTTS();
            return;
        }

        // 检查TTS是否可用
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        
        // 设置语速和音调，使用兼容API
        try {
            textToSpeech.setSpeechRate(1.0f);
            textToSpeech.setPitch(1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            // 使用兼容Android 16的API播放语音
            // 旧版API不支持Bundle参数和utteranceId
            int speakResult = textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
            
            // 检查播放结果
            if (speakResult == TextToSpeech.SUCCESS) {
                Toast.makeText(this, "正在播放: " + sentence, Toast.LENGTH_SHORT).show();
            } else {
                // 播放失败，显示错误信息
                Toast.makeText(this, "语音播放失败，错误码: " + speakResult, Toast.LENGTH_SHORT).show();
                
                // 尝试重新初始化TTS
                initializeTTS();
            }
        } catch (Exception e) {
            // 捕获异常，确保应用不会崩溃
            e.printStackTrace();
            Toast.makeText(this, "语音播放异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 清空当前句子
     */
    private void clearSentence() {
        currentSentence.setLength(0);
        sentenceTextView.setText("");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 简化语言设置，兼容Android 16
            int result = TextToSpeech.LANG_MISSING_DATA;
            try {
                // 首先尝试基本的中文设置，兼容Android 16
                result = textToSpeech.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 尝试中国区域设置
                    result = textToSpeech.setLanguage(Locale.CHINA);
                }
                
                // 调试信息：显示语言设置结果
                Toast.makeText(this, "语言设置结果: " + resultToString(result), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // 捕获可能的异常，确保应用不会崩溃
                e.printStackTrace();
                Toast.makeText(this, "语言设置异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "不支持中文语言包，请安装中文TTS引擎", Toast.LENGTH_LONG).show();
                // 提示用户安装TTS引擎
                installTTSData();
            } else {
                // 设置语速和音调，使用兼容API
                textToSpeech.setSpeechRate(1.0f);
                textToSpeech.setPitch(1.0f);
                Toast.makeText(this, "TTS初始化成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 显示更详细的错误信息
            String errorMsg = "TTS引擎初始化失败，错误码: " + status;
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            // 尝试安装TTS引擎
            installTTSData();
        }
    }
    
    /**
     * 将TTS语言设置结果转换为可读字符串
     */
    private String resultToString(int result) {
        switch (result) {
            case TextToSpeech.LANG_AVAILABLE: return "LANG_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_AVAILABLE: return "LANG_COUNTRY_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE: return "LANG_COUNTRY_VAR_AVAILABLE";
            case TextToSpeech.LANG_MISSING_DATA: return "LANG_MISSING_DATA";
            case TextToSpeech.LANG_NOT_SUPPORTED: return "LANG_NOT_SUPPORTED";
            default: return "未知结果: " + result;
        }
    }
    
    /**
     * 安装TTS数据，兼容旧版本Android
     */
    private void installTTSData() {
        try {
            // 尝试安装TTS数据
            Intent installIntent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 检查是否有可用的TTS引擎
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(installIntent, 0);
            if (activities != null && !activities.isEmpty()) {
                startActivity(installIntent);
                Toast.makeText(this, "正在打开TTS数据安装界面", Toast.LENGTH_SHORT).show();
            } else {
                // 没有找到合适的TTS引擎
                Toast.makeText(this, "未找到TTS引擎，请手动安装", Toast.LENGTH_LONG).show();
                // 提示用户安装Google TTS或其他TTS引擎
                showInstallTTSEngineDialog();
            }
        } catch (Exception e) {
            // 捕获异常，确保应用不会崩溃
            e.printStackTrace();
            Toast.makeText(this, "安装TTS数据失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 显示安装TTS引擎的提示对话框
     */
    private void showInstallTTSEngineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("缺少TTS引擎")
               .setMessage("您的设备缺少中文TTS引擎，无法正常使用语音功能。请安装Google语音引擎或其他支持中文的TTS引擎。")
               .setPositiveButton("确定", null)
               .show();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
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
        dbHelper.deletePhraseByCategoryAndContent(category, phrase);
        // 刷新ViewPager
        refreshPhraseViewPager();
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
                    // 显示添加成功提示
                    Toast.makeText(this, "添加成功: " + content, Toast.LENGTH_SHORT).show();
                    // 刷新ViewPager
                    refreshPhraseViewPager();
                } else if (requestCode == REQUEST_EDIT_PHRASE) {
                    // 更新数据库中的词组
                    // 这里简化处理，先删除旧词组，再添加新词组
                    String oldContent = data.getStringExtra(PhraseEditActivity.EXTRA_PHRASE_CONTENT);
                    if (oldContent != null) {
                        dbHelper.deletePhraseByCategoryAndContent(category, oldContent);
                        Phrase phrase = new Phrase();
                        phrase.setCategory(category);
                        phrase.setContent(content);
                        dbHelper.addPhrase(phrase);
                        // 显示编辑成功提示
                        Toast.makeText(this, "编辑成功: " + content, Toast.LENGTH_SHORT).show();
                        // 刷新ViewPager
                        refreshPhraseViewPager();
                    }
                }
            } else {
                // 显示调试信息
                Toast.makeText(this, "数据为空: category=" + category + ", content=" + content + ", dbHelper=" + dbHelper, Toast.LENGTH_SHORT).show();
            }
        } else {
            // 显示调试信息
            Toast.makeText(this, "结果码错误: resultCode=" + resultCode + ", data=" + data, Toast.LENGTH_SHORT).show();
        }
    }
}