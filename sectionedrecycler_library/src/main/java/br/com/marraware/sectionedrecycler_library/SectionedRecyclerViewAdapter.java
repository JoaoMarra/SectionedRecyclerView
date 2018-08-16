package br.com.marraware.sectionedrecycler_library;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import br.marraware.sectionedrecycler_library.R;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class SectionedRecyclerViewAdapter extends RecyclerView.Adapter implements HeaderItemDecoration.HeaderItemDecorationListener {

    private RecyclerView recyclerView;
    private List<RecyclerViewAdapterSection> sectionList;
    private int CACHED_ITEM_COUNT = 0;

    private Handler handler = new Handler();
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    private boolean stickHeader = true;
    private HeaderItemDecoration decoration;

    public SectionedRecyclerViewAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        decoration = new HeaderItemDecoration(this);
        recyclerView.addItemDecoration(decoration);
    }

    public boolean isStickHeader() {
        return stickHeader;
    }

    public void setStickHeader(boolean stickHeader) {
        this.stickHeader = stickHeader;
        handler.post(updateRunnable);
    }

    public void addSection(RecyclerViewAdapterSection section) {
        if(sectionList == null) {
            sectionList = new ArrayList<>();
        }
        sectionList.add(section);
        dataSetChanged();
    }

    public void removeSection(RecyclerViewAdapterSection section) {
        if(sectionList != null) {
            sectionList.remove(section);
        }
        dataSetChanged();
    }

    public void dataSetChanged() {
        recalculate();
        handler.post(updateRunnable);
    }

    private void recalculate() {
        decoration.clear();
        CACHED_ITEM_COUNT = 0;
        if(sectionList != null) {
            for(int i=0; i < sectionList.size(); i++) {
                sectionList.get(i).recalculate(i);
                CACHED_ITEM_COUNT += sectionList.get(i).abstractItemCount();
            }
        }
    }

    private int getSectionForPosition(int position) {
        int i = 0;
        int posCount = 0;
        if(sectionList != null) {
            while (position >= posCount+sectionList.get(i).abstractItemCount()) {
                posCount += sectionList.get(i).abstractItemCount();
                i++;
            }
            return i;
        }
        return i;
    }

    private int getRealPosition(int position) {
        int posCount = 0;
        int i = 0;
        if(sectionList != null) {
            while (position >= posCount+sectionList.get(i).abstractItemCount()) {
                posCount += sectionList.get(i).abstractItemCount();
                i++;
            }
        }
        return position-posCount;
    }

    @Override
    public final int getItemViewType(int position) {
        return getSectionForPosition(position);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if(sectionList != null) {
            holder = sectionList.get(viewType).onCreateViewHolder();
        }
        return holder;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(sectionList != null) {
            int section = getSectionForPosition(position);
            int realPosition = getRealPosition(position);
            holder.itemView.setTag(R.id.ROW_SECTION, section);
            holder.itemView.setTag(R.id.ROW_POSITION, realPosition);
            sectionList.get(section).onBindViewHolder(holder, realPosition);
        }
    }

    @Override
    public final int getItemCount() {
        return CACHED_ITEM_COUNT;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        if(sectionList != null)
            return sectionList.get(headerPosition).getHeaderLayout();
        return 0;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        if(sectionList != null)
            sectionList.get(headerPosition).onBindHeaderView(header);
    }

    @Override
    public boolean hasHeader(int section) {
        if(sectionList != null)
            return sectionList.get(section).hasHeader();
        return false;
    }

    @Override
    public boolean isLastOfSection(int position, int section) {
        if(sectionList != null)
            return (position == sectionList.get(section).abstractItemCount()-1);
        return false;
    }

}
