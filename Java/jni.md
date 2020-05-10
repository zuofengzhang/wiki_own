---
title: JNI：Java native interface
date: 2015-11-15 00:00:00
category: Java
tags:
 - Java
 - JNI

share: true
comments: true
---

## JNI -- Java Native Interface
JNI 是 Java 与本地代码通信的桥梁，因此，也是 Java 调用操作系统 API 的接口。
因为 jvm 也是采用 C 实现的，通过JNI，可以达到调用 jvm 的目的。

## JNI调用过程
1. 当加载 Java 类时，运行静态代码段。 在静态代码段中，调用`System.loadLibrary("<jni_lib_name>")`方法，加载动态库。
1. Java 自动在库中查找`JNI_OnLoad()`函数。 `JNI_OnLoad()`函数主要有两个作用：
  * 判断 JNI 版本
  * 初始化，如注册函数
1. 当调用 native 方法时，Java 将去查找注册的函数。有两种注册函数的方法，静态注册和动态注册。
  * 静态注册是指在 C 里面以JNI默认的函数名定义与 java 中对对应的函数
  * 而动态注册是指在调用`JNI_OnLoad()`函数时，调用`(*env)->RegisterNatives()`函数，将方法注册。

  当Java去查找注册函数，首先判断是不是存在动态注册函数，如果不存在再查找静态注册函数。
  如果查找不到，将报出异常。
1. 执行C 函数，Java接收返回结果。

### 动态注册

```java
static {
  System.loadLibrary("native_native");
}
private native void callNativeMethod(int x,double y);
```

```cpp
static JNINativeMethod gMethods[] = {          
  {"callNativeMethod", "(ID)Ljava/lang/String;",  (void *)abc}
};
//真正的实现方法
JNIExport jstring abc(JNIEnv *env, jobject obj,jint x,jdouble y){
//TODO 实现
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
 JNIEnv* env = NULL;
 jint result = -1;
  //获取JNI环境对象
 //  if(vm->GetEnv((void**)&env,JNI_VERSION_1_4)!=JNI_OK){ //C++
 if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) {//C
     return -1;
 }
 //如果要注册, 只需要两步骤, 首先FindClass, 然后RegisterNatives就可以了
 //注册本地方法.Load 目标类
 char className[20] = {"club/guadazi/wiki/MyFirstActivity"};
 // jclass clazz = (env)->FindClass( (const char*)className);// C++
 jclass clazz = (*env)->FindClass(env,(const char *)className);//C
 //注册本地native方法
 if((*env)->RegisterNatives(env,clazz, gMethods, 1)< 0)
 {
     return -1;
 }
 //一定要返回版本号, 否则会出错.
 result = JNI_VERSION_1_4;
 return result;
}
```

### 静态注册

在生成的头文件中会含有静态注册函数
```
JNIExport jstring
 Java_club_guadazi_wiki_MyFirstActivity_callNativeMethod(
   JNIEnv *env, jobject obj,jint x,jdouble y);
```
写法是Java+Android工程的包名+Android工程的Activity名+方法名,点号用下划线表示，这个写法很严格。 包名：com_conowen_helloworld， Activity名：HelloWorldActivity， 方法名：helloWorldFromJNI

函数名称可以通过`javah`生成:

*  编译该java文件成class文件
*  利用命令生成h头文件

```shell
javah MyFirstActivity
```


## java 调用 c
上面的实现即为 java 调用 C



## c 调用 java

### 普通函数

