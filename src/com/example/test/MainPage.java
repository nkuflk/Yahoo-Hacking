package com.example.test;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class MainPage extends TabActivity {

	TabHost tabhost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);

		tabhost = getTabHost();

		tabhost.addTab(tabhost.newTabSpec("TAB1").setIndicator("App")
				.setContent(new Intent(MainPage.this, App.class)));
		tabhost.addTab(tabhost.newTabSpec("TAB2").setIndicator("Info")
				.setContent(new Intent(MainPage.this, Info.class)));
		tabhost.addTab(tabhost.newTabSpec("TAB3").setIndicator("Connect")
				.setContent(new Intent(MainPage.this, Connect.class)));
		tabhost.setCurrentTab(0);
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				Toast toast;
				if (tabId == "TAB1") {
					toast = Toast.makeText(getApplicationContext(), "推荐的应用列表",
							Toast.LENGTH_SHORT);
				} else if (tabId == "TAB2") {
					toast = Toast.makeText(getApplicationContext(), "完善你的信息",
							Toast.LENGTH_SHORT);
				} else {
					toast = Toast.makeText(getApplicationContext(),
							"与其他社交网络连接", Toast.LENGTH_SHORT);
				}
				toast.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_page, menu);
		return true;
	}

}
