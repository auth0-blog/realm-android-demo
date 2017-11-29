package com.echessa.tasky;

import android.app.Application;

import com.echessa.tasky.models.Migration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskListApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tasky.realm")
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
