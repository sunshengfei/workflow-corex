package com.fuwafuwa.workflow.plugins.beacon;

import android.content.Context;
import android.view.ViewGroup;


import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.beacon.action.BeaconFinderTask;
import com.fuwafuwa.workflow.plugins.beacon.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.beacon.payload.BeaconPayload;
import com.fuwafuwa.workflow.plugins.beacon.render.WorkFlowBeaconFinderCmdHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class BeaconBridge implements IProcess {

    private static final String name = "Beacon";

    @Override
    public WorkFlowTypeItem pluginEntry() {
            WorkFlowTypeItem item = new WorkFlowTypeItem();
            item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
            item.setTitle("Beacon");
            List<WorkFlowTypeItem> list = new ArrayList<>();
            {
                WorkFlowTypeItem child = new WorkFlowTypeItem();
                child.setFyItemType(DefaultSystemItemTypes.TYPE_BEACON_FINDER);
                child.setTitle("发现信标");
                child.setIcon("detect");
                list.add(child);
            }
            item.setGroup(list);
            return item;
    }


    public static class Factory extends DefaultFactory<IProcess> {

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new BeaconBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return BeaconBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_BEACON_FINDER;
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
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowBeaconFinderCmdHolder(parent);
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
        public int payloadType() {
            return DefaultPayloadType.type_beacon_finder;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return BeaconPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            BeaconFinderTask beaconFinderTask = new BeaconFinderTask(context, flowNode, resultSlots);
            return new FutureTask<>(beaconFinderTask);
        }
    }


}
