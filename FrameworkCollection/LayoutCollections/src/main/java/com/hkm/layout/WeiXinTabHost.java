package com.hkm.layout;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hkm.layout.Menu.Bubble;
import com.hkm.layout.Menu.TabIconView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hesk on 17/12/15.
 */
public class WeiXinTabHost extends LinearLayout {

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;
    private ReTouchListener mReTouch;
    private ArgbEvaluator mColorEvaluator;
    private int mTextNormalColor, mTextSelectedColor;
    private boolean tab_click_animation = false, enable_rebuild_on_last_item = false;
    private int mLastPosition;
    private int mSelectedPosition;
    private float mSelectionOffset;
    private View[] mIconLayouts;
    private TabIconView mViewBubble;
    private List<TabIconView.Icon> mCustomlist = new ArrayList<>();
    /**
     * everything is based on this length
     */
    private String mTitles[] = {"News Feed", "Categories", "Videos", "Settings"};

    private int mIconRes[][] = {
            {R.drawable.icon_main_normal_grid, R.drawable.icon_main_normal_grid},
            {R.drawable.icon_main_normal_grid, R.drawable.icon_main_normal_grid},
            {R.drawable.icon_main_normal_grid, R.drawable.icon_main_normal_grid},
            {R.drawable.icon_main_normal_grid, R.drawable.icon_main_normal_grid},
            {R.drawable.icon_main_normal_grid, R.drawable.icon_main_normal_grid}
    };

    public WeiXinTabHost(Context context) {
        this(context, null);
    }

