package com.intugine.ble.beacon.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by niraj on 30-05-2017.
 */

public class UtilTextLog {

    class Log{
        String tag;
        String value;
        public Log(String pTag, String value){
            this.tag = pTag;
            this.value = value;
        }
        public void update(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            if(value!=null){

                return tag+ ": "+ value;
            }
            return "";
        }
    }

    HashMap<String, Log> logs = new HashMap<String, Log>();

    public void addText(String tag, String value){
        if(tag==null){
            return;
        }
        Log log = logs.get(tag);
        if(log==null){
            log = new Log(tag, value);
            logs.put(tag, log);
        }else {
            log.update(value);
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Log>> it = logs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Log> pair = (Map.Entry) it.next();
            Log log = pair.getValue();
            if(log!=null) {
                String logVal = log.toString();
                if(!TextUtils.isEmpty(logVal)){
                    sb.append(logVal+ "|| \n");
                }
            }
            // avoids a ConcurrentModificationException
        }
        return sb.toString();
    }
}
