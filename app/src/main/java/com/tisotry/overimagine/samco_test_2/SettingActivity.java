package com.tisotry.overimagine.samco_test_2;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity {
    private static final String TAG = "Setting";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        //      Preference
        //      현재 기압 고도계가 정확하지 않을 경우, 사용자는 현재 기압을 고도 0 로 조절하는 명령어를 송신하는 역할 수행
        //      OnPreferenceClickListener를 통해 clearSensorValue함수를 호출한다.
        Preference pref_clear = findPreference("pref_clear");
        pref_clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearSensorValue();
                return false;
            }
        });

        //      ListPreference
        //      비행체에 장착된 배터리의 종류상태(Cell 수)에 따라 배터리 잔량 부족 경고 수준을 조정할 수 있다.
        //      OnPreferenceChangeListener를 통해 변한 값을 summary로 지정, preference에 저장한다.

        // !!   아직 preference 없음
        final ListPreference pref_warningValue = (ListPreference) findPreference("pref_warningValue");
        pref_warningValue.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Log.i(TAG, "onPreferenceChange: " + o.toString());

                pref_warningValue.setSummary("현재 설정 : " + o.toString());
                return false;
            }
        });
    }

    private void clearSensorValue() {
        Toast.makeText(this, "센서값을 초기화하였습니다", Toast.LENGTH_SHORT).show();
    }


}
