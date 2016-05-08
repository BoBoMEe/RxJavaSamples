package com.bobomee.android.rxjavaexample.rx_samples.rx_bus_from_com_morihacky;

import android.os.Bundle;

import com.bobomee.android.rxjavaexample.BaseActivity;

/**
 * Created by bobomee on 16/5/8.
 */
public class Rx_bus_Activity extends BaseActivity {

    private RxBus _rxBus = null;

    // This is better done with a DI Library like Dagger
    public RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }

        return _rxBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new RxBusDemoFragment(), this.toString())
                    .commit();
        }
    }
}
