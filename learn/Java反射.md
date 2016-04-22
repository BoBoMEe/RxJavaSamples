## 概述

Java反射机制用于帮助我们在运行时获取类的各种成分，包括包括字段，方法等。
并且可以在运行时实例化对象，调用方法，设置字段值等。
所有等java项目在编译后都会以.class文件的形式存在，.class文件承载了这个类型的全部信息
进而被ClassLoader加载到虚拟机中。

## Class
```java
//知道到类名
Class<?> myObjectClass = className.class;
```

```java
//已经得到对象
Person p = new Person();
Class<?> clazz = p.getClass();
```

```java
//全类名
Class<?> clz = Class.forName("com.android.Person");
```

接口说明
```java
public static Class<?> forName (String className)

// 全路径名，是否初始化对象，指定加载 ClassLoader.
public static Class<?> forName (String className, boolean shouldInitialize, ClassLoader classLoader)
```

## Constructor

```java
private static void constructorReflect() {
        try {
            // 获取 Class 对象
            Class<?> clz = Class.forName("com.android.Person");
            // 获取带一个String参数的构造函数
            Constructor<?> constructor = clz.getConstructor(String.class);
            // 取消 Java 语言访问检查,一般用于反射私有成员
            constructor.setAccessible(true);
            // 创建对象
            Object newInstance = constructor.newInstance("constructor");
            System.out.println(" newInstance :  " + newInstance.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

接口说明：

```java
// 获取一个公有的构造函数，参数为可变参数，如果构造函数有参数，那么需要将参数的类型传递给 getConstructor 方法
public Constructor<T> getConstructor (Class...<?> parameterTypes)
// 获取目标类所有的公有构造函数
public Constructor[]<?> getConstructors ()
```

Person：
```java
public class Person {
    String mName;

    public Person(String aName) {
        mName = aName;
    }

    protected void printName(String s) {
        System.out.println("My name is " + mName+s);
    }
}
```

## Method

通过getDeclaredMethods 函数可以获取类中的所有方法，
getDeclaredMethod(String name, Class...<?> parameterTypes)可以获取某个指定的方法
Modifier.isPrivate(mehod.getModifiers()):判断是否是私有方法

```java
 private static void reflectMethods() {
         Person p = new Person("method");
         Method[] methods = p.getClass().getDeclaredMethods();
         for (Method method : methods) {
             System.out.println("declared method name : " + method.getName());
         }
 
         try {
             Method printName = p.getClass().getDeclaredMethod("printName", String.class);
             // 获取方法的参数类型列表
             Class<?>[] paramClasses = printName.getParameterTypes();
             for (Class<?> clz : paramClasses) {
                 System.out.println("printName 的参数类型 : " + clz.getName());
             }
             System.out.println(printName.getName() + " is private "
                     + Modifier.isPrivate(printName.getModifiers()));
             //调用方法，
             printName.invoke(p, "printName");
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
```

接口说明：
```java
// 函数名,参数类型列表 --> 获取指定函数名和参数的函数( 不包含从父类继承的函数 )
public Method getDeclaredMethod (String name, Class...<?> parameterTypes)

// 获取该 Class 对象中的所有函数( 不包含从父类继承的函数 )
public Method[] getDeclaredMethods ()

// 函数名，参数类型列表 --> 获取指定的 Class 对象中的 公有 函数( 包含从父类和接口类集成下来的函数 )
public Method getMethod (String name, Class...<?> parameterTypes)

// 获取该 Class 对象中的所有 公有 函数 ( 包含从父类和接口类集成下来的函数 )
public Method[] getMethods ()
```

## Field

和Method类似，把 getMethod 函数换成了 getField

接口说明：
```java
// 属性名 --> 获取 Class 对象中指定属性名的属性( 不包含从父类继承的属性 )
public Method getDeclaredField (String name)

// 获取该 Class 对象中的所有属性( 不包含从父类继承的属性 )
public Method[] getDeclaredFields ()

// 属性名 --> 获取指定的 Class 对象中的**公有**属性，( 包含从父类和接口类集成下来的公有属性 )
public Method getField (String name)

// 获取该 Class 对象中的所有**公有**属性 ( 包含从父类和接口类集成下来的公有属性 )
public Method[] getFields ()
```

## Other

Class<?> superClass = p.getClass().getSuperclass();//获取Class 对象的父类Class
Class<?>[] interfaceses = p.getClass().getInterfaces();//获取 Class 对象中实现的接口。
  
### 获取注解：

添加注解
```java
  @Test(tag = "Student class Test Annoatation")
  public class Student extends Person implements Examination {
      // 年级
      @Test(tag = "mGrade Test Annotation ")
      int mGrade;
  
      // ......
  }
```
 
//获取注解
```java
   private static void getAnnotationInfos() {
          Student student = new Student("mr.simple");
          Test classTest = student.getClass().getAnnotation(Test.class);
          System.out.println("class Annotatation tag = " + classTest.tag());
  
          Field field = null;
          try {
              field = student.getClass().getDeclaredField("mGrade");
              Test testAnnotation = field.getAnnotation(Test.class);
              System.out.println("属性的 Test 注解 tag : " + testAnnotation.tag());
          } catch (Exception e) {
              e.printStackTrace();
          }
      }

```

// 结果打印
```java
  class Annotatation tag = Student class Test Annoatation
  属性的 Test 注解 tag : mGrade Test Annotation
```

接口说明：

```java
// 获取指定类型的注解
public <A extends Annotation> A getAnnotation(Class<A> annotationClass) ;
// 获取 Class 对象中的所有注解
public Annotation[] getAnnotations() ;
```