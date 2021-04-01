package com.fuwafuwa.workflow.plugins.media;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import com.fuwafuwa.utils.ModalComposer;
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
import com.fuwafuwa.workflow.plugins.media.render.WorkFlowMediaPlayHolder;

import com.fuwafuwa.workflow.plugins.alert.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class MediaPlayBridge implements IProcess {

    private static final String name = "MediaPlay";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("高级");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_MEDIA_PLAY);
            child.setTitle("播放媒体");
            child.setIcon("media");
            list.add(child);
        }
        item.setGroup(list);
        return item;
    }

    //    public static final int MEDIA_PLAY = 0x1000;
    public static class Factory extends DefaultFactory<IProcess> {

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new MediaPlayBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return MediaPlayBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_MEDIA_PLAY};
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
            return new WorkFlowMediaPlayHolder(parent);
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
            return DefaultPayloadType.type_string;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return StringPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            return defaultTaskInvoke(context, flowNode, resultSlots);
        }

        @Override
        public void uiCall(Context context, Task bundle) {
            super.uiCall(context, bundle);
            try {
                Uri url = Uri.parse(bundle.getResult());
                ModalComposer.showPlayDialog(context, url);
//                String message = bundle.getResult();
//                Intent intent = GhostWebViewActivity.newIntent(context);
////                Uri uri= Uri.parse(message);
//                Uri url = Uri.parse("http://10.0.0.242:5500/test.webm");
////                int type = Util.inferContentType(uri);
//                intent.putExtra("MIME","video/webm");
//                intent.setData(url);
//                context.startActivity(intent);
            } catch (Exception e) {
                ModalComposer.showToast("播放地址无效");
            }
//        Uri url = Uri.parse("http://10.0.0.242:5500/test.webm");
//        Uri url = Uri.parse("rtmp://58.200.131.2:1935/livetv/cctv1");
//        Uri url = Uri.parse("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
//        Uri url = Uri.parse("http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8");
//        Uri url = Uri.parse("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8");
        }
    }


}
