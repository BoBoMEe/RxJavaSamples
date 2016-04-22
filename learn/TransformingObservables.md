## 概述

所谓转换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。
当我们想要将Observable发射出来当对象进行转化的时候非常有用。
最常见的变换操作符如map、flatmap等

*   Observable和Subscriber可以做任何事情(Observable可以是一个网络请求，Subscriber用来显示请求结果)
      
*   其中Subscriber应该做的越少越好，大部分逻辑应该在Observable中处理
      
*   Observable和Subscriber是独立于中间的变换过程的

## map
    
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

## cast

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

通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。

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

## Buffer 

从字面意思来看就知道这是用于缓存的。Buffer操作符所要做的事情就是将数据安装规定的大小做一下缓存，然后将缓存的数据作为一个集合发射出去。


