package com.ropr.mcroute.sources;

import android.content.Context;
import android.content.SharedPreferences;

import com.ropr.mcroute.models.SessionData;

import java.util.HashMap;

public class StaticResources {
	public static final String EXTRA_ROUTE = "com.mcroute.extra,route";
	public static final String EXTRA_URL = "com.mcroute.extra.url";
	public static final String EXTRA_SESSION_PROFILE = "com.mcroute.extra.session_profile";
	public static final String EXTRA_PROFILE_ID = "com.mcroute.extra.profileid";

	public static final String SAVED_INSTANCE_ROUTE = "com.mcroute.savedstate.route";
	public static final String SAVED_INSTANCE_ROUTING_IN_PROGRESS = "com.mcroute.savedstate.routeinprogress";

	public static final String EVENT_NEW_LOCATION = "com.mcroute.event.newlocation";

	public static final int LOGIN_REQUEST_CODE = 5000;
	public static final int START_ROUTE_REQUEST_CODE = 5001;
	public static final int ROUTE_SYNCED = 5002;
	public static final int ROUTE_DELETED_LOCAL = 5003;

	public static final String USER_ID = "com.mcroute.session.userid";
	public static final String ACCESS_TOKEN = "com.mcroute.session.accesstoken";
	public static final String USE_REFRESH_TOKEN = "com.mcroute.session.userefreshtoken";
	public static final String REFRESH_TOKEN = "com.mcroute.session.refreshtoken";

	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";

	public static final String CHARSET_UTF8 = "UTF-8";

	public static class SessionManager {
		private Context _context;

		private static SharedPreferences _preferences;
		private static SharedPreferences.Editor _editor;
		private static SessionManager _sessionManager;

		private static final String SESSION_NAME = "com.mcroute.sessionManager";

		private static final int PRIVATE_MODE = 0;

		public static void InitSessionManager(Context context) {
			if (_sessionManager == null)
				_sessionManager = new SessionManager(context);
		}

		private SessionManager(Context context) {
			_context = context;
			_preferences = _context.getSharedPreferences(SESSION_NAME, PRIVATE_MODE);
			_editor = _preferences.edit();
		}

		public static void addSession(SessionData sessionData) {
			_editor.putString(USER_ID, sessionData.getUserId());
			_editor.putString(ACCESS_TOKEN, sessionData.getAccessToken());
			_editor.putBoolean(USE_REFRESH_TOKEN, sessionData.getUseRefreshToken());
			_editor.putString(REFRESH_TOKEN, sessionData.getRefreshToken());

			_editor.commit();
		}

		public static boolean hasSessionValues() {
			return getUserId() != null && getAccessToken() != null && getRefreshToken() != null;
		}

		public static String getUserId() {
			return _preferences.getString(USER_ID, null);
		}

		public static String getAccessToken() {
			return _preferences.getString(ACCESS_TOKEN, null);
		}

		public static boolean getUseRefreshToken() { return _preferences.getBoolean(USE_REFRESH_TOKEN, false); }

		public static String getRefreshToken() {
			return _preferences.getString(REFRESH_TOKEN, null);
		}

		public static void updateAccessToken(String newToken) {
			_editor.remove(ACCESS_TOKEN);
			_editor.putString(ACCESS_TOKEN, newToken);
			_editor.commit();
		}

		public static void updateRefreshToken(String newToken) {
			_editor.remove(REFRESH_TOKEN);
			_editor.putString(REFRESH_TOKEN, newToken);
			_editor.commit();
		}

		public static void clearSession() {
			_editor.clear();
			_editor.commit();
		}
	}
}
