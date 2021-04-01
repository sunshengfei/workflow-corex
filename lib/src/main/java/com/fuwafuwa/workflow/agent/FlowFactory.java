package com.fuwafuwa.workflow.agent;

import androidx.annotation.NonNull;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.fuwafuwa.workflow.plugins.beacon.BeaconBridge;
import com.fuwafuwa.workflow.plugins.cipher.CipherBridge;
import com.fuwafuwa.workflow.plugins.jsonformat.JSONFormatBridge;
import com.fuwafuwa.workflow.plugins.alert.AlertBridge;
import com.fuwafuwa.workflow.plugins.app.AppBridge;
import com.fuwafuwa.workflow.plugins.app.AppSelfBridge;
import com.fuwafuwa.workflow.plugins.condition.ConditionIFBridge;
import com.fuwafuwa.workflow.plugins.evaluatejs.EvaluateBridge;
import com.fuwafuwa.workflow.plugins.flowtail.FlowEndBridge;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.plugins.jump.JumpBridge;
import com.fuwafuwa.workflow.plugins.leading.LeadingBridge;
import com.fuwafuwa.workflow.plugins.loop.LoopBridge;
import com.fuwafuwa.workflow.plugins.media.MediaPlayBridge;
import com.fuwafuwa.workflow.plugins.mqtt.MQTTBridge;
import com.fuwafuwa.workflow.plugins.remark.RemarkBridge;
import com.fuwafuwa.workflow.plugins.trailing.TrailingBridge;
import com.fuwafuwa.workflow.plugins.url.URLConnBridge;
import com.fuwafuwa.workflow.plugins.variety.VarietyBridge;
import com.fuwafuwa.workflow.plugins.wait.WaitBridge;
import com.fuwafuwa.workflow.plugins.webpage.WebPageBridge;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlowFactory {

    public static String callerActivityClassName = null;

    private static List<IFactory<? extends IProcess>> procedures = new ArrayList<>();

    static {
        //默认注册
        register(new LeadingBridge.Factory());
        register(new TrailingBridge.Factory());
        register(new ConditionIFBridge.Factory());
//        register(new ConditionElseBridge.Factory());
//        register(new ConditionEndBridge.Factory());
        register(new MQTTBridge.Factory());
        register(new URLConnBridge.Factory());
        register(new VarietyBridge.Factory());
        register(new AlertBridge.Factory());
        register(new BeaconBridge.Factory());
        register(new WaitBridge.Factory());
        register(new LoopBridge.Factory());
        register(new RemarkBridge.Factory());
        register(new CipherBridge.Factory());
        register(new JSONFormatBridge.Factory());
        register(new FlowEndBridge.Factory());
        register(new AppBridge.Factory());
        register(new AppSelfBridge.Factory());
        register(new MediaPlayBridge.Factory());
        register(new WebPageBridge.Factory());
        register(new EvaluateBridge.Factory());
        register(new JumpBridge.Factory());
    }

    /**
     * @param procedure
     */
    public static void register(IFactory<? extends IProcess> procedure) {
        procedures.add(procedure);
    }

    /**
     * Use Name to Unregister the procedure
     *
     * @param procedureName
     */
    public static void unregister(@NonNull String procedureName) {
        for (int i = 0; i < procedures.size(); i++) {
            IFactory<? extends IProcess> item = procedures.get(i);
            if (procedureName.equals(item.getProcedureName())) {
                procedures.remove(i);
                break;
            }
        }
    }

    public static void release() {
        procedures.clear();
    }

    public static List<WorkFlowTypeItem> getScriptMenus() {
        List<WorkFlowTypeItem> validScripts = Stream.of(procedures).map(item -> {
            if (item.getBridge() != null) {
                return item.getBridge().pluginEntry();
            }
            return null;
        }).filter(Objects::nonNull).toList();
        List<WorkFlowTypeItem> titles = Stream.of(validScripts).distinctBy(WorkFlowTypeItem::getTitle).sortBy(WorkFlowTypeItem::get_order).toList();
        List<WorkFlowTypeItem> result = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            WorkFlowTypeItem title = titles.get(i);
            WorkFlowTypeItem newItem = (WorkFlowTypeItem) title.clone();
            newItem.setGroup(null);
            result.add(newItem);
            for (int j = 0; j < validScripts.size(); j++) {
                WorkFlowTypeItem node = validScripts.get(j);
                if (title.getTitle().equals(node.getTitle())) {
                    if (node.getGroup() != null) {
                        result.addAll(node.getGroup());
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param procedureName
     * @return
     * @deprecated Maybe have more
     */
    public static IFactory<? extends IProcess> factoryFor(@NonNull String procedureName) {
        for (int i = 0; i < procedures.size(); i++) {
            IFactory<? extends IProcess> item = procedures.get(i);
            if (procedureName.equals(item.getProcedureName())) {
                return item;
            }
        }
        return null;
    }

    public static IFactory<? extends IProcess> factoryFor(int itemViewType) {
        for (int i = 0; i < procedures.size(); i++) {
            IFactory<? extends IProcess> item = procedures.get(i);
            if (item.acceptItemViewType() != DefaultFactory.Options.IGNORE && item.acceptItemViewType() == itemViewType) {
                return item;
            }
            if (item.acceptItemViewTypes() == null) {
                continue;
            }
            int[] types = item.acceptItemViewTypes();
            Arrays.sort(types);
            if (Arrays.binarySearch(types, itemViewType) >= 0) {
                item.setCurrentItemViewType(itemViewType);
                return item;
            }
        }
        return null;
    }


    public static Class<? extends IPayload> classForTypeInt(int typeInt) {
        Class<? extends IPayload> hasOne = defaultPayloadForTypeInt(typeInt);
        if (hasOne != null) {
            return hasOne;
        }
        for (int i = 0; i < procedures.size(); i++) {
            IFactory<? extends IProcess> factory = procedures.get(i);
            if (factory.payloadType() != DefaultPayloadType.type_none && factory.payloadType() == typeInt) {
                return factory.payloadClass();
            }
        }
        return null;
    }

    private static Class<? extends IPayload> defaultPayloadForTypeInt(int typeInt) {
        switch (typeInt) {
            case DefaultPayloadType.type_string:
                return StringPayload.class;
            case DefaultPayloadType.type_number:
                return NumberPayload.class;
        }
        return null;
    }

}
