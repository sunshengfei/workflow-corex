package com.fuwafuwa.workflow.agent;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.agent.event.WorkFlowFinEvent;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DefaultFactory<T> implements IFactory<T> {

    protected T bridge;

    public final AtomicBoolean lock = new AtomicBoolean(false);
    protected boolean isResponsive = true;

    protected int currentItemViewType;

    public DefaultFactory() {
        if (isResponsive) {
            this.bridge = create();
        }
    }

    public DefaultFactory(boolean isResponsive) {
        this.isResponsive = isResponsive;
        if (isResponsive) {
            this.bridge = create();
        }
    }

    public T getBridge() {
        return bridge;
    }

    public static class Options {
        public final static int IGNORE = ~0XFFFF;
        public int itemViewType = IGNORE;

        public Options() {
        }

        public Options(int temViewType) {
            this.itemViewType = temViewType;
        }
    }

    protected Options options;

    public DefaultFactory<T> withOptions(Options options) {
        this.options = options;
        return this;
    }

    public void setCurrentItemViewType(int currentItemViewType) {
        this.currentItemViewType = currentItemViewType;
    }

    public int payloadType() {
        return DefaultPayloadType.type_none;
    }

    public Class<? extends IPayload> payloadClass() {
        return null;
    }

    @Override
    public int acceptItemViewType() {
        return Options.IGNORE;
    }

    public int[] acceptItemViewTypes() {
        return null;
    }

    @Override
    public boolean isPipe() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public int after(int i, Task task, List<WorkFlowNode> items) {
        return i;
    }

    public boolean slotValueHasBeenSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        return false;
    }

    public void onClickFlowItem(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
    }

    public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
        return null;
    }

    @Override
    public void uiCall(Context context, Task bundle) {

    }

    @Override
    public final void invokeCaller(WorkFlowNode flowNode, Task bundle) {
        if (bundle == null) return;
        RxEventBus.post(new WorkFlowFinEvent(bundle));
        afterInvokeCaller(flowNode);
    }

    protected void lockInvokeCaller(WorkFlowNode flowNode, Task task) {
        if (flowNode != null && flowNode.isUsrActionBlocked()) {
            synchronized (lock) {
                lock.set(false);
                invokeCaller(flowNode, task);
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            invokeCaller(flowNode, task);
        }
    }

    protected void unlockInvokeCaller(WorkFlowNode flowNode) {
        try {
            if (flowNode != null && flowNode.isUsrActionBlocked()) {
                synchronized (lock) {
                    lock.set(true);
                    lock.notifyAll();
                }
            }
        } catch (IllegalMonitorStateException e) {
            Loger.e("DF", "IllegalMonitorStateException", e);
        }

    }

    protected void afterInvokeCaller(WorkFlowNode flowNode) {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreateSkeleton(IWorkFlowAdapter adapter, WorkFlowTypeItem workFlowTypeItem) {
        int lastIndex = adapter.getItemCount() - 1;
        List<IWorkFlowAdapterItem> commands = adapter.getDataSets();
        WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(workFlowTypeItem.getFyItemType(), false, null);
        workFlowItem.set_isCamel(true);
        workFlowItem.setTitle(workFlowTypeItem.getTitle());
        try {
            Class<? extends IPayload> classz = payloadClass();
            if (classz != null) {
                IPayload payload = classz.newInstance();
                workFlowItem.setPayload(payload);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        commands.add(lastIndex, workFlowItem);
        adapter.notifyItemInserted(lastIndex);
    }

    protected Task defaultTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
        Task taskInput = null;
        String varValue = null;
        if (RegexHelper.isNotEmpty(resultSlots)) {
            taskInput = resultSlots.get("defaultSlot");
            Task varValueVar = resultSlots.get("defaultVar");
            if (varValueVar != null) {
                varValue = varValueVar.getResult();
                varValue = WorkFlowRunner.strFromPool(varValue);
            }
        }
        if (taskInput != null) {
            taskInput.setType(flowNode.getItemType());
        } else if (varValue != null) {
            taskInput = new Task();
            taskInput.setType(flowNode.getItemType());
            taskInput.setResult(varValue);
        }
        return taskInput;
    }

    protected final FutureTask<Task> defaultTaskInvoke(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
        return new FutureTask<>(() -> {
            Task finalTaskInput = defaultTask(context, flowNode, resultSlots);
            invokeCaller(flowNode, finalTaskInput);
            return finalTaskInput;
        });
    }

    protected final FutureTask<Task> defaultEmptyTaskInvoke() {
        return null;
    }


    protected void taskAlert(String bundle) {
        if (bundle == null) return;
        ModalComposer.showToast(bundle);
    }
}
