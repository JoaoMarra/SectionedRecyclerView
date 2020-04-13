package br.marraware.sectionedRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public abstract class RecyclerViewAdapterSection<ROW extends RecyclerView.ViewHolder> {

    private int CACHED_ITEM_COUNT = -1;
    private int CACHED_SECTION_POS = -1;
    private SectionedRecyclerViewAdapter adapter;

    private View.OnClickListener itemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.ROW_POSITION);
            onItemClick(position);
        }
    };

    public final void recalculate(int sectionPos) {
        CACHED_SECTION_POS = sectionPos;
        CACHED_ITEM_COUNT = getItemCount();
    }

    public final int abstractItemCount() {
        return CACHED_ITEM_COUNT;
    }

    public ROW abstractOnCreateViewHolder() {
        ROW holder = onCreateViewHolder();
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(itemOnClickListener);
        return holder;
    }

    /*
    SHOULD ALWAYS BE POSITIVE!
     */
    public int getItemViewType(int position) {
        return -1;
    }
    public abstract ROW onCreateViewHolder();
    public abstract void onBindViewHolder(ROW viewHolder, int viewType);
    public abstract int getItemCount();

    public View abstractGetHeaderView(RecyclerView parent) {
        View header = LayoutInflater.from(parent.getContext()).inflate(getHeaderLayout(), parent, false);
        return header;
    }

    public boolean hasHeader() {
        return false;
    }
    public boolean stickHeader() {
        return true;
    }
    public int getHeaderLayout() {
        return 0;
    }
    public void onBindHeaderView(View header) {}

    public final int getSectionPos() {
        return CACHED_SECTION_POS;
    }

    public void onItemClick(int position) {}
    public void onHeaderClick() {}

    public final void notifyDataSetChanged() {
        if(adapter != null) {
            adapter.dataSetChanged();
        } else {
            recalculate(getSectionPos());
        }
    }

    public final SectionedRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public final void setAdapter(SectionedRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }
}
