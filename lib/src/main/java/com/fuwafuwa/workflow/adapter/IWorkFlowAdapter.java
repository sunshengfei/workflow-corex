package com.fuwafuwa.workflow.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fred 2016-11-05
 */
public abstract class IWorkFlowAdapter extends BaseRecyclerAdapter<IWorkFlowAdapterItem> {

    private int indent;

    @Override
    public void remove(BaseRecyclerViewHolder holder) {
        super.remove(holder);
        int position = holder.getBindingAdapterPosition();
        whenDrag(holder);
        getDataSets().remove(position);
        notifyDataSetChanged();
    }

    public void whenDrag(BaseRecyclerViewHolder holder) {
        //拖拽时，先从固定位置向后遍历，先判断是否存在分组
        // 如果存在，则先把分组打包，否则单独打包
        int position = holder.getBindingAdapterPosition();
        List<IWorkFlowAdapterItem> mValues = getDataSets();
        final int startIndex = position + 1;
        final int initCount = mValues.size();
        IWorkFlowAdapterItem holderItem = getItem(position);
        if (!(holderItem instanceof WorkFlowNode)) return;
        WorkFlowNode clickItem = (WorkFlowNode) holderItem;
        //遍历，先判断是否存在分组 [条件为是否存在gid]
        String holdGid = clickItem.get_gid();
        List<WorkFlowNode> groups = new ArrayList<>();
        groups.add(clickItem);
        if (RegexHelper.isNotEmpty(holdGid)) {
            List<WorkFlowNode> list = Stream.of(mValues).filterIndexed((index, value) -> {
                if (!(value instanceof WorkFlowNode)) return false;
                WorkFlowNode workFlowNode = (WorkFlowNode) value;
                return index >= startIndex && holdGid.equals(workFlowNode.get_gid());
            }).map(item -> (WorkFlowNode) item).collect(Collectors.toList());
            if (RegexHelper.isNotEmpty(list)) {
                groups.addAll(list);
            }
        }
        List<WorkFlowNode> gps = new ArrayList<>();
        clickItem.setGroups(gps);
        group2TreeGroup(startIndex, groups, mValues);
        for (int i = 0; i < groups.size(); i++) {
            WorkFlowNode mm = groups.get(i);
            if (i > 0) {
                gps.add(mm);
                for (int ii = 0; ii < mValues.size(); ii++) {
                    IWorkFlowAdapterItem item = mValues.get(ii);
                    if (item instanceof WorkFlowNode) {
                        WorkFlowNode find = (WorkFlowNode) item;
                        if (find.get_id() != null && find.get_id().equals(mm.get_id())) {
                            mValues.remove(ii);
                            ii--;
                        }
                    }
                }
            }
        }
        notifyItemRangeRemoved(startIndex, initCount - mValues.size());
    }

    private void group2TreeGroup(int startIndex, List<WorkFlowNode> groups, List<IWorkFlowAdapterItem> mValues) {
        if (RegexHelper.isEmpty(groups)) return;
        for (int l = 0; l < groups.size(); l++) {
            WorkFlowNode group = groups.get(l);
            String id = group.get_id();
            List<WorkFlowNode> children = group.getTempChild();
            if (children == null) {
                children = new ArrayList<>();
                group.setTempChild(children);
            }
            for (int i = startIndex; i < mValues.size(); i++) {
                IWorkFlowAdapterItem item = mValues.get(i);
                if (item instanceof WorkFlowNode) {
                    WorkFlowNode find = (WorkFlowNode) item;
                    if (id.equals(find.get_pid())) {
                        children.add(find);
                        mValues.remove(i);
                        i--;
                    }
                }
            }
            group2TreeGroup(0, children, mValues);
        }
    }


