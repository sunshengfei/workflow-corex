package com.fuwafuwa.workflow.bean;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import java.io.Serializable;
import java.util.List;


public class WorkFlowNode extends WorkFlowItem implements Serializable, Cloneable {

    public final static String VAR_PREFIX = "VAR://";

    public WorkFlowNode() {
    }

    private String _id;
    private String _pid;
    private String _gid;//groupId
    private int _depth = 0;
    private boolean _isCamel = true;
    @ColorInt
    private int flagColor = Color.TRANSPARENT;
    private boolean canHasChild = false;//sub condition
    private List<WorkFlowNode> groups;
    private List<WorkFlowNode> tempChild;


    @Override
    public Object clone() {
        return super.clone();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_pid() {
        return _pid;
    }

    public void set_pid(String _pid) {
        this._pid = _pid;
    }

    public String get_gid() {
        return _gid;
    }

    public void set_gid(String _gid) {
        this._gid = _gid;
    }

    public int get_depth() {
        return _depth;
    }

    public void set_depth(int _depth) {
        this._depth = _depth;
    }

    public boolean is_isCamel() {
        return _isCamel;
    }

    public void set_isCamel(boolean _isCamel) {
        this._isCamel = _isCamel;
    }

    public int getFlagColor() {
        return flagColor;
    }

    public void setFlagColor(int flagColor) {
        this.flagColor = flagColor;
    }

    public boolean isCanHasChild() {
        return canHasChild;
    }

    public void setCanHasChild(boolean canHasChild) {
        this.canHasChild = canHasChild;
    }

    public List<WorkFlowNode> getGroups() {
        return groups;
    }

    public void setGroups(List<WorkFlowNode> groups) {
        this.groups = groups;
    }

    public List<WorkFlowNode> getTempChild() {
        return tempChild;
    }

    public void setTempChild(List<WorkFlowNode> tempChild) {
        this.tempChild = tempChild;
    }

    @Override
    public String toString() {
        return "WorkFlowNode{" +
                "_id='" + _id + '\'' +
                ", _pid='" + _pid + '\'' +
                ", _gid='" + _gid + '\'' +
                ", _depth=" + _depth +
                ", _isCamel=" + _isCamel +
                ", flagColor=" + flagColor +
                ", canHasChild=" + canHasChild +
                ", groups=" + groups +
                ", tempChild=" + tempChild +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkFlowNode that = (WorkFlowNode) o;

        if (_depth != that._depth) return false;
        if (_isCamel != that._isCamel) return false;
        if (flagColor != that.flagColor) return false;
        if (canHasChild != that.canHasChild) return false;
        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;
        if (_pid != null ? !_pid.equals(that._pid) : that._pid != null) return false;
        if (_gid != null ? !_gid.equals(that._gid) : that._gid != null) return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        return tempChild != null ? tempChild.equals(that.tempChild) : that.tempChild == null;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_pid != null ? _pid.hashCode() : 0);
        result = 31 * result + (_gid != null ? _gid.hashCode() : 0);
        result = 31 * result + _depth;
        result = 31 * result + (_isCamel ? 1 : 0);
        result = 31 * result + flagColor;
        result = 31 * result + (canHasChild ? 1 : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (tempChild != null ? tempChild.hashCode() : 0);
        return result;
    }
}
