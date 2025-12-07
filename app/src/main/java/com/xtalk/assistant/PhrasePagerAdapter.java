package com.xtalk.assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class PhrasePagerAdapter extends FragmentStateAdapter {

    private final Context context;
    private final PhraseClickListener phraseClickListener;
    private final DatabaseHelper dbHelper;
    private final List<String> categoryNames;

    // 分类名称映射
    private static final String[] CATEGORY_NAMES = {
            "clothing_food_housing_transport",
            "relationships",
            "actions",
            "emotions",
            "requests",
            "other"
    };

    public PhrasePagerAdapter(@NonNull Context context, PhraseClickListener phraseClickListener) {
        super((MainActivity) context);
        this.context = context;
        this.phraseClickListener = phraseClickListener;
        this.dbHelper = new DatabaseHelper(context);
        this.categoryNames = List.of(CATEGORY_NAMES);
    }

    @NonNull
    @Override
    public PhraseCategoryFragment createFragment(int position) {
        String category = categoryNames.get(position);
        List<String> phrases = dbHelper.getPhrasesByCategory(category);
        return PhraseCategoryFragment.newInstance(
                category,
                phrases,
                phraseClickListener,
                (PhraseCategoryFragment.PhraseEditListener) context
        );
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

    /**
     * 词组点击监听器接口
     */
    public interface PhraseClickListener {
        void onPhraseClicked(String phrase);
    }
}