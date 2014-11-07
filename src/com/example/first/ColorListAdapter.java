package com.example.first;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
 
public class ColorListAdapter extends SimpleCursorAdapter {
	private ArrayList<Boolean> use;
	private long id;
    public ColorListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, ArrayList<Boolean> use, long id) {
		super(context, layout, c, from, to, flags);
		this.use=use;
		this.id=id-1;
	}
    @Override
    public View getView(int position ,View convertView,ViewGroup parent){
        View view=super.getView(position, convertView, parent);
        if (position==id)
        	view.setBackgroundColor(Color.parseColor("#F0FFFF"));//颜色设置
        else if (id!=-1&&use.get(position)) {
            view.setBackgroundColor(Color.parseColor("#FFDAB9"));//颜色设置
            System.out.println(position+" "+id);
        }
        return view; 
    } 
}