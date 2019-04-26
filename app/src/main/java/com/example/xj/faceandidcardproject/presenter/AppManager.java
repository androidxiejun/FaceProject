package com.example.xj.faceandidcardproject.presenter;

import android.app.Activity;

import java.util.Stack;


public class AppManager {

    public static boolean IS_DEBUG ;

    private static AppManager instance;

    private static Stack<Activity> activityStack = new Stack<>();

    public static AppManager getInstance(){
        if(instance == null){
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }


    /**
     * 判断是否包含某个Activity
     * @param activity
     * @return
     */
    public boolean containActivity(Activity activity){
        if(activityStack!=null&&activityStack.contains(activity)){
            return true;
        }
        return false;
    }

    public boolean containActivity(String className){
        if(activityStack==null){
            return false;
        }
        for (Activity activity : activityStack) {
            try {
                if(activity.getClass()==Class.forName(className)){
                    return true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }




    public void delExclude(Activity activity){
        for(int i=0;i<activityStack.size();i++){
            if(activityStack.get(i)!=activity){
                activityStack.remove(activityStack.get(i));

            }
        }
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束除mainActivity之外的所有页面
     */
    public void finishMostActivity(Activity activity) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)&&activityStack.get(i)!=activity) {
                activityStack.get(i).finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        } finally {
            System.exit(0);
        }
    }

    /**
     * 判断Activity是否是第一个
     * @param activity
     * @return
     */
    public static boolean isTop(Activity activity){
        if(activityStack.get(0)==activity){
            return true;
        }
        return false;
    }
}
