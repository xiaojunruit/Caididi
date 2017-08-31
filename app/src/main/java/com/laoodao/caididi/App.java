package com.laoodao.caididi;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ezy on 15-9-28.
 */
public class App extends Application {



    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            private int count = 0;
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

                if (count == 0) {
                }
                count++;
            }
            @Override
            public void onActivityResumed(Activity activity) {
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }



}
