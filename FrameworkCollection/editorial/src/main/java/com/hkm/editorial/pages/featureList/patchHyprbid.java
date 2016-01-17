package com.hkm.editorial.pages.featureList;

import android.widget.RelativeLayout;

import com.hkm.editorial.adInterstitual.ListAd;
import com.marshalchen.ultimaterecyclerview.AdmobAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.quickAdapter.BiAdAdapterSwitcher;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.marshalchen.ultimaterecyclerview.quickAdapter.simpleAdmobAdapter;

/**
 * Created by hesk on 12/8/15.
 */
public class patchHyprbid extends BiAdAdapterSwitcher {
    protected externalcb callb;
    protected int current_items;

    public interface externalcb {
        void onrefresh();
    }

    protected Runnable new_refresh_default = new Runnable() {
        public void run() {
            if (loading_more != null) {
                boolean ok = loading_more.request_start(1, 0, 0, patchHyprbid.this, true);
                if (ok) {
                    page_now = 1;
                    max_pages = 1;
                    if (callb != null) callb.onrefresh();
                } else if (auto_disable_loadmore) {
                    listview.disableLoadmore();
                }
            }

            listview.setRefreshing(false);
        }
    };

    protected void emptyviewcontroltemp() {
        current_items = with_the_ad ? withad.getItemCount() : noad.getItemCount();
        if (current_items > 0) {
            listview.hideEmptyView();
        } else {
            listview.showEmptyView();
        }
    }

    public patchHyprbid(UltimateRecyclerView view, easyRegularAdapter adapter_without_ad, simpleAdmobAdapter adapter_with_ad) {
        super(view, adapter_without_ad, adapter_with_ad);
        page_now = 2;
        max_pages = 1000;
        emptyviewcontroltemp();
        setCustomOnFresh(new_refresh_default);
    }



    public patchHyprbid setExternalCallback(externalcb cb) {
        this.callb = cb;
        return this;
    }

    public UltimateViewAdapter getAdapter() {
        if (with_the_ad) return withad;
        else return noad;
    }

}
