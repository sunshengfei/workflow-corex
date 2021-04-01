package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fuwafuwa.utils.AndroidTools;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.AppAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ChoseAppDialog extends BottomSheetDialogFragment {

    private static final String TAG = "ChoseInputFromPreviousDialog";
    Toolbar toolbar;
    LayersRecyclerLayout brokersLayers;
    private AppAdapter adapter;
    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;

    Disposable appListDisposable;

    public interface IEventHandler {
        void onClick(ResolveInfo item);
    }

    public static ChoseAppDialog instance() {
        ChoseAppDialog dialog = new ChoseAppDialog();
        Bundle args = new Bundle();
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
        toolbar.setTitle("请选择App");
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        adapter = new AppAdapter(getActivity());

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
        brokersLayers.rvDataView.setLayoutManager(manager);
        SpacingRecyclerViewDivider decoration = new SpacingRecyclerViewDivider(4, 2);
        brokersLayers.rvDataView.addItemDecoration(decoration);

        brokersLayers.rvDataView.setAdapter(adapter);
//        brokersLayers.rvDataView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        brokersLayers.disableFreshAndLoad(true);
        adapter.setOnBaseRecyclerAdapterEvent(new BaseRecyclerAdapter.OnBaseRecyclerAdapterEvent() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder) {
                if (handler != null) {
                    ResolveInfo item = adapter.getItem(holder.getBindingAdapterPosition());
                    handler.onClick(item);
                }
                close();
            }

            @Override
            public boolean onItemLongClick(BaseRecyclerViewHolder position) {
                return false;
            }
        });
        appListDisposable = Observable.create((ObservableOnSubscribe<List<ResolveInfo>>) emitter -> {
            Context context = getActivity();
            if (context == null) {
                emitter.onComplete();
                return;
            }
            List<ResolveInfo> list = AndroidTools.getLauncherApps(getActivity());
            emitter.onNext(list);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::$data,
                        e -> {
                            ModalComposer.showToast("获取App列表出错");
                        });
    }


    private void close() {
        if (appListDisposable != null && !appListDisposable.isDisposed()) {
            appListDisposable.dispose();
            appListDisposable = null;
        }
        dismiss();
    }

    private BottomSheetBehavior<?> getBehavior() {
        return behavior;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, TAG);
    }

    public void $data(List<ResolveInfo> list) {
        adapter.notify(list);
        if (RegexHelper.isEmpty(list)) {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_DATA_EMPTY);
        } else {
            brokersLayers.showLayer(LayersFrameLayout.LAYER_CUSTOM);
        }
    }


}
