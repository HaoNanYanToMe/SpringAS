package com.prism.springas.schema;

import com.alibaba.fastjson.JSON;
import com.prism.springas.api.BaseApi;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/*
    底层Schema逻辑类
    -*提供给Controller层进行调用
    -*实现及处理Api层返回结果
    -*本类仅实现BaseApi层的基本简单逻辑方法，复杂逻辑方法请创建新类（XXXSCHEMA）
 */
@Service("baseSchema")
@Transactional(readOnly = true)
public class BaseSchema implements Serializable{

    @Autowired
    public BaseApi baseApi;

    /**
     * Base服务：基本查询分页语句[用于PDM代码生成出的BaseDAO调用]
     *          --*支持Where条件复杂匹配(具体请查看sqlEngineEnum)
     *          --*支持简单连表(仅left join ... on)
     *          --***支持group分组及其他复杂性逻辑查询!
     * @param tableName     Proxy - 反射的数据表名
     * @param sqlEngine     sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return
     */
    public String selectPage(String tableName, sqlEngine sqlEngine){
        BasePage rePageData = new BasePage();
        List<BasePage> listData = baseApi.selectPage(tableName,sqlEngine);
        rePageData.put("data",listData);
        //Total Get
        //判断是否为分组数据
        if (sqlEngine.getBaseParam("groupBy")!=null){
            rePageData.put("total",listData.size());
        }else {
            List<BasePage> totals = baseApi.selectPageCount(tableName, sqlEngine);
            rePageData.put("total", totals.size() > 0 ? Integer.parseInt(totals.get(0).get("ALLSIZE") + "") : 0);
        }
        return JSON.toJSONString(rePageData);
    }

    /**
     * eChars数据
     * @param tableName
     * @param sqlEngine
     * @return
     */
    public List<BasePage> eCharsBase(String tableName, sqlEngine sqlEngine){
        return baseApi.selectPage(tableName,sqlEngine);
    }

    /**
     * Base服务：根据主键ID查询单条数据信息[用于PDM代码生成出的BaseDAO调用]
     * @param tableName     Proxy - 反射的数据表名
     * @param sqlEngine     sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return
     */
    public String  selectById(String tableName,sqlEngine sqlEngine){
        return JSON.toJSONString(baseApi.selectById(tableName,sqlEngine));
    }

    /**
     * Base服务：删除数据（暴力删除操作，不可逆操作）
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  数据删除结果（0-失败，1-成功）
     */
    @Transactional(readOnly = false)
    public String deleteData(String tableName,sqlEngine sqlEngine){
        return JSON.toJSONString(baseApi.deleteData(tableName,sqlEngine) == 0 ? "数据删除失败" : "数据删除成功");
    }

    /**
     * Base服务：创建新数据（数据入库操作）  -- 单条（ONLY ONE MODEL）
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  返回入库信息的UUID
     *       -成功则返回UUID,失败则返回''
     */
    @Transactional(readOnly = false)
    public String insertData(String tableName,sqlEngine sqlEngine){
        return JSON.toJSONString(baseApi.insertData(tableName,sqlEngine));
    }

    /**
     * Base服务：更新数据（数据更新操作）
     *             --  synchronized -同步更新~防止线程竞争
     *             --  在根据ID更新数据时,会自动判别并进入乐观锁机制
     *             --  在批量更新数据时,建议根据业务场景需要自定义安全校验机制(getWhereAnd/getWhereOr)
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  更新状态（0-失败，1-成功）
     */
    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public synchronized String updateData(String tableName,sqlEngine sqlEngine) throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append(tableName.toUpperCase()+"_ID").append(",").append(tableName.toUpperCase()+"_VERSION");
        //1.获取符合条件匹配的数据ID
        sqlEngine.getSelCols("",sb.toString());
        List<BasePage> updIds  = this.baseApi.selectPage(tableName.toUpperCase(),sqlEngine);
        //2.if updIds为批量,则跳过乐观锁判断机制
        if(updIds.size()==1){
            //3.执行乐观锁判定
            if (sqlEngine.toParseUpdateSQL().get("currentVer") != null &&
                Integer.parseInt(updIds.get(0).get(tableName.toUpperCase()+"_VERSION")+"")
                    < Integer.parseInt(sqlEngine.toParseUpdateSQL().get("currentVer")+"")){
                return JSON.toJSONString(this.baseApi.updateData(tableName,sqlEngine) == 0 ? "数据更新失败" : "数据更新成功");
            }else{
                return JSON.toJSONString("当前数据版本号[Version]不匹配或已过期。");
            }
        }else{
            //4.不执行乐观锁判定
            int success = this.baseApi.updateData(tableName,sqlEngine);
            return JSON.toJSONString(success == 0 ? "数据更新失败" : success+"条数据更新成功,"
                    +(updIds.size()-success)+"条数据更新失败");
        }
    }

    /**
     * Base服务：批量导入数据
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  导入成功数量
     */
    @Transactional(readOnly = false)
    public String insertFetchData(String tableName,sqlEngine sqlEngine){
        return JSON.toJSONString(this.baseApi.insertFetchData(tableName,sqlEngine));
    }

    /**
     * Ex拓展服务：List拓展功能复杂SQL查询语句[用于自己撰写的SQL调用]
     * @param tableName       拓展DAO唯一标识[具体配置项文件可参考BaseCoreCfg]
     *                                  --- 如USEREXDAO 则使用USEREX即可
     * @return      获取到的数据对象
     * @throws Exception
     */
    public Object selectExPage(String tableName,String functionName,sqlEngine sqlEngine){
        return JSON.toJSONString(this.baseApi.selectExPage(tableName,functionName,sqlEngine));
    }
}
