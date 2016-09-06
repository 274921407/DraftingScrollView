package com.jixin.studyproject.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by jixin on 2016/9/6
 * <p/>
 * 可拖拽的scrollview
 * <p/>
 * 1 继承ScrollView 添加构造函数
 * 2 ScrollView 只允许有一个子控件，拖拽的部分实际上是ScrollView的第一个孩子view0
 * 3 复写ScrollView的onFinishInflate（）函数：当ScrollView及其所有子控件布局加载完毕
 * 后回调，此时可以获取拖拽的对象
 * <p/>
 * 4拖拽滑动原理：
 * 4.1 当滑动到顶部或者顶部时，实现Y轴上的拖拽效果
 * 4.2 拖拽过程实际上是一个按下、移动，抬起的过程 需要监听触摸事件
 * 4.3 移动过程中不断的将view0重新设置显示位置
 * 4.4 抬起后将view0恢复到拖拽前的顶部或者底部位置，恢复过程增加动画，增加用户体验
 */
public class JXDraftingScrollView extends ScrollView {


    private View DraftingView;

    //保存第一次或者前一次触发点的Y坐标值
    private int mLastY;

    //保存底部或者顶部时的四个坐标，用以拖拽后恢复到顶部或者底部位置
    private Rect mRect = new Rect();

    //动画时间
    private final int ANIMATIONTIME=200;



    public JXDraftingScrollView(Context context) {
        super(context);
    }

    public JXDraftingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JXDraftingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 获取拖拽对象
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            DraftingView = getChildAt(0);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //拖拽对象不为空才能着手处理
        if (DraftingView != null) {
            onMyTouchEvent(ev);

        }
        return super.onTouchEvent(ev);
    }


    /**
     *  核心处理
     * @param ev
     */
    private void onMyTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int mCurrentY = (int) ev.getY();
        switch (action) {
            //按下事件
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                break;
            //滑动事件
            case MotionEvent.ACTION_MOVE:
                //Y轴上的偏移量
                int offsetY = (mCurrentY - mLastY) ;
                //是否到顶部或者底部
                if (isTopOrBottom()) {
                    if (mRect.isEmpty()) {
                        mRect.set(DraftingView.getLeft(), DraftingView.getTop(), DraftingView.getRight(),
                                DraftingView.getBottom());
                        return;
                    }
                    //重新定位
                    DraftingView.layout(DraftingView.getLeft(), DraftingView.getTop() + offsetY, DraftingView
                            .getRight(), DraftingView.getBottom() + offsetY);
                }
                //重新设置纵坐标
                mLastY = mCurrentY;
                break;
            case MotionEvent.ACTION_UP:
                if (isTopOrBottom()) {
                    ScrollDefault();
                }
                break;


        }
    }


    /**
     * 是否滑动到顶部或者底部
     * 前者为顶部
     * @return
     */
    private boolean isTopOrBottom() {
        return getScrollY() == 0 || DraftingView.getHeight() - getHeight() == getScrollY();

    }


    /**
     * 反弹回顶部或底部默认位置
     */

    private void ScrollDefault() {
        //用一个动画去实现Y轴上的平移，避免瞬间恢复到默认位置 体验不好
        TranslateAnimation ta = new TranslateAnimation(0, 0, DraftingView.getTop(), mRect.top);
        ta.setDuration(ANIMATIONTIME);
        ta.setFillAfter(true);
        DraftingView.startAnimation(ta);

        //确保恢复到默认位置
        DraftingView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();

    }
}