```java
public class TestJNIInstanceVariable {
   static {
      System.loadLibrary("myjni");
      // myjni.dll (Windows) or libmyjni.so (Unixes)
   }
   // Instance variables
   private int number = 88;
   private String message = "Hello from Java";
   // Native method that modifies the instance variables
   private native void modifyInstanceVariable();
   public static void main(String args[]) {
      TestJNIInstanceVariable test = new TestJNIInstanceVariable();
      test.modifyInstanceVariable();
      System.out.println("In Java, int is " + test.number);
      System.out.println("In Java, String is " + test.message);
   }
}
```

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNIInstanceVariable.h"
// jni方法的静态实现
JNIEXPORT void JNICALL
 Java_TestJNIInstanceVariable_modifyInstanceVariable
          (JNIEnv *env, jobject thisObj) {
   // Get a reference to this object's class
   jclass thisClass = (*env)->GetObjectClass(env, thisObj);
   // int
   // Get the Field ID of the instance variables "number"
   jfieldID fidNumber = (*env)->GetFieldID(env, thisClass, "number", "I");
   if (NULL == fidNumber) return;
   // Get the int given the Field ID
   jint number = (*env)->GetIntField(env, thisObj, fidNumber);
   printf("In C, the int is %d\n", number);
   // Change the variable
   number = 99;
   (*env)->SetIntField(env, thisObj, fidNumber, number);
   // Get the Field ID of the instance variables "message"
   jfieldID fidMessage = (*env)->GetFieldID(env, thisClass, "message", "Ljava/lang/String;");
   if (NULL == fidMessage) return;
   // String
   // Get the object given the Field ID
   jstring message = (*env)->GetObjectField(env, thisObj, fidMessage);
   // Create a C-string with the JNI String
   const char *cStr = (*env)->GetStringUTFChars(env, message, NULL);
   if (NULL == cStr) return;
   printf("In C, the string is %s\n", cStr);
   (*env)->ReleaseStringUTFChars(env, message, cStr);
   // Create a new C-string and assign to the JNI string
   message = (*env)->NewStringUTF(env, "Hello from C");
   if (NULL == message) return;
   // modify the instance variables
   (*env)->SetObjectField(env, thisObj, fidMessage, message);
}
```
注意：
1. c 调用 java 的方法，类似于反射。 首先，需要获得类对象，然后再获取函数 id。
再调用get 和 set 函数，操作数据。
1. 如果建立了局部对象， 需要释放空间， 以防数据泄露。

### 访问静态变量和静态函数
```java
public class TestJNIStaticVariable {
   static {
      System.loadLibrary("myjni"); // nyjni.dll (Windows) or libmyjni.so (Unixes)
   }
   // Static variables
   private static double number = 55.66;
   // Native method that modifies the instance variables
   private native void modifyStaticVariable();
   public static void main(String args[]) {
      TestJNIStaticVariable test = new TestJNIStaticVariable();
      test.modifyStaticVariable();
      System.out.println("In Java, the double is " + number);
   }
}
```

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNIStaticVariable.h"
JNIEXPORT void JNICALL Java_TestJNIStaticVariable_modifyStaticVariable
          (JNIEnv *env, jobject thisObj) {
   // Get a reference to this object's class
   jclass cls = (*env)->GetObjectClass(env, thisObj);
   // Read the int static variable and modify its value
   jfieldID fidNumber = (*env)->GetStaticFieldID(env, cls, "number", "D");
   if (NULL == fidNumber) return;
   jdouble number = (*env)->GetStaticDoubleField(env, thisObj, fidNumber);
   printf("In C, the double is %f\n", number);
   number = 77.88;
   (*env)->SetStaticDoubleField(env, thisObj, fidNumber, number);
}
```

