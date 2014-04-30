ForceNewDefaultAppChooserPoC
--------------------------

This is a proof-of-concept demo application which shows the possible way of overriding default
application even if it is already set by a user and no other similar application was installed recently.

The solution was developed as a response to [SO question](http://stackoverflow.com/questions/23322040/how-do-i-get-the-default-app-chooser-dialog-to-appear-even-if-the-default-app-is)

###Assumptions:

We have a few browser apps (at least two), the default one was already chosen by user.
By running our app we want to force show an app chooser dialog with an ability to set app as default (for example, to let user set our very app as the default one).

###The problem:

It seems like the only legitimate way to show app chooser again from API is to create an `Intent` with `ACTION_CHOOSER` (by call to `Intent.createChooser(Intent target, CharSequence title)`)
This works fine, but **it does not allow to override default application** for this intent.


###The solution

The solution is to fool OS into thinking that a new (browser) application has just been installed and it is capable of handling the appropriate intent (as its manifest says).
It is easy to create a fake `Activity` component disabled by default, enable it at time we want to handle our the specific `Intent`, and disabling shortly afterwards.

This leads to a "Choose application" dialog (as OS thinks a new application has just been installed) being shown with an option to "Set application as deafult"

Specifically, in our web browser case, if we want to override the default application for handling **http://** links, we do the following:

    <activity
          android:name=".FakeActivity"
          android:enabled="false">
              <intent-filter>
                  <action android:name="android.intent.action.VIEW" />
                  <category android:name="android.intent.category.DEFAULT"/>
                  <data android:scheme="http" />
              </intent-filter>
    </activity>

And from Activity code we would do this:

    void showChooser(){

        PackageManager pm = getPackageManager();
        ComponentName cm = new ComponentName(this, FakeActivity.class);
        pm.setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        runDefaultApp();

        pm.setComponentEnabledSetting(cm, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    void runDefaultApp(){
        Intent selector = new Intent(Intent.ACTION_VIEW);
        selector.setData(Uri.parse("http://stackoverflow.com"));
        startActivity(selector);
    }

