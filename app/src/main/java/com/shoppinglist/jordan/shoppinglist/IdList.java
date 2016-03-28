package com.shoppinglist.jordan.shoppinglist;


import java.util.ArrayList;
import java.util.Arrays;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class IdList extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.id_list_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadList();

        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Click a list ID to load it!", Snackbar.LENGTH_LONG);
        snack.show();
    }




    @Override
    protected void onStart() {
        super.onStart();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                IdList.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * Load list of all list IDs to the list view
     */
    public void loadList() {
        if(MainActivity.listIds != null){
            final ArrayList<String> list = new ArrayList<>(Arrays.asList(MainActivity.listIds.replaceFirst(";","").split(";")));

            //instantiate custom adapter and use it for the list view
            IdListAdapter adapter = new IdListAdapter(this, list);
            ListView lView = (ListView)findViewById(R.id.list);
            lView.setAdapter(adapter);

            lView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.loadIdFromList = list.get(position);
                    finish();
                    IdList.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
            });
        }
        else{
            Toast.makeText(this, "No IDs exist", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            IdList.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

