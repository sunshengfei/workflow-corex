package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.fuwafuwa.presenter.MQTTProfilesPresenter;
import com.fuwafuwa.presenter.composer.IMQTTProfilesComposer;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.MQTTProfileAdapter;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.ui.acitivities.MQTTProfileActivity;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.ConnectActionState;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.RegexHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ChoseBrokerDialog extends BottomSheetDialogFragment implements IMQTTProfilesComposer.View {


    private static final String TAG = "ChoseBrokerDialog";
    private static final String BROKER_ID = "BROKER_ID";
    private static final int REQUEST_ADD = 10;

    Toolbar toolbar;
    LayersRecyclerLayout brokersLayers;
    private IMQTTProfilesComposer.Presenter mPresenter;
    private MQTTProfileAdapter adapter;
    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;

    public ChoseBrokerDialog() {
    }

    public static ChoseBrokerDialog instance(String brokerId) {
        ChoseBrokerDialog dialog = new ChoseBrokerDialog();
        Bundle args = new Bundle();
        args.putString(BROKER_ID, brokerId);
        dialog.setArguments(args);
        return dialog;
    }

    public interface IEventHandler {
        void onClick(MQTTConnectUserEntity item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bar_with_recyclerview, container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        brokersLayers= rootView.findViewById(R.id.brokersLayers);
        toolbar=rootView.findViewById(R.id.toolbar);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter = new MQTTProfilesPresenter(this);
        mPresenter.apply();
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
        if (mPresenter != null) {
            mPresenter.unSubscribe();
            mPresenter = null;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        toolbar.inflateMenu(R.menu.menu_add);
        String brokerId = "";
        if (getArguments() != null) {
            brokerId = getArguments().getString(BROKER_ID, "");
        }
        adapter = new MQTTProfileAdapter(getActivity(), true);
        MQTTConnectUserEntity tem = new MQTTConnectUserEntity();
        tem.set_id(brokerId);
        adapter.setChosed(tem);
        brokersLayers.rvDataView.setAdapter(adapter);
        brokersLayers.disableFreshAndLoad(true);
        adapter.setOnBaseRecyclerAdapterEvent(new BaseRecyclerAdapter.OnBaseRecyclerAdapterEvent() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder) {
                if (handler != null) {
                    handler.onClick(adapter.getItem(holder.getBindingAdapterPosition()));
                }
                close();
            }

            @Override
            public boolean onItemLongClick(BaseRecyclerViewHolder position) {
                return false;
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add) {
                startActivityForResult(MQTTProfileActivity.newIntent(getActivity()), REQUEST_ADD);
            }
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                if (mPresenter != null) {
                    mPresenter.apply();
                }
            }
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

    @Override
    public void setPresenter(IMQTTProfilesComposer.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loading(boolean isDialog) {

    }

    @Override
    public void toast(String message) {

    }

    @Override
    public void dialog(String message) {

    }

    @Override
    public void ttTheme() {

    }

    @Override
    public void $data(List<MQTTConnectUserEntity> list) {
        adapter.notify(list);
        if (RegexHelper.isEmpty(list)) {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_DATA_EMPTY);
        } else {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_CUSTOM);
        }
    }

    @Override
    public void $deleteOk() {

    }

    @Override
    public void connectChange(String id, ConnectActionState connected) {

    }

    public void uiControl(Task payload) {

    }
}
