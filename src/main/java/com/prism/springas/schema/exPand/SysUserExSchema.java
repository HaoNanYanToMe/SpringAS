package com.prism.springas.schema.exPand;

import com.alibaba.fastjson.JSON;
import com.prism.springas.schema.BaseSchema;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserExSchema extends BaseSchema {

    /**
     * 前台获取路由信息
     * @param sqlEngine
     *      uid : 当前登录用户系统ID
     * @return
     * @throws Exception
     */
    public String getRouter(String tableName,sqlEngine sqlEngine) throws Exception{
        return JSON.toJSONString(this.getRoleMenuRouter("-1",sqlEngine.getBaseParam("uid")+"",new ArrayList<>()));
    }

    private List<BasePage> getRoleMenuRouter(String pid, String uid,List<BasePage> rmList) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getParamEx("pid",pid);
        sqlEngine.getParamEx("uid",uid);
        List<BasePage> menu = (List<BasePage>)super.baseApi.selectExPage("sysUser",
                "getRouter",sqlEngine);

        List<BasePage> list = new ArrayList<>();
        for (BasePage m: menu) {
            BasePage tree = new BasePage();
            int isParent = Integer.parseInt(m.get("isParent")+"");
            //如果为是父节点
            tree.put("path",m.get("path"));
            tree.put("title",m.get("title"));
            tree.put("icon", m.get("icon"));
            tree.put("name",m.get("name"));
//            tree.put("component","import(\'@//views/" + m.get("component") + ".vue\')");
            if (isParent==0){
                tree.put("children",this.getRoleMenuRouter(m.get("id")+"",uid,rmList));
            }
            list.add(tree);
        }
        return list;
    }
}
