package com.prism.springas.api;

import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.sqlEngine.sqlEngine;

import java.util.List;

/*
    基本应用服务发现接口
    -*本类接口为Schema通用数据获取及调用接口
    -*如需添加新接口，请注明原因。
 */
public interface BaseApi {

    /**
     * Base服务：基本查询分页语句[用于PDM代码生成出的BaseDAO调用]
     *          --*支持Where条件复杂匹配(具体请查看sqlEngineEnum)
     *          --*支持简单连表(仅left join ... on)
     *          --***尚不支持group分组及其他复杂性逻辑查询!
     * @param tableName     Proxy - 反射的数据表名
     * @param sqlEngine     sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return
     */
    List<BasePage> selectPage(String tableName, sqlEngine sqlEngine);

    /**
     * Base服务：基本查询数据总条数[与selectPage配合使用]
     *          --*支持Where条件复杂匹配(具体请查看sqlEngineEnum)
     *          --*支持简单连表(仅left join ... on)
     *          --***尚不支持group分组及其他复杂性逻辑查询!
     * @param tableName     Proxy - 反射的数据表名
     * @param sqlEngine     sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return
     */
    List<BasePage> selectPageCount(String tableName, sqlEngine sqlEngine);
    /**
     * Base服务：根据主键ID查询单条数据信息[用于PDM代码生成出的BaseDAO调用]
     * @param tableName     Proxy - 反射的数据表名
     * @param sqlEngine     sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return
     */
    BasePage selectById(String tableName,sqlEngine sqlEngine);

    /**
     * Base服务：删除数据（暴力删除操作，不可逆操作）
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  数据删除结果（0-失败，1-成功）
     */
    int deleteData(String tableName,sqlEngine sqlEngine);

    /**
     * Base服务：创建新数据（数据入库操作）  -- 单条（ONLY ONE MODEL）
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  返回入库信息的UUID
     *       -成功则返回UUID,失败则返回''
     */
    String insertData(String tableName,sqlEngine sqlEngine);

    /**
     * Base服务：更新数据（数据更新操作）
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  更新状态（0-失败，1-成功）
     */
    int updateData(String tableName,sqlEngine sqlEngine);

    /**
     * Base服务：批量导入数据
     * @param tableName  反射查询的数据表名
     * @param sqlEngine  sqlEngine - SQL引擎处理完成后返回的Engine对象
     *                          -sqlEngine.toParseEngineSQL()可以将engine对象转换成BasePage对象
     * @return  导入成功数量
     */
    int insertFetchData(String tableName,sqlEngine sqlEngine);

    /**
     * Ex拓展服务：List拓展功能复杂SQL查询语句[用于自己撰写的SQL调用]
     * @param tableName       拓展DAO唯一标识[具体配置项文件可参考BaseCoreCfg]
     *                                  --- 如USEREXDAO 则使用USEREX即可
     * @return      获取到的数据对象
     * @throws Exception
     */
    Object selectExPage(String tableName,String functionName,sqlEngine sqlEngine);
}
