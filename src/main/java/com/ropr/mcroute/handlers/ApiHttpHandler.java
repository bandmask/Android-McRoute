package com.ropr.mcroute.handlers;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ropr.mcroute.configs.ApiConfig;
import com.ropr.mcroute.sources.StaticResources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NIJO7810 on 2016-05-04.
 */
public class ApiHttpHandler {
    private static ApiHttpHandler _handler;
    private static String _baseUrl;

    public static ApiHttpHandler getInstance() {
        if (_handler == null)
            _handler = new ApiHttpHandler();
        return _handler;
    }

    private ApiHttpHandler() {
        _baseUrl = ApiConfig.API_ENDPOINT;
    }

    public JsonObject handleGet(String function, HashMap<String, String> dataValues) throws IllegalStateException{
        return handleGet(getUrlForGet(function, dataValues));
    }

    private JsonObject handleGet(String query) throws IllegalStateException {
        HttpURLConnection connection = null;
        try {
            connection = getConnectionForRequest(StaticResources.HTTP_METHOD_GET, query);
            connection.setDoInput(true);

            AsyncTaskResponse result = new GetBackgroundThread().execute(connection).get();
            if (result.hasError())
                throw result.getError();

            return handleResponseStream(result.getInputStream());
        } catch (IOException ioException) {
            throw new IllegalStateException("Handle Get failur: IOException " + ioException.getMessage());
        }catch (Exception ex) {
            throw new IllegalStateException("Thread handling error: " + ex.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public JsonObject HandlePost(String function, String data) throws IllegalStateException {
        HttpURLConnection connection = null;
        try {
            connection = getConnectionForRequest(StaticResources.HTTP_METHOD_POST, _baseUrl + function);

            connection.setDoOutput(true);
            connection.setDoInput(true);

            AsyncTaskResponse result = new PostBackgroundThread().execute(new AsyncTaskPostRequest(connection, data)).get();
            if (result.hasError()) {
                throw result.getError();
            }

            return handleResponseStream(result.getInputStream());
        } catch (IOException ioException) {
            throw new IllegalStateException("Handle Post failure: IOException " + ioException.getMessage());
        } catch (Exception ex) {
            throw new IllegalStateException("Thread handling error: " + ex.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public void HandleTokenFailure() throws IllegalStateException {
        if (!StaticResources.SessionManager.getUseRefreshToken())
            throw new IllegalStateException("Unathorized connection, not using refresh token");

        String refreshToken = StaticResources.SessionManager.getRefreshToken();
        if (refreshToken == null)
            throw new IllegalStateException("Unauthorized connection, refresh token is not present");

        HttpURLConnection connection = null;
        try {
            URL url = new URL(_baseUrl + "token");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StaticResources.HTTP_METHOD_POST);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setDoOutput(true);
            connection.setDoInput(true);

            String data = new StringBuilder()
                    .append("grant_type=refresh_token")
                    .append("&refresh_token=" + refreshToken)
                    .append("&client_id=" + ApiConfig.API_CLIENT)
                    .append("&client_secret=" + ApiConfig.API_SECRET)
                    .toString();

            connection.setChunkedStreamingMode(0);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StaticResources.CHARSET_UTF8));
            writer.write(data);
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("Token endpoint using refresh token failed, login again");
            }

            JsonObject responseObject = handleResponseStream(connection.getInputStream());

            Gson gson = new GsonBuilder().create();
            TokenEndpointResponse response = gson.fromJson(responseObject, TokenEndpointResponse.class);
            StaticResources.SessionManager.updateAccessToken(response.getAccessToken());
            StaticResources.SessionManager.updateRefreshToken(response.getRefreshToken());
        } catch (MalformedInputException malformedException) {
            throw new IllegalStateException("Update access token failed: MalformedInputException " + malformedException.getMessage());
        } catch (IOException ioException) {
            throw new IllegalStateException("Update access token failed: IOException " + ioException.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private class GetBackgroundThread extends AsyncTask<HttpURLConnection, Void, AsyncTaskResponse> {

        @Override
        protected AsyncTaskResponse doInBackground(HttpURLConnection... params) {
            try {
                if (params.length == 1) {
                    HttpURLConnection connection = params[0];
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        String usedMethod = connection.getRequestMethod();
                        String currentUrl = connection.getURL().toString();
                        connection.disconnect();
                        HandleTokenFailure();
                        return doInBackground(getConnectionForRequest(usedMethod, currentUrl));
                    }

                    InputStream stream = connection.getInputStream();
                    connection.disconnect();

                    return new AsyncTaskResponse(stream);
                }
            } catch (Exception exception) {
                return new AsyncTaskResponse(exception);
            }

            return new AsyncTaskResponse(new IllegalStateException("Network background thread failed."));
        }
    }

    private class PostBackgroundThread extends AsyncTask<AsyncTaskPostRequest, Void, AsyncTaskResponse> {

        @Override
        protected AsyncTaskResponse doInBackground(AsyncTaskPostRequest... params) {
            try {
                if (params.length == 1) {
                    AsyncTaskPostRequest request = params[0];
                    HttpURLConnection connection = request.getConnection();
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setChunkedStreamingMode(0);
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StaticResources.CHARSET_UTF8));
                    writer.write(request.getData());
                    writer.flush();
                    writer.close();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        String usedMethod = connection.getRequestMethod();
                        String currentUrl = connection.getURL().toString();
                        connection.disconnect();
                        HandleTokenFailure();
                        return doInBackground(new AsyncTaskPostRequest(getConnectionForRequest(usedMethod, currentUrl), request.getData()));
                    }

                    InputStream stream = connection.getInputStream();
                    connection.disconnect();

                    return new AsyncTaskResponse(stream);
                }
            } catch (Exception exception) {
                return new AsyncTaskResponse(exception);
            }

            return new AsyncTaskResponse(new IllegalStateException("Network background thread failed."));
        }
    }

    private HttpURLConnection getConnectionForRequest(String requestMethod, String urlString) throws IOException {
        String accessToken = StaticResources.SessionManager.getAccessToken();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Authorization", "BEARER " + accessToken);
        return connection;
    }

    private String getUrlForGet(String function, HashMap<String, String> dataValues) {
        String urlString = _baseUrl + function;
        if (dataValues != null && !dataValues.isEmpty()) {
            Character append = '?';
            for (Map.Entry<String, String> keyValue : dataValues.entrySet()){
                urlString = urlString + append + keyValue.getKey() + "=" + keyValue.getValue();
                if (append == '?')
                    append = '&';
            }
        }
        return urlString;
    }

    private JsonObject handleResponseStream(InputStream stream) throws IOException, IllegalStateException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        stream.close();

        if (builder.length() <= 0)
            throw new IOException("Response stream did not yield any data.");

        JsonParser parser = new JsonParser();
        return (JsonObject) parser.parse(builder.toString());
    }

