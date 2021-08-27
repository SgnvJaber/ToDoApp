/**************************************************************************************************/
package com.ameerj_saedj.ex3;
/**************************************************************************************************/
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**************************************************************************************************/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
/**************************************************************************************************/
    //SQL lite variables
    public static final String MY_DB_NAME = "TodosDB.db";
    private SQLiteDatabase todosDB = null;
    //Shared Preferences variables:
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String userStatus;
    //EditText fields and log in button variables.
    private EditText editUserNameField, editPasswordField;
    private Button btnLogIn;
    //User's input states
    //will be used to check if the User exists and provided a wrong password.
    private static int USER_WITH_WRONG_PASSWORD = 0;
    //will be used to check if the User exists and provided a correct password.
    private static int USER_WITH_CORRECT_PASSWORD = 1;
    //will be used to check if a new User credentials were provided.
    private static int NEW_USER = 3;
/**************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Todo Login");
        //Create Database if it doesn't exist.
        createDB();
        //Create Shared Prefrence if it doesn't exist.
        sp = getSharedPreferences("Preference", Context.MODE_PRIVATE);
        //UserStatus-User is logged in(Store name) or not.
        userStatus = sp.getString("status", "null");
        //Finding fields and log in button id.
        editUserNameField = findViewById(R.id.editUserNameField);
        editPasswordField = findViewById(R.id.editPasswordField);
        btnLogIn = findViewById(R.id.btnLogInID);
        //Setting Click listeners.
        editUserNameField.setOnClickListener(this);
        editPasswordField.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);
        //If user logged in before then navigate to ToDoListActivity.
        if (userStatus.compareTo("null") != 0)
        {
            navigate();
        }
    }
/**************************************************************************************************/
    //A function to navigate to ToDoListActivity on successful log in
    private void navigate()
    {
        startActivity(new Intent(this, ToDoListActivity.class));
        //Destroy the current activity once done moving to ToDoListActivity.
        finish();
    }
/**************************************************************************************************/
    //handling the 3 dots menu
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        // MenuItems in AppBar
        // Sub-MenuItem in 3 Dots menu
        MenuItem aboutMenu = menu.add("About");
        MenuItem exitMenu = menu.add("Exit");
        //Handling About dialog
        aboutMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAboutDialog();
                return true;
            }
        });
        //Handling Exit dialog
        exitMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showExitDialog();
                return true;
            }
        });
        return true;
    }
/**************************************************************************************************/
    //Display About Dialog on menu click.
    public void showAboutDialog()
    {
        String aboutApp = getString(R.string.app_name) + " (" + getPackageName() + ")" + "\n\n" +
                "By Ameer Jaber & Saed Jaber, 25/5/21.";
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("About App");
        dialog.setMessage(aboutApp);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
/**************************************************************************************************/
    //Display Exit Dialog on menu click.
    public void showExitDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        dialog.setTitle("Exit App");
        dialog.setMessage("Do you really want to exit ToDoApp ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // close this activity
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
    //A function to handle log in input fields
    private void handleLogIn()
    {
        if (isEmpty(editUserNameField) && isEmpty(editPasswordField)) {
            Toast.makeText(this, "Please provide username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editUserNameField)) {
            Toast.makeText(this, "Username cannot be empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editPasswordField)) {
            Toast.makeText(this, "Password cannot be empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editUserNameField) == false && isEmpty(editPasswordField) == false) {
            if (userExists() == USER_WITH_CORRECT_PASSWORD) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                navigate();
                updateUserStatus(editUserNameField.getText().toString().trim());
                return;
            } else if (userExists() == NEW_USER) {
                Toast.makeText(this, "Creating New User!", Toast.LENGTH_SHORT).show();
                addUser();
                navigate();
                updateUserStatus(editUserNameField.getText().toString().trim());
                return;
            } else if (userExists() == USER_WITH_WRONG_PASSWORD) {
                Toast.makeText(this, "Wrong password provided...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
/**************************************************************************************************/
    public void createDB()
    {
        try {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            todosDB = openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
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
    private boolean isEmpty(EditText field) {
        if (field.getText().toString().trim().length() > 0) {
            return false;
        }
        return true;
    }
/**************************************************************************************************/
    private int userExists()
    {
        String userName = editUserNameField.getText().toString().trim();
        String passwordField = editPasswordField.getText().toString().trim();
        String username = "";
        String password = "";
        // A Cursor provides read and write access to database results
        String sql = "SELECT * FROM users WHERE username = ?";
        String[] whereArgs = {userName};
        Cursor cursor = todosDB.rawQuery(sql, whereArgs);
        if (cursor.getCount() == 0) {
            cursor.close();
            return NEW_USER;
        }
        if (cursor.moveToFirst()) {
            // Get the index for the column name provided
            int usernameColumn = cursor.getColumnIndex("username");
            int passwordColumn = cursor.getColumnIndex("password");
            String contactList = "";
            // Move to the first row of results & Verify that we have results
            // Get the results and store them in a String
            username = cursor.getString(usernameColumn);
            password = cursor.getString(passwordColumn);
            cursor.close();
        }
        if (passwordField.compareTo(password) != 0) {
            return USER_WITH_WRONG_PASSWORD;
        }
        return USER_WITH_CORRECT_PASSWORD;
    }

/**************************************************************************************************/
    public void addUser()
    {
        // Get the contact name and email entered
        String userName = editUserNameField.getText().toString().trim();
        String userPassword = editPasswordField.getText().toString().trim();

        // Execute SQL statement to insert new data
        String sql = "INSERT INTO users (username, password) VALUES ('" + userName + "', '" + userPassword + "');";
        todosDB.execSQL(sql);
        Toast.makeText(this, userName + " was inserted!", Toast.LENGTH_SHORT).show();
    }
/**************************************************************************************************/
    private void updateUserStatus(String username)
    {
        editor = sp.edit();
        editor.putString("status", username);
        editor.commit();
    }
/**************************************************************************************************/
@Override
public void onClick(View v)
{
    switch (v.getId()) {
        case R.id.editUserNameField:
            break;

        case R.id.editPasswordField:
            break;

        case R.id.btnLogInID:
            handleLogIn();
            break;
    }
}
/**************************************************************************************************/
}
/**************************************************************************************************/