    public void whenDrop(RecyclerView.ViewHolder holder) {
        int position = holder.getBindingAdapterPosition();
        if (position < 0) return;
        IWorkFlowAdapterItem previous = position > 0 ? getItem(position - 1) : null;
        IWorkFlowAdapterItem item = getItem(position);
        List<IWorkFlowAdapterItem> expandedList = new ArrayList<>();
        if (item instanceof WorkFlowNode) {
            WorkFlowNode flowItem = (WorkFlowNode) item;
            if (previous != null) {
                if (previous instanceof WorkFlowNode) {
                    WorkFlowNode previousItem = (WorkFlowNode) previous;
                    if (previousItem.isCanHasChild()) {
                        flowItem.set_pid(previousItem.get_id());
                        flowItem.set_depth(previousItem.get_depth() + 1);
                    } else if (RegexHelper.isNotEmpty(previousItem.get_pid())) {
                        flowItem.set_pid(previousItem.get_pid());
                        flowItem.set_depth(previousItem.get_depth());
                    } else {
                        flowItem.set_pid(null);
                        flowItem.set_depth(0);
                    }
                } else {
                    flowItem.set_pid(null);
                    flowItem.set_depth(0);
                }
            }
            _treeGroup2List(flowItem, expandedList);
            //
            expandedList.remove(0);
            getDataSets().addAll(position + 1, expandedList);
            //检查in 与 当前flowItem id
            int newIndex = getDataSets().indexOf(flowItem);
            Optional<IWorkFlowAdapterItem> findItem1 = Stream.of(getDataSets()).filterIndexed(
                    (index, ite) -> {
                        if (ite instanceof WorkFlowNode) {
                            WorkFlowNode iit = (WorkFlowNode) ite;
                            if (iit.get_id().equals(flowItem.getIn())) {
                                if (newIndex < index) {
                                    return true;
                                }
//                            return iit.get_depth() >= flowItem.get_depth();
                            }
                            return false;
                        }
                        return false;
                    }).findFirst();
            Optional<IWorkFlowAdapterItem> findItem2 = Stream.of(getDataSets()).filterIndexed((index, ite) -> {
                if (ite instanceof WorkFlowNode) {
                    WorkFlowNode iit = (WorkFlowNode) ite;
                    if (flowItem.get_id().equals(iit.getIn())) {
                        if (newIndex > index) {
                            return true;
                        }
//                        return flowItem.get_depth() >= iit.get_depth();
                    }
                    return false;
                }
                return false;
            }).findFirst();
            if (findItem1.isPresent()) {
                flowItem.setIn(null);
            }
            if (findItem2.isPresent()) {
                ((WorkFlowNode) findItem2.get()).setIn(null);
            }
            notifyDataSetChanged();
        }
    }

    private void _treeGroup2List(WorkFlowNode flowItem, List<IWorkFlowAdapterItem> expandedList) {
        List<WorkFlowNode> groups = flowItem.getGroups();
        flowItem.setGroups(null);
        if (groups == null) {
            groups = new ArrayList<>();
            groups.add(flowItem);
        } else {
            groups.add(0, flowItem);
        }
        //先遍历子group
        for (int i = 0; i < groups.size(); i++) {
            WorkFlowNode group = groups.get(i);
            group.set_pid(flowItem.get_pid());
            group.set_depth(flowItem.get_depth());
            _treeChild2List(group, expandedList);
        }
    }

    private void _treeChild2List(WorkFlowNode group, List<IWorkFlowAdapterItem> expandedList) {
        List<WorkFlowNode> children = group.getTempChild();
        expandedList.add(group);
        if (RegexHelper.isEmpty(children)) return;
        for (int i = 0; i < children.size(); i++) {
            WorkFlowNode child = children.get(i);
            child.set_depth(group.get_depth() + 1);
            _treeGroup2List(child, expandedList);
            group.setTempChild(null);
        }
    }


    public WorkFlowVO getParentNode() {
        return parentNode;
    }

    private WorkFlowVO parentNode;

    public IWorkFlowAdapter(Context context) {
        super(context);
    }

    public void setParentNode(WorkFlowVO workFlowVO) {
        this.parentNode = workFlowVO;
    }

    @Override
    public IWorkFlowAdapterItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        int maxDepth = getMaxDepth();
        if (maxDepth < 4) {
            indent = 48;
        } else if (maxDepth < 8) {
            indent = 30;
        } else {
            indent = 20;
        }
        return super.getItemCount();
    }


    protected final void superOnBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    private int getMaxDepth() {
        List<IWorkFlowAdapterItem> list = getDataSets();
        if (RegexHelper.isEmpty(list)) return 0;
        return Stream.of(getDataSets())
                .filter(item -> item instanceof WorkFlowNode)
                .map(item -> (WorkFlowNode) item)
                .map(WorkFlowNode::get_depth)
                .max((o1, o2) -> o1 - o2).get();
    }

    public void setiWorkFlowActionHandler(IWorkFlowActionHandler iWorkFlowActionHandler) {
        this.iWorkFlowActionHandler = iWorkFlowActionHandler;
    }

    protected IWorkFlowActionHandler iWorkFlowActionHandler;

    public interface IWorkFlowActionHandler extends ISimpleFlowAction {
    }
}
