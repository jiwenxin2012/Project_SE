package com.example.first;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {
    
    private NotesDbAdapter dbHelper;
    private EditText field_note;
    private Button button_confirm;
    private Long rowId;
    private String table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	System.out.println("in the edit"); 
        super.onCreate(savedInstanceState);
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open(); 
        setContentView(R.layout.note_edit);//�༭��ʱ��ʹ����һ��UI
        findViews();
        showAndUpdateNote(savedInstanceState);
    }

    private void findViews() {
        field_note = (EditText) findViewById(R.id.note);
        button_confirm = (Button) findViewById(R.id.confirm);
    }

    /**
     * �༭�޸ı������ұ���
     * @param savedInstanceState
     */
    private void showAndUpdateNote(Bundle savedInstanceState) {
        if (rowId == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
            table = extras != null ? extras.getString("TABLE") : null;
        }
        showNote();
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dbHelper.update(table, rowId, field_note.getText().toString());
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    
    /**
     * ���Ҫ�༭�ı���
     */
    private void showNote() {
        if (rowId != null) {
            Cursor note = dbHelper.get(table, rowId);
            //startManagingCursor(note);
            field_note.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE)
                ));
        }
    }
}
