## 概述 

过滤操作符用于过滤和选择Observable发射的数据序列，让Observable只返回满足我们条件的数据。

## Debounce 

  Debounce会过滤掉发射速率过快的数据项，相当于限流，但是需要注意的是debounce过滤掉的数据会被丢弃掉。
  
  RxJava将这个操作符实现为throttleWithTimeout和debounce
  
### throttleWithTimeOut

通过时间来限流，源Observable每次发射出来一个数据后就会进行计时，
如果在设定好的时间结束前源Observable有新的数据发射出来，这个数据就会被丢弃，同时重新开始计时。
如果每次都是在计时结束前发射数据，那么这个限流就会走向极端：只会发射最后一个数据。


### deounce

 不仅可以使用时间来进行过滤，还可以根据一个函数来进行限流。
 这个函数的返回值是一个临时Observable，如果源Observable在发射一个新的数据的时候，上一个数据根据函数所生成的临时Observable还没有结束，那么上一个数据就会被过滤掉。
 
## Distinct 
