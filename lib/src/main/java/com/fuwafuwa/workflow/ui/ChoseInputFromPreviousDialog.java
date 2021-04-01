package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.SingleChoseNodeAdapter;
import com.fuwafuwa.workflow.adapter.viewholder.SingleChoseNodeHolder;
import com.fuwafuwa.workflow.adapter.viewholder.VarNodeHolder;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;
import com.fuwafuwa.workflow.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChoseInputFromPreviousDialog extends BottomSheetDialogFragment {

    private static final String TAG = "ChoseInputFromPreviousDialog";
    private static final String KEY = "_ID";
    private static final String RESULT_MODE = "_RESULT_MODE";
    Toolbar toolbar;
    LayersRecyclerLayout brokersLayers;
    private SingleChoseNodeAdapter adapter;
    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;

    public interface IEventHandler {
        void onClick(WorkFlowNode item);

        void onClick(VarPayload item);
    }

    public static ChoseInputFromPreviousDialog instance(ArrayList<WorkFlowNode> ids) {
        ChoseInputFromPreviousDialog dialog = new ChoseInputFromPreviousDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY, ids);
        dialog.setArguments(args);
        return instance(ids, true);
    }

    public static ChoseInputFromPreviousDialog instance(ArrayList<WorkFlowNode> ids, boolean resultMode) {
        ChoseInputFromPreviousDialog dialog = new ChoseInputFromPreviousDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY, ids);
        args.putBoolean(RESULT_MODE, resultMode);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bar_with_recyclerview, container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        brokersLayers = rootView.findViewById(R.id.brokersLayers);
        toolbar = rootView.findViewById(R.id.toolbar);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;
        View dialog = getDialog().findViewById(R.id.design_bottom_sheet);
        int pHeight = (int) (UIFrame.getScreenHeight() * 0.75f);
        if (dialog != null) {
            behavior = BottomSheetBehavior.from(dialog);
            dialog.getLayoutParams().height = pHeight;
            getBehavior().setPeekHeight(dialog.getLayoutParams().height);
        }
        if (getView() != null) {
            getView().getLayoutParams().height = pHeight;
        }
        getBehavior().setSkipCollapsed(true);
        getBehavior().setHideable(false);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle("请选择输入");
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        adapter = new SingleChoseNodeAdapter(getActivity());
        brokersLayers.rvDataView.setAdapter(adapter);
        brokersLayers.rvDataView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        brokersLayers.disableFreshAndLoad(true);
        adapter.setOnBaseRecyclerAdapterEvent(new BaseRecyclerAdapter.OnBaseRecyclerAdapterEvent() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder) {
                if (handler != null) {
                    if (holder instanceof SingleChoseNodeHolder) {
                        WorkFlowNode item = (WorkFlowNode) adapter.getItem(holder.getBindingAdapterPosition());
                        handler.onClick(item);
                    }
                    if (holder instanceof VarNodeHolder) {
                        VarPayload item = (VarPayload) adapter.getItem(holder.getBindingAdapterPosition());
                        handler.onClick(item);
                    }
                }
                close();
            }

            @Override
            public boolean onItemLongClick(BaseRecyclerViewHolder position) {
                return false;
            }
        });
        if (getArguments() != null && getArguments().containsKey(KEY)) {
            boolean resultMode = getArguments().getBoolean(RESULT_MODE, true);
            List<WorkFlowNode> keyList = (ArrayList<WorkFlowNode>) getArguments().getSerializable(KEY);
            $data(keyList, resultMode);
        }
    }

    private void close() {
        dismiss();
    }

    private BottomSheetBehavior<?> getBehavior() {
        return behavior;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, TAG);
    }

    public void $data(List<WorkFlowNode> list, boolean resultMode) {
        if (RegexHelper.isEmpty(list)) {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_DATA_EMPTY);
        } else {
            Set<String> keys = new HashSet<>();
            List<VarPayload> varList = new ArrayList<>();
            if (resultMode) {
                for (int i = 0; i < list.size(); i++) {
                    WorkFlowNode node = list.get(i);
                    if (node.isVariable()) {
                        IPayload p = node.getPayload();
                        if (p instanceof VarPayload) {
                            VarPayload payload = (VarPayload) p;
                            list.remove(node);
                            i--;
                            if (keys.contains(payload.getVarName())) {
                                continue;
                            }
                            keys.add(payload.getVarName());
                            varList.add(payload);
                        }
                    }
                }
            }
            List<Object> listAll = new ArrayList<>();
            listAll.addAll(varList);
            listAll.addAll(list);
            adapter.notify(listAll);
            brokersLayers.showLayer(LayersFrameLayout.LAYER_CUSTOM);
        }
    }


}
