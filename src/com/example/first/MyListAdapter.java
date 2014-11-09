package com.example.first;
import java.util.ArrayList;  
import java.util.HashMap;  
  
import android.content.Context;  
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.CheckBox;  
import android.widget.TextView;  
  
public class MyListAdapter extends BaseAdapter {  
    // ������ݵ�list  
    private Cursor list;  
    private ArrayList<Boolean> use;  
    // ��������CheckBox��ѡ��״��  
    private static HashMap<Integer, Boolean> isSelected;  
    // ������  
    private Context context;  
    // �������벼��  
    private LayoutInflater inflater = null;  
    // ������  
    public MyListAdapter(Cursor list, Context context, ArrayList<Boolean> use) {  
        this.context = context;  
        this.list = list;  
        this.use = use;
        inflater = LayoutInflater.from(context);  
        isSelected = new HashMap<Integer, Boolean>();  
        // ��ʼ������  
        initDate();  
    }  
  
    // ��ʼ��isSelected������  
    private void initDate() {  
        for (int i = 0; i < list.getCount(); i++) {  
        	list.moveToPosition(i);
            if (use.get(i)) {
            	getIsSelected().put(i, true); 
            }
            else {
            	getIsSelected().put(i, false);  
            }
        }  
    }  
  
    @Override  
    public int getCount() {  
        return list.getCount();  
    }  
  
    @Override  
    public Object getItem(int position) {  
    	if (list.moveToPosition(position)) {  
            return list;  
        } else {  
            return null;  
        }  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder = null;  
        if (convertView == null) {  
            // ���ViewHolder����  
            holder = new ViewHolder();  
            // ���벼�ֲ���ֵ��convertview  
            convertView = inflater.inflate(R.layout.listviewitem, null);  
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);  
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);  
            // Ϊview���ñ�ǩ  
            convertView.setTag(holder);  
        } else {  
            // ȡ��holder  
            holder = (ViewHolder) convertView.getTag();  
        }  
        // ����list��TextView����ʾ  
        list.moveToPosition(position);
        holder.tv.setText(list.getString(list.getColumnIndex(NotesDbAdapter.KEY_NOTE)));  
        // ����isSelected������checkbox��ѡ��״��  
        holder.cb.setChecked(getIsSelected().get(position));  
        return convertView;  
    }  
  
    public static HashMap<Integer, Boolean> getIsSelected() {  
        return isSelected;  
    }  
  
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {  
        MyListAdapter.isSelected = isSelected;  
    }  
  
    public static class ViewHolder {  
        TextView tv;  
        CheckBox cb;  
    }  
}  