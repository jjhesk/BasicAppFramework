package com.hkm.layout.Dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hkm.layout.R;

/**
 * The first time renderer
 * Created by hesk on 18/9/15.
 */
public class BottomSheetDialogFragment extends DialogFragment {
    public static final String MEASUREMENT_HEIGHT = "HEIGHT";
    private ProgressBar proloadingbar;
    private int mHeight;
    private static Fragment insideFragment;
    private View root;
    private onCallBack mCallBack = new onCallBack() {
        @Override
        public void onFragmentRender(Dialog inflated_view, boolean first) {

        }
    };

    @LayoutRes
    protected int getHiddenPanelLayout() {
        return R.layout.template_hidden_panel;
    }

    public interface onCallBack {
        void onFragmentRender(Dialog dialog, boolean isFirstTimeRender);
    }

    public static BottomSheetDialogFragment newInstace(Bundle data) {
        final BottomSheetDialogFragment fragment = new BottomSheetDialogFragment();
        fragment.setArguments(data);
        fragment.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MaterialDialogSheet);
        return fragment;
    }

    public void setRenderCallback(onCallBack mCallBackListen) {
        mCallBack = mCallBackListen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dL = super.onCreateDialog(savedInstanceState);
        dL.setCancelable(false);
        dL.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, getArguments().getInt(MEASUREMENT_HEIGHT, 800));
        dL.getWindow().setGravity(Gravity.BOTTOM);
        dL.setOnShowListener(new DialogInterface.OnShowListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onShow(DialogInterface dialog) {
                onShowExtend(dL);
            }
        });
        return dL;
    }

    protected void onShowExtend(final Dialog dL) {
        mCallBack.onFragmentRender(dL, insideFragment == null);
        dL.setCancelable(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getHiddenPanelLayout(), container);
        ViewTreeObserver vto = root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeight = root.getHeight();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        proloadingbar = (ProgressBar) root.findViewById(R.id.lylib_ui_loading_progress_bottom_up_frame);
        return root;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    protected void hideloading() {
        if (proloadingbar.getVisibility() != View.GONE) {
            proloadingbar.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    proloadingbar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void showloading() {
        proloadingbar.setVisibility(View.VISIBLE);
        proloadingbar.setAlpha(0f);
        proloadingbar.animate().alpha(1f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setFragment(Fragment mfragment) {
        insideFragment = mfragment;
        getChildFragmentManager().beginTransaction()
                .replace(R.id.lylib_bottom_fragment_holder_framelayout, mfragment, "EXTRA_OPTIONS")
                .addToBackStack(null)
                .commit();
        hideloading();
    }


}
