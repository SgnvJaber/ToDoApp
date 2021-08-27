/**************************************************************************************************/
package com.ameerj_saedj.ex3;
/**************************************************************************************************/
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
/**************************************************************************************************/
public class ToDoListActivity extends AppCompatActivity implements View.OnClickListener
{
/**************************************************************************************************/
    private int ALARM_ID = 111;
    private int numberOfAlarms = 0;
    //Variables to Create and Manage Notification
    //Will be used to create notification channel
    private static final String CHANNEL_ID = "channel_main";
    //The Main Channel for notification
    private static final CharSequence CHANNEL_NAME = "Main Channel";
    //Notifcation Manger will be used to send the notification
    private NotificationManager notificationManager;
    //A Brodcast Receiver to monitor the Battery percentage
    private BroadcastReceiver batteryReceiver;//Will be used to monitor the Battery.
    //Intent Filter for Battery Change.
    private IntentFilter filter;
    public static final String MY_DB_NAME = "TodosDB.db";
    private SQLiteDatabase todosDB = null;
    private ArrayList<TodoList> todoList;
    TodoListAdapter todoListAdapter;
    TodoListAdapter filterAdapter;
    private SearchView searchView;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static int MODE_ADD = -1;
    private String userStatus;
    private Calendar calendar;
    private boolean isFilter = false;
/**************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        notificationsSetup();//A function to configure Notification

        calendar = Calendar.getInstance();
        sp = getSharedPreferences("Preference", Context.MODE_PRIVATE);
        userStatus = sp.getString("status", "null");
        searchView = findViewById(R.id.searchView);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        this.setTitle(getTitle() + " (" + userStatus + ")");
        createDB();
        // Get a refernce to the ListView.
        listView = findViewById(R.id.ListViewID);
        if (showTodo() == true) {
            // Create a TodoListAdapter, whose date source is a list of TodoList.
            // The adapter knows how to create list item views for each item in the list.
            todoListAdapter = new TodoListAdapter(this, todoList);
            // Attach the adapter to the listView.
            listView.setAdapter(todoListAdapter);
        }

        searchView.setOnQueryTextListener(OnQueryTextListener);
        listView.setOnItemClickListener(OnListItemClick);
        listView.setOnItemLongClickListener(OnListItemLongClick);
    }

/**************************************************************************************************/
    private void notificationsSetup()
    {
        // 1. Get reference Notification Manager system Service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 2. Create Notification-Channel. ONLY for Android 8.0 (OREO API level 26) and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,    // Constant for Channel ID
                    CHANNEL_NAME,    // Constant for Channel NAME
                    NotificationManager.IMPORTANCE_HIGH);  // for popup use: IMPORTANCE_HIGH
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
/**************************************************************************************************/
    private void createOneTimeAlarm(long datetime, String title)
    {
        if (datetime > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmClockReceiver.class);
            alarmIntent.putExtra("alarmType", "OneTime");
            alarmIntent.putExtra("alarmId", ALARM_ID);
            alarmIntent.putExtra("alarmTitle", title);
            alarmIntent.putExtra("username", userStatus);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            ALARM_ID++;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, datetime, alarmPendingIntent);
            }
            calendar.setTimeInMillis(datetime);
            numberOfAlarms++;
        }
    }
/**************************************************************************************************/
    private SearchView.OnQueryTextListener OnQueryTextListener = new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextSubmit(String query) {
            ToDoListActivity.this.todoListAdapter.getFilter().filter(query);
            return false;
    }
 /**************************************************************************************************/
        @Override
        public boolean onQueryTextChange(String newText)
        {
            ArrayList<TodoList> filteredTodo = new ArrayList<TodoList>();
            isFilter = true;
            for (TodoList todo : todoList) {
                String title = todo.getTitle().toLowerCase();
                String description = todo.getDescription().toLowerCase();
                String currentText = newText.toLowerCase();
                if (title.contains(currentText)) {
                    filteredTodo.add(todo);
                }
                if (description.contains(currentText) && !(title.contains(currentText))) {
                    filteredTodo.add(todo);
                }
            }
            filterAdapter = new TodoListAdapter(ToDoListActivity.this, filteredTodo);
            listView.setAdapter(filterAdapter);
            return false;
        }
    };
/**************************************************************************************************/
    private AdapterView.OnItemClickListener OnListItemClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TodoList item = (TodoList) ((ListView) parent).getAdapter().getItem(position);
            updateEditorMode("UPDATE", item.getID());
            startActivity(new Intent(ToDoListActivity.this, EditorActivity.class));
        }

    };
/**************************************************************************************************/
    private void deleteTodo(int _id, int position)
    {
        //Delete matching id in database
        String sql = "DELETE FROM todos WHERE _id = " + _id + ";";
        todosDB.execSQL(sql);
        todoList.remove(position);
        todoListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Todo was DELETED!", Toast.LENGTH_SHORT).show();
    }
