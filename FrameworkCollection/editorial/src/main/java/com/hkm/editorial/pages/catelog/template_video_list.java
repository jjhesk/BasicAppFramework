package com.hkm.editorial.pages.catelog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.hkm.disqus.DisqusClient;
import com.hkm.editorial.Dialog.ErrorMessage;
import com.hkm.editorial.adInterstitual.ListAd;
import com.hkm.editorial.life.Config;
import com.hkm.editorial.life.EBus;
import com.hkm.editorial.life.HBUtil;
import com.hkm.editorial.life.LifeCycleApp;
import com.hkm.editorial.pages.adapters.LoadMoreGridLayoutManager;
import com.hkm.editorial.pages.articlePage.comment_count_search;
import com.hkm.editorial.pages.featureList.basicfeed;
import com.hkm.editorial.pages.featureList.featureListFragment;
import com.hkm.editorial.pages.featureList.patchHyprbid;
import com.hkm.editorial.pages.featureList.videoListFragment;
import com.hypebeast.sdk.Util.Connectivity;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hbeditorial.ArticleData;
import com.hypebeast.sdk.api.model.hbeditorial.PostsObject;
import com.hypebeast.sdk.api.model.hbeditorial.ResponsePostFromSearch;
import com.hypebeast.sdk.api.model.hbeditorial.ResponsePostW;
import com.hypebeast.sdk.api.resources.hypebeast.feedhost;
import com.hypebeast.sdk.clients.HBEditorialClient;
import com.neopixl.pixlui.components.textview.TextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;

import java.text.ParseException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 7/1/16.
 */
public class template_video_list extends videoListFragment<ArticleData> {

    protected feedhost interfacerequest;
    protected LifeCycleApp app;

    protected final Callback<ResponsePostW> post_response = new Callback<ResponsePostW>() {
        @Override
        public void success(ResponsePostW list, Response response) {
            display_list(list.postList);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d("tager", error.getMessage());
            com.hkm.advancedtoolbar.Util.ErrorMessage.alert(error.getMessage(), getChildFragmentManager());
        }
    };



    protected final Callback<ResponsePostFromSearch> search_response = new Callback<ResponsePostFromSearch>() {
        @Override
        public void success(ResponsePostFromSearch responsePostFromSearch, Response response) {
            display_list(responsePostFromSearch.posts);
        }

        @Override
        public void failure(RetrofitError error) {
            com.hkm.advancedtoolbar.Util.ErrorMessage.alert(error.getMessage(), getChildFragmentManager(), new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            });
        }
    };


    protected void display_list(PostsObject list) {
        try {
            if (isNowInitial()) {
                doneInitialLoading();
                createHypbridAdapter(list.getArticles());
                getSwitcherBi().setMaxPages(list.getPages());
                afterInitiateHyprbidAdapter();
                if (list.getArticles().size() == 0 && requestType == SEARCH) {
                    EBus.display_no_result c = new EBus.display_no_result(slugtag);
                    EBus.getInstance().post(c);
                }
            } else {
                getSwitcherBi().setMaxPages(list.getPages());
                getSwitcherBi().load_more_data(list.getArticles());
            }
        } catch (Exception e) {
            if (getChildFragmentManager() != null && getActivity() != null) {
                ErrorMessage.alert(e.getMessage(), getChildFragmentManager());
            }
        }
    }

    public static template_video_list B(final Bundle b) {
        final template_video_list t = new template_video_list();
        t.setArguments(b);
        return t;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (LifeCycleApp) activity.getApplication();
        interfacerequest = HBEditorialClient.getInstance(activity).createFeedInterface();
    }

    @Override
    protected boolean is_list_with_ad_enabled() {
        return true;
    }

    protected void init_comment_count_search(final ArticleData data, final TextView textview, final String time) {
        try {
            DisqusClient r = DisqusClient.getInstance(getActivity(), ((LifeCycleApp) getActivity().
                    getApplication()).getConfiguration());

            r.createThreads().listPostByIDAsync(
                    data._embedded.disqus_identifier,
                    "hypebeast",
                    new comment_count_search(textview, time));
        } catch (com.hkm.disqus.api.exception.ApiException e) {

        }
    }

    @Override
    protected void binddata(videoListFragment.binder holder, final ArticleData data, int position) {
        try {
            final boolean f = Connectivity.isConnectedFast(getActivity());
            final String i = f ? data._links.getThumbnail() : data._links.getThumbnail();
            picasso
                    .load(i)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .into(holder.big_image_single);
            init_comment_count_search(data, holder.comment_counts, data.getMoment());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * trigger point to the new page.
         */
        holder.click_detection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  onClickItem(data.id);
                onClickItem(data._links.getSelf());
            }
        });
        holder.tvtitle.setText(data.title);
    }

    protected void createHypbridAdapter(final @NonNull List<ArticleData> source) {
        final videoListFragment.cateadapter noad = new videoListFragment.cateadapter(source);
        final videoListFragment.admobadp lolpp = new videoListFragment.admobadp(ListAd.newAdView(getActivity()), false, ListAd.LIST_AD_INTERVAL, source, lsi);
        if (source.size() < Config.setting.single_page_items) {
            listview_layout.disableLoadmore();
        }
        sw = new patchHyprbid(listview_layout, noad, lolpp);
    }

    /**
     * step 2:
     * this is the call for the loading the data stream externally
     */
    @Override
    protected void loadDataInitial() {
        try {
            onLoadMore(0, 1, Config.setting.single_page_items);
        } catch (ApiException e) {
            ErrorMessage.alert(e.getMessage(), getChildFragmentManager());
        }
    }

    @Override
    protected void onLoadMore(int viewType, int currentpage, int ppg) throws ApiException {
        if (requestType == featureListFragment.LATEST) {
            interfacerequest.the_recent_page(currentpage, post_response);
        } else if (requestType == featureListFragment.CATE) {
            interfacerequest.cate_list(currentpage, slugtag, post_response);
        } else if (requestType == featureListFragment.SEARCH) {
            //interfacerequest.search(slugtag, currentpage, search_response);
            triggerSearch(getArguments().getString(SEARCH_WORD));
        } else if (requestType == basicfeed.GENERAL) {
            HBEditorialClient.getInstance(getActivity()).createAPIUniversal(adapter_url).atPage(currentpage, post_response);
        }
    }

    public void triggerSearch(final String searchWord) {
        try {
            requestType = featureListFragment.SEARCH;
            slugtag = searchWord;
            setRefreshInitial();
            interfacerequest.search(searchWord, 1, search_response);
        } catch (Exception e) {
            ErrorMessage.alert(e.getMessage(), getChildFragmentManager());
        }
    }

    @Subscribe
    public void eventRefresh(EBus.refresh re) {
        if (re.isEventRequestStarted()) {
            setRefreshInitial();
        }
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        EBus.getInstance().register(this);
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
        EBus.getInstance().unregister(this);
    }


}
