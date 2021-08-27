package com.ameerj_saedj.ex3;
/**************************************************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**************************************************************************************************/
public class EditorActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MY_DB_NAME = "TodosDB.db";
    private SQLiteDatabase todosDB = null;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String userName;
    private String editorMode;
    private int todoID;
    private int yearDB;
    private int monthDB;
    private int dayDB;
    private int hourDB;
    private int minuteDB;
    private EditText editTitleField, editDescriptionField, editDateField, editTimeField;
    private TextView editorModeTitle;
    private Button btnDate, btnTime, btnSubmit;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private static int DATE_LENGTH = 10;//'d'+'d'+'m'+'m'+'y'+'y'+'y'+'y'+'/'+'/'+'/'=10
    private static int TIME_LENGTH = 5;//'h'+'h'+':'+'m'+'m'=5
    private static long dateTime;
    private Calendar calendar;
    private Calendar timeCalendar;
    private Calendar dateCalendar;
    private Calendar storedCalendar;
    Date myDate;
    Date myTime;
/**************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        createDB();
        sp = getSharedPreferences("Preference", Context.MODE_PRIVATE);
        userName = sp.getString("status", "null");
        editorMode = sp.getString("mode", "null");
        todoID = sp.getInt("todoID", -1);
        //Setting View ID
        editTitleField = findViewById(R.id.editTitleField);
        editDescriptionField = findViewById(R.id.editDescriptionField);
        editDateField = findViewById(R.id.editDateField);
        editTimeField = findViewById(R.id.editTimeField);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnSubmit = findViewById(R.id.btnSubmit);
        editorModeTitle = findViewById(R.id.editorModeTitle);
        //Setting Listeners
        editTitleField.setOnClickListener(this);
        editDescriptionField.setOnClickListener(this);
        editDateField.setOnClickListener(this);
        editTimeField.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        editorModeTitle.setOnClickListener(this);
        calendar = Calendar.getInstance();
        timeCalendar = Calendar.getInstance();
        dateCalendar = Calendar.getInstance();
        storedCalendar = Calendar.getInstance();

        if (todoID != -1 && editorMode.compareTo("UPDATE") == 0) {
            enableUpdateMode();
            loadToDo();
        }
    }

/**************************************************************************************************/
    private void enableUpdateMode()
    {
        editorModeTitle.setText("UPDATE Todo (id=" + todoID + ")");
        btnSubmit.setText("UPDATE");
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
    private boolean isEmpty(EditText field)
    {
        if (field.getText().toString().trim().length() > 0) {
            return false;
        }
        return true;
    }
/**************************************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {

            case R.id.editTitleField:
                break;

            case R.id.editDescriptionField:
                break;

            case R.id.editDateField:
                isDate();
                break;
            case R.id.editTimeField:
                break;

            case R.id.btnDate:
                pickDate();
                break;

            case R.id.btnTime:
                pickTime();
                break;
            case R.id.btnSubmit:
                handleSubmit();
                break;
        }
    }
/**************************************************************************************************/
    private boolean isDate()
    {
        boolean check = false;
        String date = editDateField.getText().toString().trim();
        if (date.length() != DATE_LENGTH) {
            check = false;
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date mydate = sdf.parse(date);
            check = true;
        } catch (ParseException ex) {
            check = false;
            return false;
        }
        return check;
    }

/**************************************************************************************************/
    private boolean isTime()
    {
        boolean check = false;
        String time = editTimeField.getText().toString().trim();
        if (time.length() != TIME_LENGTH) {
            check = false;
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            sdf.parse(time);
            check = true;
        } catch (ParseException ex) {
            check = false;
            return false;
        }
        return check;
    }
/**************************************************************************************************/
    private void pickDate()
    {
        String dateNow = new SimpleDateFormat("dd/MM/yyyy").format(dateCalendar.getTime());
        if (editorMode.compareTo("UPDATE") != 0) {
            yearDB = dateCalendar.get(Calendar.YEAR);
            monthDB = dateCalendar.get(Calendar.MONTH);
            dayDB = dateCalendar.get(Calendar.DAY_OF_MONTH);
        }
        datePickerDialog = new DatePickerDialog(this, OnDateSetListener, yearDB, monthDB, dayDB);
        datePickerDialog.show();
        //Changing buttons colors to blue
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }
/**************************************************************************************************/
    private void pickTime()
    {
        String timeNow = new SimpleDateFormat("HH:mm").format(timeCalendar.getTime());
        if (editorMode.compareTo("UPDATE") != 0) {
            hourDB = timeCalendar.get(Calendar.HOUR_OF_DAY);
            minuteDB = timeCalendar.get(Calendar.MINUTE);
        }
        timePickerDialog = new TimePickerDialog(this, OnTimeSetListener, hourDB, minuteDB, true);
        timePickerDialog.show();
        //Changing buttons colors to blue
        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }
/**************************************************************************************************/
    private TimePickerDialog.OnTimeSetListener OnTimeSetListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeCalendar.set(0, 0, 0, hourOfDay, minute, 0);
            String picked_time = new SimpleDateFormat("HH:mm").format(timeCalendar.getTime());
            editTimeField.setText(picked_time);
        }

    };
