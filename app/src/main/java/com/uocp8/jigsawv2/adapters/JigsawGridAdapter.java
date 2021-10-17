package com.uocp8.jigsawv2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.model.ImageEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JigsawGridAdapter extends BaseAdapter implements OrderableAdapter{

    private Context context;
    private List<ImageEntity> items = new ArrayList<>();

    private static final int INVALID_ID = -1;
    private Long nextStableId = 0L;
    private Map<Object, Long> mIdMap = new HashMap<>();

    private int columns;


    public JigsawGridAdapter(Context context, List<ImageEntity> items, int count) {
        this.context = context;
        this.columns = count;
        init(items);
    }

    private void init(List<ImageEntity> items) {
        addAllStableId(items);
        this.items.addAll(items);
    }

    @Override
    public void reorderItems(int originalPosition, int newPosition) {
        if (newPosition < getCount()) {
            Collections.swap(items, originalPosition, newPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getColumnCount() {
        return columns;
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public boolean canReorder(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView == null ? newView(position) : (ImageView) convertView;
    }

    private ImageView newView(int position) {
        ImageEntity ie = items.get(position);
        Bitmap d = ie.getImage();

        ImageView view = new ImageView(context);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //view.setLayoutParams(new GridView.LayoutParams(d.getWidth(), d.getHeight()));
        view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setAdjustViewBounds(true);
        view.setPadding(1, 1, 1, 1);
        view.setImageDrawable(new BitmapDrawable(context.getResources(), d));
        //view.setTag(R.id.IDEAL_POSITION, ie.getIdealPosition());

        return view;
    }

    @Override
    public final boolean hasStableIds() {
        return true;
    }

    void addAllStableId(List<ImageEntity> items) {
        items.forEach(this::addStableId);
    }

    private void addStableId(Object item) {
        mIdMap.put(item, nextStableId++);
    }

    @Override
    public final long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Object item = getItem(position);
        return mIdMap.get(item);
    }


}
