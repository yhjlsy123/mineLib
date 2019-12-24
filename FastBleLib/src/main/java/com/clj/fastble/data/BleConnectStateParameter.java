package com.clj.fastble.data;

/**
 * Autor:Administrator
 * CreatedTime:2019/12/24 0024
 * UpdateTime:2019/12/24 0024 9:31
 * Des:蓝牙监听状态
 * UpdateContent:
 **/
public class BleConnectStateParameter {

    private int status;
    private boolean isActive;


    public BleConnectStateParameter(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
