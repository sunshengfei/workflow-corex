package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fuwafuwa.workflow.adapter.WorkFlowCmdV2Adapter;
import com.fuwafuwa.workflow.agent.RxEventBus;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowStore;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.bean.WorkFlowVO;
import com.fuwafuwa.za.LiveDataBus;
import com.fuwafuwa.za.WorkFlowImportFromStoreEvent;
import com.fuwafuwa.za.WorkFlowMaterialImportFromStoreEvent;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.RegexHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class PreviewWorkFlowDialog extends BottomSheetDialogFragment {

    private static final String TAG = "PreviewWorkFlowDialog";
    private static String KEY = "_KEY";
    Toolbar toolbar;
    LayersRecyclerLayout layers;
    private WorkFlowCmdV2Adapter adapter;
    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;
    private WorkFlowStore store;
    private MenuItem menuItem;

    public interface IEventHandler {
        void onClick(WorkFlowTypeItem item);
    }

    public static PreviewWorkFlowDialog instance(WorkFlowStore store) {
        PreviewWorkFlowDialog dialog = new PreviewWorkFlowDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY, store);
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
        toolbar = rootView.findViewById(R.id.toolbar);
        layers = rootView.findViewById(R.id.brokersLayers);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;
        View dialog = getDialog().findViewById(R.id.design_bottom_sheet);
        int pHeight = (int) (UIFrame.getScreenHeight() * 0.85f);
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
        toolbar.inflateMenu(R.menu.menu_add);
        menuItem = toolbar.getMenu().findItem(R.id.action_add);
        menuItem.setTitle("添加到工作流");
        menuItem.setVisible(false);
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add) {
                LiveDataBus.post(WorkFlowImportFromStoreEvent.class, new WorkFlowImportFromStoreEvent(store));
                RxEventBus.post(new WorkFlowMaterialImportFromStoreEvent(store));
                close();
            }
            return true;
        });
        initView();
        initData();
    }

    private void initData() {
        if (getArguments() != null && getArguments().containsKey(KEY)) {
            store = (WorkFlowStore) getArguments().getSerializable(KEY);
            if (store == null || store.getWorkFlowVO() == null) return;
            menuItem.setVisible(true);
            WorkFlowVO vo = store.getWorkFlowVO();
            toolbar.setTitle(RegexHelper.isEmptyElse(store.getTitle(), vo.getName()));
            adapter.setParentNode(vo);
            adapter.readOnly = true;
            List<IWorkFlowAdapterItem> commands = new ArrayList<>();
            if (vo.getItems() != null) {
                commands.addAll(vo.getItems());
            }
            $data(commands);
        }
    }

    private void initView() {
        layers.disableFreshAndLoad(true);
        RecyclerView.LayoutManager lineManager = new LinearLayoutManager(getActivity());
        layers.rvDataView.setLayoutManager(lineManager);
        adapter = new WorkFlowCmdV2Adapter(getActivity());
        layers.rvDataView.setAdapter(adapter);
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

    public void $data(List<IWorkFlowAdapterItem> list) {
        adapter.notify(list);
        if (RegexHelper.isEmpty(list)) {
            layers.showLayer(LayersFrameLayout.LAYER_DATA_EMPTY);
        } else {
            layers.showLayer(LayersFrameLayout.LAYER_CUSTOM);
        }
    }


}
