### 批量处理
1. 说明
    ~~~
        This is a basic spring batch Demo: base on Spring Boot 2.0.1
    ~~~
  
2. 启动步骤 
    ~~~
    配置数据源:      application.yml 
    初始化脚本:     metadata/batch_innodb.sql
    初始化测试数据:    metadata/access.sql
   ~~~
3. 知识点
- spring batch 定时任务执行
   - SyncTask 测试每5分钟执行一次
- spring batch 线程池
- spring batch 执行原理
    ~~~
        spring batch批量处理，需要遵守固定的规范
        总体上：  创建一个Job 
                Job下配置Step
                每个 Step 都进行读Reader 处理process 写writer 三部
                所以，数据都需要先保存在数据库中再进行处理
    ~~~
- spring 配置项
    - batch.job.enabled = false 控制是否自动执行
- name.ealen 为一个简单的例子
    ~~~
    1.启动时自动执行一下
    ~~~
- name.yangsm 为一个实际使用的例子
    ~~~
    ~~~
    
### 系统表机构说明
**说明:共9张3张表是用来生成seq,任务Job辅助表3张，步骤Step复制表3张**
1. BATCH_JOB_INSTANCE   -   实例表
~~~
       -- JOB 名称,与spring配置一致
       -- JOB KEY 对job参数的MD5编码,正因为有这个字段的存在，同一个job如果第一次运行成功，第二次再运行会抛出JobInstanceAlreadyCompleteException异常。
       CREATE TABLE BATCH_JOB_INSTANCE  (
       	JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,      #由batch_job_seq分配
       	VERSION BIGINT ,
       	JOB_NAME VARCHAR(100) NOT NULL,
       	JOB_KEY VARCHAR(32) NOT NULL,
       	constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
       ) ENGINE=InnoDB;
~~~ 
2. BATCH_JOB_EXECUTION   -   任务的执行记录     
~~~
       CREATE TABLE BATCH_JOB_EXECUTION  (
       	JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
       	VERSION BIGINT  ,
       	JOB_INSTANCE_ID BIGINT NOT NULL,
       	CREATE_TIME DATETIME NOT NULL,
       	START_TIME DATETIME DEFAULT NULL ,
       	END_TIME DATETIME DEFAULT NULL ,
       	STATUS VARCHAR(10) ,
       	EXIT_CODE VARCHAR(2500) ,
       	EXIT_MESSAGE VARCHAR(2500) ,
       	LAST_UPDATED DATETIME,
       	JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
       	constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
       	references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
       ) ENGINE=InnoDB;
~~~     
3. BATCH_JOB_EXECUTION_PARAMS   -   任务参数揭露
~~~
       -- 该表包含与该JobParameters对象相关的所有信息
       CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
       	JOB_EXECUTION_ID BIGINT NOT NULL ,
       	TYPE_CD VARCHAR(6) NOT NULL ,
       	KEY_NAME VARCHAR(100) NOT NULL ,
       	STRING_VAL VARCHAR(250) ,
       	DATE_VAL DATETIME DEFAULT NULL ,
       	LONG_VAL BIGINT ,
       	DOUBLE_VAL DOUBLE PRECISION ,
       	IDENTIFYING CHAR(1) NOT NULL ,
       	constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
       	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
       ) ENGINE=InnoDB;
~~~ 
4. BATCH_STEP_EXECUTION   -   任务的执行步骤
~~~
       -- 该表包含与该StepExecution 对象相关的所有信息
       CREATE TABLE BATCH_STEP_EXECUTION  (
       	STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
       	VERSION BIGINT NOT NULL,
       	STEP_NAME VARCHAR(100) NOT NULL,
       	JOB_EXECUTION_ID BIGINT NOT NULL,
       	START_TIME DATETIME NOT NULL ,
       	END_TIME DATETIME DEFAULT NULL ,
       	STATUS VARCHAR(10) ,
       	COMMIT_COUNT BIGINT ,
       	READ_COUNT BIGINT ,
       	FILTER_COUNT BIGINT ,
       	WRITE_COUNT BIGINT ,
       	READ_SKIP_COUNT BIGINT ,
       	WRITE_SKIP_COUNT BIGINT ,
       	PROCESS_SKIP_COUNT BIGINT ,
       	ROLLBACK_COUNT BIGINT ,
       	EXIT_CODE VARCHAR(2500) ,
       	EXIT_MESSAGE VARCHAR(2500) ,
       	LAST_UPDATED DATETIME,
       	constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
       	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
       ) ENGINE=InnoDB;
~~~
5. BATCH_STEP_EXECUTION_CONTEXT   -   ExecutionContext与Step相关的所有信息，记录执行的类
~~~
       CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
       	STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
       	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
       	SERIALIZED_CONTEXT TEXT ,
       	constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
       	references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
       ) ENGINE=InnoDB;
~~~
 6. BATCH_JOB_EXECUTION_CONTEXT   -  同上 JOB的相关信息
 ~~~ 
       -- 该表包含ExecutionContext与Job相关的所有信息
       CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
       	JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
       	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
       	SERIALIZED_CONTEXT TEXT ,
       	constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
       	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
       ) ENGINE=InnoDB;
       
 ~~~
7. BATCH_JOB_SEQ   -   ID记录表，记录当前任务最大ID
~~~
       CREATE TABLE BATCH_JOB_SEQ (
       	ID BIGINT NOT NULL,
       	UNIQUE_KEY CHAR(1) NOT NULL,
       	constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
       ) ENGINE=InnoDB;
       
# 初始化一条数据
       INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_SEQ);  
~~~
8. BATCH_STEP_EXECUTION_SEQ   -  为Step执行提供SEQ
 ~~~      
       CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
       	ID BIGINT NOT NULL,
       	UNIQUE_KEY CHAR(1) NOT NULL,
       	constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
       ) ENGINE=InnoDB;
       
       INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_STEP_EXECUTION_SEQ);
~~~
9. BATCH_JOB_EXECUTION_SEQ   -    为Job执行提供SEQ
~~~       
       CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
       	ID BIGINT NOT NULL,
       	UNIQUE_KEY CHAR(1) NOT NULL,
       	constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
       ) ENGINE=InnoDB;
       
       INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_EXECUTION_SEQ);
       
