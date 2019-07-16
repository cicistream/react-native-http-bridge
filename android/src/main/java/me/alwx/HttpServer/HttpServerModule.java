package me.alwx.HttpServer;

import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class HttpServerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    ReactApplicationContext reactContext;

    private static int port;
    private static String root;
    private static File www_root;

    private static Server server = null;

    public HttpServerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "HttpServer";
    }

    @ReactMethod
    public void start(int port, String root) {
        this.port = port;
        this.root = root;

        startServer();
    }

    @ReactMethod
    public void stop() {
        stopServer();
    }

    @ReactMethod
    public void respond(String requestId, int code, String type, String body) {
        if (server != null) {
            server.respond(requestId, code, type, body);
        }
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        stopServer();
    }

    private void startServer() {
        if (this.port == 0) {
            return;
        }

        if (this.root != null && (this.root.startsWith("/") || this.root.startsWith("file:///"))) {
            www_root = new File(root);
        } else {
            www_root = new File(this.reactContext.getFilesDir(), this.root);
        }

        if (server == null) {
            server = new Server(reactContext, port, www_root);
        }
        try {
            server.start();
        } catch (IOException e) {
            Log.e("HttpServer", e.getMessage());
        }
    }

    private void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
            port = 0;
        }
    }
}
