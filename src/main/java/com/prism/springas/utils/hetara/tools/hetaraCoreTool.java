package com.prism.springas.utils.hetara.tools;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Hetara规则引擎逻辑语句执行核心类
 *              --    使用了Jexl技术作为底层代码支持
 */
public class hetaraCoreTool {

    /**
     * Hetara执行规则语句
     * @param jexlExp     逻辑规则语句
     * @param map         逻辑规则配参数据
     * @return
     */
    public static Object convertToCode(String jexlExp,Map<String,Object> map){
        JexlEngine jexl=new JexlEngine();
        Expression e = jexl.createExpression(jexlExp);
        JexlContext jc = new MapContext();
        for(String key:map.keySet()){
            jc.set(key, map.get(key));
        }
        if(null==e.evaluate(jc)){
            return false;
        }
        return e.evaluate(jc);
    }

    public static boolean regMatch(String regEx, String str) {
        if (str==null){
            return false;
        }else {
            Pattern pattern = Pattern.compile(regEx);
            return pattern.matcher(str).matches();
        }
    }

    public static boolean regMatchFind(String regEx, String str) {
        if (str==null){
            return false;
        }else {
            Pattern pattern = Pattern.compile(regEx);
            return pattern.matcher(str).find();
        }
    }

}
