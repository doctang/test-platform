package com.ztemt.test.mtbf;

import android.os.SystemProperties;

public abstract class Cer_MTBF_Telephony extends Cer_MTBF {

    protected String getOperatorNumber() {
        String operator = SystemProperties.get("gsm.operator.numeric");
        String phoneNumber = null;

        if ("46000".equals(operator) || "46002".equals(operator)
                || "46007".equals(operator)) {
            phoneNumber = "10086";
        } else if ("46001".equals(operator)) {
            phoneNumber = "10010";
        } else if ("46003".equals(operator)) {
            phoneNumber = "10000";
        } else {
            phoneNumber = "";
        }

        return phoneNumber;
    }
}