### 调用实例方法和静态方法
```java
public class TestJNICallBackMethod {
   static {
      System.loadLibrary("myjni");
      // myjni.dll (Windows) or libmyjni.so (Unixes)
   }
   // Native method that calls back the Java methods below
   private native void nativeMethod();
   // To be called back by the native code
   private void callback() {
      System.out.println("In Java");
   }
   private void callback(String message) {
      System.out.println("In Java with " + message);
   }
   private double callbackAverage(int n1, int n2) {
      return ((double)n1 + n2) / 2.0;
   }
   // Static method to be called back
   private static String callbackStatic() {
      return "From static Java method";
   }
   public static void main(String args[]) {
      new TestJNICallBackMethod().nativeMethod();
   }
}
```

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNICallBackMethod.h"
JNIEXPORT void JNICALL Java_TestJNICallBackMethod_nativeMethod
          (JNIEnv *env, jobject thisObj) {
   // Get a class reference for this object
   jclass thisClass = (*env)->GetObjectClass(env, thisObj);
   // Get the Method ID for method "callback", which takes no arg and return void
   jmethodID midCallBack = (*env)->GetMethodID(env, thisClass, "callback", "()V");
   if (NULL == midCallBack) return;
   printf("In C, call back Java's callback()\n");
   // Call back the method (which returns void), baed on the Method ID
   (*env)->CallVoidMethod(env, thisObj, midCallBack);
   jmethodID midCallBackStr = (*env)->GetMethodID(env, thisClass,
                               "callback", "(Ljava/lang/String;)V");
   if (NULL == midCallBackStr) return;
   printf("In C, call back Java's called(String)\n");
   jstring message = (*env)->NewStringUTF(env, "Hello from C");
   (*env)->CallVoidMethod(env, thisObj, midCallBackStr, message);
   jmethodID midCallBackAverage = (*env)->GetMethodID(env, thisClass,
                                  "callbackAverage", "(II)D");
   if (NULL == midCallBackAverage) return;
   jdouble average = (*env)->CallDoubleMethod(env, thisObj, midCallBackAverage, 2, 3);
   printf("In C, the average is %f\n", average);
   jmethodID midCallBackStatic = (*env)->GetStaticMethodID(env, thisClass,
                                 "callbackStatic", "()Ljava/lang/String;");
   if (NULL == midCallBackStatic) return;
   jstring resultJNIStr = (*env)->CallStaticObjectMethod(env, thisObj, midCallBackStatic);
   const char *resultCStr = (*env)->GetStringUTFChars(env, resultJNIStr, NULL);
   if (NULL == resultCStr) return;
   printf("In C, the returned string is %s\n", resultCStr);
   (*env)->ReleaseStringUTFChars(env, resultJNIStr, resultCStr);
}
```

### 回调覆盖的父类的实例方法
//TODO




## jni 数据类型
jni在本地系统中定义了与java总的基本数据类型相对应的数据类型。

1. 基本类型: `jint` `jbyte` `jshort` `jlong` `jdouble` `jchar` `jboolean`与java中的`int` `byte` `short` `long` `double` `char` `boolean`对应。

1. 引用类型: `jobject`与`java.lang.Object`也定义了如下的子类型:
 * `jclass`与`java.lang.Class`
 * `jstring`与`java.lang.String`
 * `jthrowable`与`java.lang.Throwable`
 * `jarray`对应java数组。 java数组是包括8种基本类型数组与Object数组的应用类型。因此， 存在8个基本类型的数组: `jintArray`, `jbyteArray`, `jshortArray`, `jlongArray`, `jfloatArray`, `jdoubleArray`, `jcharArray`和`jbooleanArray`,以及`jobjectArray`.

本地函数接收到上述的JNI类型的参数，返回JNI类型的数据。在本地函数处理自己的本地类型数据时，需要JNI类型与本地类型的转换。

## 参数传递与数据类型转换

### 基本数据类型
Java基本数据类型可以直接传递。在本地系统中定义的`jXXX`类型, 如`jint`,`jbyte`,`jshort`,`jlong`,`jfloat`,`jdouble`,`jchar`和`jboolean`与java中基本数据类型`int`,`byte`,`short`,`long`,`float`,`double`,`char`和`boolean`对应.

```cpp
// In "win\jni_mh.h" - machine header which is machine dependent
typedef long            jint;
typedef __int64         jlong;
typedef signed char     jbyte;
// In "jni.h"
typedef unsigned char   jboolean;
typedef unsigned short  jchar;
typedef short           jshort;
typedef float           jfloat;
typedef double          jdouble;
typedef jint            jsize;
```

### String

```java
public class TestJNIString {
   static {
      System.loadLibrary("myjni");
      // myjni.dll (Windows) or libmyjni.so (Unixes)
   }
   // Native method that receives a Java String and return a Java String
   private native String sayHello(String msg);
   public static void main(String args[]) {
      String result = new TestJNIString().sayHello("Hello from Java");
      System.out.println("In Java, the returned string is: " + result);
   }
}
```

```
JNIEXPORT jstring JNICALL Java_TestJNIString_sayHello(JNIEnv *, jobject, jstring);
```
Java的String是一个对象，而C里面的String是一个以 null 结束的字符数组(字符指针 char\*)。JNI环境提供了转换用的函数:
1. JNI String(jstring)转换为C String(char \*): `const char* GetStringUTFChars(JNIEnv*, jstring, jboolean*)`
1. C-String(char \*)转换为JNI String(jstring): `jstring NewStringUTF(JNIEnv*, char*)`

    注意: 当调用上面的函数产生了新的内存时，需要调用`(*env)->ReleaseStringUTFChars(env, <>, <>);`

#### JNI本地字符串函数

```cpp
// UTF-8 String (encoded to 1-3 byte, backward compatible with 7-bit ASCII)
// Can be mapped to null-terminated char-array C-string
const char * GetStringUTFChars(JNIEnv *env, jstring string, jboolean *isCopy);
// Returns a pointer to an array of bytes representing the string in modified UTF-8 encoding.
void ReleaseStringUTFChars(JNIEnv *env, jstring string, const char *utf);
// Informs the VM that the native code no longer needs access to utf.
jstring NewStringUTF(JNIEnv *env, const char *bytes);
// Constructs a new java.lang.String object from an array of characters in modified UTF-8 encoding.
jsize GetStringUTFLength(JNIEnv *env, jstring string);
// Returns the length in bytes of the modified UTF-8 representation of a string.
void GetStringUTFRegion(JNIEnv *env, jstring str, jsize start, jsize length, char *buf);
// Translates len number of Unicode characters beginning at offset start into modified UTF-8 encoding
// and place the result in the given buffer buf.
// Unicode Strings (16-bit character)
const jchar * GetStringChars(JNIEnv *env, jstring string, jboolean *isCopy);
// Returns a pointer to the array of Unicode characters
void ReleaseStringChars(JNIEnv *env, jstring string, const jchar *chars);
// Informs the VM that the native code no longer needs access to chars.
jstring NewString(JNIEnv *env, const jchar *unicodeChars, jsize length);
// Constructs a new java.lang.String object from an array of Unicode characters.
jsize GetStringLength(JNIEnv *env, jstring string);
// Returns the length (the count of Unicode characters) of a Java string.
void GetStringRegion(JNIEnv *env, jstring str, jsize start, jsize length, jchar *buf);
// Copies len number of Unicode characters beginning at offset start to the given buffer buf

