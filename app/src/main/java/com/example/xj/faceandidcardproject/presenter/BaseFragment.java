package com.example.xj.faceandidcardproject.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by VNBear on 2018/4/26.
 */

public abstract class BaseFragment extends Fragment implements IBaseView {

    protected View mRootView;
    public Activity mActivity;
    protected Context mContext;

    protected BasePresenter mPresenter;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentViewId(), container, false);
        this.mContext = getActivity();
        //绑定presenter
        mPresenter = bindPresenter();
        if (mPresenter != null){
            mPresenter.attachView(this);
        }else {
        }
        initAllMembersView(savedInstanceState);
        bindView();
        initData();
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        //断开presenter的引用
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        super.onDestroyView();
    }

    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    public void showLoadingDialog(String msg) {

    }

    public void dismissLoadingDialog() {

    }

    protected  abstract int getContentViewId();

    protected abstract void initAllMembersView(Bundle savedInstanceState);

    protected abstract void bindView();

    protected abstract void initData();

    protected  abstract <T extends BasePresenter> BasePresenter bindPresenter();


    @Override
    public void showLoading() {
        checkActivityAttached();
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        checkActivityAttached();
        dismissLoadingDialog();
    }

    @Override
    public void showToast(String text) {

    }


    public void startActivity(Class c){
        startActivity(c,new Bundle());
    }

    public void startActivity(Class c, Bundle data){
        Intent intent = new Intent(mActivity,c);
        intent.putExtras(data);
        startActivity(intent);
    }

    protected boolean isAttachedContext(){
        return getActivity() != null;
    }
    /**
     * 检查activity连接情况
     */
    public void checkActivityAttached() {
        if (mActivity == null) {
            new ActivityNotAttachedException().printStackTrace();
            //throw new ActivityNotAttachedException();
        }
    }
    public static class ActivityNotAttachedException extends RuntimeException {
        public ActivityNotAttachedException() {
            super("Fragment has disconnected from Activity ! - -.");
        }
    }


}
