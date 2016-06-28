package com.ropr.mcroute.handlers;

import android.app.Activity;
import android.util.Log;

import com.mcroute.R;
import com.ropr.mcroute.models.RouteConfig;
import com.ropr.mcroute.sources.StaticResources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by NIJO7810 on 2016-06-28.
 */
public class RouteConfigHandler {
    private RouteConfig _currentConfig;
    private Activity _parent;

    public RouteConfig getCurrentConfig() {
        return _currentConfig;
    }

    public void updateRouteConfig() {
        writeConfigFile();
    }

    public RouteConfigHandler(Activity parent) {
        _parent = parent;

        if (!configExists()) {
            _currentConfig = new RouteConfig(true);
            writeConfigFile();
        } else {
            readConfigFile();
        }
    }

    private boolean configExists() {
        try {
            return getConfigFileInputStream() != null;
        } catch (FileNotFoundException ex) {
            return false;
        }
    }

    private FileInputStream getConfigFileInputStream() throws FileNotFoundException {
        return _parent.getApplicationContext().openFileInput(_parent.getString(R.string.config_route_file_name));
    }

    private void readConfigFile() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = getConfigFileInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            builder.append(line);

                        _currentConfig = JsonHandler.getInstance().fromJson(builder.toString(), RouteConfig.class);

                        reader.close();
                        inputStream.close();
                    } catch (Exception ex) {
                        Log.e("SaveRouteThread", "Error handling save thread: " + ex.getMessage());
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (Exception ex) {
            Log.e("SaveRouteToFile", "Error saving route to file: " + ex.getMessage());
        }
    }

    private void writeConfigFile() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String data = JsonHandler.getInstance().toJson(_currentConfig);
                        OutputStream outputStream = _parent.getApplicationContext().openFileOutput(_parent.getString(R.string.config_route_file_name), _parent.MODE_PRIVATE);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StaticResources.CHARSET_UTF8));
                        writer.write(data);
                        writer.flush();
                        writer.close();
                        outputStream.close();
                    } catch (Exception ex) {
                        Log.e("SaveRouteThread", "Error handling save thread: " + ex.getMessage());
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (Exception ex) {
            Log.e("SaveRouteToFile", "Error saving route to file: " + ex.getMessage());
        }
    }
}
