/**************************************************************************************************/
package com.ameerj_saedj.ex3;
/**************************************************************************************************/
/**
 * TodoList class represents a single task
 * Each object has 4 properties: title, description,date and time.
 */
/**************************************************************************************************/
public class TodoList
{
/**************************************************************************************************/
    //The id of the task(e.g Todo1,Todo2,...)
    private int _id;

    //The title of the task(e.g Todo1,Todo2,...)
    private String titleField;

    //Task's description
    private String descriptionField;

    //Task's date
    private String dateField;

    //Task's time
    private String timeField;

    //Task's position in adapter
    private int positionInAdapter;
/**************************************************************************************************/
    /*
     * Create a new TodoList object
     *
     * @param title is the title of the Task
     * @param description is the description of the Task
     * @param date is the date of the Task
     * @param time is the time of the Task
     */
/**************************************************************************************************/
    public TodoList(int _id, String title, String description, String date, String time, int position)
    {
        this._id = _id;
        this.titleField = title;
        this.descriptionField = description;
        this.dateField = date;
        this.timeField = time;
        this.positionInAdapter = position;
    }
/**************************************************************************************************/
    /**
     * Get the id of the Task
     */
    public int getID() {
        return this._id;
    }
/**************************************************************************************************/

    /**
     * Get the title of the Task
     */
    public String getTitle() {
        return this.titleField;
    }
/**************************************************************************************************/
    /**
     * Get the description of the Task
     */
    public String getDescription() {
        return this.descriptionField;
    }
/**************************************************************************************************/
    /**
     * Get the date of the Task
     */
    public String getDate() {
        return this.dateField;
    }
/**************************************************************************************************/
    /**
     * Get the time of the Task
     */
    public String getTime() {
        return this.timeField;
    }
/**************************************************************************************************/
    /**
     * Get the actual position of the Task
     */
    public int getPositionInAdapter() {
        return this.positionInAdapter;
    }
/**************************************************************************************************/
}
/**************************************************************************************************/
