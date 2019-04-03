package com.prism.springas.controller.config;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseBasicExtends extends BaseBasicControllerConfigs implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(BaseBasicExtends.class);

    /**
     * Controller层公用方法 -> 通过反射获取底层数据
     * @param  isBase           是否为base基本接口（0-是  1-不是）
     * @param tableName         所使用主表
     * @param functionName      方法名称
     * @param bp                附加信息集合
     * @return
     * @throws Exception
     */
    public Object invokeSchemaMethod(int isBase,String tableName,String functionName,Object bp) throws Exception {
        /*
            -声明需要反射获得的Class
            -声明返回对象
            -声明需要调用的方法
         */
        if (bp != null) {
            Class clazz;
            Object reObject = null;
            Method m1;
            Object obj = proxyUseSchema(isBase, tableName);
            clazz = obj.getClass();
            if (!this.checkFunctionName(functionName) && !this.checkFunctionName(tableName)) {
                try {
                    m1 = clazz.getDeclaredMethod(functionName, String.class, sqlEngine.class);
                    reObject = m1.invoke(obj, tableName, bp);
                } catch (Exception e) {
                    logger.error("---BaseSchemaExtends ERROR:(Schema业务逻辑层错误定位信息)-->:" + functionName + "--" + e.getMessage());
                }
                return reObject;
            } else {
                return "#JSON入参数据非法,请检查格式无误后重试!CODE2";
            }
        }else{
            return "#JSON入参数据非法,请检查格式无误后重试!";
        }
    }

    //优化指定配参字段
    public boolean checkFunctionName(String functionName) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(functionName);
        return m.find();
    }

    /**
     * 将前端封装好的Param查询对象转换为SqlEngine对象
     * @param isBase        --判定是否是拓展Schema[0-基本,1-拓展]
     * @param jsonData      --Json格式的Param查询字符串
     * @return
     */
    public sqlEngine jsonToSqlEngine(int isBase,String jsonData){
        try {
            sqlEngine sqlEngine = new sqlEngine();
            if (jsonData == null || jsonData.equals("null") || jsonData.equals("")) {
                return sqlEngine;
            } else {
                //为保证数据入参顺序的一致性故启用了LinkedHashMap
                LinkedHashMap<String, Object> pJson = JSONObject.parseObject(jsonData, new TypeReference<LinkedHashMap<String,Object>>(){});
                for (String key : pJson.keySet()) {
                    if (isBase == 1) {
                        //1.是拓展Select语句
                        sqlEngine.getParamEx(key, pJson.get(key));
                    } else if (isBase == 2) {
                        //2.新增
                        sqlEngine.getAddData(key, pJson.get(key));
                    } else if (isBase == 3) {
                        //2.更新
                        sqlEngine.getUpdateCols(key, pJson.get(key));
                    } else {
                        //判断是where and还是where or(W:O:N[效果等同于W])
                        //W : AND
                        //O : OR
                        //WS: AND (
                        //WE: AND )
                        //OS: OR(
                        //OE: OR  )
                        //N:  AND
                        String whereCondition = key.contains("!") ? key.split("\\!")[0] : "N";
                        //需要检索的字段
                        String tureKey = whereCondition.equals("N") ? key.split("\\#")[0] :
                                key.split("\\!")[1].split("\\#")[0];
                        //检索字段匹配条件(EQ,LIKE,LT,GT等等)
                        String keyCondition = whereCondition.equals("N") ? key.split("\\#")[1] :
                                key.split("\\!")[1].split("\\#")[1];
                        //2.是基本Select语句
                        //根据whereCondition标识使用对应SQL引擎
                        if (whereCondition.equals("O")) {
                            sqlEngine.getWhereOr(tureKey, keyCondition, pJson.get(key));
                        } else if (whereCondition.equals("W") || whereCondition.equals("N")) {
                            sqlEngine.getWhereAnd(tureKey, keyCondition, pJson.get(key));
                        } else if (whereCondition.equals("WS")) {
                            sqlEngine.getWhereAndMore(tureKey, keyCondition, pJson.get(key));
                        } else if (whereCondition.equals("WE")) {
                            sqlEngine.getWhereAndMoreEnd(tureKey, keyCondition, pJson.get(key));
                        } else if (whereCondition.equals("OS")) {
                            sqlEngine.getWhereOrMore(tureKey, keyCondition, pJson.get(key));
                        } else if (whereCondition.equals("OE")) {
                            sqlEngine.getWhereOrMoreEnd(tureKey, keyCondition, pJson.get(key));
                        }
                    }
                }
            }
            return sqlEngine;
        }catch (JSONException e){
            logger.error("#JSON入参数据非法-->"+e.getMessage());
            return null;
        }
    }
}
