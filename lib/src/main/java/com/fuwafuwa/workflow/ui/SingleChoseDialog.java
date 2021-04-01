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

import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.SingleChoseAdapter;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.RegexHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class SingleChoseDialog extends BottomSheetDialogFragment {

    private static final String TAG = "SingleChoseDialog";
    private static final String KEY = "KEY";
    private static final String LIST = "LIST";
    Toolbar toolbar;
    LayersRecyclerLayout brokersLayers;
    private SingleChoseAdapter adapter;
    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;

    public interface IEventHandler {
        void onClick(String item);
    }

    public static SingleChoseDialog instance(String selectValue, List<String> options) {
        SingleChoseDialog dialog = new SingleChoseDialog();
        Bundle args = new Bundle();
        args.putString(KEY, selectValue);
        args.putStringArrayList(LIST, (ArrayList<String>) options);
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
        brokersLayers = rootView.findViewById(R.id.brokersLayers);
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
        toolbar.setTitle("请选择");
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        adapter = new SingleChoseAdapter(getActivity());
        brokersLayers.rvDataView.setAdapter(adapter);
        brokersLayers.rvDataView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        brokersLayers.disableFreshAndLoad(true);
        adapter.setOnBaseRecyclerAdapterEvent(new BaseRecyclerAdapter.OnBaseRecyclerAdapterEvent() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder) {
                if (handler != null) {
                    String operator = adapter.getItem(holder.getBindingAdapterPosition());
                    handler.onClick(operator);
                }
                close();
            }

            @Override
            public boolean onItemLongClick(BaseRecyclerViewHolder position) {
                return false;
            }
        });
        if (getArguments() != null && getArguments().containsKey(LIST)) {
            ArrayList<String> dataList = getArguments().getStringArrayList(LIST);
            //数据
            $data(dataList);
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

    public void $data(List<String> list) {
        adapter.notify(list);
        if (RegexHelper.isEmpty(list)) {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_DATA_EMPTY);
        } else {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_CUSTOM);
        }
    }


}
