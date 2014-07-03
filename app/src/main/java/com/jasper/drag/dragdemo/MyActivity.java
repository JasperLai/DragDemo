package com.jasper.drag.dragdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.jasper.drag.dragdemo.controls.DeleteZone;
import com.jasper.drag.dragdemo.controls.DragController;
import com.jasper.drag.dragdemo.controls.DragLayer;
import com.jasper.drag.dragdemo.controls.DragListListener;
import com.jasper.drag.dragdemo.controls.DragSource;


public class MyActivity extends Activity implements DragListListener,
        DragController.DragListener {

    private GridAdapter mAdapter = null;
    int imgs[] = new int[]{R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
            R.drawable.pic4, R.drawable.pic5, R.drawable.pic6, R.drawable.pic7, R.drawable.pic8,
            R.drawable.pic9};

    private DragLayer mDragLayer = null;

    private DeleteZone mDeleteZone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        mDragLayer.setDragListener(this);

        mDeleteZone = (DeleteZone) findViewById(R.id.deletezone);
        DragableGridView gridView = (DragableGridView) findViewById(R.id.gridview);
        mAdapter = new GridAdapter(this);
        mAdapter.setData(imgs);
        gridView.setAdapter(mAdapter);


        gridView.setDragController(mDragLayer);
        gridView.setViewListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(View v, int position, int id) {
        Toast.makeText(this, "click pos " + position , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDragToDelete(Object item, int sourceType) {

    }

    @Override
    public void onDragStart(View v, DragSource source, Object info, int dragAction) {
        mDeleteZone.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDragEnd() {
        mDeleteZone.setVisibility(View.INVISIBLE);
    }

    public class GridAdapter extends BaseAdapter {

        private int[] mData = new int[0];
        private Context mContext;

        public GridAdapter(Context context) {
            super();
            mContext = context;
        }

        public void setData(int[] data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(mData[i]);
            view.setOnClickListener((View.OnClickListener) viewGroup);
            view.setOnLongClickListener((View.OnLongClickListener) viewGroup);
            return view;
        }
    }
}