    private class TokenEndpointResponse {
        @SerializedName("access_token")
        private String _accessToken;
        @SerializedName("token_type")
        private String _tokenType;
        @SerializedName("expires_in")
        private int _expires;
        @SerializedName("refresh_token")
        private String _refreshToken;
        @SerializedName("as:client_id")
        private String _clientId;
        @SerializedName("userName")
        private String _userName;
        @SerializedName(".issued")
        private String _issuedDate;
        @SerializedName(".expires")
        private String _expiresDate;

        private String getAccessToken() { return _accessToken; }
        private String getRefreshToken() { return _refreshToken; }
    }

    private class AsyncTaskPostRequest {
        private String _data;
        private HttpURLConnection _connection;

        public AsyncTaskPostRequest(HttpURLConnection connection, String data) {
            _data = data;
            _connection = connection;
        }

        public String getData() { return _data; }
        public HttpURLConnection getConnection() { return _connection; }
    }

    private class AsyncTaskResponse {
        private InputStream _stream;
        private Exception _exception;
        private boolean _hasError;

        public AsyncTaskResponse(InputStream stream) {
            super();
            _stream = stream;
        }

        public AsyncTaskResponse(Exception exception) {
            super();
            _exception = exception;
            _hasError = true;
        }

        public InputStream getInputStream() { return _stream; }
        public Exception getError() { return _exception; }
        public boolean hasError() { return _hasError; }
    }
}
