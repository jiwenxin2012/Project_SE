package com.example.first;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;

public class ShowNote extends ExpandableListActivity {
	protected final int menuInsert=Menu.FIRST;
	protected final int menuDelete=Menu.FIRST+1;
	protected final int menuChange=Menu.FIRST+2;
	protected final int menuSetConficts=Menu.FIRST+3;
	protected final int menuShowConficts=Menu.FIRST+4;
	protected final int menuCleanConficts=Menu.FIRST+5;
	protected final int menuCleanColor=Menu.FIRST+6;
	protected final int menuEdit=Menu.FIRST+7;
	protected final int menuSearch=Menu.FIRST+8;
	private static final int ACTIVITY_EDIT = 0x1001;
	private List<String> groupArray;  
    private List<List<String>> childArray; 
    private NotesDbAdapter dbHelper;
    private Cursor cursor;
    private String Table = null;
    private long rowID, centerID;
    private ArrayList<Boolean> use;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_layout);
        registerForContextMenu(getExpandableListView());
        centerID=0;
        setAdapter();
        setTitle(Table);
        
    }
    private void setAdapter() {
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open();
        if (Table==null) {
        	Bundle extras = getIntent().getExtras();
        	rowID = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        	cursor=dbHelper.getTable(rowID);
        	Table = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE));
        }
        fillData();
    }
    private void fillData() {
        cursor = dbHelper.getall(Table);
        getUse();
        groupArray = new ArrayList<String>();  
        childArray = new ArrayList<List<String>>();  
        while (cursor.moveToNext()) {
        	List<String> tempArray01 = new ArrayList<String>();
        	groupArray.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_NOTE)));
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_CONTENTS))); 
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_SUMMARY)));
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_EXECUTOR)));
        	childArray.add(tempArray01);  
        }
        ColorListAdapter adapter = new ColorListAdapter(this, 
                                    groupArray, 
                                    childArray, 
                                    use, centerID);
        getExpandableListView().setAdapter(adapter); 
    }
    private void fillData(String item) {
        cursor = dbHelper.searchNote(Table, item);
        getUse();
        groupArray = new ArrayList<String>();  
        childArray = new ArrayList<List<String>>();  
        while (cursor.moveToNext()) {
        	List<String> tempArray01 = new ArrayList<String>();
        	groupArray.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_NOTE)));
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_CONTENTS))); 
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_SUMMARY)));
        	tempArray01.add(cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_EXECUTOR)));
        	childArray.add(tempArray01);  
        }
        ColorListAdapter adapter = new ColorListAdapter(this, 
                                    groupArray, 
                                    childArray, 
                                    use, centerID);
        getExpandableListView().setAdapter(adapter); 
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0,menuInsert,0,R.string.addNote);
            menu.add(0,menuChange,0,R.string.retto);
            menu.add(0,menuCleanConficts,0,"清除矛盾");
            menu.add(0,menuCleanColor,0,"清除颜色");
            menu.add(0,menuSearch,0,"查询笔记");
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
                    long id = dbHelper.create(Table, noteName, null, null, null);
                    System.out.println("new"+id);
                    Intent intent = new Intent(this, NoteEdit.class);
                    intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
                    intent.putExtra("TABLE", Table);
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
            case menuDelete:
            	dbHelper.delete(Table, getExpandableListView().getSelectedItemId());
            	break;
            case menuChange:
            	Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                break;
            case menuCleanColor:
            	centerID=0;
            	break;
            case menuCleanConficts:
            	dbHelper.deleteConfict(Table);
            	break;
            }
            fillData();
            return super.onOptionsItemSelected(item);
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
            menu.add(0, menuEdit, 0,  "编辑日志");
            menu.add(0, menuSetConficts, 0,  "设置矛盾");
            menu.add(0, menuShowConficts, 0,  "显示矛盾");
            super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * 长按菜单的选中事件
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
    		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo(); 
    		long id = ExpandableListView.getPackedPositionGroup(info.packedPosition)+1; 
    		switch (item.getItemId()) { 
                    case menuDelete : 
                            Log.d("MENU", "group"+id) ;
                            dbHelper.delete(Table, id) ; 
                            fillData() ; 
                            break;
                    case menuSetConficts : 
                    	Intent intent = new Intent(this, ListEdit.class);
                    	intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
                        intent.putExtra("TABLE", Table);
                        startActivityForResult(intent, ACTIVITY_EDIT);
                        centerID=id;
                        fillData();
                        break;
                    case menuShowConficts: 
                        centerID=id;
                        fillData();
                        break;
                    case menuEdit:
                    	System.out.println("edit id:"+id);
                        Intent intent2 = new Intent(this, NoteEdit.class);
                        intent2.putExtra(NotesDbAdapter.KEY_ROWID, id);
                        intent2.putExtra("TABLE", Table);
                        startActivityForResult(intent2, ACTIVITY_EDIT);
                        break;
            }
            return super.onContextItemSelected(item);
    }
    private void getUse()
    {
    	use = new ArrayList<Boolean>();
    	for (int i=0; i<cursor.getCount(); i++)
    		use.add(false);
    	if (centerID==0) return ;
    	Cursor mCursor = dbHelper.getConfict(Table, centerID);
    	while (mCursor.moveToNext()) {  
	        int Nameindex = mCursor.getColumnIndex(NotesDbAdapter.KEY_B); 
	        use.set(mCursor.getInt(Nameindex)-1, true);
	    }  
    }

}
