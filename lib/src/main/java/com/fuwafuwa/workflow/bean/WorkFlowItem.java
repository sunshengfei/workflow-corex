package com.fuwafuwa.workflow.bean;

import com.fuwafuwa.workflow.agent.FlowFactory;
import com.fuwafuwa.workflow.agent.IFactory;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

import java.io.Serializable;
import java.util.HashMap;


public class WorkFlowItem implements Serializable, IWorkFlowAdapterItem {

    public final static int IGNORE = ~0XFFFF;

    //    private WorkFlowItemType type; //REMOVED
    //新增❤️
    private int itemType = IGNORE;
    private boolean usrActionBlocked;
    private String title;
    private String template;
    private IPayload payload;
    //region 输入输出
    private String in;
    private String out;
    //endregion

    //region 扩展输入 keyTypeString => _id
    private HashMap<String, String> extIn;
    //endregion

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public boolean isPipe() {
        if (itemType == IGNORE) return false;
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(this.itemType);
        if (factory != null) {
            return factory.isPipe();
        }
        return false;
    }

    public boolean isVariable() {
        if (itemType == IGNORE) return false;
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(this.itemType);
        if (factory != null) {
            return factory.isVariable();
        }
        return false;
    }


    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isUsrActionBlocked() {
        return usrActionBlocked;
    }

    public void setUsrActionBlocked(boolean usrActionBlocked) {
        this.usrActionBlocked = usrActionBlocked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public IPayload getPayload() {
        return payload;
    }

    public void setPayload(IPayload payload) {
        this.payload = payload;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public HashMap<String, String> getExtIn() {
        return extIn;
    }

    public void setExtIn(HashMap<String, String> extIn) {
        this.extIn = extIn;
    }
}
