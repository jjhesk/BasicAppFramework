package com.hkm.layout.Module;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

/**
 * Created by hesk on 17/9/15.
 */
public abstract class easyAdapter<T, BINDHOLDER extends UltimateRecyclerviewViewHolder>
        extends UltimateViewAdapter {
    private List<T> currentlistsource;

    /**
     * dynamic object to start
     *
     * @param list the list source
     */
    public easyAdapter(List<T> list) {
        currentlistsource = list;
    }

    protected abstract BINDHOLDER newViewHolder(View view);

    /**
     * the layout id for the normal data
     *
     * @return the ID
     */
    protected abstract int getNormalLayoutResId();

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(getNormalLayoutResId(), parent, false);
        return newViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //if (position == getAdapterItemCount()) return;
        if (getItemViewType(position) == VIEW_TYPES.NORMAL) {
            int bp = customHeaderView != null ? position - 1 : position;
            withBindHolder((BINDHOLDER) holder, currentlistsource.get(bp), position);
        } else if (getItemViewType(position) == VIEW_TYPES.HEADER) {
            onBindHeaderViewHolder(holder, position);
        } else {
        }
    }

    protected abstract void withBindHolder(final BINDHOLDER holder, final T data, final int position);

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new UltimateRecyclerviewViewHolder(viewGroup);
    }

    public final void insertLast(T object) {
        insertLastInternal(currentlistsource, object);
    }

    @Deprecated
    public void removeAll() {
        //making sure it is linear starting from zero
        try {
            while (currentlistsource.size() > 0) {
                currentlistsource.remove(0);
                notifyItemRemoved(currentlistsource.size() - 1);
                Log.d("removeItem", "success @" + currentlistsource.size());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("removeItem", e.getMessage());
        }
    }

    public void removeAll(int grid_padding_base) {
        try {
            removeAllInternal(currentlistsource);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("removeItem", e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.d("removeItem", e.getMessage());
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new UltimateRecyclerviewViewHolder(view);
    }

    @Override
    public int getAdapterItemCount() {
        return currentlistsource.size();
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }
}