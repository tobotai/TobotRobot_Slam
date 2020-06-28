package com.tobot.disinfect.module.main;

import android.content.Context;

import com.tobot.disinfect.R;
import com.tobot.disinfect.event.StateResultEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author houdeming
 * @date 2020/6/11
 */
public class State {
    public static final int STATE_CONNECT_ING = 0;
    public static final int STATE_NO_MAP = 1;
    public static final int STATE_LOAD_MAP = 2;
    public static final int STATE_RECOVER_ING = 3;
    public static final int STATE_RECOVER_FAIL = 4;
    public static final int STATE_FINISH = 5;

    public static void sendState(Context context, boolean isSend, int state) {
        if (isSend) {
            boolean isShowDialog = true;
            String reason = "";

            switch (state) {
                case STATE_CONNECT_ING:
                    reason = context.getString(R.string.connect_ing_tips);
                    break;
                case STATE_NO_MAP:
                    isShowDialog = false;
                    reason = context.getString(R.string.no_map_tips);
                    break;
                case STATE_LOAD_MAP:
                    reason = context.getString(R.string.map_load_ing_tips);
                    break;
                case STATE_RECOVER_ING:
                    reason = context.getString(R.string.recover_ing_tips);
                    break;
                case STATE_RECOVER_FAIL:
                    isShowDialog = false;
                    reason = context.getString(R.string.recover_fail_tips);
                    break;
                case STATE_FINISH:
                    isShowDialog = false;
                    reason = context.getString(R.string.init_finish_tips);
                    break;
                default:
                    break;
            }

            EventBus.getDefault().post(new StateResultEvent(isShowDialog, reason));
        }
    }
}
