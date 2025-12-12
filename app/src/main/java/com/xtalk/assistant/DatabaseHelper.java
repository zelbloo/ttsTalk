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
        // 人称分类
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "我"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "你"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "他"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "家"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "爸"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "妈"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "儿子"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "女儿"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "爷爷"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "奶奶"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "姥姥"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "姥爷"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "老婆"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "男"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "女"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "亲戚"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "哥"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "姐"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "妹"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "姑"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "姨"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "舅"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "叔"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "表"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "老师"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "医生"));
        addPhrase(db, new Phrase(0, "clothing_food_housing_transport", "谁"));

        // 判断分类
        addPhrase(db, new Phrase(0, "relationships", "是"));
        addPhrase(db, new Phrase(0, "relationships", "不是"));
        addPhrase(db, new Phrase(0, "relationships", "有"));
        addPhrase(db, new Phrase(0, "relationships", "没有"));
        addPhrase(db, new Phrase(0, "relationships", "不"));
        addPhrase(db, new Phrase(0, "relationships", "别"));
        addPhrase(db, new Phrase(0, "relationships", "什么"));
        addPhrase(db, new Phrase(0, "relationships", "吗"));
        addPhrase(db, new Phrase(0, "relationships", "了"));
        addPhrase(db, new Phrase(0, "relationships", "和"));
        addPhrase(db, new Phrase(0, "relationships", "的"));
        addPhrase(db, new Phrase(0, "relationships", "在"));

        // 方位分类
        addPhrase(db, new Phrase(0, "actions", "上"));
        addPhrase(db, new Phrase(0, "actions", "下"));
        addPhrase(db, new Phrase(0, "actions", "左"));
        addPhrase(db, new Phrase(0, "actions", "右"));
        addPhrase(db, new Phrase(0, "actions", "面"));
        addPhrase(db, new Phrase(0, "actions", "前"));
        addPhrase(db, new Phrase(0, "actions", "后"));
        addPhrase(db, new Phrase(0, "actions", "里"));
        addPhrase(db, new Phrase(0, "actions", "外"));
        addPhrase(db, new Phrase(0, "actions", "哪"));
        addPhrase(db, new Phrase(0, "actions", "那"));

        // 动作分类
        addPhrase(db, new Phrase(0, "emotions", "吃"));
        addPhrase(db, new Phrase(0, "emotions", "想"));
        addPhrase(db, new Phrase(0, "emotions", "说"));
        addPhrase(db, new Phrase(0, "emotions", "去"));
        addPhrase(db, new Phrase(0, "emotions", "看"));
        addPhrase(db, new Phrase(0, "emotions", "听"));
        addPhrase(db, new Phrase(0, "emotions", "喝"));
        addPhrase(db, new Phrase(0, "emotions", "用"));
        addPhrase(db, new Phrase(0, "emotions", "睡"));
        addPhrase(db, new Phrase(0, "emotions", "走"));
        addPhrase(db, new Phrase(0, "emotions", "穿"));
        addPhrase(db, new Phrase(0, "emotions", "脱"));
        addPhrase(db, new Phrase(0, "emotions", "来"));
        addPhrase(db, new Phrase(0, "emotions", "等"));
        addPhrase(db, new Phrase(0, "emotions", "坐"));
        addPhrase(db, new Phrase(0, "emotions", "站"));
        addPhrase(db, new Phrase(0, "emotions", "买"));
        addPhrase(db, new Phrase(0, "emotions", "借"));
        addPhrase(db, new Phrase(0, "emotions", "需要"));
        addPhrase(db, new Phrase(0, "emotions", "好"));
        addPhrase(db, new Phrase(0, "emotions", "不好"));
        addPhrase(db, new Phrase(0, "emotions", "洗澡"));
        addPhrase(db, new Phrase(0, "emotions", "刷牙"));
        addPhrase(db, new Phrase(0, "emotions", "剪"));

        // 状态分类
        addPhrase(db, new Phrase(0, "requests", "饿"));
        addPhrase(db, new Phrase(0, "requests", "渴"));
        addPhrase(db, new Phrase(0, "requests", "困"));
        addPhrase(db, new Phrase(0, "requests", "疼"));
        addPhrase(db, new Phrase(0, "requests", "痒"));
        addPhrase(db, new Phrase(0, "requests", "高兴"));
        addPhrase(db, new Phrase(0, "requests", "难受"));
        addPhrase(db, new Phrase(0, "requests", "生气"));
        addPhrase(db, new Phrase(0, "requests", "害怕"));
        addPhrase(db, new Phrase(0, "requests", "新"));
        addPhrase(db, new Phrase(0, "requests", "旧"));
        addPhrase(db, new Phrase(0, "requests", "老"));
        addPhrase(db, new Phrase(0, "requests", "干净"));
        addPhrase(db, new Phrase(0, "requests", "脏"));
        addPhrase(db, new Phrase(0, "requests", "快"));
        addPhrase(db, new Phrase(0, "requests", "慢"));
        addPhrase(db, new Phrase(0, "requests", "大"));
        addPhrase(db, new Phrase(0, "requests", "小"));
        addPhrase(db, new Phrase(0, "requests", "多"));
        addPhrase(db, new Phrase(0, "requests", "少"));
        addPhrase(db, new Phrase(0, "requests", "冷"));
        addPhrase(db, new Phrase(0, "requests", "热"));

        // 数字分类
        addPhrase(db, new Phrase(0, "other", "1"));
        addPhrase(db, new Phrase(0, "other", "2"));
        addPhrase(db, new Phrase(0, "other", "3"));
        addPhrase(db, new Phrase(0, "other", "4"));
        addPhrase(db, new Phrase(0, "other", "5"));
        addPhrase(db, new Phrase(0, "other", "6"));
        addPhrase(db, new Phrase(0, "other", "7"));
        addPhrase(db, new Phrase(0, "other", "8"));
        addPhrase(db, new Phrase(0, "other", "9"));
        addPhrase(db, new Phrase(0, "other", "0"));
        addPhrase(db, new Phrase(0, "other", "个"));
        addPhrase(db, new Phrase(0, "other", "十"));
        addPhrase(db, new Phrase(0, "other", "百"));
        addPhrase(db, new Phrase(0, "other", "千"));
        addPhrase(db, new Phrase(0, "other", "万"));
        addPhrase(db, new Phrase(0, "other", "点"));
        addPhrase(db, new Phrase(0, "other", "块"));

        // 时间分类
        addPhrase(db, new Phrase(0, "time", "今天"));
        addPhrase(db, new Phrase(0, "time", "明天"));
        addPhrase(db, new Phrase(0, "time", "后天"));
        addPhrase(db, new Phrase(0, "time", "昨天"));
        addPhrase(db, new Phrase(0, "time", "以前"));
        addPhrase(db, new Phrase(0, "time", "以后"));
        addPhrase(db, new Phrase(0, "time", "时候"));
        addPhrase(db, new Phrase(0, "time", "早上"));
        addPhrase(db, new Phrase(0, "time", "中午"));
        addPhrase(db, new Phrase(0, "time", "晚上"));
        addPhrase(db, new Phrase(0, "time", "刚才"));

        // 名词分类
        addPhrase(db, new Phrase(0, "noun", "医院"));
        addPhrase(db, new Phrase(0, "noun", "药"));
        addPhrase(db, new Phrase(0, "noun", "电话"));
        addPhrase(db, new Phrase(0, "noun", "车"));
        addPhrase(db, new Phrase(0, "noun", "房子"));
        addPhrase(db, new Phrase(0, "noun", "床"));
        addPhrase(db, new Phrase(0, "noun", "钱"));
        addPhrase(db, new Phrase(0, "noun", "红包"));
        addPhrase(db, new Phrase(0, "noun", "电视"));
        addPhrase(db, new Phrase(0, "noun", "手机"));
        addPhrase(db, new Phrase(0, "noun", "饮料"));
        addPhrase(db, new Phrase(0, "noun", "水"));
        addPhrase(db, new Phrase(0, "noun", "饭"));
        addPhrase(db, new Phrase(0, "noun", "肉"));
        addPhrase(db, new Phrase(0, "noun", "东西"));
        addPhrase(db, new Phrase(0, "noun", "头"));
        addPhrase(db, new Phrase(0, "noun", "胳膊"));
        addPhrase(db, new Phrase(0, "noun", "手"));
        addPhrase(db, new Phrase(0, "noun", "腿"));
        addPhrase(db, new Phrase(0, "noun", "头发"));
        addPhrase(db, new Phrase(0, "noun", "脚"));
        addPhrase(db, new Phrase(0, "noun", "衣服"));
        addPhrase(db, new Phrase(0, "noun", "鞋"));
        addPhrase(db, new Phrase(0, "noun", "厕所"));
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