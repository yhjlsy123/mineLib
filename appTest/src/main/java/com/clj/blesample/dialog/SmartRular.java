package com.clj.blesample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Autor:Administrator
 * CreatedTime:2019/11/13 0013
 * UpdateTime:2019/11/13 0013 11:28
 * Des:智能腰尺数据读取
 * UpdateContent:
 **/
public class SmartRular extends Dialog {
    public SmartRular(@NonNull Context context) {
        super(context);
    }

    public SmartRular(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
