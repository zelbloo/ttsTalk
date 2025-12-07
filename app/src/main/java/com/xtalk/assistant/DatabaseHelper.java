package com.xtalk.assistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库帮助类，用于管理词组数据的存储和操作
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // 数据库版本
    private static final int DATABASE_VERSION = 1;
    // 数据库名称
    private static final String DATABASE_NAME = "PhraseDB";
    // 词组表名
    private static final String TABLE_PHRASE = "phrase";
    // 词组表列名
    private static final String KEY_ID = "id";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_CONTENT = "content";

    /**
     * 构造函数
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建词组表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHRASE_TABLE = "CREATE TABLE " + TABLE_PHRASE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_CONTENT + " TEXT" + ")";
        db.execSQL(CREATE_PHRASE_TABLE);
        
        // 初始化默认词组数据
        initializeDefaultPhrases(db);
    }

    /**
     * 初始化默认词组数据
     */
    private void initializeDefaultPhrases(SQLiteDatabase db) {
        // 衣食住行分类
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "我"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "想要"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "吃"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "喝"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "睡觉"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "去"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "家"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "医院"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "餐厅"));

        // 人际关系分类
        addPhrase(db, new Phrase(0, "relationships", "妈妈"));
        addPhrase(db, new Phrase(0, "relationships", "爸爸"));
        addPhrase(db, new Phrase(0, "relationships", "医生"));
        addPhrase(db, new Phrase(0, "relationships", "护士"));
        addPhrase(db, new Phrase(0, "relationships", "朋友"));
        addPhrase(db, new Phrase(0, "relationships", "家人"));
        addPhrase(db, new Phrase(0, "relationships", "老师"));
        addPhrase(db, new Phrase(0, "relationships", "同学"));
        addPhrase(db, new Phrase(0, "relationships", "同事"));

        // 行为动作分类
        addPhrase(db, new Phrase(0, "actions", "走路"));
        addPhrase(db, new Phrase(0, "actions", "坐下"));
        addPhrase(db, new Phrase(0, "actions", "站起来"));
        addPhrase(db, new Phrase(0, "actions", "躺下"));
        addPhrase(db, new Phrase(0, "actions", "阅读"));
        addPhrase(db, new Phrase(0, "actions", "写"));
        addPhrase(db, new Phrase(0, "actions", "看电视"));
        addPhrase(db, new Phrase(0, "actions", "听音乐"));
        addPhrase(db, new Phrase(0, "actions", "跑步"));

        // 情绪表达分类
        addPhrase(db, new Phrase(0, "emotions", "开心"));
        addPhrase(db, new Phrase(0, "emotions", "难过"));
        addPhrase(db, new Phrase(0, "emotions", "饿了"));
        addPhrase(db, new Phrase(0, "emotions", "渴了"));
        addPhrase(db, new Phrase(0, "emotions", "累了"));
        addPhrase(db, new Phrase(0, "emotions", "生气"));
        addPhrase(db, new Phrase(0, "emotions", "害怕"));
        addPhrase(db, new Phrase(0, "emotions", "惊讶"));
        addPhrase(db, new Phrase(0, "emotions", "平静"));

        // 需求请求分类
        addPhrase(db, new Phrase(0, "requests", "需要"));
        addPhrase(db, new Phrase(0, "requests", "帮助"));
        addPhrase(db, new Phrase(0, "requests", "请"));
        addPhrase(db, new Phrase(0, "requests", "谢谢"));
        addPhrase(db, new Phrase(0, "requests", "对不起"));
        addPhrase(db, new Phrase(0, "requests", "请帮我"));
        addPhrase(db, new Phrase(0, "requests", "我需要"));
        addPhrase(db, new Phrase(0, "requests", "可以吗"));
        addPhrase(db, new Phrase(0, "requests", "麻烦你"));

        // 其他常用语分类
        addPhrase(db, new Phrase(0, "other", "是"));
        addPhrase(db, new Phrase(0, "other", "不是"));
        addPhrase(db, new Phrase(0, "other", "好"));
        addPhrase(db, new Phrase(0, "other", "不好"));
        addPhrase(db, new Phrase(0, "other", "可以"));
        addPhrase(db, new Phrase(0, "other", "不可以"));
        addPhrase(db, new Phrase(0, "other", "现在"));
        addPhrase(db, new Phrase(0, "other", "后来"));
        addPhrase(db, new Phrase(0, "other", "今天"));
        addPhrase(db, new Phrase(0, "other", "明天"));
        addPhrase(db, new Phrase(0, "other", "昨天"));
        addPhrase(db, new Phrase(0, "other", "早上"));
        addPhrase(db, new Phrase(0, "other", "中午"));
        addPhrase(db, new Phrase(0, "other", "晚上"));
        addPhrase(db, new Phrase(0, "other", "这里"));
        addPhrase(db, new Phrase(0, "other", "那里"));
        addPhrase(db, new Phrase(0, "other", "这个"));
        addPhrase(db, new Phrase(0, "other", "那个"));
    }

    /**
     * 数据库升级时调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHRASE);
        // 重新创建表
        onCreate(db);
    }

    /**
     * 添加词组到数据库
     */
    public void addPhrase(Phrase phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        addPhrase(db, phrase);
        db.close();
    }

    /**
     * 添加词组到数据库（内部方法，使用现有数据库连接）
     */
    private void addPhrase(SQLiteDatabase db, Phrase phrase) {
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY, phrase.getCategory());
        values.put(KEY_CONTENT, phrase.getContent());
        db.insert(TABLE_PHRASE, null, values);
    }

    /**
     * 根据ID获取词组
     */
    public Phrase getPhrase(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHRASE, new String[]{KEY_ID, KEY_CATEGORY, KEY_CONTENT},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Phrase phrase = null;
        if (cursor != null && cursor.moveToFirst()) {
            phrase = new Phrase(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            cursor.close();
        }
        db.close();
        return phrase;
    }

    /**
     * 获取指定分类的所有词组
     */
    public List<String> getPhrasesByCategory(String category) {
        List<String> phrases = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHRASE, new String[]{KEY_CONTENT},
                KEY_CATEGORY + "=?", new String[]{category}, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                phrases.add(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();
        return phrases;
    }

    /**
     * 获取所有分类的名称
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + KEY_CATEGORY + " FROM " + TABLE_PHRASE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();
        return categories;
    }

    /**
     * 更新词组
     */
    public int updatePhrase(Phrase phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY, phrase.getCategory());
        values.put(KEY_CONTENT, phrase.getContent());
        int rowsAffected = db.update(TABLE_PHRASE, values, KEY_ID + "=?",
                new String[]{String.valueOf(phrase.getId())});
        db.close();
        return rowsAffected;
    }

    /**
     * 删除词组
     */
    public void deletePhrase(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHRASE, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * 根据分类和内容删除词组
     */
    public void deletePhraseByCategoryAndContent(String category, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHRASE, KEY_CATEGORY + "=? AND " + KEY_CONTENT + "=?",
                new String[]{category, content});
        db.close();
    }
}