package name.yangsm.service;


import name.yangsm.util.SpringBeanUtil;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @description:  简单的新增  删除
 * @author: yangsm
 * @create: 2019-09-17 15:07
 **/
public class MyService<T> {
    public void doAdd(T t){
        EntityManagerFactory entityManagerFactory = SpringBeanUtil.getBean(EntityManagerFactory.class);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        //开启事务
        entityManager.getTransaction().begin();
        //保存数据
        entityManager.persist(t);
        //提交事务
        entityManager.getTransaction().commit();
        //关闭资源
        entityManager.close();
    }

//    public void doUpdate(T t){
//        //Hibernate配置
//        Configuration cfg=new Configuration();
//        cfg.configure();
//        //产生SessionFactory
//        SessionFactory sessionFactory=cfg.buildSessionFactory();
//        //开启Session
//        Session session=sessionFactory.openSession();
//        //开启事物
//        Transaction transaction = session.beginTransaction();
//        //保存数据
//        session.update(t);
//        //提交事物
//        transaction.commit();
//        //关闭事物
//        session.close();
//    }

//    /**
//     *@descption  Class<T>的形式获取泛型
//     *@author yangsm
//     *@date  2019/9/17
//     */
//    public T findOne(Class<T> clazz,String id){
//        T t;
//        //配置Hebernate
//        Configuration cfg=new Configuration();
//        cfg.configure();
//        //产生SessionFactory
//        SessionFactory sessionFactory=cfg.buildSessionFactory();
//        //打开session
//        Session session=sessionFactory.openSession();
//        //获取对象
//        t = session.get(clazz,id);
//        //关闭session
//        session.close();
//
//        return t;
//    }
//    public void doDelete(T t){
//        //Hibernate配置
//        Configuration cfg=new Configuration();
//        cfg.configure();
//        //产生SessionFactory
//        SessionFactory sessionFactory=cfg.buildSessionFactory();
//        //开启session
//        Session session=sessionFactory.openSession();
//        //开启事物
//        Transaction transaction =session.beginTransaction();
//        //删除
//        session.delete(t);
//        //事物提交
//        transaction.commit();
//        //关闭session
//        session.close();
//    }

}