/**************************************************************************************************/
    private DatePickerDialog.OnDateSetListener OnDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            dateCalendar.set(selectedYear, selectedMonth, selectedDay);


            String picked_date = new SimpleDateFormat("dd/MM/yyyy").format(dateCalendar.getTime());
            editDateField.setText(picked_date);
        }
    };
    /**************************************************************************************************/
    private void loadToDo()
    {
        // A Cursor provides read and write access to database results
        String sql = "SELECT * FROM todos WHERE _id = ?";
        String[] whereArgs = {Integer.toString(todoID)};
        Cursor cursor = todosDB.rawQuery(sql, whereArgs);
        boolean isNotEmpty = false;
        // Get the index for the column name provided
        int _idColumn = cursor.getColumnIndex("_id");
        int usernameColumn = cursor.getColumnIndex("username");
        int titleColumn = cursor.getColumnIndex("title");
        int descriptionColumn = cursor.getColumnIndex("description");
        int datetimeColumn = cursor.getColumnIndex("datetime");
        // Move to the first row of results & Verify that we have results
        if (cursor.moveToFirst()) {
            // Get the results and store them in a String
            int _idDB = cursor.getInt(_idColumn);
            String usernameDB = cursor.getString(usernameColumn);
            String titleDB = cursor.getString(titleColumn);
            String descriptionDB = cursor.getString(descriptionColumn);
            long datetimeDB = cursor.getLong(datetimeColumn);
            calendar.setTimeInMillis(datetimeDB);
            String dateDB = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            String timeDB = new SimpleDateFormat("HH:mm").format(calendar.getTime());
            yearDB = calendar.get(Calendar.YEAR);
            monthDB = calendar.get(Calendar.MONTH);
            dayDB = calendar.get(Calendar.DAY_OF_MONTH);
            hourDB = calendar.get(Calendar.HOUR_OF_DAY);
            minuteDB = calendar.get(Calendar.MINUTE);
            editTitleField.setText(titleDB);
            editDescriptionField.setText(descriptionDB);
            editDateField.setText(dateDB);
            editTimeField.setText(timeDB);
        } else {
            Toast.makeText(this, "error loading TODO", Toast.LENGTH_SHORT).show();
        }
    }
/**************************************************************************************************/
    private void addTodo()
    {
        // Get the title name and description entered
        String title = editTitleField.getText().toString().trim();
        String description = editDescriptionField.getText().toString().trim();
        // Execute SQL statement to insert new data
        String sql = "INSERT INTO todos (username ,title,description,datetime) VALUES" +
                " ('" + userName + "', '" + title + "', '" + description + "', '" + dateTime + "');";
        todosDB.execSQL(sql);
        Toast.makeText(this, "Todo was ADDED", Toast.LENGTH_SHORT).show();
    }
/**************************************************************************************************/
    private void updateTodo()
    {
        String updateTitle = editTitleField.getText().toString().trim();
        String updateDescription = editDescriptionField.getText().toString().trim();
        String updateDate = editDateField.getText().toString().trim();
        String updateTime = editTimeField.getText().toString().trim();


        //UPDATE matching id in database
        String sql = " UPDATE todos " +
                "SET" +
                " username = '" + userName + "'," +
                " title = '" + updateTitle + "'," +
                " description = '" + updateDescription + "'," +
                "datetime='" + dateTime + "' " +
                "WHERE _id = '" + todoID + "';";

        todosDB.execSQL(sql);
        Toast.makeText(this, "Todo was UPDATED", Toast.LENGTH_SHORT).show();
    }

/**************************************************************************************************/
    private void handleSubmit()
    {

        //Validation
        if (isEmpty(editTitleField)) {
            Toast.makeText(this, "Please provide title!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editDescriptionField)) {
            Toast.makeText(this, "Please provide Description!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editDateField)) {
            Toast.makeText(this, "Please provide date!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(editTimeField)) {
            Toast.makeText(this, "Please provide time!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDate() == false) {
            Toast.makeText(this, "invalid date!...correct format is dd/mm/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTime() == false) {
            Toast.makeText(this, "invalid date!...correct format is HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }
        //if we reached this line and passed all tests,then we have time and date in the correct format
        String pickedDate = editDateField.getText().toString().trim();
        String pickedTime = editTimeField.getText().toString().trim();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setLenient(false);
            timeFormat.setLenient(false);
            myDate = dateFormat.parse(pickedDate);
            myTime = timeFormat.parse(pickedTime);
        } catch (ParseException ex) {
            Toast.makeText(this, "Error parsing data please check again!", Toast.LENGTH_SHORT).show();
        }
        dateCalendar.setTime(myDate);
        timeCalendar.setTime(myTime);
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);//Month start from 0
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = timeCalendar.get(Calendar.MINUTE);
        int second = 0;//setting zero seconds.
        calendar.set(year, month, day, hour, minute, second);
        dateTime = calendar.getTimeInMillis();

        if (editorMode.compareTo("UPDATE") == 0) {
            updateTodo();
            startActivity(new Intent(this, ToDoListActivity.class));
        } else {
            addTodo();
            //Empty fields if user want to add another task
            resetFields();
            //startActivity(new Intent(this, ToDoListActivity.class));

        }
    }

    /**************************************************************************************************/
    private void resetFields()
    {
        editTitleField.setText("");
        editDescriptionField.setText("");
        editTimeField.setText("");
        editDateField.setText("");
    }
 /**************************************************************************************************/
    @Override
    protected void onDestroy() {
        todosDB.close();
        super.onDestroy();
    }
/**************************************************************************************************/
}
/**************************************************************************************************/