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

package com.mokee.permissionsmanager.adapter;

import com.mokee.permissionsmanager.R;
import com.mokee.permissionsmanager.domain.AppInfoDomain;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<AppInfoDomain> aidList;
	
	public AppsAdapter(Context context,ArrayList<AppInfoDomain> aidList) {
		super();
		this.context = context;
		this.aidList=aidList;
		this.layoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return aidList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		WrapperView wrapperView;
		AppInfoDomain aid=aidList.get(position);
		if(convertView==null)
		{
			convertView=layoutInflater.inflate(R.layout.mk_app_list_item, null);
			wrapperView=new WrapperView();
			wrapperView.appName_textView=(TextView) convertView.findViewById(R.list.appName);
            wrapperView.appVersionName_textView = (TextView) convertView.findViewById(R.list.appVersionName);
			wrapperView.icon_imageView=(ImageView) convertView.findViewById(R.list.icon);
			wrapperView.totalPermission_textView=(TextView) convertView.findViewById(R.list.totalPermission);
			wrapperView.showPermission_textView=(TextView) convertView.findViewById(R.list.showPermission);
			convertView.setTag(wrapperView);
		}
		else{
		wrapperView=(WrapperView) convertView.getTag();
		}
		wrapperView.appName_textView.setText(aid.getAppName());
		wrapperView.appVersionName_textView.setText(aid.getVersionName());
		wrapperView.totalPermission_textView.setText(getStringToHtml(context, R.string.app_total_permission, aid.getTotalPermission()));
		wrapperView.showPermission_textView.setText(getStringToHtml(context, R.string.app_show_permission, new Integer[]{aid.getDisabledNum(),aid.getEnabledNum()}));
		wrapperView.icon_imageView.setImageDrawable(aid.getIcon());
		return convertView;
	}
	public static Spanned getStringToHtml(Context context,int format,Object... str)
	{
		
		return Html.fromHtml(String.format(context.getText(format).toString(),str));
		
	}
	class WrapperView
	{
		TextView appName_textView, appVersionName_textView, totalPermission_textView, showPermission_textView;
		ImageView icon_imageView;
	}

}
