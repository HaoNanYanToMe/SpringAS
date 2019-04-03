package com.prism.springas.controller;

import com.alibaba.fastjson.JSON;
import com.prism.springas.controller.config.BaseBasicExtends;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.echars.eCharsEngine;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BaseController extends BaseBasicExtends{
    //序列化
    private static final long serialVersionUID = 1L;
    //开启log4j日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 全局通用原生简单查询Controller层入口
     * @param selTable          需要检索的数据表表名
     * @param tables            执行检索的全字段表表名(*必须在BaseTableCfg类中,如USERINFO等)
     * @param columns           执行检索的特定字段名称
     *                              --参考格式：T_C#B
     *                                      --最终生成SQL: select t_c as B ...
     * @param sortColumns       排序字段(多个可用逗号隔开,如A,B,C)
     * @param sortTypes         排序类型,与排序字段一一对应(如1,2,3)
     *                                  --0:DESC
     *                                  --1:根据汉语拼音ASC
     *                                  --2:ASC
     *                                  --3:根据汉语拼音DESC
     * @param pageNo            当前第X页(X默认值为1)
     * @param pageSize          每页显示X条数据(X默认值为10)
     * @param data              查询条件数据组装:*JSON格式
     *                                  --{'W!T_C#EQ':'bbb'}
     *                                   --参考格式：W!T_C#EQ
     *                                       --W:W/O(where and / where or)
                                            //W : AND   - 单字段AND
                                            //O : OR    - 单字段OR
                                            //WS: AND ( - 多字段AND开始标识
                                            //WE: AND ) - 多字段AND结束标识
                                            //OS: OR(   - 多字段OR开始标识
                                            //OE: OR  ) - 多字段OR结束标识
     *                                      --T_C:tableName_colName
     *                                      --EQ:EQ/LIKE/IN/GT/LT..
     *                                     --最终引擎生成sql为： and t_c = 'bbb'
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/selectBase",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String selectBase(
            @RequestParam(value = "selTable")String  selTable,
            @RequestParam(value = "tables", required = false , defaultValue = "")String  tables,
            @RequestParam(value = "columns", required = false , defaultValue = "")String  columns,
            @RequestParam(value = "sortColumns", required = false , defaultValue = "")String  sortColumns,
            @RequestParam(value = "sortTypes", required = false , defaultValue = "")String  sortTypes,
            @RequestParam(value = "pageNo")Integer pageNo,
            @RequestParam(value = "pageSize")Integer pageSize,
            @RequestParam(value = "data", required = false , defaultValue = "{}")String  data) throws Exception{
        sqlEngine sqlEngine = super.jsonToSqlEngine(0,data);
        if (sqlEngine == null){
            return JSON.toJSONString("#JSON入参数据非法,请检查格式无误后重试!");
        }else {
            //1.必传参数[需查询数据表表名]
            sqlEngine.getSelTable(selTable)
                    //2.非必传参数[查询指定表全表字段,查询指定表特定字段](如都不传,则视为查询全部)
                    .getSelCols(tables == null ? "" : tables, columns == null ? "" : columns)
                    //3.是否需要执行分页(默认为第1页,单页载入10条数据)
                    .getPage(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
            //4.是否需要执行排序
            if (sortColumns != null && !sortColumns.equals("")) {
                sqlEngine.getSort(sortColumns, sortTypes);
            }
            //获取主表NAME
            String [] tableArray = selTable.split(",");
            //通过Prism棱镜获取数据返回结果
            Object reObj = super.invokeSchemaMethod(0, tableArray[0],
                    "selectPage", sqlEngine);
            return reObj == null ? "" : reObj.toString();
        }
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/eCharsBase",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String eCharsBase(
            @RequestParam(value = "selTable") String selTable,
            @RequestParam(value = "eCharsType") String eCharsType,
            @RequestParam(value = "columns", required = false, defaultValue = "") String columns,
            @RequestParam(value = "title", required = false, defaultValue = "") String title,
            @RequestParam(value = "xDataCol", required = false, defaultValue = "") String xDataCol,
            @RequestParam(value = "seriesDataCol", required = false, defaultValue = "") String seriesDataCol,
            @RequestParam(value = "legendCol", required = false, defaultValue = "") String legendCol,
            @RequestParam(value = "seriesName", required = false, defaultValue = "") String seriesName,
            @RequestParam(value = "sortColumns", required = false, defaultValue = "") String sortColumns,
            @RequestParam(value = "sortTypes", required = false, defaultValue = "") String sortTypes,
            @RequestParam(value = "data", required = false, defaultValue = "{}") String data) throws Exception {
        sqlEngine sqlEngine = super.jsonToSqlEngine(0, data);
        if (sqlEngine == null) {
            return JSON.toJSONString("#JSON入参数据非法,请检查格式无误后重试!");
        } else {
            //1.必传参数[需查询数据表表名]
            sqlEngine.getSelTable(selTable)
                    //2.非必传参数[查询指定表全表字段,查询指定表特定字段](如都不传,则视为查询全部)
                    .getSelCols("", columns == null ? "" : columns);
            //4.是否需要执行排序
            if (sortColumns != null && !sortColumns.equals("")) {
                sqlEngine.getSort(sortColumns, sortTypes);
            }
            //获取主表NAME
            String[] tableArray = selTable.split(",");
            //通过Prism棱镜获取数据返回结果
            Object reObj = super.invokeSchemaMethod(0, tableArray[0],
                    "eCharsBase", sqlEngine);
            if (reObj != null) {
                eCharsEngine eCharsEngine = new eCharsEngine(0, (List<BasePage>) reObj);
                if (eCharsType.equals("line") || eCharsType.equals("bar")) {
                    reObj = eCharsEngine.basicLine(xDataCol, seriesDataCol).toParseBasicChars(title).getEcharsData();
                } else if (eCharsType.equals("lines") || eCharsType.equals("bars")) {
                    reObj = eCharsEngine.moreLine(xDataCol, seriesDataCol, legendCol).toParseMoreChars(title).getEcharsData();
                } else if (eCharsType.equals("pie")) {
                    reObj = eCharsEngine.basicPie(legendCol, seriesDataCol).toParseDoughnut(title, seriesName).getEcharsData();
                }
            }
            return reObj == null ? "" : reObj.toString();
        }
    }



    /**
     * 拓展方法Controller层接口
     *                  --  xxExSchema数据获取
     * @param selExTable      BaseCoreCfg中配置的参数(拓展表别名)
     *                      --ver1.2:@proxySchema("test")
     * @param functionName    拓展方法名称
     * @param isBase          数据处理格式(1-查询/删除 2-新增 3-更新)
     * @param data            查询条件数据组装:*JSON格式
     *                              -- 示例:{'a':'b'}
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/exFunction",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String exFunction(
            @RequestParam(value = "selExTable")String  selExTable,
            @RequestParam(value = "functionName")String  functionName,
            @RequestParam(value = "isBase", required = false , defaultValue = "1")Integer  isBase,
            @RequestParam(value = "data", required = false , defaultValue = "{}")String  data) throws Exception{

        sqlEngine sqlEngine = super.jsonToSqlEngine(isBase,data);
        if (isBase == 3){
            sqlEngine sqlEngineSel = super.jsonToSqlEngine(1,data);
            sqlEngine.getParamEx("updId",sqlEngineSel.getBaseParam("ID"))
                     .getParamEx("version",sqlEngineSel.getBaseParam("VERSION"));
        }
        if (sqlEngine == null){
            return JSON.toJSONString("#JSON入参数据非法,请检查格式无误后重试!");
        }else {
            //通过Prism棱镜获取数据返回结果
            Object reObj = super.invokeSchemaMethod(1, selExTable,
                    functionName, sqlEngine);
            return reObj == null ? "" : reObj.toString();
        }
    }

    /**
      * 数据新增Controller层接口
     * @param insertTable     需要执行新增的数据表表名
     * @param data           新增数据组装:*JSON格式
     *                              -- 示例:{'a':'b'}
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/insertData",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String insertData(
            @RequestParam(value = "insertTable", required = false) String insertTable,
            @RequestParam(value = "data", required = false , defaultValue = "{}")String data) throws Exception{
        sqlEngine sqlEngine = super.jsonToSqlEngine(2,data);
        if (sqlEngine == null){
            return JSON.toJSONString("#JSON入参数据非法,请检查格式无误后重试!");
        }else {
            //通过Prism棱镜获取数据返回结果
            Object reObj = super.invokeSchemaMethod(0, insertTable,
                    "insertData", sqlEngine);
            return reObj == null ? "" : reObj.toString();
        }
    }

    /**
     * 数据暴力删除Controller层接口(通过本接口删除的数据无法进行数据还原!)
     * @param delTable      需要执行删除的数据表(Base基础表,可在BaseTableCfg中查询)
     * @param delId         需要执行删除的数据主键ID
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/deleteForce",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String deleteForce(
            @RequestParam(value = "delTable") String delTable,
            @RequestParam(value = "delId") String delId) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getDeleteById(delId);
        sqlEngine.getDelTables(delTable);
        //通过Prism棱镜获取数据返回结果
        Object reObj = super.invokeSchemaMethod(0,delTable,
                "deleteData",sqlEngine);
        return reObj == null ? "" : reObj.toString();
    }

    /**
     * 数据软删除Controller层接口(通过本接口删除的数据可以进行数据还原)
     * @param delTable      需要执行删除的数据表(Base基础表,可在BaseTableCfg中查询)
     * @param delId         需要执行删除的数据主键ID
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/deleteAffair",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String deleteAffair(
            @RequestParam(value = "delTable") String delTable,
            @RequestParam(value = "delId") String delId,
            @RequestParam(value = "version" , required = false , defaultValue = "0") String version
    ) throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("{\'");
        sb.append(delTable.toUpperCase()).append("_").append("ISDELETE");
        sb.append("\':\'1\'}");
        return updateData(delTable,version,sb.toString(),delId);
    }

    /**
     * 数据更新Controller层接口(根据主键ID更新对应数据)
     * @param updTableName     需要执行更新的表名(Base基础表,可在BaseTableCfg查询)
     * @param version          当前数据版本(Version)
     * @param updData          需要更新的字段及值：*JSON格式
     *                                  --示例：{'a':'b'}
     * @param updId            需要执行更新的主键ID值
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/updateData",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String updateData(
            @RequestParam(value = "updTableName") String updTableName,
            @RequestParam(value = "version" , required = false , defaultValue = "0") String version,
            @RequestParam(value = "updData") String updData,
            @RequestParam(value = "updId") String updId
    ) throws Exception{
        sqlEngine sqlEngine = super.jsonToSqlEngine(3,updData);
        if (sqlEngine == null){
            return JSON.toJSONString("#JSON入参数据非法,请检查格式无误后重试!");
        }else{
            sqlEngine.getUpdateTables(updTableName)
                    .getUpdateById(updId)
                    .getUpdCurrentVersion(updTableName,Integer.parseInt(version));
            //通过Prism棱镜获取数据返回结果
            Object reObj = super.invokeSchemaMethod(0,updTableName,
                    "updateData",sqlEngine);
            return reObj == null ? "" : reObj.toString();
        }
    }

    /**
     * 根据入参ID查询单条数据信息
     * @param selTable      需要执行查询的主表
     * @param tables        需要查询的表(支持连表,多个表间以','隔开)
     *                              --> 传入的数据需要与BaseTableCfg匹配
     * @param columns       需要查询的字段名（多个字段间以','隔开）
     *                              --> 入参字段格式:A_B / A_B#C
     *                              --> A:tableName(表)  B:ColName(字段)  C:aliasName(别名)
     *                              --> *如不需要使用别名,则可以省略#C
     * @param selId         zhu
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/selectById",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String selectById(
            @RequestParam(value = "selTable") String selTable,
            @RequestParam(value = "tables", required = false , defaultValue = "")String  tables,
            @RequestParam(value = "columns", required = false , defaultValue = "")String  columns,
            @RequestParam(value = "selId") String selId
    )throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelById(selId)
                 .getSelTable(selTable)
                 .getSelCols(tables,columns);
        //通过Prism棱镜获取数据返回结果
        Object reObj = super.invokeSchemaMethod(0,selTable,
                "selectById",sqlEngine);
        return reObj == null ? "" : reObj.toString();
    }
}
