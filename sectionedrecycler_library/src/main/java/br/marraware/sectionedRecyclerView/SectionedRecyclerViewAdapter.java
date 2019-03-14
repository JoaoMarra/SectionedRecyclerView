package br.marraware.sectionedRecyclerView;

import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class SectionedRecyclerViewAdapter extends RecyclerView.Adapter implements HeaderItemDecoration.HeaderItemDecorationListener {

    private List<Integer> startOfSection;
    private List<RecyclerViewAdapterSection> sectionList;
    private int CACHED_ITEM_COUNT = 0;

    private int LAST_SECTION_FOR_POSITION = -1;
    private int LAST_SECTION_FOR_POSITION_POSITION = -1;

    private Handler handler = new Handler();
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            LAST_SECTION_FOR_POSITION = -1;
            LAST_SECTION_FOR_POSITION_POSITION = -1;
            notifyDataSetChanged();
        }
    };

    private boolean stickHeader = true;
    private HeaderItemDecoration decoration;
    private RecyclerView.OnItemTouchListener onItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                int overSection = decoration.overSectionOnPosition(motionEvent.getX(), motionEvent.getY());
                if(overSection != -1) {
                    if (overSection < sectionList.size()) {
                        sectionList.get(overSection).onHeaderClick();
                        return true;
                    }
                }
                View view = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (view == null) {
                    int section = decoration.sectionForHeaderOnPosition(motionEvent.getX(), motionEvent.getY());
                    if (section != -1) {
                        if (section < sectionList.size()) {
                            sectionList.get(section).onHeaderClick();
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    };

    private RecyclerView recyclerView;
    private boolean checkForGridLayout;
    private int spanCount = 1;
    private GridLayoutManager.SpanSizeLookup oldSpanSizeLookup;
    private GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int section = getSectionForPosition(position);
            int firstOfSection = firstPositionForSection(section);
            int lastOfSection = lastPositionForSection(section);
            int oldSpanSize = 1;
            int newSpanSize = 1;
            if(oldSpanSizeLookup != null)
                oldSpanSize = oldSpanSizeLookup.getSpanSize(position);
            if(position == lastOfSection) {
                if(position == firstOfSection) {
                    newSpanSize = spanCount;
                } else {
                    int spanIndex = getSpanIndex(position-1, spanCount)+1;
                    if(spanIndex >= spanCount)
                        spanIndex = 0;
                    newSpanSize = spanCount-spanIndex;
                }

            }
            return Math.max(oldSpanSize, newSpanSize);
        }
    };

    public SectionedRecyclerViewAdapter() {
        startOfSection = new ArrayList<>();
        decoration = new HeaderItemDecoration(this);
    }

    public boolean isStickHeader() {
        return stickHeader;
    }

    public boolean isStickHeaderForSection(int section) {
        return stickHeader && sectionList.get(section).stickHeader();
    }

    public void setStickHeader(boolean stickHeader) {
        this.stickHeader = stickHeader;
        handler.post(updateRunnable);
    }

    public void addSection(RecyclerViewAdapterSection section) {
        if(sectionList == null) {
            sectionList = new ArrayList<>();
        }
        int indexOf = sectionList.indexOf(section);
        if(indexOf != -1)
            return;

        sectionList.add(section);
        section.setAdapter(this);
        dataSetChanged();
    }

    public void putSection(RecyclerViewAdapterSection section, int position) {
        if(sectionList == null) {
            sectionList = new ArrayList<>();
        }
        int indexOf = sectionList.indexOf(section);
        if(indexOf != -1)
            return;
        if(position > sectionList.size())
            position = sectionList.size();

        sectionList.add(position, section);
        section.setAdapter(this);
        dataSetChanged();
    }

    public void removeSection(RecyclerViewAdapterSection section) {
        if(sectionList != null) {
            int position = sectionList.indexOf(section);
            if(position != -1) {
                sectionList.remove(position);
                dataSetChanged();
            }
            section.setAdapter(null);
        }
    }

    public RecyclerViewAdapterSection removeSection(int position) {
        if(sectionList != null) {
            if(position < sectionList.size()) {
                RecyclerViewAdapterSection section = sectionList.remove(position);
                section.setAdapter(null);
                dataSetChanged();
                return section;
            }
        }
        return null;
    }

    public void clear() {
        if(sectionList != null) {
            sectionList.clear();
            dataSetChanged();
        }
    }

    public void dataSetChanged() {
        recalculate();
        handler.post(updateRunnable);
    }

    private void recalculate() {
        decoration.clear();
        CACHED_ITEM_COUNT = 0;
        if(sectionList != null) {
            int start = 0;
            startOfSection.clear();
            for(int i=0; i < sectionList.size(); i++) {
                sectionList.get(i).recalculate(i);
                startOfSection.add(start);
                start += sectionList.get(i).abstractItemCount();
                CACHED_ITEM_COUNT += sectionList.get(i).abstractItemCount();
            }
        }
    }

    private int getSectionForPosition(int position) {
        if(LAST_SECTION_FOR_POSITION_POSITION == position) {
            return LAST_SECTION_FOR_POSITION;
        }
        int i = 0;
        if(sectionList != null) {
            for(i = 0;i < startOfSection.size() && position >= startOfSection.get(i); i++);
            if(i >= startOfSection.size())
                i = startOfSection.size()-1;
            if(position < startOfSection.get(i))
                i--;
        }
        LAST_SECTION_FOR_POSITION_POSITION = position;
        LAST_SECTION_FOR_POSITION = i;
        return i;
    }

    private int getRealPosition(int position) {
        int i = getSectionForPosition(position);
        return position-startOfSection.get(i);
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
            holder = sectionList.get(viewType).abstractOnCreateViewHolder();
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
        if(!checkForGridLayout) {
            if(recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                oldSpanSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
                spanCount = gridLayoutManager.getSpanCount();
            }
            checkForGridLayout = true;
        }
        return CACHED_ITEM_COUNT;
    }

    @Override
    public View getHeaderView(RecyclerView parent, int section) {
        if(sectionList != null) {
            return sectionList.get(section).abstractGetHeaderView(parent);
        }
        return null;
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

    @Override
    public int firstPositionForSection(int section) {
        return startOfSection.get(section);
    }

    @Override
    public int lastPositionForSection(int section) {
        if(section == sectionList.size()-1)
            return CACHED_ITEM_COUNT-1;
        return startOfSection.get(section+1)-1;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(decoration);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
        checkForGridLayout = false;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        checkForGridLayout = false;
        this.recyclerView = null;
    }
}
