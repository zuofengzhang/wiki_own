---
title: "Oceanus: table meta API"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Image
 - Flink
 - Oceanus

share: true
comments: true
---

# Oceanus

## 拉取库表信息

```shell
curl -o tank_pos_meta.json 'http://<host>:<port>/ec/v1/listTable?pageNum=1&pageSize=999&type=hippo&dbName=bank-pos-info&name=pos_yyyymmdd'
```

```json
{
    "result_code": "0",
    "result_msg": "操作成功",
    "result_content": {
        "tables": [
            {
                "id": 2255,
                "dbName": "info",
                "name": "pos_yyyymmdd",
                "type": "hippo",
                "principals": "user_name",
                "fields": "db_name,String,db_name: tb_name,String,tb_name: op_name,String,op_name: exp_time_stample,String,exp_time_stample: exp_time_stample_order,String,exp_time_stample_order: Fbill_no,String,Fbill_no: Fbank_type,Long,Fbank_type: Fbiz_type,Long,Fbiz_type: Fpos_status,Long,Fpos_status: Freverse_status,Long,Freverse_status: Freverse_times,Long,Freverse_times: Ftransaction_id,String,Ftransaction_id: Freal_bill_no,String,Freal_bill_no: Ftrace_no,String,Ftrace_no: Ftx_date,String,Ftx_date: Famount,Long,Famount: Fcur_type,Long,Fcur_type: Fuin,String,Fuin: Fuid,Long,Fuid: Fuser_name,String,Fuser_name: Fuser_id_type,Long,Fuser_id_type: Fuser_id,String,Fuser_id: Fuser_phone,String,Fuser_phone: Fcard_no,String,",
                "content": "",
                "description": "银行pos流水",
                "attributes": {
                    "table.topic": "bank_pos",
                    "table.bid": "bank_pos",
                    "data.encode": "UTF8",
                    "table.hippo.addrlist": "<hippo_master>",
                    "table.package": "true",
                    "table.interfaceId": "t_bank_pos_yyyymmdd",
                    "source.data.type": "data.type.default",
                    "table.needwatermark": "false",
                    "table.kv": "false",
                    "table.field.splitter": "0x1",
                    "is.temporal.table": "false",
                    "table.field.splitter.other": "1",
                    "table.usage": "table",
                    "table.used": "false",
                    "table.running.used": "false"
                },
                "modifier": "<table_owner>",
                "modify_time": 1563420065831,
                "create_time": 1563420065831,
                "tablesLogs": null
            }
        ],
        "total": 1,
        "pageNum": 1,
        "pageSize": 999
    }
}
```
