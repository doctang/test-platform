package com.ztemt.test.auto;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztemt.test.auto.item.AccelerometerSensorTest;
import com.ztemt.test.auto.item.AirplaneModeTest;
import com.ztemt.test.auto.item.BaseTest;
import com.ztemt.test.auto.item.BasebandVersionTest;
import com.ztemt.test.auto.item.BluetoothTest;
import com.ztemt.test.auto.item.BreathinglightTest;
import com.ztemt.test.auto.item.CallTest;
import com.ztemt.test.auto.item.CameraTest;
import com.ztemt.test.auto.item.ChargerTest;
import com.ztemt.test.auto.item.CompassSensorTest;
import com.ztemt.test.auto.item.DataTest;
import com.ztemt.test.auto.item.FileSystemTest;
import com.ztemt.test.auto.item.GyroscopeSensorTest;
import com.ztemt.test.auto.item.HumiditySensorTest;
import com.ztemt.test.auto.item.LightSensorTest;
import com.ztemt.test.auto.item.NetworkTest;
import com.ztemt.test.auto.item.NetworkTest2;
import com.ztemt.test.auto.item.NetworkTest3;
import com.ztemt.test.auto.item.NfcTest;
import com.ztemt.test.auto.item.PressureSensorTest;
import com.ztemt.test.auto.item.ProximitySensorTest;
import com.ztemt.test.auto.item.RebootTest;
import com.ztemt.test.auto.item.RingtoneTest;
import com.ztemt.test.auto.item.SDCardTest;
import com.ztemt.test.auto.item.SleepWakeTest;
import com.ztemt.test.auto.item.SmsTest;
import com.ztemt.test.auto.item.TemperatureSensorTest;
import com.ztemt.test.auto.item.VibratorTest;
import com.ztemt.test.auto.item.WifiTest;
import com.ztemt.test.auto.item.WirelessTest;
import com.ztemt.test.auto.util.PreferenceUtils;

public class AutoTestAdapter extends BaseAdapter {

    private Context mContext;
    private BaseTest[] mTests;
    private PreferenceUtils mPrefUtils;

    private Comparator<BaseTest> mComparator = new Comparator<BaseTest>() {

        @Override
        public int compare(BaseTest lhs, BaseTest rhs) {
            return lhs.getOrdinal() - rhs.getOrdinal();
        }
    };

    public AutoTestAdapter(Context context) {
        mContext = context;
        mTests = new BaseTest[] {
                new AirplaneModeTest(context),
                new WirelessTest(context),
                new BluetoothTest(context),
                new WifiTest(context),
                new NfcTest(context),
                new RingtoneTest(context),
                new FileSystemTest(context),
                new CameraTest(context),
                new SDCardTest(context),
                new ChargerTest(context),
                new SleepWakeTest(context),
                new BreathinglightTest(context),
                //new ApnTest(context),
                new CallTest(context),
                new SmsTest(context),
                new VibratorTest(context),
                new CompassSensorTest(context),
                new AccelerometerSensorTest(context),
                new ProximitySensorTest(context),
                new LightSensorTest(context),
                new GyroscopeSensorTest(context),
                new PressureSensorTest(context),
                new HumiditySensorTest(context),
                new TemperatureSensorTest(context),
                new RebootTest(context),
                //new RecoveryTest(context),
                new NetworkTest(context),
                new BasebandVersionTest(context),
                new NetworkTest2(context),
                new NetworkTest3(context),
                new DataTest(context)
        };
        mPrefUtils = new PreferenceUtils(context);
    }

    @Override
    public int getCount() {
        return mTests.length;
    }

    @Override
    public BaseTest getItem(int position) {
        if (position < getCount()) {
            return mTests[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseTest test = mTests[position];
        ViewHolder holder = new ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
        holder.icon = (ImageView) convertView.findViewById(android.R.id.icon);
        holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
        holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
        holder.checkbox = (CheckBox) convertView.findViewById(android.R.id.checkbox);

        holder.icon.setBackgroundResource(position == mPrefUtils.getCurrent() ? android.R.drawable.ic_media_play : 0);
        holder.text1.setText(test.getTitle() + " [" + test.getTestTimes()
                + "/" + test.getTotalTimes() + "]");
        holder.text2.setText(mContext.getString(R.string.test_summary,
                test.getSuccessTimes(), test.getFailureTimes()));
        holder.checkbox.setChecked(test.isEnabled());
        holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                test.setEnabled(isChecked);
            }
        });
        return convertView;
    }

    public void setExtras(Bundle bundle) {
        for (int i = 0; i < mTests.length; i++) {
            mTests[i].setExtras(bundle);
        }
        Arrays.sort(mTests, mComparator);
    }

    public void clearTimes() {
        for (int i = 0; i < mTests.length; i++) {
            mTests[i].setSuccessTimes(0);
            mTests[i].setFailureTimes(0);
        }
    }

    public void disableAll() {
        for (int i = 0; i < mTests.length; i++) {
            mTests[i].setEnabled(false);
        }
    }

    public byte[] report() {
        StringBuffer sb = new StringBuffer(mContext.getString(R.string.report_titles));
        for (int i = 0; i < mTests.length; i++) {
            sb.append((i + 1) + "\t");
            sb.append(mTests[i].getTotalTimes() + "\t");
            sb.append(mTests[i].getTestTimes() + "\t");
            sb.append(mTests[i].getSuccessTimes() + "\t");
            sb.append(mTests[i].getFailureTimes() + "\t");
            sb.append(mTests[i].getTitle() + "\n");
        }
        return sb.toString().getBytes();
    }

    private class ViewHolder {
        ImageView icon;
        TextView text1;
        TextView text2;
        CheckBox checkbox;
    }
}