```


### 数组

```java
private native double[] sumAndAverage(int[] numbers);
public static void main(String args[]) {
int[] numbers = {22, 33, 33};
double[] results = new TestJNIPrimitiveArray().sumAndAverage(numbers);
System.out.println("In Java, the sum is " + results[0]);
System.out.println("In Java, the average is " + results[1]);
}
```
```
#include <jni.h>
#include <stdio.h>
#include "TestJNIPrimitiveArray.h"
JNIEXPORT jdoubleArray JNICALL Java_TestJNIPrimitiveArray_sumAndAverage
          (JNIEnv *env, jobject thisObj, jintArray inJNIArray) {
   // Step 1: Convert the incoming JNI jintarray to C's jint[]
   jint *inCArray = (*env)->GetIntArrayElements(env, inJNIArray, NULL);
   if (NULL == inCArray) return NULL;
   jsize length = (*env)->GetArrayLength(env, inJNIArray);
   // Step 2: Perform its intended operations
   jint sum = 0;
   int i;
   for (i = 0; i < length; i++) {
      sum += inCArray[i];
   }
   jdouble average = (jdouble)sum / length;
   (*env)->ReleaseIntArrayElements(env, inJNIArray, inCArray, 0); // release resources
   jdouble outCArray[] = {sum, average};
   // Step 3: Convert the C's Native jdouble[] to JNI jdoublearray, and return
   jdoubleArray outJNIArray = (*env)->NewDoubleArray(env, 2);  // allocate
   if (NULL == outJNIArray) return NULL;
   (*env)->SetDoubleArrayRegion(env, outJNIArray, 0 , 2, outCArray);  // copy
   return outJNIArray;
}
```
JNI基本类型数组函数

```cpp
// ArrayType: jintArray, jbyteArray, jshortArray, jlongArray, jfloatArray, jdoubleArray, jcharArray, jbooleanArray
// PrimitiveType: int, byte, short, long, float, double, char, boolean
// NativeType: jint, jbyte, jshort, jlong, jfloat, jdouble, jchar, jboolean
NativeType * Get<PrimitiveType>ArrayElements(JNIEnv *env, ArrayType array, jboolean *isCopy);
void Release<PrimitiveType>ArrayElements(JNIEnv *env, ArrayType array, NativeType *elems, jint mode);
void Get<PrimitiveType>ArrayRegion(JNIEnv *env, ArrayType array, jsize start, jsize length, NativeType *buffer);
void Set<PrimitiveType>ArrayRegion(JNIEnv *env, ArrayType array, jsize start, jsize length, const NativeType *buffer);
ArrayType New<PrimitiveType>Array(JNIEnv *env, jsize length);
void * GetPrimitiveArrayCritical(JNIEnv *env, jarray array, jboolean *isCopy);
void ReleasePrimitiveArrayCritical(JNIEnv *env, jarray array, void *carray, jint mode);
```


## 函数签名



## 创建对象与对象数组

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNIConstructor.h"
JNIEXPORT jobject JNICALL Java_TestJNIConstructor_getIntegerObject
          (JNIEnv *env, jobject thisObj, jint number) {
   // Get a class reference for java.lang.Integer
   jclass cls = (*env)->FindClass(env, "java/lang/Integer");
   // Get the Method ID of the constructor which takes an int
   jmethodID midInit = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
   if (NULL == midInit) return NULL;
   // Call back constructor to allocate a new instance, with an int argument
   jobject newObj = (*env)->NewObject(env, cls, midInit, number);
   // Try runnning the toString() on this newly create object
   jmethodID midToString = (*env)->GetMethodID(env, cls, "toString", "()Ljava/lang/String;");
   if (NULL == midToString) return NULL;
   jstring resultStr = (*env)->CallObjectMethod(env, newObj, midToString);
   const char *resultCStr = (*env)->GetStringUTFChars(env, resultStr, NULL);
   printf("In C: the number is %s\n", resultCStr);
   return newObj;
}
```

