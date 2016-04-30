## 概述

所谓转换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。
当我们想要将Observable发射出来当对象进行转化的时候非常有用。
最常见的变换操作符如map、flatmap等

*   Observable和Subscriber可以做任何事情(Observable可以是一个网络请求，Subscriber用来显示请求结果)
      
*   其中Subscriber应该做的越少越好，大部分逻辑应该在Observable中处理
      
*   Observable和Subscriber是独立于中间的变换过程的

## Map,Cast
    
```java
    //通过map操作符将String -> Integer
    private void map1() {
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
```

>这里需要注意：
      Func1 和 Action1的区别，Func1用于包装有返回值 的方法。
      FuncX类似于ActionX也有多个。

Cast将Observable发射的数据强制转化为另外一种类型，属于Map的一种具体的实现，主要是做类型转换的。
源Observable产生的结果不能转成指定的class，则会抛出ClassCastException运行时异常。

```java
private void cast() {
        Observable.just(1,2,3,4,5,6).cast(Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer value) {
               logger("next:"+value);
            }
        });
    }
```
打印结果：next:1 ，next:2 ，next:3 ，next:4 ，next:5 ，next:6

## FlatMap
 
与map不同的是，flatMap返回的是Observable对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber的回调方法中

原理：
1. 使用传入的事件对象创建一个 Observable 对象；
     2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
     3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable
     负责将这些事件统一交给 Subscriber 的回调方法。

通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。而这个『铺平』就是 flatMap() 所谓的 flat。

比如有如下需求：每个学生有多个课程，如需打印每个学生 所修课程
代码可以这样写：

```java
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
```

但是上面不是说过Subscriber应该做的越少越好，我们不想在Subscriber中做for循环。这就需要flatmap了
用flatmap写法：

```java
//Student -->Observable<Course> -->Course
private void flatMap() {

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
```

由于可以在嵌套的 Observable 中添加异步代码， flatMap() 也常用于嵌套的异步操作，例如嵌套的网络请求(不需要嵌套的callback)。

```java
//Retrofit + RxJava
networkClient.token() // 返回 Observable<String>，在订阅时请求 token，并在响应后发送 token
    .flatMap(new Func1<String, Observable<Messages>>() {
        @Override
        public Observable<Messages> call(String token) {
            // 返回 Observable<Messages>，在订阅时请求消息列表，并在响应后发送请求到的消息列表
            return networkClient.messages();
        }
    })
    .subscribe(new Action1<Messages>() {
        @Override
        public void call(Messages messages) {
            // 处理显示消息列表
            showMessages(messages);
        }
    });
```

注意：
>FlatMap对这些Observables发射的数据做的是`合并(merge)`的方式，因此它们可能是交错的，即转换后等顺序和原Observable是不一样的
>如果任何一个通过flatMap操作产生的单独的Observable调用onError异常终止了，原Observable自身也会立即调用onError并终止。

### flatMapIterable
和flatmap不同等是，其转化的多个Observable使用Iterable作为源数据.

```java
 public void flatMapIterable() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMapIterable(
                        integer -> {
                            ArrayList<Integer> s = new ArrayList<>();
                            for (int i = 0; i < integer; i++) {
                                s.add(integer);
                            }
                            return s;
                        }
                ).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger(integer);
            }
        });
    }
```
打印结果：
1，
2，2，
3，3，3，
4，4，4，4，
5，5，5，5，5 ...

### concatMap 

采用的是`连接(concat)`的方式，而不是`合并(merge)`的方式，不会让变换后的Observables发射的数据交错，按照严格的顺序发射这些数据

