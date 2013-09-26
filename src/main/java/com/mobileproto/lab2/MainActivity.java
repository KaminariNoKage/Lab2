package com.mobileproto.lab2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    public static NotesDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Opening the database
        mDbHelper = new NotesDBHelper(this);


        //File system
        final TextView title = (TextView) findViewById(R.id.titleField);
        final TextView note = (TextView) findViewById(R.id.noteField);


        List<String> files = new ArrayList<String>(Arrays.asList(fileList()));

        final NoteListAdapter aa = new NoteListAdapter(this, android.R.layout.simple_list_item_1, files);

        final ListView notes = (ListView) findViewById(R.id.noteList);

        notes.setAdapter(aa);


        //Retrieving past DB information to display
        String[] oldCols = {NotesDBHelper.FeedEntry._ID, NotesDBHelper.FeedEntry.COLUMN_NAME_TITLE, NotesDBHelper.FeedEntry.COLUMN_NAME_TEXT};
        //Turning DB into string array
        Cursor savedDB = mDbHelper.getWritableDatabase().query(NotesDBHelper.FeedEntry.TABLE_NAME,
                oldCols, null, null, null, null, null);

        savedDB.moveToFirst();
        while(!savedDB.isAfterLast()){
            String name = savedDB.getString(1);
            System.out.println("RETRIEVED: " + name);
            aa.insert(name, 0);
            aa.notifyDataSetChanged();
            savedDB.moveToNext();
        }


            Button save = (Button)findViewById(R.id.saveButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                //Gets the text created by the user
                String fileName = title.getText().toString();
                String noteText = note.getText().toString();
                if (fileName != null && noteText != null){
                   // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(NotesDBHelper.FeedEntry.COLUMN_NAME_TITLE, fileName);
                    values.put(NotesDBHelper.FeedEntry.COLUMN_NAME_TEXT, noteText);

                    System.out.println("ADDED TO DATABASE");
                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            NotesDBHelper.FeedEntry.TABLE_NAME,
                            null,
                            values);

                    aa.insert(fileName,0);
                    aa.notifyDataSetChanged();
                }
            }
        });

        save.setFocusable(false);

        notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Getting title (id) of what is clicked
                TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
                String getTitle = titleTextView.getText().toString();

                String[] allCols = {NotesDBHelper.FeedEntry._ID, NotesDBHelper.FeedEntry.COLUMN_NAME_TITLE, NotesDBHelper.FeedEntry.COLUMN_NAME_TEXT};
                //Turning DB into string array
                Cursor notedb = mDbHelper.getWritableDatabase().query(NotesDBHelper.FeedEntry.TABLE_NAME,
                        allCols, "title=" + "\"" + getTitle + "\"", null, null, null, null);

                //Moving the cursor to the selected point (what was clicked on) in the database
                notedb.moveToFirst();
                System.out.println(notedb.getString(i));

                //Creating intent to pass information
                Intent in = new Intent(getApplicationContext(), NoteDetailActivity.class);

                //Getting the Title and content of the note
                String title = notedb.getString(1);
                String text = notedb.getString(2);
                in.putExtra("title", title);
                in.putExtra("text", text);

                //Going to new display of the note
                startActivity(in);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
