package com.fuwafuwa.workflow.plugins.condition.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fuwafuwa.utils.EnumTool;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.condition.payload.IFPayload;
import com.fuwafuwa.workflow.plugins.condition.payload.OperatorEnum;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;
import com.fuwafuwa.workflow.plugins.common.EasyUtils;
import com.fuwafuwa.workflow.ui.ChoseInputFromPreviousDialog;
import com.fuwafuwa.workflow.ui.SingleChoseDialog;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
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
        IFPayload payload = null;
        if (p instanceof IFPayload) {
            payload = (IFPayload) p;
        } else {
            payload = new IFPayload();
            workFlowItem.setPayload(payload);
        }
        final IFPayload mirror = payload;
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
        } else if ("#operator".equals(urlTag)) {
            //弹窗输入正则
            List<OperatorEnum> list = Arrays.asList(OperatorEnum.values());
            List<String> mlist = Stream.of(list).map(OperatorEnum::getValue).collect(Collectors.toList());
            SingleChoseDialog dialog = SingleChoseDialog.instance(
                    RegexHelper.isEmpty(mirror.getOperator()) ?
                            null :
                            mirror.getOperator().getValue(), mlist);
            dialog.handler = item1 -> {
                Optional<OperatorEnum> ob = EnumTool.getEnum(OperatorEnum.class, mitem -> item1.equals(mitem.getValue()));
                if (ob != null && !ob.isEmpty()) {
                    mirror.setOperator(ob.get());
                }
                adapter.notifyItemChanged(holder.getBindingAdapterPosition());
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
        } else if ("#condition".equals(urlTag)) {
            EasyUtils.showSingleInputDialog(mContextRef, mirror.getParam(), item1 -> {
                mirror.setParam(item1);
                adapter.notifyItemChanged(holder.getBindingAdapterPosition());
            });
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<IWorkFlowAdapterItem> adapter = (BaseRecyclerAdapter<IWorkFlowAdapterItem>) holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        WorkFlowNode workFlowItem = (WorkFlowNode) item;
        String in = workFlowItem.getIn();
        Object p = workFlowItem.getPayload();
        if (p instanceof IFPayload) {
            IFPayload payload = (IFPayload) p;
            if ("#input".equals(urlTag)) {
                return in != null;
            } else if ("#operator".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getOperator());
            } else if ("#condition".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getParam());
            }
        }
        return false;
    }
}
