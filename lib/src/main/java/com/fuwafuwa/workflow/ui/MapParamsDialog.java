package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.annimon.stream.Stream;
import com.fuwafuwa.workflow.bean.Kwags;
import com.fuwafuwa.workflow.plugins.ibase.MapFormDict;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.SystemBaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class MapParamsDialog extends BottomSheetDialogFragment {


    private static final String TAG = "MapParamsDialog";
    private static final String REMARK = "REMARK";
    private static final String PAYLOAD = "PAYLOAD";
    private static final String TITLE = "TITLE";

    Toolbar toolbar;
    TableLayout tableForm;
    LinearLayout addForm;
    LinearLayout panelBody;

    TextView remark;

    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;
    private HashMap<String, String> payload;
    List<RowHolder> rowHolders;

    public MapParamsDialog() {
    }

    public static MapParamsDialog instance(String title, HashMap<String, String> payload, String remark) {
        MapParamsDialog dialog = new MapParamsDialog();
        Bundle args = new Bundle();
        if (remark != null) {
            args.putString(REMARK, remark);
        }
        args.putSerializable(PAYLOAD, payload);
        args.putString(TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    public static MapParamsDialog instance(HashMap<String, String> payload, String remark) {
        return instance(null, payload, remark);
    }

    public interface IEventHandler {
        void onClick(HashMap<String, String> item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bar_with_hashmap, container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        toolbar=rootView.findViewById(R.id.toolbar);
        tableForm=rootView.findViewById(R.id.table_form);
        addForm=rootView.findViewById(R.id.add_form);
        panelBody=rootView.findViewById(R.id.panel_body);
        remark=rootView.findViewById(R.id.remark);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();if (getDialog() == null) return;
        View dialog = getDialog().findViewById(R.id.design_bottom_sheet);
        if (dialog != null) {
            dialog.getLayoutParams().height = (int) (UIFrame.getScreenHeight() * 0.75f);
            behavior = BottomSheetBehavior.from(dialog);
        }
        if (getView() != null) {
            getBehavior().setPeekHeight(PEEK_HEIGHT_AUTO);
        }
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String remarkText = null;
        if (getArguments() != null) {
            payload = (HashMap<String, String>) getArguments().getSerializable(PAYLOAD);
            remarkText = getArguments().getString(REMARK);
        }
        remark.setText("");
        if (remarkText != null) {
            remark.setText(remarkText);
        }
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        toolbar.setTitle("请输入内容");
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_text_save) {
                if (handler != null) {
                    final HashMap<String, String> payload = new HashMap<>();
                    Stream.of(rowHolders)
                            .map(RowHolder::getKeyValue)
                            .forEach(mapItem -> {
                                if (RegexHelper.isNotEmpty(mapItem.getKey())) {
                                    payload.put(mapItem.getKey(), mapItem.getValue());
                                }
                            });
                    handler.onClick(payload);
                    close();
                }
                return true;
            }
            return false;
        });
        renderTable();
    }

    private void renderTable() {
        if (rowHolders == null) {
            rowHolders = new ArrayList<>();
        }
        if (RegexHelper.isNotEmpty(payload)) {
            Set<String> keys = payload.keySet();
            for (String key : keys) {
                Kwags kwags = new Kwags();
                kwags.setKey(key);
                kwags.setValue(payload.get(key));
                RowHolder row = new RowHolder(null, kwags);
                row.keyEditable = false;
                rowHolders.add(row);
                tableForm.addView(row.itemView);
            }
        }
    }


    private void close() {
        FragmentActivity activity = getActivity();
        if (activity != null && activity.getCurrentFocus() != null) {
            SystemBaseUtils.hideSoft(activity, activity.getCurrentFocus());
        }
        getBehavior().setState(STATE_HIDDEN);
    }

    private BottomSheetBehavior<?> getBehavior() {
        return behavior;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, TAG);
    }


    public interface RowEvent {
        void onRemoveRow(Kwags t);
    }


    class RowHolder {
        public boolean keyEditable = false;
        private Kwags kv;
        private MaterialIconView kvRemove;
        private AppCompatEditText kvKey;
        private AppCompatEditText kvVal;
        private View itemView;

        private RowEvent rowEvent;

        public void setRowEvent(RowEvent rowEvent) {
            this.rowEvent = rowEvent;
        }

        public RowHolder(View view, Kwags kv) {
            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_table_row_kv, null);
            }
            kvRemove = view.findViewById(R.id.kv_remove);
            kvRemove.setVisibility(View.GONE);
            kvKey = view.findViewById(R.id.kv_key);
            kvKey.setEnabled(keyEditable);
            kvVal = view.findViewById(R.id.kv_val);
            this.itemView = view;
            this.itemView.setTag(this);
            this.kv = kv == null ? new Kwags() : kv;
            render();
            initEvent();
        }

        private void render() {
            if (this.kv != null) {
                if (kv.getKey() != null) {
                    kvKey.setTag(kv.getKey());
                    kvKey.setText(MapFormDict.labelFor(kv.getKey()));
                }
                if (kv.getValue() != null)
                    kvVal.setText(kv.getValue());
            }
        }

        private void initEvent() {
            kvRemove.setOnClickListener(view -> {
                removeSelf();
            });
        }

        private void removeSelf() {
            if (this.itemView == null) return;
            ViewGroup parent = (ViewGroup) this.itemView.getParent();
            parent.removeView(this.itemView);
            if (rowEvent != null) {
                rowEvent.onRemoveRow(this.kv);
            }
        }

        public Kwags getKeyValue() {
            if (this.kv != null) {
                if (kvKey.getText() != null) {
                    if (keyEditable) {
                        kv.setKey(kvKey.getText().toString());
                    } else {
                        if (RegexHelper.isNotEmpty(kvKey.getTag())) {
                            kv.setKey(String.valueOf(kvKey.getTag()));
                        }
                    }
                }
                if (kvVal.getText() != null)
                    kv.setValue(kvVal.getText().toString());
            }
            return kv;
        }
    }
}
