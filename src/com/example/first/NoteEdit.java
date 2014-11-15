package com.example.first;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {
    
    private NotesDbAdapter dbHelper;
    private EditText note, contents, summary, executor;
    private Button button_confirm;
    private long rowId;
    private String table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open(); 
        setContentView(R.layout.note_edit);//编辑的时候使用另一个UI
        findViews();
        showAndUpdateNote(savedInstanceState);
    }

    private void findViews() {
        note = (EditText) findViewById(R.id.note);
        contents = (EditText) findViewById(R.id.contents);
        summary = (EditText) findViewById(R.id.summary);
        executor = (EditText) findViewById(R.id.executor);
        button_confirm = (Button) findViewById(R.id.confirm);
    }

    /**
     * 编辑修改备忘并且保存
     * @param savedInstanceState
     */
    private void showAndUpdateNote(Bundle savedInstanceState) {
        if (table == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
            table = extras != null ? extras.getString("TABLE") : null;
        }
        showNote();
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dbHelper.update(table, rowId, note.getText().toString(), contents.getText().toString(),
                		summary.getText().toString(), executor.getText().toString());
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    
    /**
     * 填充要编辑的备忘
     */
    private void showNote() {
        if (table != null) {
            Cursor Note = dbHelper.get(table, rowId);
            note.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE)
                ));
            contents.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CONTENTS)
                ));
            summary.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_SUMMARY)
                ));
            executor.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_EXECUTOR)
                ));
        }
    }
}
