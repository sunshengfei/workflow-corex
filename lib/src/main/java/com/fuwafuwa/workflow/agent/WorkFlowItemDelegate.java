package com.fuwafuwa.workflow.agent;

import android.graphics.Color;

import com.fuwafuwa.theme.ThemeIconConf;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.util.List;
import java.util.UUID;

public class WorkFlowItemDelegate {

    private static List<Integer> flagColors = ThemeIconConf.getDarkColors();
    private static int colorIndex = 0;

    public void apply(IWorkFlowAdapter adapter, WorkFlowTypeItem item1) {
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(item1.getFyItemType());
        if (factory != null) {
            factory.onCreateSkeleton(adapter, item1);
        }
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }


    public static WorkFlowNode __build_item__(int type, boolean canHasChild, String gid) {
        String uuid = getUUID();
        WorkFlowNode workFlowItem = new WorkFlowNode();
        workFlowItem.setItemType(type);
        workFlowItem.set_id(uuid);
        workFlowItem.set_gid(gid);
        workFlowItem.setCanHasChild(canHasChild);
        return workFlowItem;
    }

    public static int __pick_color__() {
        int flagColor = Color.TRANSPARENT;
        if (flagColors != null && flagColors.size() > 0) {
            if (colorIndex >= flagColors.size()) colorIndex = 0;
            flagColor = flagColors.get(colorIndex);
            colorIndex++;
        }
        return flagColor;
    }
}
