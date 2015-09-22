package com.ichapter.testlistviewrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author: Finch
 * @Description: ListView头部刷新提示
 * @Date: 2015/9/15 16:01
 */
public class ListViewHeader extends LinearLayout {

    private LinearLayout layout_header_hint;

    private ImageView mArrowImageView;
    private TextView mHintTextView;
    private ProgressBar mProgressBar;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    private int mState = STATE_NORMAL;// 初始状态

    public ListViewHeader(Context context) {
        super(context);
        initHeaderView(context);
    }

    public ListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView(context);
    }

    public ListViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
    }

    /**
     * 添加ListView头部刷新布局
     *
     * @param context
     */
    private void initHeaderView(Context context) {
        //初始化的时候头部的默认高度应该为0
        LinearLayout.LayoutParams layout_header = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        //刷新时间
        layout_header_hint = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_header_item, null);
        addView(layout_header_hint, layout_header);
        setGravity(Gravity.BOTTOM);

        //初始化头部控件
        mArrowImageView = (ImageView) findViewById(R.id.iv_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.tv_header_refresh_hint);
        mProgressBar = (ProgressBar) findViewById(R.id.listView_header_progressBar);

        //初始刷新加载动画
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    // 设置状态
    public void setState(int state) {
        if (state == mState)
            return;

        if (state == STATE_REFRESHING) { // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);// 不显示图片
            mProgressBar.setVisibility(View.VISIBLE);// 显示进度条
        } else { // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {// 当状态时准备的时候，显示动画
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {// 当状态显示进度条的时候，清除动画
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText("下拉刷新");// 文字提示：下拉刷新
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText("松开刷新数据");// 松开刷新数据
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText("loading...");
                break;
            default:
        }
        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout_header_hint
                .getLayoutParams();
        lp.height = height;
        layout_header_hint.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return layout_header_hint.getHeight();
    }
}
