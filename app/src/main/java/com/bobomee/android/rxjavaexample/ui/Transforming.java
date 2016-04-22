package com.bobomee.android.rxjavaexample.ui;

import com.bobomee.android.rxjavaexample.RecyclerActivity;
import com.bobomee.android.rxjavaexample.model.Course;
import com.bobomee.android.rxjavaexample.model.DataFactory;
import com.bobomee.android.rxjavaexample.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 2016/4/20.
 */
public class Transforming extends RecyclerActivity {

    //////////////////////////////变换map/////////////////////////////////

    public void mapString2hashCode() {
        Observable.just("Hello, world!").map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return s.hashCode();
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger(integer.toString());
            }
        });
    }

    public void cast() {
        Observable.just(1, 2, 3, 4, 5, 6).cast(Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer value) {
                logger("next:" + value);
            }
        });

    }


    ///////////////////////////flatMap////////////////////////

    public void studentNameMap() {

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
                        logger("学生的姓名：" + s);
                    }
                });
    }


    /**
     * 每个学生有多个课程，如需打印每个学生 所修课程，上面的代码则类似下面这样
     */

    public void courseMap() {

        ArrayList<Student> students = DataFactory.getData();

         Observable.from(students).subscribe(new Action1<Student>() {
            @Override
            public void call(Student student) {
                List<Course> courses = student.courses;

                for (Course course : courses) {
                    logger(course.toString());
                }
            }
        });

    }


    /**
     * <p>
     * Student -->Observable<Course> -->Course
     * <p>
     */

    public void flatMap() {

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
                        logger(course.name);
                    }
                });

    }

    ///////////////////////////buffer////////////////////////////////

    public void buffer() {
         Observable.just(1, 2, 3, 4, 5, 6)
                .buffer(2, 3)
                .subscribe(this::logger);
    }

    public void intervalBuffer() {
        Subscription subscribe = Observable.interval(1, TimeUnit.SECONDS)
                .buffer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::logger);
        addSubscription(subscribe);
    }


    //lift
    public void lift() {

    }

}
