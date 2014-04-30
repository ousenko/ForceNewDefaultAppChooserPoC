package com.realitybytes.poc.appchooser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View changeDefaultButton = findViewById(R.id.changeDefButton);
        changeDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();//this shows the app chooser dialog with an ability to choose default app
            }
        });
        View testDefaultButton = findViewById(R.id.testDefaultButton);
        testDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDefaultApp();
            }
        });
    }



    void showChooser(){
        PackageManager pm = getPackageManager();
        ComponentName cm = new ComponentName(this, FakeActivity.class);

        /*By default our FakeActivity is disabled. By enabling it we lead OS into thinking
        * that a new app has just been installed and it is capable of handling a specified intent
        * */
        pm.setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        runDefaultApp();
        /*
        * As our FakeActivity is completely blank and dummy, we disable it, thus preventing it from being
        * shown to end-user in app chooser dialog
        * */
        pm.setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    void runDefaultApp(){

        /*For testing purpose we specify an intent which would handle only http-links (as we want to override
        * our default web-browser app).
        * https, ftp and other are left aside for the sake of simplicity
        * */
        Intent selector = new Intent(Intent.ACTION_VIEW);
        selector.setData(Uri.parse("http://stackoverflow.com"));
        startActivity(selector);
    }

}
