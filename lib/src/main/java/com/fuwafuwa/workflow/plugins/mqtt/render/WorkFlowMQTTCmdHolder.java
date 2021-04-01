package com.fuwafuwa.workflow.plugins.mqtt.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTPayload;
import com.fuwafuwa.workflow.plugins.mqtt.template.CMDTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowMQTTCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private TextView title;
    private TextView _id;

    public WorkFlowMQTTCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_order);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
    }

    @Override
    public void update(IWorkFlowAdapterItem data, int positon) {
        super.update(data);
        if (!(data instanceof WorkFlowNode)) {
            return;
        }
        if (adapter.readOnly) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        WorkFlowNode workFlowItem = (WorkFlowNode) data;
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        boolean isPublish = workFlowItem.getItemType() == DefaultSystemItemTypes.TYPE_MQTT_PUBLISH;
        if (isPublish) {
            cmdIcon.setImageResource(R.drawable.send);
            cmdType.setText("发送消息");
        } else {
            cmdIcon.setImageResource(R.drawable.download);
            cmdType.setText("订阅消息");
        }
        Object payload = workFlowItem.getPayload();
        MQTTPayload mqttPayload = null;
        if (payload instanceof MQTTPayload) {
            mqttPayload = (MQTTPayload) payload;
        }
        if (mqttPayload == null) {
            mqttPayload = new MQTTPayload();
            workFlowItem.setPayload(mqttPayload);
        }
        String server = RegexHelper.isEmpty(mqttPayload.getBrokerId()) ? "请选择" : RegexHelper.isEmptyElse(mqttPayload.getHost(), "已设置");
        String action = isPublish ? "发布" : "订阅";
        String topic = RegexHelper.isEmpty(mqttPayload.getBody()) ? "请设置话题" : RegexHelper.isEmptyElse(mqttPayload.getBody().getTopic(), "");
        String content = RegexHelper.isEmpty(mqttPayload.getBody()) ? "消息内容" : RegexHelper.isEmptyElse(mqttPayload.getBody().getMessage(), "");
        String template = isPublish ?
                CMDTemplateDelegate.getPublishTemplate(server, action, topic, content) :
                CMDTemplateDelegate.getSubscribeTemplate(server, action, topic);
        SpannableStringBuilder strBuilder = TemplateHandler.spannedStringAndHandler(template, this,adapter.readOnly);
        title.setText(strBuilder);
        title.setFocusable(true);
        title.setClickable(true);
        title.setLinksClickable(true);
        title.setMovementMethod(LinkMovementMethod.getInstance());
        delete.setTag(data);
        delete.setOnClickListener(v -> {
            adapter.remove(this);
        });
    }


}
