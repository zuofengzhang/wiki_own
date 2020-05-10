---
title: "Flink:数据类型与序列化"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink

share: true
comments: true
---


# Flink Data Types & Serialization


## 使用case class的坑

```scala
case class Event(id: Int) {
  val lb = new ListBuffer[Int]
}
```

```scla
13:00:43,342 INFO  org.apache.flink.api.java.typeutils.TypeExtractor             - class org.myorg.quickstart.Event does not contain a setter for field id
13:00:43,343 INFO  org.apache.flink.api.java.typeutils.TypeExtractor             - Class class org.myorg.quickstart.Event cannot be used as a POJO type because not all fields are valid POJO fields, and must be processed as GenericType. Please read the Flink documentation on "Data Types & Serialization" for details of the effect on performance.
```

**提示信息：找不到setter，对于POJO类型必须所有的字段必须要有setter和getter**
命名是case class啊

再看生产环境的例子： 折腾了一下午

```scala
case class Event(uin: String,
                 sPid: String,
                 applyId: String,
                 bankType: Long,
                 transactionId: String,
                 amount: Long,
                 createTime: Long,
                 bizType: Long,
                 modifyTime: String,
                 equ: ListBuffer[Int]
                ) {

   def this(uin: String,
           sPid: String,
           applyId: String,
           bankType: Long,
           transactionId: String,
           amount: Long,
           createTime: Long,
           bizType: Long,
           modifyTime: String,
           equ: List[Int]
          ) = {
    this(uin, sPid, applyId, bankType, transactionId, amount, createTime, bizType, modifyTime)
    if (equ != null) {
      equities.appendAll(equ)
    }
  }


  private var _active = false
  val equities: ListBuffer[Int] = new ListBuffer[Int]

  def addEquity(id: Int): Event = {
    equities.append(id)
    this
  }

  def equityString(separator: String): String = {
    equities.mkString(separator)
  }

  def setActive(): Event = {
    _active = true
    this
  }

  def setActive(active: String): Event = {
    _active = "1".equals(active)
    this
  }

  def isActive() = {
    _active
  }
}
```

使用的是flink 1.6版本的，case class识别出来了，但是equities没有传递到下一个算子中，始终没有值

老老实实的修改成普通类

```scala
class Event(_uin: String,
            _sPid: String,
            _applyId: String,
            _bankType: Long,
            _transactionId: String,
            _amount: Long,
            _createTime: Long,
            _bizType: Long,
            _modifyTime: String
           ) extends Serializable {


  private var equities: ListBuffer[Int] = new ListBuffer[Int]
  private var uin: String = _uin
  private var sPid: String = _sPid
  private var applyId: String = _applyId
  private var bankType: Long = _bankType
  private var transactionId: String = _transactionId
  private var amount: Long = _amount
  private var createTime: Long = _createTime
  private var bizType: Long = _bizType
  private var modifyTime: String = _modifyTime
  private var _active = false

  def this(uin: String,
           sPid: String,
           applyId: String,
           bankType: Long,
           transactionId: String,
           amount: Long,
           createTime: Long,
           bizType: Long,
           modifyTime: String,
           equ: List[Int]
          ) = {
    this(uin, sPid, applyId, bankType, transactionId, amount, createTime, bizType, modifyTime)
    if (equ != null) {
      equities.appendAll(equ)
    }
  }

  def getUin: String = uin

  def setUin(Uin: String): Unit = {
    this.uin = Uin
  }

  def getSPid: String = sPid

  def setSPid(SPid: String): Unit = {
    this.sPid = SPid
  }

  def getApplyId: String = applyId

  def setApplyId(ApplyId: String): Unit = {
    this.applyId = ApplyId
  }

  def getBankType: Long = bankType

  def setBankType(BankType: Long): Unit = {
    this.bankType = BankType
  }

  def getTransactionId: String = transactionId

  def setTransactionId(TransactionId: String): Unit = {
    this.transactionId = TransactionId
  }

  def getAmount: Long = amount

  def setAmount(Amount: Long): Unit = {
    this.amount = Amount
  }

  def getCreateTime: Long = createTime

  def setCreateTime(CreateTime: Long): Unit = {
    this.createTime = CreateTime
  }

  def getBizType: Long = bizType

  def setBizType(BizType: Long): Unit = {
    this.bizType = BizType
  }

  def getModifyTime: String = modifyTime

  def setModifyTime(ModifyTime: String): Unit = {
    this.modifyTime = ModifyTime
  }

  def addEquity(id: Int): Event = {
    equities.append(id)
    this
  }

  def equityString(separator: String): String = {
    equities.mkString(separator)
  }

  def setActive(): Event = {
    _active = true
    this
  }

  def setActive(active: String): Event = {
    _active = "1".equals(active)
    this
  }

  def isActive() = {
    _active
  }

  def rights(split: String) = {
    RightEvent(this, equities.mkString(split))
  }

  def getEquities = equities

  def setEquities(equities: ListBuffer[Int]) = {
    this.equities = equities
  }
}
```

可以了

初步估计，序列化除了问题