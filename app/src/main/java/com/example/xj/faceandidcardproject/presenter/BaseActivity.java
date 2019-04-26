package com.example.xj.faceandidcardproject.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public  abstract class  BaseActivity extends AppCompatActivity implements IBaseView{

    protected BasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定presenter
        mPresenter = bindPresenter();
        if (mPresenter != null){
            mPresenter.attachView(this);
        }else {
        }

        // 初始化控件
        initView();
        // 初始化数据
        initData();
        // 初始化adapter
        initAdapter();
        // 为控件注册监听器
        bindView();

        //添加到activity管理栈
        AppManager.getInstance().addActivity(this);
    }

    /**
     * 获取布局文件ID
     */
    protected abstract int getLayoutId();


    protected  abstract <T extends BasePresenter> BasePresenter bindPresenter();

    /**
     * 定义初始化界面的抽象方法
     */
    protected abstract void initView();

    /**
     * 定义初始化数据的抽象方法
     */
    protected abstract void initData();

    /**
     * 定义数据和控件发生关系的抽象方法
     */
    protected abstract void bindView();


    /**
     * 定义适配器
     */
    protected void initAdapter() {

    }


    protected void showSnackBar(String msg, int color){

    }


    public void startActivity(Class c){
        startActivity(c,new Bundle());
    }

    public void startActivity(Class c, Bundle data){
        Intent intent = new Intent(this,c);
        intent.putExtras(data);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从栈中移除该activity
        AppManager.getInstance().finishActivity(this);
        //断开presenter的引用
        if (mPresenter!=null){
            mPresenter.detachView();
        }

    }

    @Override
    public Context getContext() {
        return BaseActivity.this;
    }
}
