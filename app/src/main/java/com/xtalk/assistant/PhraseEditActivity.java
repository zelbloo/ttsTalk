package com.xtalk.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhraseEditActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_PHRASE_CONTENT = "phrase_content";
    public static final String EXTRA_PHRASE_ID = "phrase_id";

    private EditText phraseEditText;
    private Button saveButton;
    private Button cancelButton;
    private String category;
    private int phraseId;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_edit);

        // 初始化UI组件
        phraseEditText = findViewById(R.id.phrase_edit_text);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);

        // 获取传递过来的参数
        Intent intent = getIntent();
        category = intent.getStringExtra(EXTRA_CATEGORY);
        String phraseContent = intent.getStringExtra(EXTRA_PHRASE_CONTENT);
        phraseId = intent.getIntExtra(EXTRA_PHRASE_ID, -1);

        // 判断是添加模式还是编辑模式
        isEditMode = phraseId != -1;

        // 设置标题
        if (isEditMode) {
            setTitle(R.string.edit_phrase);
            phraseEditText.setText(phraseContent);
        } else {
            setTitle(R.string.add_phrase);
        }

        // 设置保存按钮点击事件
        saveButton.setOnClickListener(v -> savePhrase());

        // 设置取消按钮点击事件
        cancelButton.setOnClickListener(v -> cancel());
    }

    /**
     * 保存词组
     */
    private void savePhrase() {
        String content = phraseEditText.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, R.string.phrase_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建返回意图
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CATEGORY, category);
        resultIntent.putExtra(EXTRA_PHRASE_CONTENT, content);
        if (isEditMode) {
            resultIntent.putExtra(EXTRA_PHRASE_ID, phraseId);
        }

        // 设置结果并结束活动，使用系统常量
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 取消操作
     */
    private void cancel() {
        // 设置结果并结束活动，使用系统常量
        setResult(RESULT_CANCELED);
        finish();
    }
}