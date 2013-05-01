/*
**
** Copyright 2013, The MoKee OpenSource Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.mokee.permissionsmanager.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;

import com.mokee.permissionsmanager.R;
import com.mokee.permissionsmanager.adapter.AppsAdapter;
import com.mokee.permissionsmanager.domain.AppInfoDomain;

public class MainActivity extends Activity implements OnItemClickListener, OnCheckedChangeListener {
    private ListView listView;

    public static ArrayList<AppInfoDomain> aidList = new ArrayList<AppInfoDomain>();

    private AppsAdapter appsAdapter;

    private Switch mEnabled;

    private Context mContext;

    private Handler mHandler = new Handler();

    private int currentRevokedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_app_list);
        mContext = this;
        listView = (ListView) findViewById(R.mk.listView);
        listView.setOnItemClickListener(this);
        boolean mIsEnabled = Settings.Secure.getInt(mContext.getContentResolver(),
             Settings.Secure.ENABLE_PERMISSIONS_MANAGEMENT, 0) == 1;
        if (mIsEnabled)
            getInstalledAppsList();//faster speed when open
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // The user clicked on the Messaging icon in the action bar. Take them back from
                // wherever they came from
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SharedPreferences prefs = getPreferences(0);
        if (!prefs.getBoolean("show_disclaimer", false)) {
            showDisclaimer();
        }
    }

    private void showDisclaimer() {
        (new AlertDialog.Builder(mContext))
                .setTitle(R.string.dlg_disclaimer_title)
                .setMessage(R.string.dlg_disclaimer_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_disclaimer_agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = getPreferences(0).edit();
                                editor.putBoolean("show_disclaimer", true).commit();
                            }
                        })
                .setNegativeButton(R.string.dlg_disclaimer_tldr,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        }).show();
    }

    /**
     * get All
     */
    private void getInstalledAppsList() {
        aidList.clear();
        mHandler.post(new Runnable() {
            public void run() {
                PackageManager pm = mContext.getPackageManager();
                List<PackageInfo> allPackages = pm
                        .getInstalledPackages(PackageManager.GET_PERMISSIONS);
                for (int i = 0; i < allPackages.size(); i++) {
                    PackageInfo pi = allPackages.get(i);
                    if (!isThisASystemPackage(pi)) {
                        if (pi.requestedPermissions != null && pi.requestedPermissions.length > 0) {
                            AppInfoDomain aid = new AppInfoDomain();
                            aid.appName = pm.getApplicationLabel(pi.applicationInfo).toString();
                            aid.packageInfo = pi;
                            aid.appVersionName = pi.versionName;
                            try {
                                aid.icon = pm.getApplicationIcon(pi.packageName);
                                aid.revokedPerList = new ArrayList<String>(Arrays.asList(pm
                                        .getRevokedPermissions(pi.packageName)));
                                aid.disabledNum = aid.getRevokedPerList().size();
                                aid.enabledNum = (pi.requestedPermissions.length - aid
                                        .getRevokedPerList().size());
                                aid.totalPermission = pi.requestedPermissions.length;
                            } catch (NameNotFoundException e) {
                                aid.icon = pm.getDefaultActivityIcon();
                                // e.printStackTrace();
                            }
                            aidList.add(aid);
                        }
                    }
                }
                appsAdapter = new AppsAdapter(mContext, aidList);
                listView.setAdapter(appsAdapter);
            }
        });
    }

    private static boolean isThisASystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(mContext, PermissionInfoActivity.class);

        currentRevokedNum = aidList.get(position).getRevokedPerList().size();
        intent.putExtra("position", position);
        // intent.putExtra("aid", aidList.get(position));
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manager, menu);
        MenuItem item = menu.getItem(0);
        mEnabled = (Switch) item.getActionView();
        mEnabled.setChecked(Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.ENABLE_PERMISSIONS_MANAGEMENT, 0) == 1);
        mEnabled.setOnCheckedChangeListener(this);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean newValue) {
        if (compoundButton == mEnabled) {
            if (newValue) {
                Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.ENABLE_PERMISSIONS_MANAGEMENT, 1);
                    getInstalledAppsList();
                listView.setVisibility(View.VISIBLE);
            } else {
                Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.ENABLE_PERMISSIONS_MANAGEMENT, 0);
                listView.setVisibility(View.GONE);
            }
            
            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1024) {
            if (currentRevokedNum != data.getIntExtra("newRevokedNum", 0)) {
                appsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        aidList.clear();
        super.finish();
    }

}
