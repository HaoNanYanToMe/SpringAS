package com.prism.springas.controller.config;

import com.prism.springas.schema.BaseSchema;
import com.prism.springas.utils.BaseCoreCfg;
import com.prism.springas.utils.cache.CacheClass;
import org.springframework.beans.factory.annotation.Autowired;

/*
    -shcema逻辑层反射配置类
 */
public class BaseBasicControllerConfigs extends CacheClass {

    @Autowired
    BaseCoreCfg baseCoreCfg;
    @Autowired
    BaseSchema baseSchema;

    /*
        -开启二级缓存
     */
    Object proxyUseSchema(int isBase,String tableName){
        String keys = tableName.toLowerCase()+"Schema"+(isBase==0?"Base":"Ex");
         Object obj = getCache(keys);
        if (obj == null) {
            if (isBase == 0) {
                obj = baseSchema;
            } else {
                obj = baseCoreCfg.parseRefObject(tableName,1);
            }
            obj = obj == null ? baseSchema : obj;
            setCache(keys,obj,-1);
        }
        return obj;
    }
}
