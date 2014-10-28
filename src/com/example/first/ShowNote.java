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

public class ShowNote extends ListActivity {
	protected final int menuInsert=Menu.FIRST;
	protected final int menuDelete=Menu.FIRST+1;
	protected final int menuChange=Menu.FIRST+2;
	private static final int ACTIVITY_EDIT = 0x1001;
    private NotesDbAdapter dbHelper;
    private Cursor cursor;
    private String Table = null;
    private long rowID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	System.out.println("here in shownote");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        setAdapter();
        setTitle(Table);
    }
    private void setAdapter() {
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open();
        if (Table==null) {
        	Bundle extras = getIntent().getExtras();
        	if (extras==null) {
        		System.out.println("???");
        	}
        	rowID = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        	cursor=dbHelper.getTable(rowID);
        	Table = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE));
        }
        fillData();
    }
    private void fillData() {
        cursor = dbHelper.getall(Table);
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
            menu.add(0,menuInsert,0,R.string.addNote);
            menu.add(0,menuChange,0,R.string.retto);
            return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * 对HOME键产生的菜单的选中事件 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            case menuInsert:
                    String noteName = "New Note";
                    long id = dbHelper.create(Table, noteName);
                    System.out.println("new"+id);
                    Intent intent = new Intent(this, NoteEdit.class);
                    intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
                    intent.putExtra("TABLE", Table);
                    startActivityForResult(intent, ACTIVITY_EDIT);
                    break;
            case menuDelete:
            	dbHelper.delete(Table, getListView().getSelectedItemId());
            	fillData();
            	break;
            case menuChange:
            	Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
            }
            return super.onOptionsItemSelected(item);
    }
    

    /**
     * 当选中ListView 中的一个 View 时的动作
     * 在这里作为 编辑备忘的入口  
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        System.out.println("click"+id);
        Intent intent = new Intent(this, NoteEdit.class);
        intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
        intent.putExtra("TABLE", Table);
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    /**
     * startActivityForResult() 和 onActivityResult()  相伴而生 
     * 前者 启动并进入另外一个activity 后者 处理从另外一个 activity 回来的事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    /**
     * 创建长按菜单  点击一个菜单1秒钟不松开 即可创建一个右键菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenuInfo menuInfo) {
            menu.add(0, menuDelete, 0,  "删除日志");
            super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * 长按菜单的选中事件
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
            AdapterView.AdapterContextMenuInfo info;
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) { 
                    case menuDelete : 
                            Log.d("MENU", "item"+info.id) ;
                            dbHelper.delete(Table, info.id) ; 
                fillData() ; 
                break ; 
            }
            return super.onContextItemSelected(item);
    }
	

}
