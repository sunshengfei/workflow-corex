package com.fuwafuwa.workflow.agent;

import androidx.annotation.NonNull;

import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.List;

public class FlowScanner {

    public static int backSearchIndex(List<WorkFlowNode> items, int i, @NonNull String maybeGid, int itemType) {
        if (i == 0) return -1;
        for (int j = i - 1; j >= 0; j--) {
            WorkFlowNode value = items.get(j);
            if (maybeGid.equals(value.get_gid())
                    && value.getItemType() == itemType) {
                return j;
            }
        }
        return -1;
    }

    public static int removeAfterElseItems(List<WorkFlowNode> items,
                                           int from,
                                           String parentId, int startItemType, int endItemType) {
        int start = findIndexFromIndex(items, from, parentId, startItemType);
        int end = findIndexFromIndex(items, from, parentId, endItemType);
        int count = end - start - 1;
        int del = count;
        while (del-- > 0) {
            items.remove(start + 1);
        }
        return count;
    }

    public static int findIndexFromIndex(List<WorkFlowNode> items,
                                         int from,
                                         String parentId,
                                         int itemType
    ) {
        WorkFlowNode node = items.get(from);
        for (int i = from + 1; i < items.size(); i++) {
            WorkFlowNode value = items.get(i);
            if (parentId.equals(value.get_gid())
                    && value.getItemType() == itemType && node.get_depth() == value.get_depth()) {
                return i;
            }
        }
        return from;
    }
}
