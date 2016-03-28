/*
* Shopping List
*
* Created by: Jordan Elvidge
* Last Modified: March 23, 2016
*
* About: A very simple shopping list app that lets users create various
*        shopping lists and add/remove items to it
*
*
* Design: - The app uses shared preferences to store data between sessions
*         - Lists are stored using the list ID as the key and the value is a string
*           of the combined items separated by ;
*                    {String key, String value}
*           Example: {"list ID 1", ";item 1;item 2;item 3;item 4;"}
*         - This method provides a simple and effective way to store/retrieve lists that are
*           properly ordered unlike various other data structures which don't preserve ordering
*         - All naming for IDs and items are case insensitive
*         - Lists will auto-load for convenience when user enters their ID in the ID field
*
*/

package com.shoppinglist.jordan.shoppinglist;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements OnClickListener{

    static String listItems;
    static String backupListItems;
    static String listIds;

    static String loadIdFromList = "";
    static String previousListId = "";

    static int refreshList = 0;
    static int undoOption  = 0;

    String previousAutoLoad = "";
    int exit = 0;


    private EditText list_id_field;
    private EditText list_item_field;
    private ListView lView;


    private Button Load_List_Button;
    private Button Show_IDs_Button;
    private ImageButton Add_Item_Button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Load information from previous sessions
        SharedPreferences prefs = getSharedPreferences("com.shoppinglist.app", 0);
        listIds = prefs.getString("listIDs", ";");
        listItems = "";

        // Set UI objects with view IDs
        list_id_field    = (EditText)findViewById(R.id.list_id_field);
        list_item_field  = (EditText)findViewById(R.id.list_item_field);
        Load_List_Button = (Button) findViewById(R.id.Load_List_Button);
        Show_IDs_Button  = (Button) findViewById(R.id.Show_IDs_Button);
        Add_Item_Button  = (ImageButton) findViewById(R.id.Add_Item_Button);
        lView            = (ListView)findViewById(R.id.list);

        // Set onClickListenrs for Buttons
        Load_List_Button.setOnClickListener(this);
        Show_IDs_Button.setOnClickListener(this);
        Add_Item_Button.setOnClickListener(this);


        // Add key listener to let user press the enter key to add to the list
        list_item_field.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addItem();
                    return true;
                }

                return false;
            }
        });



        // Create a timer to periodically perform necessary tasks
        TimerTask scanTaskLI;
        final Handler handlerLI = new Handler();
        Timer tLI = new Timer();

        scanTaskLI = new TimerTask() {
            public void run() {

                // only destroy activity after canceling thread
                if(exit == 1){
                    this.cancel();
                    finish();
                }

                handlerLI.post(new Runnable() {
                    public void run() {

                        // Load a list when user has entered a saved list ID
                        if (!previousAutoLoad.equals(getField(list_id_field)) && listIds.contains(";" + getField(list_id_field) + ";")) {
                            loadList(getField(list_id_field));
                            previousAutoLoad = getField(list_id_field);
                            lView.setVisibility(View.VISIBLE);
                        }
                        // Remove list when user has entered a list ID that doesn't exist
                        else if(!previousAutoLoad.equals(getField(list_id_field)) && !listIds.contains(";" + getField(list_id_field) + ";")) {
                            listItems = "";
                            previousAutoLoad = getField(list_id_field);
                            lView.setVisibility(View.GONE);
                        }

                        // Refresh the list (used for deletions)
                        if(refreshList == 1){
                            saveList(getField(list_id_field));
                            refreshList = 0;
                        }

                        // Load a specific list selected by the "All IDs" list screen
                        if(loadIdFromList.length() > 0){
                            list_id_field.setText(loadIdFromList);
                            previousListId = loadIdFromList;
                            loadList(getField(list_id_field));
                            loadIdFromList = "";
                        }

                        // Display Snackbar with "undo" option when user deletes an item
                        if(undoOption == 1) {
                            showSnack();
                            undoOption = 0;
                        }

                    }
                });
            }};
        tLI.schedule(scanTaskLI, 500, 50);

    }




    @Override
    protected void onStart() {
        super.onStart();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public void onClick(View v) {
        final Animation animate = AnimationUtils.loadAnimation(this, R.anim.button_fade);

        switch (v.getId()) {

            // User selects to load a list (not really functional since lists will auto load as they are typed)
            case R.id.Load_List_Button:
                Load_List_Button.startAnimation(animate);

                // No list ID has been entered
                if(list_id_field.length() == 0) {
                    Toast.makeText(this, "Please enter a list ID.", Toast.LENGTH_SHORT).show();
                }

                hideKeyboard();

                break;

            // User selects to view all IDs
            case R.id.Show_IDs_Button:
                Show_IDs_Button.startAnimation(animate);

                // Open page with a list of list IDs
                if(!listIds.equals(";")) {
                    hideKeyboard();
                    Intent intent = new Intent(MainActivity.this, IdList.class);
                    startActivity(intent);
                    MainActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                // Inform user that no list IDs have been created yet
                else {
                    Toast.makeText(this, "No list IDs have been create yet.", Toast.LENGTH_SHORT).show();
                }

                break;

            // User selects to add an item
            case R.id.Add_Item_Button:
                Add_Item_Button.startAnimation(animate);
                addItem();

                break;
        }
    }




    public String getField(EditText field){
        return field.getText().toString().trim();
    }




    /**
     * Attempt to add new item to the current list
     */
    public void addItem(){

        // User hasn't entered a list ID
        if(getField(list_id_field).length() == 0) {
            Toast.makeText(this, "Please enter a list ID before adding an item", Toast.LENGTH_SHORT).show();
        }

        // User hasn't entered an item
        else if(getField(list_item_field).length() == 0) {
            Toast.makeText(this, "No item added", Toast.LENGTH_SHORT).show();
        }

        // User has entered a list ID and an item to add to the list
        else {

            // Able to add item to the list if it hasn't been entered already
            if (isNewItem(";" + getField(list_item_field) + ";") && !(listItems.endsWith(";" + getField(list_item_field)))) {
                hideKeyboard();

                if (listItems != null) {
                    listItems = listItems + ";" + getField(list_item_field);
                }
                else {
                    listItems = getField(list_item_field) + ";";
                }

                backupListItems = listItems;
                saveList(getField(list_id_field));
                saveListId(getField(list_id_field));
                list_item_field.setText("");
            }

            // Duplicate item found (don't add to list)
            else {
                Toast.makeText(this, "Item is already in the list", Toast.LENGTH_SHORT).show();
            }
        }
    }




    /**
     * Manually hide keyboard
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }




    /**
     * Check if item has already been added to the list to avoid duplicate entries
     * @param item         the item to be added to the list
     * @return boolean     should the item be added to the list (is it not a duplicate)
     */
    public boolean isNewItem(String item) {

        if(listItems == null) {
            return true;
        }
        else if(listItems.contains(item)) {
            return false;
        }

        return true;
    }




    /**
     * Save the list IDs that have been created
     * @param listID      the name of the list ID
     */
    public void saveListId(String listID) {

        if(!listIds.contains(";" + listID + ";")) {

            SharedPreferences settings = getSharedPreferences("com.shoppinglist.app", 0);
            SharedPreferences.Editor editor = settings.edit();

            listIds = listIds + listID + ";";

            editor.putString("listIDs", listIds);
            editor.apply();

            Toast.makeText(this, "New list created!", Toast.LENGTH_SHORT).show();
            lView.setVisibility(View.VISIBLE);
        }

    }




    /**
     * Save the current list of items to a shared preference
     * @param listID      the current list ID being used (used as a unique key to save the list)
     */
    public void saveList(String listID) {
        SharedPreferences settings = getSharedPreferences("com.shoppinglist.app", 0);
        SharedPreferences.Editor editor = settings.edit();

        listItems = listItems.replace(";;", ";");

        editor.putString(listID, listItems);
        editor.apply();

        loadList(listID);
    }




    /**
     * Load the current list of items from a shared preference and populate the list view
     * @param listID      the current list ID being used (used as a unique key to save the list)
     */
    public void loadList(String listID) {
        SharedPreferences prefs = getSharedPreferences("com.shoppinglist.app", 0);
        listItems = prefs.getString(listID, null);

        if(listItems != null){
            ArrayList<String> list = new ArrayList<>(Arrays.asList(listItems.replaceFirst(";","").split(";")));

            //instantiate custom adapter and use it for the list view
            ShoppingListAdapter adapter = new ShoppingListAdapter(this, list);
            ListView lView = (ListView)findViewById(R.id.list);
            lView.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "That list ID does not exist", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * Delete an item from the list when user is "Done/Complete" with it
     * @param position      the index of the item to be deleted
     */
    public static void deleteItem(int position) {

        ArrayList<String> list = new ArrayList<>(Arrays.asList(listItems.replaceFirst(";", "").split(";")));

        backupListItems = listItems;
        listItems = listItems + ";";

        if (listItems.contains(";" + list.get(position) + ";")) {
            listItems = listItems.replace(";" + list.get(position) + ";", ";");
        }

        listItems = listItems.replace(";;", ";");

        refreshList = 1;
        undoOption = 1;

    }




    /**
     * Undo a deletion of an item
     */
    public void undoDelete(){
        listItems = backupListItems;
        refreshList = 1;
    }




    /**
     * Show Snackbar for ability to undo a deletion
     */
    public void showSnack(){

        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Item was completed", Snackbar.LENGTH_LONG).setAction("UNDO",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoDelete();
                    }
                });

        snack.show();
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Override back button to ensure that timer thread is destroyed
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit = 1;
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
