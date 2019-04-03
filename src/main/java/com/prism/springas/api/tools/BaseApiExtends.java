package com.prism.springas.api.tools;

import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.BaseTableCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/*
    BaseApi公用提取方法
    -*invokeMethod  底层反射
 */
public class BaseApiExtends  extends BaseApiBasicConfigs{
    private final Logger logger = LoggerFactory.getLogger(BaseApiExtends.class);
    @Autowired
    BaseTableCfg baseTableCfg;

    /**
     * Api层公用方法 -> 通过反射获取底层数据
     * @param tableName         指定主DAO类名
     *                              -isBase=1 -->BaseDao反射：需要传入作为主表的Name
     *                              -isBase=1 -->ExDao反射：需要传入BaseCoreCfg中设定的拓展类名称
     * @param functionName      自定义SQL方法(ExDAO)
     * @param bp                SqlEngine转化生成的BasePage对象
     *                              -BasePage:DAO的入参对象
     * @return
     * @throws Exception
     */
    public Object invokeMethod(int isBase,String tableName,String functionName,Object bp) {
        /*
            -声明需要反射获得的Class
            -声明返回对象
            -声明需要调用的方法
         */
        Class clazz;
        Object reObject = null;
        Method m1;
        Object obj = proxyUseDAO(isBase,tableName);
        clazz = obj.getClass();
        try {
            String methodName = isBase == 1 ? functionName : tableName+"_"+functionName;
            m1 = clazz.getDeclaredMethod(methodName,BasePage.class);
            reObject = m1.invoke(obj,bp);
        }catch (Exception e){
            logger.error("---BaseApiExtends ERROR:(SQL及DAO数据层错误定位信息)-->"+e.getMessage(),e);
        }
        return reObject;
    }
}
