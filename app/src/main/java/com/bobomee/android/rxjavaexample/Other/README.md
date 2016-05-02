
## 概述 

RxJava中的条件和boolean操作，及算术和 聚合操作,连接操作。

## 条件操作符

- amb() — 给定多个Observable，只让第一个发射数据的Observable发射全部数据
- defaultIfEmpty(T t) — 发射来自原始Observable的数据，如果原始Observable没有发射数据，就发射一个默认数据T
- skipUntil() — 丢弃原始Observable发射的数据，直到第二个Observable发射了一个数据，然后发射原始Observable的剩余数据
- skipWhile() — 丢弃原始Observable发射的数据，直到一个特定的条件为假，然后发射原始Observable剩余的数据
- takeUntil() — 发射来自原始Observable的数据，直到第二个Observable发射了一个数据或一个通知
- takeWhile() and takeWhileWithIndex( ) — 发射原始Observable的数据，直到一个特定的条件为真，然后跳过剩余的数据

## 布尔操作符 

- all() — 判断是否所有的数据项都满足某个条件
- contains() — 判断Observable是否会发射一个指定的值
- exists() and isEmpty( ) — 判断Observable是否发射了一个值
- sequenceEqual() — 判断两个Observables发射的序列是否相等

## 算术操作符

- averageInteger( ) — 求序列平均数并发射
- averageLong( ) — 求序列平均数并发射
- averageFloat( ) — 求序列平均数并发射
- averageDouble( ) — 求序列平均数并发射
- max( ) — 求序列最大值并发射
- maxBy( ) — 求最大key对应的值并发射
- min( ) — 求最小值并发射
- minBy( ) — 求最小Key对应的值并发射
- sumInteger( ) — 求和并发射
- sumLong( ) — 求和并发射
- sumFloat( ) — 求和并发射
- sumDouble( ) — 求和并发射

## 聚合操作符 

- concat( ) — 顺序连接多个Observables,并且严格按照发射顺序，前一个没有发射完，是不能发射后面的。
- count( ) and countLong( ) — 计算数据项的个数并发射结果
- reduce( ) — Reduce操作符应用一个函数接收Observable发射的数据和函数的计算结果作为下次计算的参数，输出最后的结果。
跟scan操作符很类似，只是scan会输出每次计算的结果，而reduce只会输出最后的结果。
-  collect(Func0,Action2) — 将原始Observable发射的数据放到一个单一的可变的数据结构中，然后返回一个发射这个数据结构的Observable
下面几个to方法，也属于辅助操作符.
- toList( ) — 收集原始Observable发射的所有数据到一个列表，然后返回这个列表
- toSortedList( ) — 收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表
- toMap( ) — 将序列数据转换为一个Map，Map的key是根据一个函数计算的
- toMultiMap( ) — 将序列数据转换为一个列表，同时也是一个Map，Map的key是根据一个函数计算的

## 连接操作

连接的Observable在被订阅时并不开始发射数据，只有在它的connect()被调用时才开始。
用这种方法，你可以等所有的潜在订阅者都订阅了这个Observable之后才开始发射数据。

- ConnectableObservable.connect( ) — 指示一个可连接的Observable开始发射数据
- Observable.publish( ) — 将一个Observable转换为一个可连接的Observable
- Observable.replay( ) — 确保所有的订阅者看到相同的数据序列，即使它们在Observable开始发射数据之后才订阅,这里可以通过空间和时间上对其进行缓存
- ConnectableObservable.refCount( ) — 让一个可连接的Observable表现得像一个普通的Observable

示例：
[Other.java](https://github.com/BoBoMEe/RxJavaLearn/blob/master/app/src/main/java/com/bobomee/android/rxjavaexample/Other)

参考：
[ReactiveX文档中文翻译](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Filtering-Observables.html)
[RxJava操作符（九）Connectable Observable Operators](http://mushuichuan.com/2016/01/12/rxjava-operator-9/)