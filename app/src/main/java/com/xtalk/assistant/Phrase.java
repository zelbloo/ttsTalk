package com.xtalk.assistant;

/**
 * 词组数据模型
 */
public class Phrase {
    private int id;
    private String category;
    private String content;

    public Phrase() {
    }

    public Phrase(int id, String category, String content) {
        this.id = id;
        this.category = category;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}