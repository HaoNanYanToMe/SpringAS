package com.prism.springas.api.tools;

import com.prism.springas.dao.BaseDAO;
import com.prism.springas.utils.BaseCoreCfg;
import com.prism.springas.utils.cache.CacheClass;
import org.springframework.beans.factory.annotation.Autowired;

/*
    BaseApi底层配置类
    -*在本层引入拓展类的DAO
        - @Autowired
          BaseDAO baseDAO;
    -*在proxyUseDAO方法内配置拓展DAO的反射对象
        - if(tableName.equals("USEREX")){ obj = userExDAO; }
    -#！proxyUseDAO新增时必须添加备注，声明用途及创建人/创建时间
 */
public class BaseApiBasicConfigs  extends CacheClass {

    @Autowired
    BaseCoreCfg baseCoreCfg;
    @Autowired
    BaseDAO baseDAO;

    /*
        -启用二级缓存
     */
     Object proxyUseDAO(int isBase,String tableName){
        String keys = tableName.toLowerCase()+"Dao"+(isBase==0?"Base":"Ex");
        Object obj = getCache(keys);
        if (obj == null) {
            if (isBase == 0) {
                obj = baseDAO;
            } else {
                obj = baseCoreCfg.parseRefObject(tableName,0);
            }
            setCache(keys,obj == null ? baseDAO : obj,-1);
        }
        return obj == null ? baseDAO : obj;
    }
}
