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
compile 'br.marraware:sectionedrecycler_library:1.9'
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
	SectionedRecyclerViewAdapter adapter = new SectionedRecyclerViewAdapter();
	CustomSection sectionA = new CustomSection();
	adapter.addSection(sectionA);
	...
    recyclerView.setAdapter(adapter);
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

And enable and disable stick to top on your adapter using `public void setStickHeader(boolean stickHeader);`. The default value is `TRUE`.

```
   ...
   adapter.setStickHeader(false);
   ...
```

# Row and Header click event

`SectionedRecyclerViewAdapter` also handle row and header clicks for you.

For the row click event just add this method to you `RecyclerViewAdapterSection<RecyclerView.ViewHolder>` class

```
    @Override
    public void onItemClick(int position) {
    	//click logic
    }
```

For the header click event just add this method to you `RecyclerViewAdapterSection<RecyclerView.ViewHolder>` class

```
    @Override
    public void onHeaderClick() {
        //click logic
    }
```

# Updating your section's data

You can call `notifyDataSetChanged()` on your custom section;

Never use `notifyDataSetChanged()` on your `SectionedRecyclerViewAdapter`. `SectionedRecyclerViewAdapter` uses cached counts to improve performance. If you want to update all your adapter use `dataSetChanged()`.

```
    ...
    mySection.addNewRow(rowModel);
    mySection.notifyDataSetChanged() //this will update the adapter/recycler
    ...
```
```
    ...
    mySectionA.addNewRow(rowModelA);
    mySectionB.addNewRow(rowModelB);
    ...
    sectionedAdapter.dataSetChanged() //this will update the adapter/recycler
    ...
```
