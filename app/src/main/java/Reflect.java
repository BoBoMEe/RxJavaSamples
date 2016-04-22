import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by bobomee on 16/4/21.
 */
public class Reflect {

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

    public static class Person {
        String mName;

        public Person(String aName) {
            mName = aName;
        }

        protected void printName(String s) {
            System.out.println("My name is " + mName + s);
        }
    }
}
