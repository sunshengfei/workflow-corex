package com.fuwafuwa.workflow.plugins.url.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.fuwafuwa.utils.AnimationCenter;
import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.RequestBodyMaker;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Kwags;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.url.payload.HttpPayload;
import com.fuwafuwa.workflow.plugins.url.template.HttpTemplateDelegate;
import com.fuwafuwa.workflow.ui.SingleChoseDialog;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowURLCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private TextView _id;
    private Drawable collapseIcon, expandIcon;
    private TextView expandAll;
    private TextView method;
    private TextView expandHeader;
    private TableLayout tableHeaders;
    private LinearLayout addHeader;
    private SegmentedButtonGroup requestContentType;
    private TableLayout tableForm;
    private LinearLayout addForm;
    private AppCompatEditText bodyInput;
    private View panelAll;
    private View panelBody;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private TextView title;
    private List<Kwags> mHeaders;
    private List<Kwags> mForms;

    WorkFlowNode workFlowItem;

    public WorkFlowURLCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_http);
        title = $(R.id.title);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        _id = $(R.id._id);

        expandAll = $(R.id.expand_all);
        panelAll = $(R.id.panel_all);
        panelBody = $(R.id.panel_body);
        method = $(R.id.method);
        expandHeader = $(R.id.expand_header);
        tableHeaders = $(R.id.table_headers);
        tableForm = $(R.id.table_form);
        addForm = $(R.id.add_form);
        addHeader = $(R.id.add_header);
        bodyInput = $(R.id.body_input);
        requestContentType = $(R.id.requestContentType);
        this.collapseIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_collapse_more_black_24dp);
        this.expandIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_more_black_24dp);

        mHeaders = new ArrayList<>();
        mForms = new ArrayList<>();
    }

    @Override
    public void update(IWorkFlowAdapterItem data, int positon) {
        super.update(data);
        if (!(data instanceof WorkFlowNode)) {
            return;
        }
        if (adapter.readOnly) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        workFlowItem = (WorkFlowNode) data;
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        cmdIcon.setImageResource(R.drawable.icon_md_link);
        cmdType.setText("获取URL内容");
        HttpPayload payload;
        if (workFlowItem.getPayload() instanceof HttpPayload) {
            payload = (HttpPayload) workFlowItem.getPayload();
        } else {
            payload = new HttpPayload();
            workFlowItem.setPayload(payload);
        }
        String url = payload.getUrl();
        if (RegexHelper.isEmpty(url)) {
            url = "URL";
        } else if (RegexHelper.isURL(url)) {
            url = payload.getUrl();
        } else if (url.startsWith(WorkFlowNode.VAR_PREFIX)) {
            url = "变量" + url.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
        } else {
            url = url + "的结果";
        }
        String template = HttpTemplateDelegate.getTemplate(url);
        SpannableStringBuilder strBuilder = TemplateHandler.spannedStringAndHandler(template, this, adapter.readOnly);
        title.setText(strBuilder);
        title.setFocusable(true);
        title.setClickable(true);
        title.setLinksClickable(true);
        title.setMovementMethod(LinkMovementMethod.getInstance());
        delete.setTag(data);
        initEvent(payload);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(HttpPayload payload) {
        tableForm.setVisibility(View.GONE);
        addForm.setVisibility(View.GONE);
        bodyInput.setVisibility(View.GONE);
        panelAll.setVisibility(View.GONE);
        //renderData
        if (!RequestBodyMaker.isValidMethod(payload.getMethod())) {
            payload.setMethod("GET");
        }
        methodControl(payload.getMethod());
        if (RegexHelper.isEmpty(payload.getContentType())) {
            payload.setContentType("form");
        }
        method.setText(payload.getMethod().toUpperCase());
        HashMap<String, String> headers = payload.getHeaders();
        tableHeaders.removeAllViews();
        if (RegexHelper.isNotEmpty(headers)) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                Kwags kwags = new Kwags();
                kwags.setKey(key);
                kwags.setValue(headers.get(key));
                mHeaders.add(kwags);
                KVHolder row = new KVHolder(null, kwags, adapter.readOnly);
                row.isHeader = true;
                tableHeaders.addView(row.itemView);
            }
        }
        //body
        String bodyStr = payload.getBody();
        _contentTypeChange("form".equalsIgnoreCase(payload.getContentType()));
        tableForm.removeAllViews();
        if ("form".equalsIgnoreCase(payload.getContentType())) {
            requestContentType.setPosition(0, false);
            HashMap<String, String> map = GsonUtils.parseJson(bodyStr, HashMap.class);
            if (RegexHelper.isNotEmpty(map)) {
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    Kwags kwags = new Kwags();
                    kwags.setKey(key);
                    kwags.setValue(map.get(key));
                    mForms.add(kwags);
                    KVHolder row = new KVHolder(null, kwags, adapter.readOnly);
                    tableForm.addView(row.itemView);
                }
            }
        } else {
            if ("json".equalsIgnoreCase(payload.getContentType())) {
                requestContentType.setPosition(1, false);
                bodyInput.setText(GsonUtils.pretty(bodyStr));
            } else {
                requestContentType.setPosition(2, false);
                bodyInput.setText(bodyStr);
            }
        }
        delete.setOnClickListener(v -> {
            adapter.remove(this);
        });
        expandAll.setOnClickListener(v -> {
            if (panelAll.getVisibility() == View.VISIBLE) {
                AnimationCenter.collapse(panelAll);
                expandAll.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, collapseIcon, null
                );
            } else {
                AnimationCenter.expand(panelAll);
                expandAll.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, expandIcon, null
                );
            }
        });
        expandHeader.setOnClickListener(v -> {
            if (tableHeaders.getVisibility() == View.VISIBLE) {
                expandHeader.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, collapseIcon, null
                );
                AnimationCenter.collapse(tableHeaders);
            } else {
                expandHeader.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, expandIcon, null
                );
                AnimationCenter.expand(tableHeaders);
            }
            addHeader.setVisibility(addHeader.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
        addHeader.setOnClickListener(v -> {
            Kwags kwags = new Kwags();
            mHeaders.add(kwags);
            KVHolder row = new KVHolder(null, kwags, adapter.readOnly);
            row.isHeader = true;
            tableHeaders.addView(row.itemView);
        });
        addForm.setOnClickListener(v -> {
            Kwags kwags = new Kwags();
            mForms.add(kwags);
            KVHolder row = new KVHolder(null, kwags, adapter.readOnly);
            tableForm.addView(row.itemView);
        });
        requestContentType.setOnPositionChangedListener(position -> {
            _contentTypeChange(position == 0);
            bodyChange();
        });
        bodyInput.setOnTouchListener((view, motionEvent) -> {
            if (bodyInput.getLineCount() > bodyInput.getMaxLines()) {
                if (bodyInput.isFocused() && (view.getId() == R.id.body_input && canVerticalScroll(bodyInput))) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
            return false;
        });
        method.setOnClickListener(view -> {
            if (getContext() == null) return;
            if (getContext() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                SingleChoseDialog dialog = SingleChoseDialog.instance(null, RequestBodyMaker.methods);
                dialog.handler = item -> {
                    method.setText(item);
                    payload.setMethod(item);
                    methodControl(item);
                };
                dialog.show(activity.getSupportFragmentManager());
            }
        });
        bodyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null)
                    payload.setBody(editable.toString());
            }
        });
        bodyInput.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void methodControl(String item) {
        if (RequestBodyMaker.isNoBodyMethod(item)) {
            AnimationCenter.collapse(panelBody);
            AnimationCenter.collapse(panelBody);
        } else {
            AnimationCenter.expand(panelBody);
        }
    }

    @Override
    public void absorb() {
        if (workFlowItem.getPayload() == null) return;
        if (workFlowItem.getPayload() instanceof HttpPayload) {
            HttpPayload payload = (HttpPayload) workFlowItem.getPayload();
            if (requestContentType.getPosition() == 0) {
                payload.setContentType("form");
            } else if (requestContentType.getPosition() == 1) {
                payload.setContentType("json");
            } else {
                payload.setContentType("raw");
            }
            payload.setHeaders(mapFromList(mHeaders));
            String body = null;
            HashMap<String, String> tableMap = null;
            if (requestContentType.getPosition() == 0) {
                tableMap = mapFromList(mForms);
                if (tableMap != null)
                    body =  GsonUtils.toJson(tableMap);
            } else {
                body = RegexHelper.isNotEmpty(bodyInput.getText()) ? bodyInput.getText().toString() : "";
            }
            payload.setBody(body);
            HashMap<String, String> flowIdsMap = new HashMap<>();
            if (isRef(payload.getUrl())) {
                flowIdsMap.put("x-URL", payload.getUrl());
            }
            HashMap<String, String> hders = payload.getHeaders();
            if (RegexHelper.isNotEmpty(hders)) {
                int t = 0;
                for (Map.Entry<String, String> hh : hders.entrySet()) {
                    if (isRef(hh.getKey())) {
                        flowIdsMap.put("x-H" + t++, hh.getKey());
                    }
                    if (isRef(hh.getValue())) {
                        flowIdsMap.put("x-HV" + t++, hh.getKey());
                    }
                }
            }
            if (RegexHelper.isNotEmpty(tableMap)) {
                int t = 0;
                for (Map.Entry<String, String> hh : tableMap.entrySet()) {
                    if (isRef(hh.getKey())) {
                        flowIdsMap.put("x-B" + t++, hh.getKey());
                    }
                    if (isRef(hh.getValue())) {
                        flowIdsMap.put("x-BV" + t++, hh.getKey());
                    }
                }
            }
            workFlowItem.setExtIn(flowIdsMap);
        }
    }

    private void bodyChange() {
        if (workFlowItem.getPayload() == null) return;
        if (workFlowItem.getPayload() instanceof HttpPayload) {
            HttpPayload payload = (HttpPayload) workFlowItem.getPayload();
            if (requestContentType.getPosition() == 0) {
                payload.setContentType("form");
            } else if (requestContentType.getPosition() == 1) {
                payload.setContentType("json");
            } else {
                payload.setContentType("raw");
            }
            String body = null;
            HashMap<String, String> tableMap = null;
            if (requestContentType.getPosition() == 0) {
                tableMap = mapFromList(mForms);
                if (tableMap != null)
                    body = GsonUtils.toJson(tableMap);
            } else {
                body = RegexHelper.isNotEmpty(bodyInput.getText()) ? bodyInput.getText().toString() : "";
            }
            payload.setBody(body);
        }
    }

    private void headerChange() {
        if (workFlowItem.getPayload() == null) return;
        if (workFlowItem.getPayload() instanceof HttpPayload) {
            HttpPayload payload = (HttpPayload) workFlowItem.getPayload();
            payload.setHeaders(mapFromList(mHeaders));
        }
    }


    private boolean isRef(String ref) {
        if (RegexHelper.isEmpty(ref)) return false;
        if (ref.matches("\\w{32}")) return true;
        if (ref.startsWith(WorkFlowNode.VAR_PREFIX)) return true;
        return false;
    }


    private HashMap<String, String> mapFromList(@NonNull List<Kwags> maps) {
        HashMap<String, String> headers = new HashMap<>();
        for (Kwags kwags : maps) {
            if (RegexHelper.isAllNotEmpty(kwags.getKey(), kwags.getValue())) {
                headers.put(kwags.getKey(), kwags.getValue());
            }
        }
        return headers;
    }

    private HashMap<String, String> mapFromTable(@NonNull TableLayout tableLayout) {
        int childCount = tableLayout.getChildCount();
        if (childCount > 0) {
            HashMap<String, String> headers = new HashMap<>();
            for (int i = 0; i < childCount; i++) {
                View child = tableLayout.getChildAt(i);
                Object tag = child.getTag();
                if (tag instanceof KVHolder) {
                    KVHolder holder = (KVHolder) tag;
                    Kwags kw = holder.getKeyValue();
                    if (RegexHelper.isAllNotEmpty(kw.getKey(), kw.getValue())) {
                        headers.put(kw.getKey(), kw.getValue());
                    }
                }
            }
            return headers;
        }
        return null;
    }

    private void _contentTypeChange(boolean isForm) {
        if (isForm) {
            AnimationCenter.expand(tableForm);
            AnimationCenter.expand(addForm);
            AnimationCenter.collapse(bodyInput);
        } else {
            AnimationCenter.collapse(tableForm);
            AnimationCenter.collapse(addForm);
            AnimationCenter.expand(bodyInput);
        }
    }

    private boolean canVerticalScroll(EditText editText) {
        if (editText.canScrollVertically(-1) || editText.canScrollVertically(1)) {
            //垂直方向上可以滚动
            return true;
        }
        return false;
    }


    class KVHolder implements TextWatcher {
        private boolean isHeader;
        private Kwags kv;
        private MaterialIconView kvRemove;
        private AppCompatEditText kvKey;
        private AppCompatEditText kvVal;
        private View itemView;

        public KVHolder(View view, Kwags kv, boolean readonly) {
            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_table_row_kv, null);
            }
            kvRemove = view.findViewById(R.id.kv_remove);
            kvRemove.setVisibility(readonly ? View.GONE : View.VISIBLE);
            kvKey = view.findViewById(R.id.kv_key);
            kvVal = view.findViewById(R.id.kv_val);
            this.itemView = view;
            this.itemView.setTag(this);
            this.kv = kv;
            render();
            if (!readonly) {
                initEvent();
            }
        }

        private void render() {
            if (this.kv != null) {
                if (kv.getKey() != null)
                    kvKey.setText(kv.getKey());
                if (kv.getValue() != null)
                    kvVal.setText(kv.getValue());
            }
        }

        private void initEvent() {
            kvRemove.setOnClickListener(view -> {
                removeSelf();
            });
            kvVal.addTextChangedListener(this);
            kvKey.addTextChangedListener(this);
        }

        private void removeSelf() {
            if (this.itemView == null) return;
            ViewGroup parent = (ViewGroup) this.itemView.getParent();
            parent.removeView(this.itemView);
            if (isHeader) {
                mHeaders.remove(kv);
                headerChange();
            } else {
                mForms.remove(kv);
                bodyChange();
            }
        }

        public Kwags getKeyValue() {
            if (this.kv != null) {
                if (kvKey.getText() != null)
                    kv.setKey(kvKey.getText().toString());
                if (kvVal.getText() != null)
                    kv.setValue(kvVal.getText().toString());
                if (isHeader) {
                    headerChange();
                } else {
                    bodyChange();
                }
            }
            return kv;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            getKeyValue();
        }
    }
}
