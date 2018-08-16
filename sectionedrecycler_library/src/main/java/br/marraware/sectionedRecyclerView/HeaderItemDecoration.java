package br.marraware.sectionedRecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class HeaderItemDecoration extends RecyclerView.ItemDecoration {

    private HeaderItemDecorationListener mListener;
    private HashMap<Integer, View> headerMap;

    public HeaderItemDecoration(HeaderItemDecorationListener mListener) {
        this.mListener = mListener;
        headerMap = new HashMap<>();
    }

    private View headerForSection(int section, RecyclerView parent) {
        View header;
        if(headerMap.containsKey(section)) {
            header = headerMap.get(section);
        } else {
            header = LayoutInflater.from(parent.getContext()).inflate(mListener.getHeaderLayout(section), parent, false);
            headerMap.put(section, header);
            mListener.bindHeaderData(header, section);
            measureHeader(parent, header);
        }
        return header;
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
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            section = (int) child.getTag(R.id.ROW_SECTION);
            position = (int) child.getTag(R.id.ROW_POSITION);
            if (mListener.hasHeader(section) && position == 0) {
                header = headerForSection(section, parent);
                c.save();
                c.translate(0, child.getTop() - header.getHeight() - parent.getScrollY());
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
                if(mListener.hasHeader(section)) {
                    View header = headerForSection(section, parent);
                    c.save();
                    if(mListener.isLastOfSection(position, section)) {
                        c.translate(0, child.getTop());
                    }
                    header.draw(c);
                    c.restore();
                }
            }
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

    public interface HeaderItemDecorationListener {

        int getHeaderLayout(int section);

        void bindHeaderData(View header, int section);

        boolean hasHeader(int section);

        boolean isLastOfSection(int position, int section);

        boolean isStickHeader();
    }
}
