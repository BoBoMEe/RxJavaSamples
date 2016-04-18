package com.bobomee.android.rxjavaexample;

import android.view.View;

import com.bobomee.android.rxjavaexample.model.Course;
import com.bobomee.android.rxjavaexample.model.DataFactory;
import com.bobomee.android.rxjavaexample.model.Student;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/4/17.
 */
public class OperatorActivity extends ToolBarActivity {
    @Override
    protected int provideContentViewId() {
        return R.layout.operator_introduction;
    }


    @Override
    public boolean canBack() {
        return true;
    }

    @OnClick(R.id.button_rx_opreator)
    public void opreator(View view) {

        testMethod(1);

    }

    private void testMethod(int i) {

        switch (i) {
            case 0: {
                method0();
            }
            break;
            case 1: {
                method1();
            }
            break;
        }
    }


    //////////////////////////////变换map/////////////////////////////////

    /**
     * 可以看到通过map'操作符将String -> Integer
     * Observable和Subscriber可以做任何事情(Observable可以是一个网络请求，Subscriber用来显示请求结果)
     * 其中Subscriber应该做的越少越好，大部分逻辑应该在Observable中处理
     * Observable和Subscriber是独立于中间的变换过程的
     * <p>
     * <p>
     * 这里需要注意：
     * <p>
     * Func1 和 Action1的区别，Func1用于包装有返回值 的方法。
     * FuncX类似于ActionX也有多个。
     */
    private void method0() {
        Observable.just("Hello, world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Logger.d(integer.toString());
                    }
                })
        ;

    }

    ///////////////////////////flatMap////////////////////////

    private void method1() {

        ArrayList<Student> students = DataFactory.getData();

        Observable.from(students)
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.name;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.d("学生的姓名：" + s);
                    }
                });

    }

    /**
     * 每个学生有多个课程，如需打印每个学生 所修课程，上面的代码则类似下面这样
     */

    private void method2() {

        ArrayList<Student> students = DataFactory.getData();

        Observable.from(students)
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        List<Course> courses = student.courses;

                        for (Course course : courses) {
                            Logger.d(course.toString());
                        }
                    }
                });

    }

    /**
     * 但是上面不是说过Subscriber应该做的越少越好，我们不想在Subscriber中做for循环。这就需要flatmap了
     * <p>
     * Student -->Observable<Course> -->Course
     * <p>
     * <p>
     * 与map不同的是，flatMap返回的是Observable对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中
     * 原理：
     * 1. 使用传入的事件对象创建一个 Observable 对象；
     * 2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
     * 3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。
     * <p>
     * 通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。
     */

    private void method3() {

        ArrayList<Student> students = DataFactory.getData();

        Observable.from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        return Observable.from(student.courses);
                    }
                })
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        Logger.d(course.name);
                    }
                });

    }



    ///////////////////////////lift()////////////////////////////////




//    /**
//     * 防手抖 短时间重复 点击，打开两个相同界面
//     */
//    private void method4(){
//
//    }


}