/**************************************************************************************************/
    public void showSimpleAlert(int itemID, String itemTitle, String itemDescription, String itemDate, String itemTime, int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_baseline_delete_forever_24);
        dialog.setTitle(itemTitle);
        dialog.setMessage("Are you sure you want to delete this todo?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTodo(itemID, position);

            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
/**************************************************************************************************/
    private AdapterView.OnItemLongClickListener OnListItemLongClick = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            TodoList item = (TodoList) ((ListView) parent).getAdapter().getItem(position);
            showSimpleAlert(item.getID(), item.getTitle(), item.getDescription(), item.getDate(), item.getTime(), item.getPositionInAdapter());
            return true;
        }
    };
/**************************************************************************************************/
    private void updateEditorMode(String mode, int _id)
    {
        editor = sp.edit();
        editor.putString("mode", mode);
        if (_id != MODE_ADD) {
            editor.putInt("todoID", _id);
        }
        editor.commit();
    }
/**************************************************************************************************/
    public void updateToDoList()
    {
        if (showTodo() == true) {
            // Create a TodoListAdapter, whose date source is a list of TodoList.
            // The adapter knows how to create list item views for each item in the list.
            todoListAdapter = new TodoListAdapter(this, todoList);
            // Attach the adapter to the listView.
            listView.setAdapter(todoListAdapter);
            todoListAdapter.notifyDataSetChanged();
        }
    }
/**************************************************************************************************/
    public void showExitDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_baseline_exit_to_app_pop_up);
        dialog.setTitle("Exit App");
        dialog.setMessage("Do you really want to sign out ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logOut();
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
/**************************************************************************************************/
    private void updateUserStatus(String username)
    {
        editor = sp.edit();
        editor.putString("status", username);
        editor.commit();
    }
/**************************************************************************************************/
    private void logOut()
    {
        startActivity(new Intent(this, LoginActivity.class));
        updateUserStatus("null");
        Toast.makeText(this, "You have successfully logged out!", Toast.LENGTH_SHORT).show();
        finish();
    }
/**************************************************************************************************/
    public void createDB()
    {
        try {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            todosDB = openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
            todoList = new ArrayList<TodoList>();
            // build an SQL statement to create 'username' and 'todos' table (if not exists)
            String users_table = "CREATE TABLE IF NOT EXISTS users (username VARCHAR primary key, password VARCHAR);";
            String todos_table = "CREATE TABLE IF NOT EXISTS todos (_id integer primary key autoincrement, username VARCHAR, title VARCHAR,description VARCHAR,datetime BIGINT);";
            todosDB.execSQL(users_table);
            todosDB.execSQL(todos_table);
        } catch (Exception e) {
            Log.d("debug", "Error Creating Database");
        }
    }

/**************************************************************************************************/
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        // MenuItem icon on AppBar
        MenuItem settingsMenu = menu.add("Logout");
        settingsMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        settingsMenu.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        settingsMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showExitDialog();
                return true;
            }
        });
        return true;
    }
 /**************************************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.searchView:
                break;

            case R.id.btnAdd:
                updateEditorMode("ADD", MODE_ADD);
                startActivity(new Intent(this, EditorActivity.class));
                break;
        }
    }
/**************************************************************************************************/
    public boolean showTodo()
    {
        // A Cursor provides read and write access to database results
        String sql = "SELECT * FROM todos WHERE username = ?";
        String[] whereArgs = {userStatus};
        Cursor cursor = todosDB.rawQuery(sql, whereArgs);
        boolean isNotEmpty = false;
        // Get the index for the column name provided
        int _idColumn = cursor.getColumnIndex("_id");
        int usernameColumn = cursor.getColumnIndex("username");
        int titleColumn = cursor.getColumnIndex("title");
        int descriptionColumn = cursor.getColumnIndex("description");
        int datetimeColumn = cursor.getColumnIndex("datetime");
        int position = 0;
        String List = "";

        // Move to the first row of results & Verify that we have results
        if (cursor.moveToFirst()) {
            do {
                //List is not empty
                isNotEmpty = true;
                // Get the results and store them in a String
                int _idDB = cursor.getInt(_idColumn);
                String usernameDB = cursor.getString(usernameColumn);
                String titleDB = cursor.getString(titleColumn);
                String descriptionDB = cursor.getString(descriptionColumn);
                long datetimeDB = cursor.getLong(datetimeColumn);
                createOneTimeAlarm(datetimeDB, titleDB);
                calendar.setTimeInMillis(datetimeDB);
                String dateDB = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
                String timeDB = new SimpleDateFormat("HH:mm").format(calendar.getTime());
                todoList.add(new TodoList(_idDB, titleDB, descriptionDB, dateDB, timeDB, position));
                position++;
                // Keep getting results as long as they exist
            } while (cursor.moveToNext());

        } else {
            Toast.makeText(this, userStatus + " todo list is empty!", Toast.LENGTH_SHORT).show();
            isNotEmpty = false;
        }
        return isNotEmpty;
    }
/**************************************************************************************************/
    @Override
    protected void onResume()
    {
        todoList.clear();
        updateToDoList();
        super.onResume();
    }
/**************************************************************************************************/
    @Override
    protected void onPause()
    {
        super.onPause();
    }
/**************************************************************************************************/
}
/**************************************************************************************************/
