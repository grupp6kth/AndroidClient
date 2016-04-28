package se.learning.home.androidclient.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import DTO.Schedule;
import DTO.ScheduledEvent;
import se.learning.home.androidclient.R;
import se.learning.home.androidclient.controller.Controller;
import se.learning.home.androidclient.interfaces.ScheduleObserver;

public class ScheduleActivity extends CustomActivity implements ScheduleObserver {
    private final Controller controller = super.getController();
    private final ScheduleObserver scheduleObserver = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        eventBttn();
        showSchedule();
    }

    private void showSchedule(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {
                System.out.println("-----Schedule");
                controller.requestScheduleFromServer(scheduleObserver);
                return null;
            }
        }.execute();
    }

    private void eventBttn(){
        final Button eventButton = (Button) findViewById(R.id.addNewEvent);
        if(eventButton !=null){
            eventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextScreen = new Intent (getApplicationContext(), AddNewEventToSchedule.class);
                    startActivity(nextScreen);
                }
            });
        }
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        System.out.println("----Notify schedule");
        ScheduleActivity.this.runOnUiThread(new ShowSchedule(schedule, this));
    }

    private class ShowSchedule implements Runnable{
        private Schedule schedule;
        private Context context;

        public ShowSchedule(Schedule schedule, Context context){
            this.schedule = schedule;
            this.context = context;
        }

        @Override
        public void run() {
            LinearLayout scheduleLayout = (LinearLayout) findViewById(R.id.scheduleLayout);
            for(ScheduledEvent se : schedule.getSchedule()){
                System.out.println(se.getDeviceName());
                ScheduleInformationBlock block = new ScheduleInformationBlock(se.getDeviceID(), se.getDeviceName(), se.getScheduleDescription(), se.getScheduleDate(), context);
                scheduleLayout.addView(block.getScheduleBlock());
            }
        }
    }

    private class ScheduleInformationBlock{
        private int deviceID;
        private String deviceName;
        private String scheduleDate;
        private String scheduleDescription;
        private Context context;
        private LinearLayout scheduleBlock;

        public ScheduleInformationBlock(int deviceID, String deviceName, String scheduleDescription, String scheduleDate, Context context) {
            this.deviceID = deviceID;
            this.deviceName = deviceName;
            this.scheduleDescription = scheduleDescription;
            this.scheduleDate = scheduleDate;
            this.context = context;

            createLayout();
        }

        private void createLayout() {
            /*Outer parent block*/
            scheduleBlock = new LinearLayout(context);
            scheduleBlock.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scheduleBlock.setLayoutParams(params);

            /*Inner name and date block*/
            LinearLayout nameDateBlock = new LinearLayout(context);
            nameDateBlock.setOrientation(LinearLayout.HORIZONTAL);
            nameDateBlock.setLayoutParams(params);

            /*Name text view*/
            TextView deviceNameTextView = new TextView(context);
            deviceNameTextView.setText(this.deviceName);
            deviceNameTextView.setTextColor(Color.BLACK);
            deviceNameTextView.setShadowLayer(10, 0, 0, Color.WHITE);
            nameDateBlock.addView(deviceNameTextView);

            /*Date text ve*/
            TextView scheduleDateTextView = new TextView(context);
            scheduleDateTextView.setText(this.scheduleDate);
            scheduleDateTextView.setGravity(Gravity.RIGHT);
            scheduleDateTextView.setTextColor(Color.WHITE);
            scheduleDateTextView.setShadowLayer(10, 0, 0, Color.BLACK);
            nameDateBlock.addView(scheduleDateTextView);

            /*Add inner block to outer block*/
            scheduleBlock.addView(nameDateBlock);

            /*Create description view and add to outer*/
            TextView descriptionTextView = new TextView(context);
            descriptionTextView.setText(this.scheduleDescription);
            scheduleBlock.addView(descriptionTextView);
        }

        public LinearLayout getScheduleBlock(){
            return scheduleBlock;
        }
    }
}