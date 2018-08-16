package br.com.marraware.sectionedrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.marraware.sectionedrecycler_library.RecyclerViewAdapterSection;
import br.marraware.sectionedrecycler_library.SectionedRecyclerViewAdapter;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public class TestActivity extends AppCompatActivity {

    class Row extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public Row() {
            super(View.inflate(TestActivity.this, R.layout.row, null));
            textView = itemView.findViewById(R.id.text_view);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int section = (int) v.getTag(R.id.ROW_SECTION);
            int position = (int) v.getTag(R.id.ROW_POSITION);
            System.out.println("ROW - "+section+" : "+position);
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
            viewHolder.textView.setText(getSectionPos()+" ROW "+section+" : "+pos);
        }

        @Override
        public int getItemCount() {
            return count;
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
    }

    SectionedRecyclerViewAdapter adapter;
    CustomSection lastSection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SectionedRecyclerViewAdapter(recyclerView);
        CustomSection section = new CustomSection(false, 2);
        adapter.addSection(section);
        section = new CustomSection(true, 3);
        adapter.addSection(section);
        section = new CustomSection(false, 5);
        adapter.addSection(section);
        lastSection = new CustomSection(true, 10);
        adapter.addSection(lastSection);
        recyclerView.setAdapter(adapter);
    }

    public void addRow(View view) {
        lastSection.count += 1;
        adapter.setStickHeader(!adapter.isStickHeader());
        adapter.dataSetChanged();
    }
}
