package com.example.ibesteeth.git_keybordtest;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ibesteeth.git_keybordtest.emoji.EmojiKeyboardFragment;
import com.example.ibesteeth.git_keybordtest.emoji.Emojicon;
import com.example.ibesteeth.git_keybordtest.emoji.InputHelper;
import com.example.ibesteeth.git_keybordtest.emoji.OnEmojiClickListener;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：iBesteeth on 2016/8/11 09:36
 * 邮箱：gujingjing@ibesteeth.com
 */

public class TweetPublishFragment extends Fragment {

    @Bind(R.id.edit_content)
    EditText mEditContent;

    public static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REQUEST_CODE = 100;
    private static final String TEXT_TAG = "#输入软件名#";

    private final EmojiKeyboardFragment mEmojiKeyboard = new EmojiKeyboardFragment();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);

        initWidget();

        return view;
    }

    public void initWidget() {
//         EmojiKeyboardFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.lay_emoji_keyboard, mEmojiKeyboard)
                .commit();

        mEmojiKeyboard.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                if(v.getValue()==0){//删除的
                    InputHelper.backspace(mEditContent);
                }else{
                    InputHelper.input2OSC(mEditContent, v);
                }
            }

            @Override
            public void onDeleteButtonClick(View v) {
                InputHelper.backspace(mEditContent);
            }
        });

        // set hide action
//        mLayImages.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mEmojiKeyboard.hideAllKeyBoard();
//                return false;
//            }
//        });

        // add text change listener
        mEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = MAX_TEXT_LENGTH - len;
                Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mEmojiKeyboard.hideSoftKeyboard();
    }

    /**
     * Emoji 表情点击
     *
     * @param v View
     */
    private void handleEmojiClick(View v) {
        if (!mEmojiKeyboard.isShow()) {
            mEmojiKeyboard.hideSoftKeyboard();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEmojiKeyboard.showEmojiKeyBoard();
                }
            }, 280);
        } else {
            mEmojiKeyboard.hideEmojiKeyBoard();
        }
    }

    @OnClick({R.id.iv_emoji,R.id.icon_send})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_emoji:
                handleEmojiClick(v);
                break;
            case R.id.icon_send:
                mEmojiKeyboard.hideEmojiKeyBoard();
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
