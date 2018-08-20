package br.marraware.sectionedRecyclerView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class HeaderItemDecoration extends RecyclerView.ItemDecoration {

    private HeaderItemDecorationListener mListener;
    private HashMap<Integer, HeaderContainer> headerMap;
    private Rect overRect = new Rect();
    private int overSection = -1;

    public HeaderItemDecoration(HeaderItemDecorationListener mListener) {
        this.mListener = mListener;
        headerMap = new HashMap<>();
    }

    private View headerForSection(int section, RecyclerView parent) {
        HeaderContainer headerContainer;
        if(headerMap.containsKey(section)) {
            headerContainer = headerMap.get(section);
        } else {
            View header = mListener.getHeaderView(parent, section);
            header.setClickable(true);
            headerContainer = new HeaderContainer(header);
            headerMap.put(section, headerContainer);
            mListener.bindHeaderData(header, section);
            measureHeader(parent, header);
        }
        return headerContainer.getHeaderView();
    }

    public void clear() {
        headerMap.clear();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int section = (int) view.getTag(R.id.ROW_SECTION);
        int position = (int) view.getTag(R.id.ROW_POSITION);
        if(mListener.hasHeader(section) && position == 0) {
            View header = headerForSection(section, parent);
            outRect.top = header.getHeight();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        View child, header;
        int section, position;
        int x = 0;
        int y, x2, y2;
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            section = (int) child.getTag(R.id.ROW_SECTION);
            position = (int) child.getTag(R.id.ROW_POSITION);
            if (mListener.hasHeader(section) && position == 0) {
                header = headerForSection(section, parent);
                x = 0;
                y = child.getTop() - header.getHeight() - parent.getScrollY();
                x2 = header.getWidth();
                y2 = y+header.getHeight();
                headerMap.get(section).setHeaderRect(x, y, x2, y2);
                c.save();
                c.translate(x, y);
                header.draw(c);
                c.restore();
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mListener.isStickHeader()) {
            if(parent.getChildCount() > 0) {
                View child = parent.getChildAt(0);
                int section = (int) child.getTag(R.id.ROW_SECTION);
                int position = (int) child.getTag(R.id.ROW_POSITION);
                int x, y, x2, y2;
                if(mListener.hasHeader(section)) {
                    View header = headerForSection(section, parent);
                    c.save();
                    x = 0;
                    x2 = header.getWidth();
                    y = 0;
                    y2 = y+header.getHeight();
                    RecyclerView.ViewHolder lastVH = parent.findViewHolderForAdapterPosition(mListener.lastPositionForSection(section));
                    if(lastVH != null) {
                        View lastView = lastVH.itemView;
                        if(lastView != null && lastView.getBottom() < y2) {
                            y2 = lastView.getBottom();
                            y = y2-header.getHeight();
                        }
                    }
                    c.translate(0, y);
                    header.draw(c);
                    c.restore();
                    overRect.set(x, y, x2, y2);
                    overSection = section;
                } else {
                    overSection = -1;
                }
            } else {
                overSection = -1;
            }
        } else {
            overSection = -1;
        }
    }

    private void measureHeader(ViewGroup parent, View header) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

        header.measure(childWidthSpec, childHeightSpec);

        header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
    }

    public int overSectionOnPosition(float x, float y) {
        if(overSection != -1) {
            if(overRect.contains((int) x, (int) y))
                return overSection;
        }
        return -1;
    }

    public int sectionForHeaderOnPosition(float x, float y) {
        Iterator<Integer> iterator = headerMap.keySet().iterator();
        Integer key;
        while (iterator.hasNext()) {
            key = iterator.next();
            if(headerMap.get(key).getHeaderRect().contains((int)x, (int)y)) {
                return key;
            }
        }
        return -1;
    }

    public HeaderContainer headerViewForSection(int section) {
        return headerMap.get(section);
    }

    public interface HeaderItemDecorationListener {

        View getHeaderView(RecyclerView parent, int section);

        void bindHeaderData(View header, int section);

        boolean hasHeader(int section);

        boolean isLastOfSection(int position, int section);

        int lastPositionForSection(int section);

        boolean isStickHeader();
    }
}
