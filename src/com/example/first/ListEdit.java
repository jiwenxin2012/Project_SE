package com.example.first;

import java.util.ArrayList;

import com.example.first.MyListAdapter.ViewHolder;

import android.R.integer;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ListEdit extends Activity {  
    private ListView lv;  
    private MyListAdapter mAdapter;  
    private Cursor list;  
    private Button bt_finish; 
    private int checkNum; // ��¼ѡ�е���Ŀ����  
    private TextView tv_show;// ������ʾѡ�е���Ŀ����  
    private NotesDbAdapter dbHelper;
    private String Table = null;
    private ArrayList<Boolean> use;
    private Long id;
    /** Called when the activity is first created. */  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.listedit);  
        /* ʵ���������ؼ� */  
        lv = (ListView) findViewById(R.id.lv);  
        bt_finish = (Button) findViewById(R.id.bt_finish); 
        tv_show = (TextView) findViewById(R.id.tv);  
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open();
        Bundle extras = getIntent().getExtras();
        id = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        Table = extras != null ? extras.getString("TABLE") : null;
        list=dbHelper.getall(Table);
        getUse();
        // ʵ�����Զ����MyAdapter  
        mAdapter = new MyListAdapter(list, this, use);  
        // ��Adapter  
        lv.setAdapter(mAdapter);  
        // ȫѡ��ť�Ļص��ӿ�  
        bt_finish.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	dbHelper.close();
                finish();
            }  
        });  
  
        // ��listView�ļ�����  
        lv.setOnItemClickListener(new OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                // ȡ��ViewHolder����������ʡȥ��ͨ������findViewByIdȥʵ����������Ҫ��cbʵ���Ĳ���  
                ViewHolder holder = (ViewHolder) arg1.getTag();  
                // �ı�CheckBox��״̬  
                holder.cb.toggle();  
                // ��CheckBox��ѡ��״����¼����  
                MyListAdapter.getIsSelected().put(arg2, holder.cb.isChecked());  
                // ����ѡ����Ŀ  
                if (holder.cb.isChecked() == true) {  
                    checkNum++;  
                    dbHelper.createConfict(Table, id, arg3+1);
                    dbHelper.createConfict(Table, arg3+1, id);
                } else {  
                    checkNum--; 
                    dbHelper.deleteConfict(Table, id, arg3+1);
                    dbHelper.deleteConfict(Table, arg3+1, id);
                }  
                // ��TextView��ʾ  
                tv_show.setText("��ѡ��" + checkNum + "��");  
            }  
        });  
    }  
    private void getUse()
    {
    	use = new ArrayList<Boolean>();
    	for (int i=0; i<list.getCount(); i++)
    		use.add(false);
    	Cursor mCursor = dbHelper.getConfict(Table, id);
    	System.out.println(id+" "+Table);
    	System.out.println("size:"+mCursor.getCount());
    	while (mCursor.moveToNext()) {  
	        int Nameindex = mCursor.getColumnIndex(NotesDbAdapter.KEY_B); 
	        use.set(mCursor.getInt(Nameindex)-1, true);
	        System.out.println("!!");
	    }  
    }
    // ˢ��listview��TextView����ʾ  
    private void dataChanged() {  
        // ֪ͨlistViewˢ��  
        mAdapter.notifyDataSetChanged();  
        // TextView��ʾ���µ�ѡ����Ŀ  
        tv_show.setText("��ѡ��" + checkNum + "��");  
    };  
}  