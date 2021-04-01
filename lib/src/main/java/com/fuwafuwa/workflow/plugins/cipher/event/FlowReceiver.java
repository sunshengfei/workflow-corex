package com.fuwafuwa.workflow.plugins.cipher.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fuwafuwa.utils.EnumTool;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload;
import com.fuwafuwa.workflow.plugins.common.EasyUtils;
import com.fuwafuwa.workflow.plugins.ibase.MapFormDict;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;
import com.fuwafuwa.workflow.ui.ChoseInputFromPreviousDialog;
import com.fuwafuwa.workflow.ui.MapParamsDialog;
import com.fuwafuwa.workflow.ui.SingleChoseDialog;
import com.fuwafuwa.workflow.plugins.cipher.lib.CipherRemark;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FlowReceiver {

    /**
     * 独自处理模块事件
     *
     * @param mContextRef
     * @param holder
     * @param urlTag
     */
    public static void onClick(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        Context context = mContextRef.get();
        if (context == null) return;
        BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter = holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        final WorkFlowNode workFlowItem = (WorkFlowNode) item;
        Object p = workFlowItem.getPayload();
        CipherPayload payload = null;
        if (p instanceof CipherPayload) {
            payload = (CipherPayload) p;
        } else {
            payload = new CipherPayload();
            workFlowItem.setPayload(payload);
        }
        final CipherPayload mirror = payload;
        if ("#input".equals(urlTag)) {
            //弹出同级之前的node 选_id
            ArrayList<WorkFlowNode> prIds = EasyUtils.findOutputFromPrevious(adapter, workFlowItem, holder.getBindingAdapterPosition() - 1);
            ChoseInputFromPreviousDialog dialog = ChoseInputFromPreviousDialog.instance(prIds);
            dialog.handler = new ChoseInputFromPreviousDialog.IEventHandler() {
                @Override
                public void onClick(WorkFlowNode item) {
                    workFlowItem.setIn(item.get_id());
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                }

                @Override
                public void onClick(VarPayload item) {
                    workFlowItem.setIn(WorkFlowNode.VAR_PREFIX + item.getVarName());
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                }
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
        } else if ("#pipe".equals(urlTag)) {
            //弹窗输入正则
            List<CipherPayload.CipherType> list =
                    Arrays.asList(CipherPayload.CipherType.values());
            List<String> mlist = Stream.of(list).filter(ritem -> ritem != CipherPayload.CipherType.NONE)
                    .map(CipherPayload.CipherType::getValue).collect(Collectors.toList());
            SingleChoseDialog dialog = SingleChoseDialog.instance(
                    RegexHelper.isEmpty(mirror.getCipherType()) ?
                            null :
                            mirror.getCipherType().getValue(), mlist);
            dialog.handler = item1 -> {
                Optional<CipherPayload.CipherType> ob = EnumTool.getEnum(CipherPayload.CipherType.class, mitem -> item1.equals(mitem.getValue()));
                if (ob != null && !ob.isEmpty()) {
                    boolean isNew = ob.get() != mirror.getCipherType();
                    mirror.setCipherType(ob.get());
                    if (CipherPayload.CipherType.isNeedParams(ob.get())) {
                        if (isNew || RegexHelper.isEmpty(mirror.getParam())) {
                            HashMap<String, String> map = MapFormDict.mapMaker(mirror.getCipherType());
                            mirror.setParam(map);
                            if (!mirror.getCipherType().isSecretMethod()) {
                                mirror.setAction(null);
                            }
                            adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                        }
                        MapParamsDialog mapDialog = MapParamsDialog.instance(mirror.getParam(), CipherRemark.get(mirror));
                        mapDialog.handler = map -> {
                            mirror.setParam(map);
                            adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                        };
                        mapDialog.show(((AppCompatActivity) context).getSupportFragmentManager());
                    }
                }
                adapter.notifyItemChanged(holder.getBindingAdapterPosition());
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
        } else if ("#action".equals(urlTag)) {
            List<CipherPayload.CipherAction> list =
                    Arrays.asList(CipherPayload.CipherAction.values());
            List<String> mlist = Stream.of(list).filter(it -> {
                if (RegexHelper.isNotEmpty(mirror.getCipherType()) &&
                        mirror.getCipherType().isSecretMethod()) {
                    return true;
                }
                return it == CipherPayload.CipherAction.ENCODE;
            }).map(MapFormDict::getTextValue).collect(Collectors.toList());
            SingleChoseDialog dialog = SingleChoseDialog.instance(
                    RegexHelper.isEmpty(mirror.getCipherType()) ?
                            null :
                            mirror.getCipherType().getValue(), mlist);
            dialog.handler = item1 -> {
                Optional<CipherPayload.CipherAction> ob = EnumTool.getEnum(CipherPayload.CipherAction.class,
                        mitem -> item1.equals(MapFormDict.getTextValue(mitem)));
                if (ob != null && !ob.isEmpty()) {
                    mirror.setAction(ob.get());
                }
                adapter.notifyItemChanged(holder.getBindingAdapterPosition());
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter = holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        final WorkFlowNode workFlowItem = (WorkFlowNode) item;
        IPayload p = workFlowItem.getPayload();
        String in = workFlowItem.getIn();
        if (p instanceof CipherPayload) {
            CipherPayload payload = (CipherPayload) p;
            if ("#input".equals(urlTag)) {
                return in != null;
            } else if ("#action".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getAction());
            } else if ("#pipe".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getCipherType());
            }
        }
        return false;
    }
}
