## RxJava学习基础

RxJava是一个实现异步操作的库，采用链式掉用来实现响应式编程，使逻辑代码更加清晰。

RxJava类似观察者模式，Observables (被观察者)和 Observers(Subscribers) (观察者)通过 subscribe(订阅)方法实现订阅关系
Observables 在需要的时候发出事件来通知 Observers(Subscribers).

类似Android中Button的点击事件的监听：
Button -> 被观察者、OnClickListener -> 观察者、setOnClickListener() -> 订阅，onClick() -> 事件

和观察者模式不同的是 ：如果一个Observerble没有任何的的Subscriber，那么这个Observable就不会发出任何事件。

## RxJava回调方法

RxJava中定义了三种回调方法：
  onNext()：相当于 onClick() / onEvent()
  onCompleted(): 事件队列完结，时间队列中没有 新的 onNext() 发出时触发
  onError(): 事件队列异常，事件处理过程出异常时，onError() 会被触发，同时队列自动终止，不再有事件发出
  onCompleted() 和 onError() 二者互斥，在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只会触发一个
  
## 基本实现

创建一个RxJava掉用实现需要三个步骤
   1. 创建观察者 Observer或者Subscriber
   2. 创建被观察者 Observable
   3. 订阅subscribe
       
 HelloWorld:

```java
@NonNull
    private Observer<String> createObserver() {
        return new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Logger.d("Item: " + s);
            }

            @Override
            public void onCompleted() {
                Logger.d("Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("Error!");
            }
        };
    }
```

```java
     @NonNull
         private Observable<String> createObservable() {
             return Observable.create(new Observable.OnSubscribe<String>() {
                 @Override
                 public void call(Subscriber<? super String> subscriber) {
                     subscriber.onNext("Hello");
                     subscriber.onNext("Hi");
                     subscriber.onNext("Aloha");
                     subscriber.onCompleted();
                     subscriber.onError(new Throwable());
                 }
             });
         }
```

   
>这里传入了一个 OnSubscribe 对象作为参数.
OnSubscribe 会被存储在返回的 Observable 对象中，它的作用相当于一个计划表，
当 Observable 被订阅的时候，OnSubscribe 的 call() 方法会自动被调用，事件序列就会依照设定依次触发
         

>上面的定义就是：
观察者Subscriber 将会被调用三次 onNext() 和一次 onCompleted(),其中onError和onCompleted互斥。
被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
  
```java
    //Creating Observables
       private void create() {
           //1.观察者
           Observer<String> subscriber = createObserver();
           //2.被观察者
           Observable<String> observable = createObservable();
           //3.订阅
           observable.subscribe(subscriber);
       }
```

>流式 API 的设计 使得 这里看起来像是:被观察者 订阅了 观察者
 
       
> 其中Subscriber是Observer的抽象类，在使用过程中，Observer 也总是会先被转换成一个 Subscriber 再使用
 onStart()：Subscriber类中新增方法，在subscribe所在线程执行，用于一些准备工作，如果需要指定线程可以使用doOnSubscribe()方法
 unsubscribe()：Subscriber类中新增方法，是Subscription接口中的方法，Subscriber实现它，用于取消订阅，可以放置内存泄漏
 isUnsubscribed()：Subscription接口中的方法，用于判断订阅状态，一般在使用unsubscribe()时先判断一下

## Just、From

 just 和 from操作符用来快捷创建事件队列。
 其中just(T...):将传入的参数依次发送出来(一次来将整个的数组发射出去)。
 而from(T[])/from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来（发射T.lenght次）。
 
 //1.观察者
```java
 Observer<String> subscriber = createObserver();
```

//2. 被观察者
```java
//just
Observable observable = Observable.just("just", "test", "just");
```

>上面代码将会依次调用：
            onNext("Hello");
            onNext("Hi");
            onNext("Aloha");
            onCompleted();或者程序出现异常掉用onError

```java
//from
String[] words = {"from", "test", "from"};
Observable observable = Observable.from(words);
```

//3. 订阅
```java
observable.subscribe(subscriber);
```

## Range 
Range操作符根据出入的初始值n和数目m发射一系列大于等于n的m个值

```java
@NonNull
    private Observer<Integer> createIntegerObserver() {
        return new Subscriber<Integer>() {
            @Override
            public void onNext(Integer s) {
                Logger.d("Item: " + s);
            }

            @Override
            public void onCompleted() {
                Logger.d("Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("Error!");
            }
        };
    }
```

```java
private void range() {
        //1.观察者
        Observer<Integer> subscriber = createIntegerObserver();
        //2.被观察者
        Observable observable = Observable.range(10, 5);
        //3:订阅:
        observable.subscribe(subscriber);
    }
```
>上述代码将会打印10、11、12、13、14、Completed!

## Defer 
Defer操作符只有当有Subscriber来订阅的时候才会创建一个新的Observable对象,
每次订阅都会得到一个刚创建的最新的Observable对象，确保Observable对象里的数据是最新的.
如下代码就会打印当前实时时间

```java
 private void defer() {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(System.currentTimeMillis() + "");
            }
        })
                .subscribe(createStringObserver());
    }
```

> 和 just不同的是，just可以将数字、字符串、数组、Iterate对象转为Observable对象发射出去，但值是创建的时候就不变了的。

