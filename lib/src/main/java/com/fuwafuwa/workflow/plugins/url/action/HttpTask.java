package com.fuwafuwa.workflow.plugins.url.action;

import android.content.Context;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fuwafuwa.hitohttp.HttpUtils;
import com.fuwafuwa.hitohttp.model.HttpRequest;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.WorkFlowRunner;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.url.payload.HttpPayload;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpTask implements Callable<Task>, Callback {

    private SoftReference<Context> mContextRef;
    private Map<String, Task> inputs;
    private WorkFlowNode workFlowNode;
    private HttpPayload payload;
    private final AtomicInteger lock = new AtomicInteger();
    private String result;

    public HttpTask(Context context, WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.payload = (HttpPayload) workFlowNode.getPayload();
        this.inputs = resultSlots;
        this.mContextRef=new SoftReference<>(context);
    }

    @Override
    public Task call() throws Exception {
        if (payload == null) return null;
        if (inputs == null) return null;
        //填充
        HttpRequest request = new HttpRequest();
        Collection<Task> values = inputs.values();
        String url = this.payload.getUrl();
        String body = this.payload.getBody();
        request.setBody(WorkFlowRunner.strFromPool(body));
        String urlString = url;
        if (RegexHelper.isURL(url)) {
            urlString = url;
        } else if (url.startsWith(WorkFlowNode.VAR_PREFIX)) {
            String vKey = url.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            Task varValue = WorkFlowRunner.var.get(vKey);
            if (varValue != null && RegexHelper.isNotEmpty(varValue.getResult())) {
                urlString = WorkFlowRunner.strFromPool(varValue.getResult());
            }
        } else {
            urlString = WorkFlowRunner.strFromPool(url);
            for (Task input : values) {
                if (input.get_id().equals(url)) {
                    urlString = input.getResult();
                }
                if (input.get_id().equals(body)) {
                    request.setBody(input.getResult());
                }
            }
        }
        request.setUrl(new URL(urlString));
        HashMap<String, String> varHeaders = this.payload.getHeaders();
        if ("json".equalsIgnoreCase(payload.getContentType())) {
            varHeaders.put("Content-Type", "application/json;charset=utf8");
        } else if ("form".equalsIgnoreCase(payload.getContentType())) {
            varHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=utf8");
        } else if ("raw".equalsIgnoreCase(payload.getContentType())) {
            if (!varHeaders.containsKey("Content-Type")) {
                varHeaders.put("Content-Type", "text/plain");
            }
        }
        HashMap<String, String> headers = new HashMap<>();
        String method = this.payload.getMethod();
        request.setMethod(method);
        if (varHeaders != null) {
            Set<Map.Entry<String, String>> sets = varHeaders.entrySet();
            for (Map.Entry<String, String> header : sets) {
                String key = WorkFlowRunner.matcherFromPool(header.getKey());
                String val = WorkFlowRunner.matcherFromPool(header.getValue());
                Optional<Task> findKey = Stream.of(values).filter(item -> item.get_id().equals(key)).findFirst();
                Optional<Task> findVal = Stream.of(values).filter(item -> item.get_id().equals(val)).findFirst();
                String finalVal = findVal.isEmpty() ? val : findVal.get().getResult();
                String finalKey = findKey.isEmpty() ? key : findKey.get().getResult();
                headers.put(finalKey, finalVal);
            }
        }
        request.setHeaders(headers);
        try {
            if (request.getUrl() == null) {
                throw new URISyntaxException("urlString", "URL格式错误");
            }
            synchronized (lock) {
                HttpUtils.getInstance(mContextRef).request(request, this);
                lock.wait();
            }
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        task.setResult(result);
        return task;
    }

    private void releaseLock() {
        try {
            if (lock.get() == -1) return;
            synchronized (lock) {
                lock.notify();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        result = e.getMessage();
        releaseLock();

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        result = response.body().string();
        releaseLock();
    }
}
