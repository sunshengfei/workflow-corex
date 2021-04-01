package com.fuwafuwa.workflow.agent;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fuwafuwa.workflow.agent.event.AlertEvent;
import com.fuwafuwa.workflow.agent.event.TaskProgressEvent;
import com.fuwafuwa.workflow.agent.exception.RunException;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.AndroidTools;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.PermissionUtil;
import com.fuwafuwa.utils.PlatformApiTools;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Created by fred on 2018/10/24.
 */

public class WorkFlowService extends JobIntentService {

    private WorkFlowService mContext;
    private int notificationID = 100;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, Task> taskResult;
    private List<FutureTask<Task>> futureTasks;
    public final static String KEY = "KEY";
    public final static String NOTIFY_BACK_ACTIVITY_CLASSNAME = "BACK_ACTIVITY_CLSNAME";
    private Notification notification;
    NotificationManagerCompat notificationManager;
    private Disposable disposable;
    private RemoteViews remoteView;
    private WorkFlowVO mirrorVo;
    private PowerManager.WakeLock awake;
    private boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseResource();
    }

    static final int JOB_ID = 0x8901;

    public static void enqueueWork(Context appContext, Intent intent) {
        enqueueWork(appContext, WorkFlowService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        onStartCommandJob(intent, 0, 0);
    }

    private void releaseResource() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (futureTasks == null) futureTasks = new ArrayList<>();
        for (int i = 0; i < futureTasks.size(); i++) {
            FutureTask<Task> futureTask = futureTasks.get(i);
            if (futureTask != null && (!futureTask.isCancelled() || !futureTask.isDone())) {
                futureTask.cancel(true);
            }
        }
        futureTasks.clear();
        stopForeground(true);
        if (notificationManager != null) {
            notificationManager.cancel(notificationID);
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public int onStartCommandJob(Intent intent, int flags, int startId) {
        mContext = this;
        String notificationIntentClass = intent.getStringExtra(NOTIFY_BACK_ACTIVITY_CLASSNAME);
//        if (intent == null) return START_NOT_STICKY;
        taskResult = PlatformApiTools.getMap(String.class, Task.class);
        futureTasks = new ArrayList<>();
        mirrorVo = (WorkFlowVO) intent.getSerializableExtra(KEY);
        if (mirrorVo == null) return START_NOT_STICKY;
        if (RegexHelper.isNotEmpty(notificationIntentClass)) {
            Intent notificationIntent = new Intent();
            notificationIntent.setClassName(mContext, notificationIntentClass);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //1.通知栏占用，不清楚的看官网或者音乐类APP的效果
            notificationManager = NotificationManagerCompat.from(this);
            remoteView = new RemoteViews(getPackageName(), R.layout.notification_template_ichi);
            remoteView.setTextViewText(R.id.action_text, "任务正在运行");
            remoteView.setTextViewText(R.id.action_desc, "运行完成后，会自动关闭");
            String channelId = "CHANNEL_TASK";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.workflow_black)
                    .setWhen(System.currentTimeMillis())
                    .setContent(remoteView)
                    .setContentTitle("任务正在运行")
                    .setContentText("运行完成后，会自动关闭")
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false);
//        remoteView=builder.getContentView();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelName = "物联Flow_任务通知";
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
                );
                if (!mirrorVo.isAlert()) {
                    mChannel.setSound(null, null);
                }
                notificationManager.createNotificationChannel(mChannel);
            } else {
                builder.setPriority(Notification.PRIORITY_MAX);
            }
            notification = builder.build();
            if (mirrorVo.isNotification()) {
                notificationID = 100;
                notifyUpdate();
            } else {
                notificationID = 0;
            }
        }
        if (mirrorVo.isKeepLive()) {
            if (PermissionUtil.getInstance().checkPermission(this, Manifest.permission.FOREGROUND_SERVICE)) {
                startForeground(notificationID, notification);
            } else {
                ModalComposer.showToast("此指令开启了前台运行，需要开启相应权限");
            }
        }
        subscribe();
        if (!isRunning) {
//            executorService.submit(() -> {
//                try {
////                synchronized (mirrorVo) {
//                    isRunning = true;
//                    doJob(mirrorVo);
////                }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    isRunning = false;
//                }
//            });
            try {
//                synchronized (mirrorVo) {
                isRunning = true;
                doJob(mirrorVo);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        }
        return START_STICKY;
    }

    private void notifyUpdate() {
        if (mirrorVo != null && mirrorVo.isNotification()) {
            notificationManager.notify(notificationID, notification);
        }
    }

    private int lastStep;

    private void subscribe() {
        disposable = RxEventBus.subscribeEvent(TaskProgressEvent.class,
                event -> {
                    if (remoteView == null) return;
                    int step = event.getProgress();
                    if (event.isComplete()) {
                        remoteView.setTextViewText(R.id.action_text, "执行完毕");
                        stopSelf();
                    } else {
                        if (step < lastStep) return;
                        lastStep = step;
                        if (remoteView != null) {
                            remoteView.setTextViewText(R.id.action_text, String.format("当前正在进行第%s项指令", step));
                        }
                    }
                    notifyUpdate();
                    if (event.isComplete()) {
                        releaseResource();
                    }
                }, e -> {
                    e.printStackTrace();
                });
    }


    private void doJob(WorkFlowVO vo) throws Exception {
        if (vo == null) return;
        awake = AndroidTools.screenAwake(this);
        lastStep = 0;
        if (futureTasks != null)
            futureTasks.clear();
        WorkFlowRunner.clear();
        WorkFlowVO mirrorVo = (WorkFlowVO) vo.clone();
        List<WorkFlowNode> items = mirrorVo.getItems();
        TaskProgressEvent taskProgress = new TaskProgressEvent();
        taskProgress.set_id(mirrorVo.get_id());
        taskProgress.setProgress(1);
        RxEventBus.post(taskProgress);
        for (int i = 0; i < items.size(); i++) {
            //判断是否存在输入 payload
            WorkFlowNode node = items.get(i);
            String input = node.getIn();
            Map<String, Task> resultSlots = new HashMap<>();
            Task taskInput = null;
            if (input != null && input.startsWith(WorkFlowNode.VAR_PREFIX)) {
                String vKey = input.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
                Task varValue = WorkFlowRunner.var.get(vKey);
                resultSlots.put("defaultVar", varValue);
            } else if (RegexHelper.isNotEmpty(input)) {
                taskInput = taskResult.get(input);
                resultSlots.put("defaultSlot", taskInput);
            }
            HashMap<String, String> inputMap = node.getExtIn();
            if (RegexHelper.isNotEmpty(inputMap)) {
                Set<Map.Entry<String, String>> eSet = inputMap.entrySet();
                for (Map.Entry<String, String> set : eSet) {
                    String in = set.getValue();
                    Task val = taskResult.get(in);
                    if (val != null) {
                        resultSlots.put(in, val);
                    } else if (in != null && in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                        String vKey = in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
                        Task varValue = WorkFlowRunner.var.get(vKey);
                        resultSlots.put(in, varValue);
                    }
                }
            }

            TaskProgressEvent tp = (TaskProgressEvent) taskProgress.clone();
            tp.setProgress(i + 1);
            RxEventBus.post(tp);
            FutureTask<Task> futureTask = null;
            futureTask = WorkFlowRunner.run(getApplicationContext(), node, resultSlots);
            IFactory<? extends IProcess> factory = FlowFactory.factoryFor(node.getItemType());
            if (futureTask == null) {
                if (factory != null) {
                    i = factory.after(i, null, items);
                }
                continue;
            }
            futureTasks.add(futureTask);
            if (executorService.isShutdown()) {
                executorService = Executors.newCachedThreadPool();
            }
            Future<?> future = executorService.submit(futureTask);
            Task task = null;
            try {
                Object tt = future.get();
                if (tt instanceof Task){
                    task = (Task) tt;
                }else{
                    task = futureTask.get();
                }
                if (factory != null) {
                    i = factory.after(i, task, items);
                }
                if (task == null) continue;
                taskResult.put(task.get_id(), task);
//                Loger.d("FutureTask",  GsonUtils.toJson(task));
                tp.setResult(task);
                RxEventBus.post(tp);
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                taskProgress.set_id(mirrorVo.get_id());
                taskProgress.setComplete(true);
                RxEventBus.post(taskProgress);
                if (e.getCause() instanceof RunException) {
                    RunException ee = (RunException) e.getCause();
                    RxEventBus.post(new AlertEvent(ee.getMessage(), true));
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        taskProgress.setProgress(0);
        taskProgress.setComplete(true);
        RxEventBus.post(taskProgress);
        AndroidTools.screenAwakeRelease(awake);
    }

}
