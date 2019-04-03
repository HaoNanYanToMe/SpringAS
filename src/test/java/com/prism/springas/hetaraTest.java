package com.prism.springas;

import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.hetara.hetaraEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Hetara条件引擎测试用例
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class hetaraTest {

    @Autowired
    hetaraEngine hetaraEngine;

    @Test
    public void contextLoads() throws Exception{
        this.initHetaraByKey("test");
    }


    public void initHetaraByKey(String code) throws Exception{
        BasePage execute = new BasePage();
        for (int i = 0 ;  i < 100 ; i++){
            execute.put("classCode","PD1001"+(i%2==0?"":"M"));
            execute.put("className",i%2==0?"牛山点题":"幼儿教程");
            execute.put("classHour",i/(double)100);
            System.out.println(hetaraEngine.provingEngine(code,execute));
        }

    }
}
