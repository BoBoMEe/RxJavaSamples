package com.bobomee.android.rxjavaexample.ui;

import com.bobomee.android.rxjavaexample.RecyclerActivity;
import com.bobomee.android.rxjavaexample.model.Course;
import com.bobomee.android.rxjavaexample.model.DataFactory;
import com.bobomee.android.rxjavaexample.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
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


    public void groupBy() {
         Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).groupBy(integer -> integer % 2)
                .subscribe(new Action1<GroupedObservable<Integer, Integer>>() {
                    @Override
                    public void call(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) {
                        integerIntegerGroupedObservable.count().
                                subscribe(integer -> logger("key" + integerIntegerGroupedObservable.getKey() + " contains:" + integer + " numbers"));
                    }
                });
    }

    public void scan(){
        Observable.just(1, 2, 3, 4, 5)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer item) {
                        return sum + item;
                    }
                }).subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Sequence complete.");
                    }
                });
    }


    public void windowCount(){
        Subscription subscribe = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).window(3)
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        integerObservable.subscribe(integer -> {
                            logger(integer);
                        });
                    }
                });
        addSubscription(subscribe);
    }


    public void windowTime(){
        Subscription subscribe = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .window(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> longObservable) {
                        longObservable.subscribe(aLong -> {
                            logger(aLong);
                        });
                    }
                });
        addSubscription(subscribe);
    }

    //lift
    public void lift() {

    }

}
