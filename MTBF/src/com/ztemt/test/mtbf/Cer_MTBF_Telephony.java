package com.ztemt.test.mtbf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Cer_MTBF_Telephony extends Cer_MTBF {

    protected String getOperatorNumber() {
        String operator = null;
        String phoneNumber = null;

        try {
            Process p = Runtime.getRuntime().exec("getprop gsm.operator.numeric");
            InputStreamReader in = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(in);
            operator = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