    public WeiXinTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeiXinTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mColorEvaluator = new ArgbEvaluator();
        mReTouch = new ReTouchListener() {


            @Override
            public void againTouch(int position) {

            }

            @Override
            public void firstTouch(int position) {

            }
        };
        mTextNormalColor = ContextCompat.getColor(context, R.color.theme_inactive);
        mTextSelectedColor = ContextCompat.getColor(context, R.color.theme_active);
        mSelectedPosition = 0;
        mLastPosition = 0;
    }

    public void addCustomButtons(List<TabIconView.Icon> list) {
        mCustomlist = list;
    }

    public void enable_last_touch_active() {
        enable_rebuild_on_last_item = true;
    }

    protected int getAvailableBadgetLocation() {
        return 3;
    }


    public WeiXinTabHost setRetouchListener(ReTouchListener ret) {
        this.mReTouch = ret;
        return this;
    }

    public void setNotificationNumDisplay(int b) {
        if (mViewBubble != null) {
            mViewBubble.updateBadget(b);
        }
    }

    public WeiXinTabHost setInitialPosition(final int e) {
        if (e >= 0 && e < mTitles.length) {
            mSelectedPosition = e;
            mLastPosition = e;
        }
        return this;
    }

    public void build() {
        if (getChildCount() > 0)
            removeAllViews();
        populateTabLayout();
    }

    private void populateTabLayout() {
        if (mCustomlist.size() > 0) {
            custom_render();
        } else {
            default_render();
        }
    }

    private void default_render() {
        final OnClickListener tabClickListener = new TabClickListener();
        mIconLayouts = new View[mTitles.length];
        for (int i = 0; i < mTitles.length; i++) {
            final View tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_mainbottom, this, false);
            mIconLayouts[i] = tabView;
            TabIconView iconView = (TabIconView) tabView.findViewById(R.id.lylib_main_bottom_tab_icon);
            if (i == getAvailableBadgetLocation()) {
                iconView.addBubble(new Bubble(getContext()));
                mViewBubble = iconView;
            }

            iconView.init(mIconRes[i][0], mIconRes[i][1]);
            TextView textView = (TextView) tabView.findViewById(R.id.lylib_main_bottom_tab_text);
            textView.setText(mTitles[i]);
            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }
            LayoutParams lp = (LayoutParams) tabView.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
            lp.gravity = Gravity.CENTER_VERTICAL;
            tabView.setOnClickListener(tabClickListener);
            addView(tabView);
            if (i == mSelectedPosition) {
                iconView.transformPage(0);
                tabView.setSelected(true);
                textView.setTextColor(mTextSelectedColor);
                mReTouch.firstTouch(mSelectedPosition);
            }
        }
    }

    private void custom_render() {
        final OnClickListener tabClickListener = new TabClickListener();
        mIconLayouts = new View[mCustomlist.size()];
        Iterator<TabIconView.Icon> it = mCustomlist.iterator();
        int n = 0;
        while (it.hasNext()) {
            TabIconView.Icon ico = it.next();
            //===== starts here
            final View tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_mainbottom, this, false);
            mIconLayouts[n] = tabView;
            TabIconView iconView = (TabIconView) tabView.findViewById(R.id.lylib_main_bottom_tab_icon);
            if (n == getAvailableBadgetLocation()) {
                iconView.addBubble(new Bubble(getContext()));
                mViewBubble = iconView;
            }
            if (ico.isScalable()) {
                iconView.initVector(ico.normal, ico.active);
            } else
                iconView.init(ico.normal, ico.active);

            TextView textView = (TextView) tabView.findViewById(R.id.lylib_main_bottom_tab_text);
            textView.setText(ico.title);
            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }
            LayoutParams lp = (LayoutParams) tabView.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
            lp.gravity = Gravity.CENTER_VERTICAL;
            tabView.setOnClickListener(tabClickListener);
            addView(tabView);
            if (n == mSelectedPosition) {
                iconView.transformPage(0);
                tabView.setSelected(true);
                textView.setTextColor(mTextSelectedColor);
                mReTouch.firstTouch(mSelectedPosition);
            }

            //===== ends here
            n++;
        }

        if (enable_rebuild_on_last_item) {
            enable_rebuild_on_last_item = false;
            mSelectedPosition = mLastPosition = n;
            View mView = mIconLayouts[mSelectedPosition];
            TabIconView vie = (TabIconView) mView.findViewById(R.id.lylib_main_bottom_tab_text);
            mReTouch.firstTouch(mSelectedPosition);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int childCount = getChildCount();
        if (childCount > 0) {
            if (mSelectedPosition < childCount) {
                if (mLastPosition >= childCount) {
                    mLastPosition = childCount - 1;
                }
                View selectedTab = getChildAt(mLastPosition);
                View nextTab = getChildAt(mSelectedPosition);

                View selectedIconView = ((RelativeLayout) selectedTab).getChildAt(0);
                View nextIconView = ((RelativeLayout) nextTab).getChildAt(0);

                View selectedTextView = ((RelativeLayout) selectedTab).getChildAt(1);
                View nextTextView = ((RelativeLayout) nextTab).getChildAt(1);


                //draw icon alpha
                if (selectedIconView instanceof TabIconView && nextIconView instanceof TabIconView) {
                    ((TabIconView) selectedIconView).transformPage(1f);
                    ((TabIconView) nextIconView).transformPage(0f);
                }

                //draw text color
                Integer selectedColor = (Integer) mColorEvaluator.evaluate(1f,
                        mTextSelectedColor,
                        mTextNormalColor);
                Integer nextColor = (Integer) mColorEvaluator.evaluate(0f,
                        mTextSelectedColor,
                        mTextNormalColor);

                if (selectedTextView instanceof TextView && nextTextView instanceof TextView) {
                    ((TextView) selectedTextView).setTextColor(selectedColor);
                    ((TextView) nextTextView).setTextColor(nextColor);
                }

                //if(selectedIconView.)
                selectedIconView.setSelected(false);
                nextIconView.setSelected(true);

            }
        }
    }

    public void apiSelectPos(int n, boolean withReTouchCallback) {
        if (getChildCount() > n && n >= 0) {
            mLastPosition = mSelectedPosition;
            mSelectedPosition = n;
            invalidate();
            if (withReTouchCallback) {
                mReTouch.firstTouch(n);
            }
        }
    }


    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < getChildCount(); i++) {
                if (v == getChildAt(i)) {
                    if (i == mSelectedPosition) {
                        mReTouch.againTouch(i);
                    } else {
                        mLastPosition = mSelectedPosition;
                        mSelectedPosition = i;
                        invalidate();
                        mReTouch.firstTouch(i);
                    }
                    return;
                }
            }
        }
    }

    public interface ReTouchListener {
        /**
         * the is the action trigger when the item is selected again on the same spot
         *
         * @param position the position of the tab
         */
        void againTouch(final int position);

        void firstTouch(final int position);
    }
}
