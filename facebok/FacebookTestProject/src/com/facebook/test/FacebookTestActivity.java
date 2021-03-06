package com.facebook.test;

import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.SessionTracker;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * @author vivek 
 **/
//======================================================================
public class FacebookTestActivity extends Activity{

	private UiLifecycleHelper uiHelper;
	private SessionTracker tracker;

	//=============================================
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.facebook_test);

		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(b);
		ActionBar ab = getActionBar();
		if(ab!=null) ab.hide();

		findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login();				
			}
		});
		findViewById(R.id.update_status).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				shareLink();				
			}
		});
		findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				likePage();				
			}
		});		
	}

	//=============================================
	private void login(){
		// start Facebook Login
		Session.openActiveSession(this, true, Arrays.asList("public_profile, email,user_friends"),
				new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {

					getFriends();
				}
			}
		});
	}
	
	//=============================================
	private void getFriends(){        
		
		Request.newMyFriendsRequest(Session.getActiveSession(), new GraphUserListCallback() {
			
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				JsonParser p = new JsonParser();
				JsonArray arr = p.parse(response.getGraphObject().getProperty("data").toString()).getAsJsonArray();
				int size = arr.size();
				for(int i=0;i<size;i++){
					Log.i("facebook","Name: "+arr.get(i).getAsJsonObject().get("name").getAsString());
				}
			}
		}).executeAsync();
	}
	
	//=============================================
	private void onLogin(){
		findViewById(R.id.update_status).setVisibility(View.VISIBLE);
		findViewById(R.id.login).setVisibility(View.INVISIBLE);
	}
	
	//=============================================
	private void likePage(){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("https://www.facebook.com/v.viv.dubey"));
		startActivity(i);
	}
	
	//=============================================
	private void shareLink(){
		try{
		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
			.setLink("http://www.vivekdubey.com")
			.build();
		uiHelper.trackPendingDialogCall(shareDialog.present());
		}catch(FacebookException fe){Toast.makeText(this, "Please install facebook app", Toast.LENGTH_SHORT).show();}
	}

	//=============================================
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	//=============================================
	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	//=============================================
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	//=============================================
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	//=============================================
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
}