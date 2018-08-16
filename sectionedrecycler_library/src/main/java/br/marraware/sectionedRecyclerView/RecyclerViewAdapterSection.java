package br.marraware.sectionedRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public abstract class RecyclerViewAdapterSection<R extends RecyclerView.ViewHolder> {

    private int CACHED_ITEM_COUNT = -1;
    private int CACHED_SECTION_POS = -1;

    public final void recalculate(int sectionPos) {
        CACHED_SECTION_POS = sectionPos;
        CACHED_ITEM_COUNT = getItemCount();
    }

    public final int abstractItemCount() {
        return CACHED_ITEM_COUNT;
    }

    public abstract R onCreateViewHolder();
    public abstract void onBindViewHolder(R viewHolder, int position);
    public abstract int getItemCount();

    public boolean hasHeader() {
        return false;
    }
    public int getHeaderLayout() {
        return -1;
    }
    public void onBindHeaderView(View header) {

    }

    public final int getSectionPos() {
        return CACHED_SECTION_POS;
    }
}
