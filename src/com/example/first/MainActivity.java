package com.example.first;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ListActivity {
	protected final int menuInsert=Menu.FIRST;
	protected final int menuDelete=Menu.FIRST+1;
	private static final int ACTIVITY_EDIT = 0x1001;
    private NotesDbAdapter dbHelper;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        setAdapter();
        setTitle("����");
    }
    private void setAdapter() {
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open();
        fillData();
    }
    private void fillData() {
        cursor = dbHelper.getallTable();
        //startManagingCursor(cursor);

        String[] from = new String[]{"note"};
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
                                    android.R.layout.simple_list_item_1, 
                                    cursor, 
                                    from, 
                                    to,
                                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0,menuInsert,0,R.string.addTable);
            return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * ��HOME�������Ĳ˵���ѡ���¼� 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            case menuInsert:
                    Intent intent = new Intent(this, TableEdit.class);
                    startActivityForResult(intent, ACTIVITY_EDIT);
            case menuDelete:
            	dbHelper.deleteTable(getListView().getSelectedItemId());
            	fillData();
            }
            return super.onOptionsItemSelected(item);
    }
    

    /**
     * ��ѡ��ListView �е�һ�� View ʱ�Ķ���
     * ��������Ϊ �༭���������  
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, ShowNote.class);
        intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    /**
     * startActivityForResult() �� onActivityResult()  ������ 
     * ǰ�� ��������������һ��activity ���� ���������һ�� activity �������¼�
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    /**
     * ���������˵�  ���һ���˵�1���Ӳ��ɿ� ���ɴ���һ���Ҽ��˵�
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenuInfo menuInfo) {
            menu.add(0, menuDelete, 0,  "ɾ������");
            super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * �����˵���ѡ���¼�,here is changed!
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
            AdapterView.AdapterContextMenuInfo info;
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) { 
                    case menuDelete : 
                            Log.d("MENU", "item"+info.id) ;
                            dbHelper.deleteTable(info.id) ; 
                fillData() ; 
                break ; 
            }
            return super.onContextItemSelected(item);
    }
	

}
