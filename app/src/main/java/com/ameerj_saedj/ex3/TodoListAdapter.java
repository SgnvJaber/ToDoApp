/**************************************************************************************************/
package com.ameerj_saedj.ex3;
/**************************************************************************************************/
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**************************************************************************************************/
/**
 * TodoListAdapter class in ArrayAdapter that can provide the layout for each list
 * based on a data source, which os a list of TodoList objects.
 */
/**************************************************************************************************/
public class TodoListAdapter extends ArrayAdapter<TodoList>
{
/**************************************************************************************************/
    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is ued to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param todoList A List of TodoList objects to display in a list.
     */
/**************************************************************************************************/
    public TodoListAdapter(Activity context, ArrayList<TodoList> todoList)
    {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews, the adapter is not
        // going to use this argument, so it can be any value. Here, we used 0.
        super(context, 0, todoList);
    }
/**************************************************************************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            convertView = View.inflate(getContext(), R.layout.list_item, null);
        }
        // Get the {@link TodoList} object located at this position in the list
        TodoList currentTodoList = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID(listTitle)
        TextView txvTitle = convertView.findViewById(R.id.listTitle);
        // Get the title name from the current TodoList object and
        // set this text on the title TextView
        txvTitle.setText(currentTodoList.getTitle());

        // Find the TextView in the list_item.xml layout with the ID (listDescription)
        TextView txvDescription = convertView.findViewById(R.id.listDescription);
        // Get the description from the current TodoList object and
        // set this text on the description TextView
        txvDescription.setText(currentTodoList.getDescription());


        // Find the TextView in the list_item.xml layout with the ID (listDate)
        TextView txvDate = convertView.findViewById(R.id.listDate);
        // Get the date from the current TodoList object and
        // set this text on the date TextView
        txvDate.setText(currentTodoList.getDate());

        // Find the TextView in the list_item.xml layout with the ID (listTime)
        TextView txvTime = convertView.findViewById(R.id.listTime);
        // Get the time from the current TodoList object and
        // set this text on the time TextView
        txvTime.setText(currentTodoList.getTime());

        // Return the whole list item layout (containing 4 TextViews)
        // so that it can be shown in the ListView
        return convertView;
    }
/**************************************************************************************************/
}
/**************************************************************************************************/

