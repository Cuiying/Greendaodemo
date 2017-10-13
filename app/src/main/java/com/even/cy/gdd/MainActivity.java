package com.even.cy.gdd;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import gdd.cy.even.com.DaoMaster;
import gdd.cy.even.com.DaoSession;
import gdd.cy.even.com.Father;
import gdd.cy.even.com.FatherDao;
import gdd.cy.even.com.Son;
import gdd.cy.even.com.SonDao;

public class MainActivity extends AppCompatActivity {
    private DaoMaster master;
    private DaoSession session;
    private SQLiteDatabase db;

    private SonDao sonDao;
    private FatherDao fatherDao;

    // 打开一个数据库
    private void openDb(){
        // 获得一个可读的数据库
        db = new DaoMaster.DevOpenHelper(MainActivity.this,"person.db",null).getWritableDatabase();
        master = new DaoMaster(db);
        session = master.newSession();
        sonDao = session.getSonDao();
        fatherDao = session.getFatherDao();
    }

    public void queryOneToMany(){
        List<Son> sons = sonDao.queryBuilder().list();
        for(Son son : sons){
            List<Father> fathers = son.getFathers();
            for(Father father : fathers){
                Log.d("nate","queryOneToMany() called with: " + "" + son.getName() + " father:" + father.getName());
            }
        }
    }

    public void queryOneToOne(){
        List<Son> sons = sonDao.queryBuilder().list();
        for(Son son : sons){
            // 打印father的信息
//            Log.d("nate","queryOneToOne() called with: " + "" + son.getFather().getName());
        }
    }

    // 查询
    public void queryAll(){
        // list()：访问数据库SON表中的全部数据，直接加载到内存中
//        List<Son> list = sonDao.queryBuilder().list();
        // listLazy()：懒加载
//        LazyList<Son> list = sonDao.queryBuilder().listLazy();
        sonDao.queryBuilder().listLazy();
//        for(Son son : list){
//            Log.d("nate","queryAll() called with: " + "" + son);
//        }
//        list.close();

//        Iterator list = sonDao.queryBuilder().listIterator();
//        while(list.hasNext()){
//            Son son = (Son) list.next();
//            Log.d("nate","queryAll() called with: " + "" + son);
//        }
    }

    // 条件查询
    public void queryEq(){
        Son son = sonDao.queryBuilder().where(SonDao.Properties.Name.eq("nate")).unique();
        Log.d("nate","queryEq() called with: " + "" + son);
    }

    // 模糊查询
    public void queryLike(){
        // %：通配符
        List<Son> data = sonDao.queryBuilder().where(SonDao.Properties.Name.like("nate%")).list();
        Log.d("nate","queryLike() called with: " + "" + data);
    }

    // 范围查询
    public void queryBetween(){
        /**
         * gt(18)：greater than，大于18
         * lt(18)：less than，小于18
         *  notEq：不等于
         *  isNull：空
         *  isNotNull：非空
         *  排序：
         *  ge：大于等于
         *  le：小于等于
         */
        List<Son> data = sonDao.queryBuilder().where(SonDao.Properties.Age.between(20,30)).list();
        Log.d("nate","queryBetween() called with: " + "" + data);
    }

    /**
     * 排序查询
     * asc：升序
     * desc：降序
     */
    public void queryLikeAsc(){
        // %：通配符
//        List<Son> data = sonDao.queryBuilder().where(SonDao.Properties.Name.like("nate%")).orderAsc(SonDao.Properties.Age).list();
        List<Son> data = sonDao.queryBuilder().where(SonDao.Properties.Name.like("nate%")).orderDesc(SonDao.Properties.Age).list();
        Log.d("nate","queryLikeAsc() called with: " + "" + data);
    }

    // 自定义查询
    public void querySql(){
        List data = sonDao.queryBuilder().where(
                new WhereCondition.StringCondition("FATHER_ID IN " +
                "(SELECT _ID FROM FATHER WHERE AGE > 45)")).list();
        Log.d("nate","querySql() called with:" + "" + data);
    }

    // 多线程查询
    public void queryThread(){
        final Query query = sonDao.queryBuilder().build();

        new Thread(){
            @Override
            public void run() {
                // 比加锁性能好，减少了加锁的开销
                List data = query.forCurrentThread().list();
                Log.d("nate","queryThread() called with:" + "" + data);
            }
        }.start();
    }

    // 对数据库增删改查
    private void addPerson(){

        // 先有son，再关联
        Son son = new Son();
        son.setName("nate");
        son.setAge(20);
        sonDao.insert(son);

        // 每一个father和son建立关联
        Father tom = new Father();
        tom.setName("tom");
        tom.setSon(son);
        fatherDao.insert(tom);
        Father jake = new Father();
        jake.setName("jake");
        jake.setSon(son);
        fatherDao.insert(jake);

//        Son son = new Son();
//        son.setName("nate_");
//        son.setAge(20);

//        Father father = new Father();
//        father.setAge(45);
//        father.setName("Kevin");
//
//        long fatherId = fatherDao.insert(father);
//        son.setFatherId(fatherId);
//        sonDao.insert(son);

//        Son tom = new Son();
//        tom.setName("tom");
//        tom.setAge(28);
//
//        Father kobe = new Father();
//        kobe.setAge(55);
//        kobe.setName("Kobe");
//
//        fatherId = fatherDao.insert(kobe);
//        tom.setFatherId(fatherId);
//        sonDao.insert(tom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDb();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
//        queryAll();
//        addPerson();
//        queryEq();
//        queryLike();
//        queryBetween();
//        querySql();
//        queryOneToOne();
        queryOneToMany();
    }
}
