package br.com.marraware.sectionedrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import br.marraware.sectionedRecyclerView.RecyclerViewAdapterSection;
import br.marraware.sectionedRecyclerView.SectionedRecyclerViewAdapter;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class TestActivity extends AppCompatActivity {

    class Row extends RecyclerView.ViewHolder {

        TextView textView;

        public Row() {
            super(View.inflate(TestActivity.this, R.layout.row, null));
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    class CustomSection extends RecyclerViewAdapterSection<Row> {

        boolean hasHeader;
        int count;

        public CustomSection(boolean hasHeader, int count) {
            this.hasHeader = hasHeader;
            this.count = count;
        }

        @Override
        public Row onCreateViewHolder() {
            return new Row();
        }

        @Override
        public void onBindViewHolder(Row viewHolder, int position) {
            int section = (int) viewHolder.itemView.getTag(R.id.ROW_SECTION);
            int pos = (int) viewHolder.itemView.getTag(R.id.ROW_POSITION);
            if(count == 0)
                viewHolder.textView.setText("\n\n=== VAZIO ===\n\n");
            else
                viewHolder.textView.setText(getSectionPos()+" ROW "+section+" : "+pos);
        }

        @Override
        public int getItemCount() {
            return Math.max(count, 0);
        }

        @Override
        public boolean hasHeader() {
            return hasHeader;
        }

        @Override
        public int getHeaderLayout() {
            return R.layout.header;
        }

        @Override
        public void onBindHeaderView(View header) {
            TextView textView = header.findViewById(R.id.text_view);
            textView.setText("HEADER("+getSectionPos()+") : "+count+" rows");
        }

        @Override
        public void onItemClick(int position) {
            System.out.println("ROW - "+getSectionPos()+" : "+position);
            count--;
            count = Math.max(count, 0);
            notifyDataSetChanged();
        }

        @Override
        public void onHeaderClick() {
            Log.d("TEST", "CLICK TITLE - "+getSectionPos());
        }
    }

    SectionedRecyclerViewAdapter adapter;
    CustomSection lastSection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        adapter = new SectionedRecyclerViewAdapter();
        CustomSection section = new CustomSection(false, 2);
        adapter.addSection(section);
        section = new CustomSection(true, 3);
        adapter.addSection(section);
        section = new CustomSection(true, 5);
        adapter.addSection(section);
        lastSection = new CustomSection(true, 10);
        adapter.addSection(lastSection);
        section = new CustomSection(true, 0);
        adapter.putSection(section, 10);
        recyclerView.setAdapter(adapter);
    }

    public void addRow(View view) {
        lastSection.count += 1;
        lastSection.notifyDataSetChanged();
    }
}
