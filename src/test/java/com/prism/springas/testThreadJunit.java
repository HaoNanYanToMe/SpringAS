package com.prism.springas;

import com.prism.springas.schema.BaseSchema;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * schema多线程环境测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class testThreadJunit  {

    @Autowired
    BaseSchema baseSchema;

    @Test
    public void testThreadJunit() throws Throwable {
        //并发数量
        int runnerSize = 10;

        //Runner数组，相当于并发多少个。
        TestRunnable[] trs = new TestRunnable [runnerSize];

        for(int i=0;i<runnerSize;i++){
            trs[i]=new ThreadA();
        }

        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

        // 开发并发执行数组里定义的内容
        mttr.runTestRunnables();
    }

    private class ThreadA extends TestRunnable {
        @Override
        public void runTest() throws Throwable {
            //数据更新多线程测试
//            updateThreadTest();
            //数据查询多线程测试
            selectThreadTest();
        }
    }

    //Test:数据更新多线程测试
    //---->测试目标:无数据抢占情况出现
    public void updateThreadTest() throws Exception {
        sqlEngine sqlEngine = new sqlEngine();
        int rand = (int)(Math.random()*10+1);
        sqlEngine.getUpdateTables("SPORTSINFO")
                .getSelCols("","SPORTSINFO_ID,SPORTSINFO_VERSION")
                .getUpdateWhereAnd("SPORTSINFO_ID","EQ","50e8d893bfc64ff4b85c5a4aedbb68d9")
                .getUpdateCols("SPORTSINFO_SNAME","更新测试Thread"+Thread.currentThread().getId()+"--Data"+rand)
                .getUpdCurrentVersion("SPORTSINFO",rand);
            System.out.println("===" + Thread.currentThread().getId() +"==Data :" + rand + "~~:"+ this.baseSchema.updateData("SPORTSINFO",sqlEngine));
    }

    //Test:数据查询多线程测试
    //---->测试目标:可以准确返回指定查询数据
    public void selectThreadTest() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        int rand = (int)(Math.random()*10+1);
        sqlEngine.getSelTable("SPORTSINFO")
                .getSelCols("","SPORTSINFO_SNAME#NAME")
                .getWhereAnd("SPORTSINFO_VERSION","EQ",null)
                .getSort("SPORTSINFO_CREATETIME","1")
                .getPage(rand,1);
        System.out.println("===" + Thread.currentThread().getId() + " ->pageNo"+rand+"->data:"+
            this.baseSchema.selectPage("SPORTSINFO",sqlEngine));
    }
}
