## RxJava学习

RxJava是一个基于观察者模式设计的异步库(响应式编程)

## 学习资料：

- [给 Android 开发者的 RxJava 详解 ](http://gank.io/post/560e15be2dca930e00da1083) 
——扔物线
文章配套Samples：[RxJavaSamples](https://github.com/rengwuxian/RxJavaSamples)

- 系列博客：
 [深入浅出RxJava（一：基础篇）](http://blog.csdn.net/lzyzsd/article/details/41833541)
 [深入浅出RxJava ( 二：操作符 )](http://blog.csdn.net/lzyzsd/article/details/44094895)
 [深入浅出RxJava ( 三--响应式的好处 )](http://blog.csdn.net/lzyzsd/article/details/44891933)
 [深入浅出RxJava ( 四-在Android中使用响应式编程 )](http://blog.csdn.net/lzyzsd/article/details/45033611)
——hi大头鬼hi

- [RxJava使用场景小结](http://blog.csdn.net/theone10211024/article/details/50435325)                                        
——THEONE10211024

- [RxJava使用场景小结 ](http://blog.csdn.net/lzyzsd/article/details/50120801)                                       
——hi大头鬼hi

- [RxJava-Android-Samples ](https://github.com/kaushikgopal/RxJava-Android-Samples)                             
——kaushikgopal

- 开发技术前线:
[NotRxJava懒人专用指南 ](http://www.devtf.cn/?p=323):从代码的角度还原RxJava库的实现原理
——作者：Yaroslav Heriatovych  译者：Rocko  

- [ReactiveX中文翻译文档](https://mcxiaoke.gitbooks.io/rxdocs/content/index.html)：操作符介绍

- [那些年我们错过的响应式编程](http://www.devtf.cn/?p=174)        
——很详细的介绍什么是响应式编程

- [使用RxJava.Observable取代AsyncTask和AsyncTaskLoader](http://www.devtf.cn/?p=114)    
——通过比较介绍RxJava在异步处理上的优势

- [RxJava部分操作符介绍 ](http://mushuichuan.com/tags/RxJava/)       
——水木川博客

- [Awesome-RxJava](https://github.com/lzyzsd/Awesome-RxJava) :RxJava资源的总结分享              
——hi大头鬼hi 
    
## 大纲之操作符分类

### 创建操作

Just,
From,
Repeat(repeatWhen),
Create,
Defer,
Range,
Interval,
Empty/Never/Throw,Timer

### 变换操作

Map,
FlatMap(concatMap,flatMapIterable),
SwitchMap,
Buffer,
GroupBy,
Scan,
Window,
Cast

### 过滤操作

Filter(ofType),
Take(TakeLast,takeLastBuffer),
Skip(SkipLast),
Last(lastOrDefault),
First(takeFirst,firstOrDefault),
ElementAt(elementAtOrDefault),
Sample(sample,throttleLast)
ThrottleFirst,
Distinct(distinctUntilChanged),
IgnoreElements

### 组合操作
  
StartWith,
Merge(mergeDelayError),
Zip(zip,zipwith),
And/Then/When,
CombineLatest,
Join(join/groupJoin),
Switch(switchOnNext)

### 错误处理

Catch,
Retry 

### 辅助操作

Materialize/Dematerialize,
Timestamp/TimeInterval,
ObserveOn/SubscribeOn/Subscribe,
Delay(delay,delaySubscription),
Using,
Single,
Timeout,
Do,
To,
Serialize,
cache(replay),

