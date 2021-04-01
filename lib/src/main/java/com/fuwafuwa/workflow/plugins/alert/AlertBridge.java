package com.fuwafuwa.workflow.plugins.alert;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.alert.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.alert.render.WorkFlowPromptHolder;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.ModalComposer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class AlertBridge implements IProcess {

    private static final String name = "Alert";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("UI组件");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_UI_ALERT_TEXT);
            child.setTitle("弹窗展示结果");
            child.setIcon("icon_dialog");
            list.add(child);
        }
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_UI_TOAST);
            child.setTitle("提示条展示结果");
            child.setIcon("icon_toast");
            list.add(child);
        }
        item.setGroup(list);
        return item;
    }

    public static class Factory extends DefaultFactory<IProcess> {

        private WorkFlowNode flowNode;

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new AlertBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return AlertBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_UI_ALERT_TEXT, DefaultSystemItemTypes.TYPE_UI_TOAST};
        }

        @Override
        public boolean canDrag() {
            return true;
        }

        @Override
        public boolean canDrop() {
            return true;
        }

        @Override
        public boolean isPipe() {
            return true;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowPromptHolder(parent);
        }

        @Override
        public boolean slotValueHasBeenSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            return FlowReceiver.isSlotSet(mContextRef, holder, urlTag);
        }

        @Override
        public void onClickFlowItem(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            super.onClickFlowItem(mContextRef, holder, urlTag);
            FlowReceiver.onClick(mContextRef, holder, urlTag);
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            this.flowNode = flowNode;
            return new FutureTask<>(() -> {
                Task task = defaultTask(context, flowNode, resultSlots);
                int itemType = flowNode.getItemType();
                if (itemType == DefaultSystemItemTypes.TYPE_UI_ALERT_TEXT) {
                    lockInvokeCaller(flowNode, task);
                } else {
                    invokeCaller(flowNode, task);
                }
                return task;
            });
        }

        @Override
        public void uiCall(Context context, Task bundle) {
            super.uiCall(context, bundle);
            int itemType = bundle.getType();
            String message = bundle.getResult();
            if (itemType == DefaultSystemItemTypes.TYPE_UI_TOAST) {
                ModalComposer.showToast(message);
            } else if (itemType == DefaultSystemItemTypes.TYPE_UI_ALERT_TEXT) {
                ModalComposer.showDialog(context, context.getString(R.string.dialog_title_info), message,
                        (e, baseDialog) -> {
                            if (baseDialog == null) return false;
                            unlockInvokeCaller(flowNode);
                            return false;
                        });
            }
        }
    }

}
