package com.fuwafuwa.sys.ss;

import android.content.Context;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpHead;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.async.util.StreamUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static android.content.res.AssetManager.ACCESS_BUFFER;

public class InputServer {

    private final Context mContext;
    private HttpCallBack httpCallBack;

    public static String ASSET_DIR = "ssdist";

    public String defaultText = "";

    public int port = 8421;

    public InputServer(Context instance) {
        this(instance, null);
    }

    public InputServer(Context instance, HttpCallBack httpCallBack) {
        this.mContext = instance;
        this.httpCallBack = httpCallBack;
    }


    public interface HttpCallBack {

        boolean receive(String path, JSONObject result);
    }


    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            //反射改掉默认的编码格式
            Class<Charsets> charsetClass = Charsets.class;
            try {
                Field field = charsetClass.getDeclaredField("US_ASCII");
                field.setAccessible(true);
                field.set(Charsets.class, Charsets.UTF_8);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            AsyncHttpServer server = new AsyncHttpServer();
//        List<WebSocket> _sockets = new ArrayList<WebSocket>();
            directory(server, mContext, "/(css|js|image)/.*", ASSET_DIR);
            directory(server, mContext, "/\\w+\\.html", ASSET_DIR);
            server.get("/", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    response.setContentType("text/html");
                    InputStream in = null;
                    try {
                        in = mContext.getAssets().open(ASSET_DIR + "/index.html", ACCESS_BUFFER);
                        response.sendStream(in, in.available());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            server.get("/api/sync/js", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    response.setContentType("application/json");
                    try {
                        JSONObject json = new JSONObject();
                        json.put("err", 0);
                        json.put("data", defaultText);
                        response.send(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            server.post("/api/sync/js", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    AsyncHttpRequestBody body = request.getBody();
                    boolean isOk = false;
                    if (body instanceof JSONObjectBody) {
                        JSONObject result = ((JSONObjectBody) body).get();
                        if (httpCallBack != null) {
                            isOk = httpCallBack.receive(request.getPath(), result);
                        }
                    }
                    response.setContentType("application/json");
                    try {
                        JSONObject json = new JSONObject();
                        json.put("err", isOk ? 0 : 1);
                        json.put("data", defaultText);
                        response.send(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
// listen on port 5000
            server.listen(port);
        }
    };

    public void createServer() {
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void shutDown() {
        AsyncServer.getDefault().stop();
    }


    public void directory(AsyncHttpServer server, Context context, String regex, final String assetPath) {
        final Context _context = context.getApplicationContext();
        server.addAction(AsyncHttpGet.METHOD, regex, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                String path = request.getPath();//request.getMatcher().replaceAll("");
                android.util.Pair<Integer, InputStream> pair = AsyncHttpServer.getAssetStream(_context, assetPath + path);
                if (pair == null || pair.second == null) {
                    response.code(404);
                    response.end();
                    return;
                }
                final InputStream is = pair.second;
                response.getHeaders().set("Content-Length", String.valueOf(pair.first));
                response.code(200);
                response.getHeaders().add("Content-Type", AsyncHttpServer.getContentType(assetPath + path));
                Util.pump(is, response, new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        response.end();
                        StreamUtility.closeQuietly(is);
                    }
                });
            }
        });
        server.addAction(AsyncHttpHead.METHOD, regex, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                String path = request.getPath();//request.getMatcher().replaceAll("");
                android.util.Pair<Integer, InputStream> pair = AsyncHttpServer.getAssetStream(_context, assetPath + path);
                if (pair == null || pair.second == null) {
                    response.code(404);
                    response.end();
                    return;
                }
                final InputStream is = pair.second;
                StreamUtility.closeQuietly(is);
                response.getHeaders().set("Content-Length", String.valueOf(pair.first));
                response.code(200);
                response.getHeaders().add("Content-Type", AsyncHttpServer.getContentType(assetPath + path));
                response.writeHead();
                response.end();
            }
        });
    }

}