相关文章：[RxJava Observable tranformation: concatMap() vs flatMap()](http://fernandocejas.com/2015/01/11/rxjava-observable-tranformation-concatmap-vs-flatmap/)


### switchMap 
 和FlatMap类似，不同的是switchMap操作符会保存最新的Observable产生的结果而舍弃旧的结果。
 
 如下代码：
 ```java
 public void switchMap() {
         //switchMap操作符的运行结果
         Subscription subscribe = Observable.just(10, 20, 30).switchMap(new Func1<Integer, Observable<Integer>>() {
             @Override
             public Observable<Integer> call(Integer integer) {
                 //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
                 int delay = 200;
                 if (integer > 10)
                     delay = 180;
 
                 return Observable.from(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
             }
         }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
             @Override
             public void call(Integer integer) {
                 logger("switchMap Next:" + integer);
             }
         });
         addSubscription(subscribe);
     }
 ```
 
 打印结果：
 >switchMap Next:15
 >switchMap Next:30 
 
 switchMap使用场景：多用于频繁的网络请求，如EditText输入联想。
 由于输入的不断变化，返回的结果和输入框字符的不同步，且多线程同时运行，易发生错误，用switchMap取消上次请求。
 

## Buffer 

从字面意思来看就知道这是用于缓存的。
Buffer 操作符会定期收集Observable的数据放进一个数据包裹，然后发射这些数据包裹，而不是一次发射一个值

Window操作符与Buffer类似，但是它在发射之前把收集到的数据放进单独的Observable，而不是放进一个数据结构。

buffer(count):缓存count个
buffer(count，skip)：从原始Observable的第一项数据开始创建新的缓存，此后每当收到skip项数据，用count项数据填充缓存
如：buffer(2，3):每3个数据发射一个包含两个数据的集合,若count == skip则和 buffer(count)效果一致。

```java
 public void buffer() {
         Observable.just(1, 2, 3, 4, 5, 6)
                .buffer(2, 3)
                .subscribe(this::logger);
    }
```

打印结果：
`[1,2]`
`[4,5]`

而如果是下面的代码
```java
 public void buffer() {
         Observable.just(1, 2, 3, 4, 5, 6,7,8)
                .buffer(2, 4)
                .subscribe(this::logger);
    }
```
打印结果和上面相同。

注意：
> 一旦源Observable在产生结果的过程中出现异常，即使buffer已经存在收集到的结果，订阅者也会马上收到这个异常，并结束整个过程


## GroupBy 

GroupBy操作符将原始Observable发射的数据按照key来拆分成一些小的Observables集合，然后这些小的Observable分别发射其所包含的的数据，类似于sql里面的groupBy。

使用GroupBy将返回Observable的一个特殊子类GroupedObservable，GroupedObservable接口的对象有一个额外的方法getKey，这个Key用于将数据分组到指定的Observable

```java
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
```

打印结果将按照奇数偶数分组
> key0 contains:4 numbers
key1 contains:5 numbers

> 需要注意的是：groupBy将原始Observable分解为一个发射多个GroupedObservable的Observable，一旦有订阅，每个GroupedObservable就开始缓存数据。
如果你忽略这些GroupedObservable中的任何一个，GroupedObservable 中的任何一个，这个缓存可能形成一个潜在的内存泄露。

##Scan
  
连续地对数据序列的每一项应用一个函数，然后连续发射结果
并将这个函数的结果发射出去作为下个数据应用这个函数时候的第一个参数使用，有点类似于递归操作


如下将数组 Observable 对象，使用scan进行转化，转化的函数就是计算的结果加上下一个数
 
```java
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
```

输出：

```java
Next: 1
Next: 3
Next: 6
Next: 10
Next: 15
Sequence complete.
```

## Window

Window 和 buffer有点相似，只是Buffer是将数据放进 一个数据结构进行缓存，而window是放进单独的Observable
由Observable对象来发射内部包含的数据，与buffer相同的是都可以通过数目或时间来分组

// 使用数目3 进行分组，每次发射出一个包含3个数据的小Observable
```java
 public void windowCount(){
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).window(3)
        .subscribe(new Action1<Observable<Integer>>() {
            @Override
            public void call(Observable<Integer> integerObservable) {
                integerObservable.subscribe(integer -> {
                    logger(integer);
                });
            }
        });
    }
```

//每隔3秒钟发射出一个包含2~4个数据的Observable对象
```java
public void windowTime(){
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .window(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Observable<Long>>() {
            @Override
            public void call(Observable<Long> longObservable) {
                longObservable.subscribe(aLong -> {
                    logger(aLong);
                });
            }
        });
    }
```

参考：[ReactiveX中文翻译文档](https://mcxiaoke.gitbooks.io/rxdocs/content/Observables.html)
[Android RxJava使用介绍（三） RxJava的操作符](http://blog.csdn.net/job_hesc/article/details/46495281)