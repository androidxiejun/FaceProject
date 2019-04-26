package com.example.xj.faceandidcardproject.presenter;

import android.content.Context;


public interface IBaseView {
    /**
     * 显示正在加载view
     */
    void showLoading();
    /**
     * 关闭正在加载view
     */
    void hideLoading();
    /**
     * 显示提示
     * @param msg
     */
    void showToast(String msg);

    /**
     * 获取上下文
     * @return 上下文
     */
    Context getContext();
}
