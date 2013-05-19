package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroidj.sample.android.tasks.GetOAuthTokenTask;
import com.googlecode.flickrjandroidj.sample.android.tasks.LoadPhotostreamTask;
import com.googlecode.flickrjandroidj.sample.android.tasks.LoadUserTask;
import com.googlecode.flickrjandroidj.sample.android.tasks.OAuthTask;

public class MainActivity extends Activity {

	public static final String CALLBACK_SCHEME = "yahootest"; 
	public static final String PREFS_NAME = "yahootest"; 
	public static final String KEY_OAUTH_TOKEN = "yahootoken"; 
	public static final String KEY_TOKEN_SECRET = "yahootokenSecret"; 
	public static final String KEY_USER_NAME = "yahoouserName";
	public static final String KEY_USER_ID = "yahoouserId"; 
	
	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
	private ListView listView;
	private TextView textUserTitle;
	private TextView textUserName;
	private TextView textUserId;
	private ImageView userIcon;
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.textUserTitle = (TextView) this.findViewById(R.id.profilePageTitle);
		this.textUserName = (TextView) this.findViewById(R.id.userScreenName);
		this.textUserId = (TextView) this.findViewById(R.id.userId);
		this.userIcon = (ImageView) this.findViewById(R.id.userImage);
		this.listView = (ListView) this.findViewById(R.id.imageList);
		this.button = (Button) this.findViewById(R.id.button1);
		
		this.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				load(getOAuthToken());
				/*Intent intent = new Intent();
				intent.setClass(MainActivity.this, Show.class);
				startActivity(intent);*/
			}
		});
		
		OAuth oauth = getOAuthToken();
		if (oauth == null || oauth.getUser() == null) {
			OAuthTask task = new OAuthTask(this);
			task.execute();
		} else {
			load(oauth);
		}
	}
	
	private void load(OAuth oauth) {
		if (oauth != null) {
			new LoadUserTask(this, userIcon).execute(oauth);
			new LoadPhotostreamTask(this, listView).execute(oauth);
		}
	}
    
    @Override
    public void onDestroy() {
    	listView.setAdapter(null);
        super.onDestroy();
    }
    

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}
	
	public void setUser(User user) {
		textUserTitle.setText(user.getUsername());
		textUserName.setText(user.getRealName());
		textUserId.setText(user.getId());
	}
	
	public ImageView getUserIconImageView() {
		return this.userIcon;
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		OAuth savedToken = getOAuthToken();
		if (CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			logger.debug("Returned Query: {}", query); 
			String[] data = query.split("&");
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1);
				logger.debug("OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier);

				OAuth oauth = getOAuthToken();
				if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
				}
			}
		}

	}
    
    public void onOAuthDone(OAuth result) {
		if (result == null) {
			Toast.makeText(this,
					"Authorization failed",
					Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(this,
						"Authorization failed",
						Toast.LENGTH_LONG).show();
				return;
			}
			String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s",
					user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			Toast.makeText(this,
					message,
					Toast.LENGTH_LONG).show();
			saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			load(result);
		}
	}
    
    
    public OAuth getOAuthToken() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
        String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
        	logger.warn("No oauth token retrieved");
        	return null;
        }
        OAuth oauth = new OAuth();
        String userName = settings.getString(KEY_USER_NAME, null);
        String userId = settings.getString(KEY_USER_ID, null);
        if (userId != null) {
        	User user = new User();
        	user.setUsername(userName);
        	user.setId(userId);
        	oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        logger.debug("Retrieved token from preference store: oauth token={}, and token secret={}", oauthTokenString, tokenSecret);
        return oauth;
    }
    
    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
    	logger.debug("Saving userName=%s, userId=%s, oauth token={}, and token secret={}", new String[]{userName, userId, token, tokenSecret});
    	SharedPreferences sp = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.commit();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
