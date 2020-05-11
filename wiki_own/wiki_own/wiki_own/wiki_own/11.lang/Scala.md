[TOC]
# Scala

## implact


## List
```scala
def mkString(start: String, sep: String, end: String)
```


##  Maven

maven打包scala项目

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>8</java.version>
    <scala.version>2.11.8</scala.version>
    <scala.binary.version>2.11</scala.binary.version>
</properties>

<build>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <sourceDirectory>src/main/scala</sourceDirectory>
    <testOutputDirectory>src/test/scala</testOutputDirectory>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>

    <plugins>
        <plugin>
            <groupId>net.alchim31.maven</groupId>
            <artifactId>scala-maven-plugin</artifactId>
            <version>3.2.2</version>
            <executions>
                <execution>
                    <id>scala-compile-first</id>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>compile</goal>
                        <goal>add-source</goal>
                    </goals>
                </execution>
                <execution>
                    <id>scala-test-compile-first</id>
                    <phase>process-test-resources</phase>
                    <goals>
                        <goal>testCompile</goal>
                    </goals>
                </execution>
                <execution>
                    <id>attach-scaladocs</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>doc-jar</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <scalaVersion>${scala.version}</scalaVersion>
                <recompileMode>incremental</recompileMode>
                <useZincServer>true</useZincServer>
                <args>
                    <arg>-unchecked</arg>
                    <arg>-deprecation</arg>
                    <arg>-feature</arg>
                </args>
                <javacArgs>
                    <javacArg>-source</javacArg>
                    <javacArg>${java.version}</javacArg>
                    <javacArg>-target</javacArg>
                    <javacArg>${java.version}</javacArg>
                </javacArgs>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.4.3</version>
            <executions>
                <execution>
                    <id>uber-jar</id>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <finalName>${project.artifactId}-${project.version}-jar-with-dependencies</finalName>
                        <transformers>
                            <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ApacheLicenseResourceTransformer"/>
                            <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ApacheNoticeResourceTransformer"/>
                        </transformers>
                        <filters>
                            <filter>
                                <artifact>*:*</artifact>
                                <excludes>
                                    <exclude>LICENSE</exclude>
                                    <exclude>META-INF/*.SF</exclude>
                                    <exclude>META-INF/*.DSA</exclude>
                                    <exclude>META-INF/*.RSA</exclude>
                                </excludes>
                            </filter>
                        </filters>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <encoding>${project.build.sourceEncoding}</encoding>
                <maxmem>1024m</maxmem>
                <fork>true</fork>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
    </build>
```


## case class

序列化与反序列化

在Flink中必须支持标准的Java Bean


```scala
scala> case class E(a:Int){
     | val lb:ListBuffer[Int]=new ListBuffer[Int]
     | }
defined class E

scala> val e1=E(1)
e1: E = E(1)
scala> e1.lb.append(1)
res5: e1.lb.type = ListBuffer(1)

scala> e1
res6: E = E(1)

scala>

scala> e1.lb
res7: scala.collection.mutable.ListBuffer[Int] = ListBuffer(1)

scala> import java.io._
import java.io._

scala> val bos = new ByteArrayOutputStream
bos: java.io.ByteArrayOutputStream =

scala>  val oos = new ObjectOutputStream(bos)
oos: java.io.ObjectOutputStream = java.io.ObjectOutputStream@94b5fe3

scala> oos.writeObject(e1)

scala> val bts=bos.toByteArray
bts: Array[Byte] = Array(-84, -19, 0, 5, 115, 114, 0, 23, 36, 108, 105, 110, 101, 49, 50, 46, 36, 114, 101, 97, 100, 36, 36, 105, 119, 36, 36, 105, 119, 36, 69, 19, 100, 18, -68, -123, -33, 63, 90, 2, 0, 2, 73, 0, 1, 97, 76, 0, 2, 108, 98, 116, 0, 37, 76, 115, 99, 97, 108, 97, 47, 99, 111, 108, 108, 101, 99, 116, 105, 111, 110, 47, 109, 117, 116, 97, 98, 108, 101, 47, 76, 105, 115, 116, 66, 117, 102, 102, 101, 114, 59, 120, 112, 0, 0, 0, 1, 115, 114, 0, 50, 115, 99, 97, 108, 97, 46, 99, 111, 108, 108, 101, 99, 116, 105, 111, 110, 46, 103, 101, 110, 101, 114, 105, 99, 46, 68, 101, 102, 97, 117, 108, 116, 83, 101, 114, 105, 97, 108, 105, 122, 97, 116, 105, 111, 110, 80, 114, 111, 120, 121, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 1, 76, 0, 7, 102, 97, 99, 116, 111, 114, 121, 116, 0, 26, 76, 115, 99,...

scala> val bis=new ByteArrayInputStream(bts)
bis: java.io.ByteArrayInputStream = java.io.ByteArrayInputStream@63a833c5

scala> val e2=ois.readObject()
e2: Object = E(1)

scala> e2.lb
          ^
       error: value lb is not a member of Object

scala> val e3=e2.asInstanceOf[E]
e3: E = E(1)

scala> e3.lb
res12: scala.collection.mutable.ListBuffer[Int] = ListBuffer(1)
```

## match case


支持多个选项和默认值
```scala
val equs = amount / 10000 match {
    case 0 | 1 => List(10000)
    case 2 => List(1)
    case 3 => List(1, 2)
    case 4 => List(1, 2, 3)
    case 5 => List(1, 2, 3, 4)
    case 6 => List(1, 2, 3, 4, 5)
    case 7 | _ => List(1, 2, 3, 4, 5, 6)
  }
```