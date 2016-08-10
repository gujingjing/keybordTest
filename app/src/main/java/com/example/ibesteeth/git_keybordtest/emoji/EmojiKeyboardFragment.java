package com.example.ibesteeth.git_keybordtest.emoji;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ibesteeth.git_keybordtest.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class EmojiKeyboardFragment extends Fragment implements
        SoftKeyboardStateHelper.SoftKeyboardStateListener {

    private LinearLayout mEmojiContent;
    private RadioGroup mEmojiBottom;
    private View[] mEmojiTabs;
    private ViewPager mEmojiPager;

    private EmojiPagerAdapter adapter;

    private LinearLayout mRootView;
    private OnEmojiClickListener listener;
    public static int EMOJI_TAB_CONTENT;

    private boolean isDelegate;

    private SoftKeyboardStateHelper mKeyboardHelper;
    private PopupWindow popupWindow;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            hideSoftKeyboard();
            popupWindow.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEmojiKeyBoard();
                }
            }, 280);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != mRootView) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = (LinearLayout) inflater.inflate(R.layout.frag_keyboard, container, false);
            initWidget(mRootView);
        }
        return mRootView;
    }

    private void initWidget(View rootView) {
        // bottom
        mEmojiBottom = (RadioGroup) rootView.findViewById(R.id.emoji_bottom);
        //mEmojiBottom.setVisibility(View.VISIBLE);
        EMOJI_TAB_CONTENT = mEmojiBottom.getChildCount() - 1; // 减一是因为有一个删除按钮
        mEmojiTabs = new View[EMOJI_TAB_CONTENT];
        if (EMOJI_TAB_CONTENT <= 1) { // 只有一个分类的时候就不显示了
            mEmojiBottom.setVisibility(View.GONE);
        }
        for (int i = 0; i < EMOJI_TAB_CONTENT; i++) {
            mEmojiTabs[i] = mEmojiBottom.getChildAt(i);
            mEmojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }
        mEmojiBottom.findViewById(R.id.emoji_bottom_del).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDeleteButtonClick(v);
                        }
                    }
                });

        // content必须放在bottom下面初始化
        mEmojiContent = (LinearLayout) rootView.findViewById(R.id.emoji_content);
        mEmojiPager = (ViewPager) mEmojiContent.findViewById(R.id.emoji_pager);
        adapter = new EmojiPagerAdapter(getChildFragmentManager(), EMOJI_TAB_CONTENT, listener);
        mEmojiPager.setAdapter(adapter);
        //mEmojiContent.setVisibility(View.VISIBLE);

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow().getDecorView());
//        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity(),getActivity().getWindow().getDecorView());

        mKeyboardHelper.addSoftKeyboardStateListener(this);
    }

    /**
     * 底部栏点击事件监听器
     *
     * @param index
     * @return
     */
    private OnClickListener getBottomBarClickListener(final int index) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiPager.setCurrentItem(index);
            }
        };
    }

    public void setOnEmojiClickListener(OnEmojiClickListener l) {
        this.listener = l;
    }

    public void hideAllKeyBoard() {
        hideEmojiKeyBoard();
        hideSoftKeyboard();
    }

    public boolean isShow() {
        return mEmojiContent.getVisibility() == View.VISIBLE;
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        if (!isDelegate) {
            mEmojiContent.setVisibility(View.GONE);
            mEmojiBottom.setVisibility(View.GONE);
        }
    }

    /**
     * 显示Emoji并隐藏软键盘
     */
    public void showEmojiKeyBoard() {
        mEmojiContent.setVisibility(View.VISIBLE);
        if (EMOJI_TAB_CONTENT > 1) {
            mEmojiBottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEmojiBottom.getWindowToken(), 0);
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard(EditText et) {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(et,
                InputMethodManager.SHOW_FORCED);
    }

    /**
     * 当软键盘显示时回调
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        if (!isDelegate) {
            mEmojiContent.setVisibility(View.GONE);
            mEmojiBottom.setVisibility(View.GONE);
        }
        initPop(keyboardHeightInPx);
    }

    @Override
    public void onSoftKeyboardClosed() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }

    public void setDelegate(boolean delegate) {
        isDelegate = delegate;
    }

    public void initPop(int keyboardHeightInPx) {

//        View view = View.inflate(getActivity(), R.layout.popshow, null);
        View view = View.inflate(getActivity(), R.layout.popshow_test, null);

        ImageView iv_emoji = (ImageView) view.findViewById(R.id.iv_emojis);

        iv_emoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                hideEmojiKeyBoard();
//                showEmojiKeyBoard();
                handler.sendEmptyMessage(0);
//                popupWindow.dismiss();
            }
        });

//        popupWindow = new PopupWindow(getActivity());
//        popupWindow = new PopupWindow(view, 300,500);
        int height=dip2px(getActivity(),getActivity().getResources().getDimension(R.dimen.size_44));
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                height + keyboardHeightInPx, true);

//        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
//                (int) getActivity().getResources().getDimension(R.dimen.size_44) + keyboardHeightInPx, true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable()); // To avoid borders & overdraw

        popupWindow.setContentView(view);


        // 设置弹出窗体需要软键盘
//        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        // 再设置模式，和Activity的一样，覆盖，调整大小。
//        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


//        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        size_44
//        int height = dip2px(getActivity(), (int) getActivity().getResources().getDimension(R.dimen.size_44));

//        popupWindow.setHeight(height);


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        showAtBottom(mRootView);
    }

    private void showAtBottom(View view) {
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * show comment popupwindow（弹出评论的popupWindow）
     */
    public void showPopupCommnet() {// pe表示是评论还是举报1.代表评论。2.代表举报
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.popshow, null);
        ImageView iv_emoji = (ImageView) view.findViewById(R.id.iv_emoji);

        iv_emoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                hintKbTwo();
                showEmojiKeyBoard();
            }
        });
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setFocusable(true);
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置弹出窗体需要软键盘
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        // 再设置模式，和Activity的一样，覆盖，调整大小。
        popupWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//        ColorDrawable cd = new ColorDrawable(0x000000);
//        popupWindow.setBackgroundDrawable(cd);
        // 设置popWindow的显示和消失动画
//        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
//        popupWindow.update();
        popupInputMethodWindow();
    }

    private void popupInputMethodWindow() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }.start();
        //
    }
}
