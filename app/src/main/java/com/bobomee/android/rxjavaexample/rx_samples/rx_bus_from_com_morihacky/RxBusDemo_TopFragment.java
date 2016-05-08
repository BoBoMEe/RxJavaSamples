package com.bobomee.android.rxjavaexample.rx_samples.rx_bus_from_com_morihacky;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobomee.android.rxjavaexample.R;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RxBus Demo顶部的Fragment
 * */
public class RxBusDemo_TopFragment extends RxFragment {

    private RxBus _rxBus;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_rxbus_top, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _rxBus = ((Rx_bus_Activity) getActivity()).getRxBusSingleton();
    }

    @OnClick(R.id.btn_demo_rxbus_tap)
    public void onTapButtonClicked() {
        if (_rxBus.hasObservers()) {    //是否有观察者，有，则发送一个事件
            _rxBus.send(new RxBusDemoFragment.TapEvent());
        }
    }
}
