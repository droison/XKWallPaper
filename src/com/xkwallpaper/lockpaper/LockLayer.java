package com.xkwallpaper.lockpaper;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * 该类实现屏蔽Home键，但却不是真的屏蔽，只是将当前的锁屏 UI 添加到windowManager里了，然后让他置顶显示。
 * 按了Home，后面的还是退到Home了，只不过看不到了，同样的，长按电源键也会看不到
 * @author song
 *
 */
public class LockLayer {     
    private Activity mActivty;     
    private WindowManager mWindowManager;     
    private View mLockView;     
    private LayoutParams mLockViewLayoutParams;     
    private static LockLayer mLockLayer;     
    private boolean isLocked;     
         
    public static synchronized LockLayer getInstance(Activity act){     
        if(mLockLayer == null){     
            mLockLayer = new LockLayer(act);     
        }     
        return mLockLayer;     
    }     
         
    private LockLayer(Activity act) {     
        mActivty = act;     
        init();     
    }     
     
    private void init(){     
        isLocked = false;     
        mWindowManager = mActivty.getWindowManager();     
        mLockViewLayoutParams = new LayoutParams();     
        mLockViewLayoutParams.width = LayoutParams.MATCH_PARENT;     
        mLockViewLayoutParams.height = LayoutParams.MATCH_PARENT;     
        //实现关键     
        mLockViewLayoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;     
        mLockViewLayoutParams.flags = 1280;     
    }     
    public synchronized void lock() {     
        if(mLockView!=null&&!isLocked){     
            mWindowManager.addView(mLockView, mLockViewLayoutParams);     
        }     
        isLocked = true;     
    }     
    public synchronized void unlock() {     
        if(mWindowManager!=null&&isLocked){     
            mWindowManager.removeView(mLockView);     
        }     
        isLocked = false;     
    }     
    public synchronized void setLockView(View v){     
        mLockView = v;     
    }     
}    