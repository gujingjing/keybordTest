package com.example.ibesteeth.git_keybordtest;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ibesteeth.git_keybordtest.contract.TweetPublishContract;
import com.example.ibesteeth.git_keybordtest.emoji.EmojiKeyboardFragment;
import com.example.ibesteeth.git_keybordtest.emoji.Emojicon;
import com.example.ibesteeth.git_keybordtest.emoji.InputHelper;
import com.example.ibesteeth.git_keybordtest.emoji.OnEmojiClickListener;

import net.qiujuer.genius.ui.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.edit_content)
    EditText mEditContent;
    @Bind(R.id.iv_picture)
    ImageView ivPicture;
    @Bind(R.id.iv_mention)
    ImageView ivMention;
    @Bind(R.id.iv_tag)
    ImageView ivTag;
    @Bind(R.id.iv_emoji)
    ImageView ivEmoji;
    @Bind(R.id.lay_emoji_keyboard)
    FrameLayout layEmojiKeyboard;

    public static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REQUEST_CODE = 100;
    private static final String TEXT_TAG = "#输入软件名#";

    private final EmojiKeyboardFragment mEmojiKeyboard = new EmojiKeyboardFragment();
    private TweetPublishContract.Operator mOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        this.mOperator = (TweetPublishContract.Operator) this;
//        this.mOperator.setDataView(this);

        initWidget();

    }

    public void initWidget() {
    // EmojiKeyboardFragment

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.lay_emoji_keyboard, mEmojiKeyboard);
        trans.commit();


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
                Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
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

}
