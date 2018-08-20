package br.marraware.sectionedRecyclerView;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by joao_gabriel on 20/08/2018.
 */
public class HeaderContainer {

    private View headerView;
    private Rect headerRect;

    public HeaderContainer(View headerView) {
        this.headerView = headerView;
        this.headerRect = new Rect(-1,-1,-2,-2);
    }

    public View getHeaderView() {
        return headerView;
    }

    public Rect getHeaderRect() {
        return headerRect;
    }

    public void setHeaderRect(int x, int y, int x2, int y2) {
        if(headerRect != null)
            headerRect.set(x, y, x2, y2);
        else
            headerRect = new Rect(x, y, x2, y2);
    }
}
