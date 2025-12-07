package com.xtalk.assistant;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PhraseCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_PHRASES = "phrases";
    private String category;
    private List<String> phrases;
    private PhrasePagerAdapter.PhraseClickListener phraseClickListener;
    private PhraseEditListener phraseEditListener;

    public PhraseCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * 词组编辑监听器接口
     */
    public interface PhraseEditListener {
        void onAddPhrase(String category);
        void onEditPhrase(String category, String phrase);
        void onDeletePhrase(String category, String phrase);
    }

    /**
     * 创建新的Fragment实例
     */
    public static PhraseCategoryFragment newInstance(
            String category,
            List<String> phrases,
            PhrasePagerAdapter.PhraseClickListener phraseClickListener,
            PhraseEditListener phraseEditListener) {
        PhraseCategoryFragment fragment = new PhraseCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        // 确保转换为ArrayList
        args.putStringArrayList(ARG_PHRASES, new java.util.ArrayList<>(phrases));
        fragment.setArguments(args);
        fragment.phraseClickListener = phraseClickListener;
        fragment.phraseEditListener = phraseEditListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            phrases = getArguments().getStringArrayList(ARG_PHRASES);
        }
        // 确保phrases列表永远不会为null
        if (phrases == null) {
            phrases = new java.util.ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phrase_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置RecyclerView适配器
        RecyclerView phraseRecyclerView = view.findViewById(R.id.phrase_recycler_view);
        PhraseAdapter adapter = new PhraseAdapter(phrases, phraseClickListener, (position, phrase, anchorView) -> {
            // 长按词组时显示操作菜单，在长按的view旁边弹出
            showPhraseOptions(position, phrase, anchorView);
        });
        phraseRecyclerView.setAdapter(adapter);

        // 找到并设置浮动操作按钮的点击事件
        FloatingActionButton addFab = view.findViewById(R.id.add_phrase_fab);
        addFab.setOnClickListener(v -> {
            if (phraseEditListener != null) {
                phraseEditListener.onAddPhrase(category);
            }
        });
    }

    /**
     * 显示词组操作选项，在长按的view旁边弹出
     */
    private void showPhraseOptions(int position, String phrase, View anchorView) {
        // 使用长按的view作为锚点，在其旁边显示弹出菜单
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.phrase_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                // 编辑词组
                if (phraseEditListener != null) {
                    phraseEditListener.onEditPhrase(category, phrase);
                }
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                // 删除词组
                if (phraseEditListener != null) {
                    phraseEditListener.onDeletePhrase(category, phrase);
                }
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * 词组适配器
     */
    private static class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.PhraseViewHolder> {

        private final List<String> phrases;
        private final PhrasePagerAdapter.PhraseClickListener phraseClickListener;
        private final OnPhraseLongClickListener longClickListener;

        public interface OnPhraseLongClickListener {
            void onPhraseLongClick(int position, String phrase, View view);
        }

        public PhraseAdapter(List<String> phrases, 
                            PhrasePagerAdapter.PhraseClickListener phraseClickListener,
                            OnPhraseLongClickListener longClickListener) {
            this.phrases = phrases;
            this.phraseClickListener = phraseClickListener;
            this.longClickListener = longClickListener;
        }

        @NonNull
        @Override
        public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_phrase_button, parent, false);
            return new PhraseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhraseViewHolder holder, int position) {
            String phrase = phrases.get(position);
            // 确保phrase不为null
            if (phrase != null) {
                holder.phraseButton.setText(phrase);
                // 确保phraseClickListener不为null
                if (phraseClickListener != null) {
                    holder.phraseButton.setOnClickListener(v -> phraseClickListener.onPhraseClicked(phrase));
                }
                // 设置长按监听器
                holder.phraseButton.setOnLongClickListener(v -> {
                    if (longClickListener != null) {
                        longClickListener.onPhraseLongClick(position, phrase, v);
                        return true;
                    }
                    return false;
                });
            } else {
                holder.phraseButton.setText("");
                holder.phraseButton.setOnClickListener(null);
                holder.phraseButton.setOnLongClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return phrases != null ? phrases.size() : 0;
        }

        /**
         * 词组ViewHolder
         */
        static class PhraseViewHolder extends RecyclerView.ViewHolder {
            Button phraseButton;

            public PhraseViewHolder(@NonNull View itemView) {
                super(itemView);
                phraseButton = itemView.findViewById(R.id.phrase_button);
            }
        }
    }
}