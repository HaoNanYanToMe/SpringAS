package com.prism.springas.utils.hetara.utils;

import com.prism.springas.api.BaseApi;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.cache.CacheClass;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 初始化Hetara规则引擎
 */
@Component
public class hetaraInitBase extends CacheClass{

    @Autowired
    BaseApi baseApi;

    public Object initRuleBase(String code) throws Exception{
        Map map = new ConcurrentHashMap();
        Object obj = getCache("RL-" + code);
        if (obj == null) {
            //1.获取指定的规则引擎
            List<BasePage> list = this.byRuleEngine(code);
            BasePage engine = list.size() > 0 ? list.get(0) : null;
            //2.根据引擎Key检索,最后获得的规则引擎不为空
            if (engine != null) {
                map.put("ENGINE", engine);
//                map.put("RULES", this.byRules(new ArrayList<>(), engine.getString("ID"), "0"));
                map.put("RULES",this.byRules(engine.getString("ID")));
                //载入cache缓存
                setCache("RL-" + code,map,-1);
                //赋值并返回
                obj = map;
            }
        }
        return obj;
    }

    private List<BasePage> byRuleEngine(String code) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("HETARAENGINE")
                 .getSelCols("","HETARAENGINE_ID#ID,HETARAENGINE_CODE#CODE,HETARAENGINE_NAME#NAME")
                 .getWhereAnd("HETARAENGINE_CODE","EQ",code);
        return baseApi.selectPage("HETARAENGINE", sqlEngine);
    }

    //直接获取当前引擎下所有规则数据
    private List<BasePage> byRules(String engId) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("HETARARULES")
                .getSelTable("HETARARULES")
                .getSort("HETARARULES_SORT","2")
                .getWhereAnd("HETARARULES_EID","EQ",engId);
        return this.baseApi.selectPage("HETARARULES",sqlEngine);
    }

    //使用递归获取规则网络
    private List<BasePage> byRules(List list,String engId,String pid) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("HETARARULES")
                 .getSelTable("HETARARULES")
                 .getSort("HETARARULES_SORT","2")
                 .getWhereAnd("HETARARULES_EID","EQ",engId)
                 .getWhereAnd("HETARARULES_PID","EQ",pid);
        List<BasePage> rules = this.baseApi.selectPage("HETARARULES",sqlEngine);
        for (BasePage rule : rules) {
            if (Integer.parseInt(rule.get("ISP")+"")==1){
                sqlEngine.getWhereAnd("PID","EQ",pid);
                rule.put("CHILDS",this.byRules(list,engId,rule.getString("ID")));
            }
            list.add(rule);
        }
        return rules;
    }
}