对象函数

```cpp
jclass FindClass(JNIEnv *env, const char *name);
jobject NewObject(JNIEnv *env, jclass cls, jmethodID methodID, ...);
jobject NewObjectA(JNIEnv *env, jclass cls, jmethodID methodID, const jvalue *args);
jobject NewObjectV(JNIEnv *env, jclass cls, jmethodID methodID, va_list args);
//Constructs a new Java object.The method ID indicates which constructor method to invoke
jobject AllocObject(JNIEnv *env, jclass cls);
//Allocates a new Java object without invoking any of the constructors for the object.
```

对象数组

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNIObjectArray.h"
JNIEXPORT jobjectArray JNICALL Java_TestJNIObjectArray_sumAndAverage
          (JNIEnv *env, jobject thisObj, jobjectArray inJNIArray) {
   // Get a class reference for java.lang.Integer
   jclass classInteger = (*env)->FindClass(env, "java/lang/Integer");
   // Use Integer.intValue() to retrieve the int
   jmethodID midIntValue = (*env)->GetMethodID(env, classInteger, "intValue", "()I");
   if (NULL == midIntValue) return NULL;
   // Get the value of each Integer object in the array
   jsize length = (*env)->GetArrayLength(env, inJNIArray);
   jint sum = 0;
   int i;
   for (i = 0; i < length; i++) {
      jobject objInteger = (*env)->GetObjectArrayElement(env, inJNIArray, i);
      if (NULL == objInteger) return NULL;
      jint value = (*env)->CallIntMethod(env, objInteger, midIntValue);
      sum += value;
   }
   double average = (double)sum / length;
   printf("In C, the sum is %d\n", sum);
   printf("In C, the average is %f\n", average);
   // Get a class reference for java.lang.Double
   jclass classDouble = (*env)->FindClass(env, "java/lang/Double");
   // Allocate a jobjectArray of 2 java.lang.Double
   jobjectArray outJNIArray = (*env)->NewObjectArray(env, 2, classDouble, NULL);
   // Construct 2 Double objects by calling the constructor
   jmethodID midDoubleInit = (*env)->GetMethodID(env, classDouble, "<init>", "(D)V");
   if (NULL == midDoubleInit) return NULL;
   jobject objSum = (*env)->NewObject(env, classDouble, midDoubleInit, (double)sum);
   jobject objAve = (*env)->NewObject(env, classDouble, midDoubleInit, average);
   // Set to the jobjectArray
   (*env)->SetObjectArrayElement(env, outJNIArray, 0, objSum);
   (*env)->SetObjectArrayElement(env, outJNIArray, 1, objAve);
   return outJNIArray;
}
```

局部变量

```java
public class TestJNIReference {
   static {
      System.loadLibrary("myjni"); // myjni.dll (Windows) or libmyjni.so (Unixes)
   }
   // A native method that returns a java.lang.Integer with the given int.
   private native Integer getIntegerObject(int number);
   // Another native method that also returns a java.lang.Integer with the given int.
   private native Integer anotherGetIntegerObject(int number);
   public static void main(String args[]) {
      TestJNIReference test = new TestJNIReference();
      System.out.println(test.getIntegerObject(1));
      System.out.println(test.getIntegerObject(2));
      System.out.println(test.anotherGetIntegerObject(11));
      System.out.println(test.anotherGetIntegerObject(12));
      System.out.println(test.getIntegerObject(3));
      System.out.println(test.anotherGetIntegerObject(13));
   }
}
```

```cpp
#include <jni.h>
#include <stdio.h>
#include "TestJNIReference.h"
// Global Reference to the Java class "java.lang.Integer"
static jclass classInteger;
static jmethodID midIntegerInit;
jobject getInteger(JNIEnv *env, jobject thisObj, jint number) {
   // Get a class reference for java.lang.Integer if missing
   if (NULL == classInteger) {
      printf("Find java.lang.Integer\n");
      classInteger = (*env)->FindClass(env, "java/lang/Integer");
   }
   if (NULL == classInteger) return NULL;
   // Get the Method ID of the Integer's constructor if missing
   if (NULL == midIntegerInit) {
      printf("Get Method ID for java.lang.Integer's constructor\n");
      midIntegerInit = (*env)->GetMethodID(env, classInteger, "<init>", "(I)V");
   }
   if (NULL == midIntegerInit) return NULL;
   // Call back constructor to allocate a new instance, with an int argument
   jobject newObj = (*env)->NewObject(env, classInteger, midIntegerInit, number);
   printf("In C, constructed java.lang.Integer with number %d\n", number);
   return newObj;
}
JNIEXPORT jobject JNICALL Java_TestJNIReference_getIntegerObject
          (JNIEnv *env, jobject thisObj, jint number) {
   return getInteger(env, thisObj, number);
}
JNIEXPORT jobject JNICALL Java_TestJNIReference_anotherGetIntegerObject
          (JNIEnv *env, jobject thisObj, jint number) {
   return getInteger(env, thisObj, number);
}
```

```
   // Get a class reference for java.lang.Integer if missing
   if (NULL == classInteger) {
      printf("Find java.lang.Integer\n");
      // FindClass returns a local reference
      jclass classIntegerLocal = (*env)->FindClass(env, "java/lang/Integer");
      // Create a global reference from the local reference
      classInteger = (*env)->NewGlobalRef(env, classIntegerLocal);
      // No longer need the local reference, free it!
      (*env)->DeleteLocalRef(env, classIntegerLocal);
   }
```

# 相关问题
## 如何避免内存泄露
