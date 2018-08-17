# SectionedRecyclerView
Easy lib for sectioned Recycler View with stick header

# Add to project

First add on you project\`s build.gradle :

```
allprojects {
    repositories {
        jcenter()
    }
}
```

Now add on your app module\`s build.gradle dependecy:
```
compile 'br.marraware:sectionedrecycler_library:1.6'
```

# How to use

Create your custom `RecyclerViewAdapterSection<RecyclerView.ViewHolder>`

```
class CustomSection extends RecyclerViewAdapterSection<Row> {
	@Override
    public Row onCreateViewHolder() {
        return new Row();
    }

    @Override
    public void onBindViewHolder(Row viewHolder, int position) {
    	//bind Row logic
    }

    @Override
    public int getItemCount() {
    	//your section row count
        return 0;
    }
}
```

Then add this section to your `SectionedRecyclerViewAdapter` object

```
	...
	SectionedRecyclerViewAdapter adapter = new SectionedRecyclerViewAdapter(recyclerView);
	CustomSection sectionA = new CustomSection();
	adapter.addSection(sectionA);
	...
```

# Add header to section

You can add header to your section

```
class CustomSection extends RecyclerViewAdapterSection<Row> {
	...
	@Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public int getHeaderLayout() {
        return R.layout.header_layout;
    }

    @Override
    public void onBindHeaderView(View header) {
    	//bind header logic - this headerview is just for draw - can't be interactive
    }
    ...
```

# Row click event

`SectionedRecyclerViewAdapter` also handle row clicks for you, just add this method to you `RecyclerViewAdapterSection<RecyclerView.ViewHolder>` class

```
	@Override
    public void onItemClick(int position) {
    	//click logic
    }
```