package com.example.first;

import android.R.string;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ListActivity {
	protected final int menuInsert=Menu.FIRST;
	protected final int menuDelete=Menu.FIRST+1;
	protected final int menuSearch=Menu.FIRST+2;
	protected final int menuLogin=Menu.FIRST+3;
	protected final int menuRegister=Menu.FIRST+4;
	private static final int ACTIVITY_EDIT = 0x1001;
    private NotesDbAdapter dbHelper;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        setAdapter();
        setTitle("选择一个话题");
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
    private void fillData(String item) {
        cursor = dbHelper.searchTable(item);
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
            menu.add(0,menuSearch,0,"查询话题");
            menu.add(0,menuLogin,0,"登录");
            menu.add(0,menuRegister,0,"注册");
            return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * 对HOME键产生的菜单的选中事件 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            case menuInsert:
                    Intent intent = new Intent(this, TableEdit.class);
                    startActivityForResult(intent, ACTIVITY_EDIT);
                    break;
            case menuSearch:
            	final EditText inputServer = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Server").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    		String input = inputServer.getText().toString();
                    		fillData(input);
                     }
                });
                builder.show();
                break;
            case menuLogin:
            	Intent intent2 = new Intent(this, Login.class);
                startActivityForResult(intent2, ACTIVITY_EDIT);
                break;
            case menuRegister:
            	Intent intent3 = new Intent(this, register.class);
                startActivityForResult(intent3, ACTIVITY_EDIT);
                break;
            case menuDelete:
            	dbHelper.deleteTable(getListView().getSelectedItemId());
            	fillData();
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
        Intent intent = new Intent(this, ShowNote.class);
        intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
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
            menu.add(0, menuDelete, 0,  "删除话题");
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
                            dbHelper.deleteTable(info.id) ; 
                fillData() ; 
                break ; 
            }
            return super.onContextItemSelected(item);
    }
	

}
