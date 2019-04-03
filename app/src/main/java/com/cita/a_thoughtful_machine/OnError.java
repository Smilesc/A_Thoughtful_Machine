package com.cita.a_thoughtful_machine;

import android.os.Handler;
import android.os.Message;

class OnError implements Handler.Callback {

    @Override
    public boolean handleMessage(Message msg) {

        System.out.println("ON ERROR CALLED: " + msg);
        return false;
    }
}
