package com.prism.springas.utils.sqlEngine;

import com.prism.springas.tools.BaseTool;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.BaseTableCfg;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * MySQL - 动态SQL引擎
 */
public class sqlEngine implements Serializable{

    private static final long serialVersionUID = 1L;

    BasePage basePage = null;

    @Autowired
    BaseTableCfg baseTableCfg;

    public sqlEngine() {
        baseTableCfg = new BaseTableCfg();
        basePage = new BasePage();
    }

    /**
     * 获取封装在引擎中的Param参数
     * @param key   Key
     * @return
     */
    public Object getBaseParam(String key){
        return basePage.get(key);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                          SELECT ENGINE                                  //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * SQL生成(SELECT)
     * @return
     */
    public BasePage toParseEngineSQL(){
        String selSQL = "SELECT columns  FROM tables WHERE 1=1 condition groupBy having sortSQL sqlPage";
        basePage.put("parseSQL",selSQL.replaceAll("columns",basePage.get("columns") == null? " * " : basePage.get("columns").toString())
                .replaceAll("tables",basePage.get("tables") == null? "" : basePage.get("tables").toString())
                .replaceAll("condition",basePage.get("condition") == null? "" : basePage.get("condition").toString())
                .replaceAll("groupBy",basePage.get("groupBy") == null? "" : "GROUP BY " + basePage.get("groupBy").toString())
                .replaceAll("having",basePage.get("having") == null? "" : "HAVING " + basePage.get("having").toString())
                .replaceAll("sortSQL",basePage.get("sortSQL") == null? "" : basePage.get("sortSQL").toString())
                .replaceAll("sqlPage",basePage.get("sqlPage") == null? "" : basePage.get("sqlPage").toString()));
        return basePage;
    }

    /**
     * 置空入参对象(--主要用于单条数据查询)
     * @param options   ...需要置空的engine参数对象
     * @return
     */
    public sqlEngine removeEngineCondition(String... options){
        for (String s : options){
            this.basePage.remove(s);
        }
        return this;
    }

    /**
     * 处理/生成SELECT BY ID 根据ID检索数据的匹配条件
     * @param id    入参Obj     - ID值(UUID)
     * @return
     */
    public sqlEngine getSelById(String id){
        this.basePage.put("byOneCondition",id);
        return this;
    }


    /**
     * 处理/生成表关系SQL
     * -     (-> a join b on a.1 = b.2)
     * @param tables    表名  -  [多个表间用','隔开,连表表明需要从BaseTableCfg获取]
     * @return
     */
    public sqlEngine getSelTable(String tables) throws Exception{
        String [] tableArray = injectionParse(tables).split(",");
        StringBuffer sb = new StringBuffer();
        sb.append(tableArray[0]);
        if (tableArray.length > 1){
            //获取外连表关联信息
            String  [] joinTables = selJoinTable(tableArray[0]);
            //tableArray.length > 1,获取并拼接对应连表信息
            //从第一个开始获取,0为数据源表
            for (int i = 1 ; i < tableArray.length ; i++){
                String nStr = tableArray[i].substring(0,tableArray[i].length()-1);
                //判断当前截取字段是否匹配
                if (tables.contains(nStr)) {
                    sb.append(" LEFT JOIN ");
                    sb.append(Character.isDigit(
                            tableArray[i].substring(tableArray[i].length() - 1, tableArray[i].length()).charAt(0))
                            ? nStr : tableArray[i]).append(" AS ").append(tableArray[i]);
                    sb.append(" ON ");
                    //二次判别,以保证JOIN的准确性
                    if(joinTables[i-1].contains(nStr)){
                        sb.append(joinTables[i-1]);
                    }else{
                        sb.append(joinTables[i]);
                    }
                }
            }
        }
        this.basePage.put("tables",sb.toString());
        return this;
    }

    /**
     * 处理/生成匹配表查询字段语句
     *     @@type:A  - 只传入了表名,则定向拼接获取主表内全部字段信息
     * @param tables    表名  -  [多个表间用','隔开,连表表明需要从BaseTableCfg获取]
     * @return
     * @throws Exception
     */
    public sqlEngine getSelCols(String tables) throws Exception{
        String [] tableArray = tables.split(",");
        StringBuffer sb = new StringBuffer();
        for (String tCols:tableArray) {
            sb.append(!tCols.equals("") ? parseTableCols(tCols) : "");
            sb.append(" ,");
        }
        this.basePage.put("columns",sb.deleteCharAt(sb.length()-1));
        return this;
    }

    /**
     * 处理/生成匹配表查询字段语句
     * @param tables    表名  -  [多个表间用','隔开,连表表明需要从BaseTableCfg获取]
     * @param cols      字段名  -  [多个表间用','隔开,如需使用别名请使用如下格式:'a#b']
     * @return
     * @throws Exception
     */
    public sqlEngine getSelCols(String tables,String cols) throws Exception{
        //1.判断Table是否为空
        StringBuffer sb = new StringBuffer();
        StringBuffer gp = new StringBuffer();
        if (!tables.trim().equals("")){
            sb.append(getSelCols(tables).basePage.get("columns"));
            sb.append(",");
        }
        //2.判断Col是否为空
        if (!cols.trim().equals("")){
            for (String col:injectionParse(cols).split(",")) {
                //1.分组判断
                boolean isGroup = col.trim().contains("||");
                if (!isGroup) {
                    String [] colArray = col.split("#");
                    sb.append(" ");
                    sb.append(colArray[0].replaceAll("\\_", "."));
                    sb.append(" AS ");
                    sb.append(col.contains("#") ? colArray[1] : colArray[0].replaceAll("\\.", "\\_"));
                    sb.append(",");
                }else{
                    String [] colArray = col.split("\\|\\|");
                    String [] colSplit = colArray[1].split("#");
                    sb.append(" ");
                    sb.append(colArray[0].toUpperCase());
                    sb.append("(");
                    sb.append(colSplit[0].replaceAll("\\_","."));
                    sb.append(") AS ");
                    sb.append(colArray[1].contains("#") ? colSplit[1].replaceAll("\\.", "\\_") : "G_" + colSplit[0].replaceAll("\\.", "\\_"));
                    sb.append(",");
                    //计入Group分组字段内
                    gp.append(colSplit[0]);
                    gp.append(",");
                }
            }
        }
        //3.都为空
        if (tables.trim().equals("") && cols.trim().equals("")){
            sb.append(" * ");
            sb.append(",");
        }
        this.basePage.put("columns",sb.deleteCharAt(sb.length() - 1).toString());
        this.basePage.put("groupBy",gp.length() > 1 ? gp.deleteCharAt(gp.length() - 1).toString() : null);
        return this;
    }

    /**
     * SqlPage-数据分页
     * @param pageNo    当前第几页(默认为第1页)
     * @param pageSize  每页查询/展示?条
     * @return
     */
    public sqlEngine getPage(Integer pageNo,Integer pageSize){
        StringBuffer sb = new StringBuffer();
        //pageSize为空则默认为一页15条
        pageSize = pageSize == null ? 15 : pageSize;
        //pageNo为空则默认为第一页
        pageNo = pageNo == null ? 1 : pageNo;
        sb.append(" LIMIT ");
        sb.append((pageNo - 1) * pageSize);
        sb.append(",");
        sb.append(pageSize);
        this.basePage.put("sqlPage",sb.toString());
        return this;
    }

    /**
     * 查询条件（和）
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getWhereAnd(String key,String condition,Object val){
       return getWhere(0,key,condition,val);
    }

    /**
     * 查询条件（或）
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getWhereOr(String key,String condition,Object val){
        return getWhere(1,key,condition,val);
    }

    /**
     * 查询条件（和）-多字段组合查询
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getWhereAndMore(String key,String condition,Object val){
        return getWhere(2,key,condition,val);
    }

    /**
     * 查询条件（和）-多字段组合查询
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getWhereOrMore(String key,String condition,Object val){
        return getWhere(3,key,condition,val);
    }

    /**
     * 查询条件（和）-多字段组合查询结束
     * @param key
     * @param condition
     * @param val
     * @return
     */
    public sqlEngine getWhereAndMoreEnd(String key,String condition,Object val){
        return getWhere(4,key,condition,val);
    }


    /**
     * 查询条件（或）-多字段组合查询结束
     * @param key
     * @param condition
     * @param val
     * @return
     */
    public sqlEngine getWhereOrMoreEnd(String key,String condition,Object val){
        return getWhere(5,key,condition,val);
    }

    /**
     * 数据排序
     * @param keys          数据排序字段（支持多个，用','隔开）
     * @param sortTypes     数据排序类型（支持多个，用','隔开，与keys值对应）
     *                      0 - desc  1 - asc[拼音首字母]  2- desc[拼音首字母]  3- asc
     * @return
     */
    public sqlEngine getSort(String keys,String sortTypes){
        StringBuffer sb = new StringBuffer();
        String [] st = sortTypes.split(",");
        //1.分割数组
        String [] skeys =   injectionParse(keys).split(",");
        if (skeys.length > 0){sb.append(" ORDER BY ");}
        for (int i = 0 ; i < skeys.length ; i++) {
            if(Integer.parseInt(st[i]) == 1){
                //根据汉语拼音ASC
                sb.append("  CONVERT( " + skeys[i].replaceAll("\\_",".") + " USING GBK)  ASC,");
            }else if(Integer.parseInt(st[i]) == 2){
                //正常 ASC
                sb.append(" " + skeys[i].replaceAll("\\_",".") + " ASC,");
            }else if(Integer.parseInt(st[i]) == 3){
                //根据汉语拼音DESC
                sb.append("  CONVERT( " + skeys[i].replaceAll("\\_",".") + " USING GBK)  DESC,");
            }else{
                //正常 DESC
                sb.append(" " + skeys[i].replaceAll("\\_",".") + " DESC,");
            }
        }

        this.basePage.put("sortSQL",sb.deleteCharAt(sb.length()-1).toString());
        return this;
    }

    /**
     * COUNT - 统计数据总条数
     * @return
     */
    public sqlEngine getCount(){
        //置空sqlPage分页条件
        this.basePage.put("sqlPage","");
        this.basePage.put("columns"," COUNT(1) AS ALLSIZE ");
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                          DELETE ENGINE                                   //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 单/多表删除 --  DELETE TABLES
     * @param tables       删除需要操作的表
     * @return             -- tables:经过引擎处理后的${tables}入参
     * @throws Exception
     */
    public sqlEngine getDelTables(String tables) throws Exception{
        return this.getSelTable(tables);
    }

    /**
     * 根据ID删除数据表信息
     *          --*使用本方法,将强制清空在此前设置的所有condition(Where)条件
     * @param id
     * @return
     */
    public sqlEngine getDeleteById(String id){
        return toById(id);
    }


    /**
     * 指定需要执行删除的信息表
     * @param tables    需要删除的信息表
     * @return
     */
    public sqlEngine getNeedDelTable(String tables){
        this.basePage.put("needDelTables", injectionParse(tables));
        return this;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                          INSERT ENGINE                                   //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 数据入库  -- insert data
     * @param key   入库字段
     * @param val   匹配值
     * @return
     */
    public sqlEngine getAddData(String key,Object val){
        if (val != null) {
            //1.keys
            StringBuffer insertKeys = new StringBuffer();
            insertKeys.append(this.basePage.get("insertKeys") == null ? "" :
                    this.basePage.get("insertKeys")).append(",");
            insertKeys.append(injectionParse(key));

            //2.vals
            StringBuffer insertVals = new StringBuffer();
            insertVals.append(this.basePage.get("insertVals") == null ? "" :
                    this.basePage.get("insertVals")).append(",");
            insertVals.append(val instanceof String ?
                    "'" +StringEscapeUtils.escapeSql(val.toString())+"'" : val);

            this.basePage.put("insertKeys", insertKeys);
            this.basePage.put("insertVals", insertVals);
        }
        return this;
    }

    /**
     * INSERT入参处理
     *      --  将sqlEngine处理成DAO的BasePage入参
     * @return  BasePage
     */
    public BasePage toParseInsertSQL(){
        String insertKeys = basePage.get("insertKeys").toString();
        String insertVals = basePage.get("insertVals").toString();
        this.basePage.put("insertKeys",insertKeys.substring(1,insertKeys.length()));
        this.basePage.put("insertVals"," ( " + insertVals.substring(1,insertVals.length()) + " ) ");
        return this.basePage;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                          UPDATE ENGINE                                   //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * UPDATE -- 需要执行更新的信息
     * @param colKey        执行更行的字段
     * @param colValue      字段对应的值
     * @return
     */
    public sqlEngine getUpdateCols(String colKey,Object colValue){
        //0.colValue是否为空或null
        colValue = colValue  == null ? "" : colValue;
        if (!colValue.equals("")) {
            //1.获取已有需更新字段
            String updateCols = basePage.get("updateCols") == null ? "" : basePage.get("updateCols").toString();
            StringBuffer sb = new StringBuffer();
            sb.append(updateCols);
            sb.append(",").append(injectionParse(colKey).replaceAll("\\_",".")).append(" = '")
                    .append(StringEscapeUtils.escapeSql(colValue+"")).append("'");
            this.basePage.put("updateCols", sb);
        }
        return this;
    }

    /**
     * 更新条件（和）
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getUpdateWhereAnd(String key,String condition,String val){
        return getWhere(0,key,condition,val);
    }

    /**
     * 更新条件（或）
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    public sqlEngine getUpdateWhereOr(String key,String condition,String val){
        return getWhere(1,key,condition,val);
    }

    /**
     * 数据更新(版本校验-乐观锁判定)
     * @param tableName     主数据表表名
     * @param value     当前数据版本值Ver
     * @return
     */
    public sqlEngine getUpdCurrentVersion(String tableName,Integer value){
        this.basePage.put("currentVer",value);
        return getUpdateCols(tableName.toUpperCase()+"_VERSION",value.toString());
    }
    /**
     * 需要执行更新的数据主表
     * @param tables       表集合
     * @return
     * @throws Exception
     */
    public sqlEngine getUpdateTables(String tables) throws Exception{
        return getSelTable(tables);
    }

    /**
     * 根据ID更新数据表信息
     *          --*使用本方法,将强制清空在此前设置的所有condition(Where)条件
     * @param id
     * @return
     */
    public sqlEngine getUpdateById(String id){
        return toById(id);
    }

    /**
     * UPDATE入参处理
     *      --  将sqlEngine处理成DAO的BasePage入参
     * @return  BasePage
     */
    public BasePage toParseUpdateSQL(){
        String updateCols = this.basePage.get("updateCols").toString();
        //1.去除最开始的分隔符号
        this.basePage.put("updateCols",updateCols.substring(0,1).equals(",") ?
                updateCols.substring(1,updateCols.length()) : updateCols);
        return this.basePage;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                            FETCH                                         //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 流式批量新增数据->(入参列)
     *          --  多个列名间以‘,’隔开
     * @param cols
     * @return
     */
    public sqlEngine getAddFetchCols(String cols){
        StringBuffer insertKeys = new StringBuffer();
        for (String col:cols.split(",")) {
            insertKeys.append(this.basePage.get("insertKeys") == null ? "" :
                    this.basePage.get("insertKeys")).append(",");
            insertKeys.append(injectionParse(col));
        }
        this.basePage.put("insertKeys",insertKeys);
        return this;
    }

    /**
     * 流式批量新增数据->(入参值)
     * @param fetchList     入参对象List集合
     * @return
     */
    public sqlEngine getAddFetchVals(List<BasePage> fetchList){
        StringBuffer insertVals = new StringBuffer();
        String []insertKeys = this.basePage.get("insertKeys").toString().split(",");
        for (BasePage fe:fetchList) {
            insertVals.append(",(");
            //1.固有数据
            String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            insertVals.append("'").append(BaseTool.getUUID()).append("'").append(",");//ID
            insertVals.append(0).append(",");//VERSION
            insertVals.append(0).append(",");//ISDELETE
            insertVals.append("'").append(nowDate).append("'").append(",");//DATETIME
            insertVals.append("'").append(nowDate).append("'").append(",");//CREATETIME
            for (String key : insertKeys){
                if (!key.equals(""))
                    if (fe.get("SNAME") != null)
                        insertVals.append(fe.get(key) instanceof String ?
                                "'" + StringEscapeUtils.escapeSql(fe.get(key).toString()) + "'" : fe.get(key)).append(",");
            }
            insertVals.append(")");
        }
        StringBuffer appendKey = new StringBuffer();
        appendKey.append(",ID").append(",VERSION").
                append(",ISDELETE").append(",DATETIME").append(",CREATETIME");
        this.basePage.put("insertKeys",appendKey.toString() +  this.basePage.get("insertKeys").toString());
        this.basePage.put("insertVals",insertVals.toString().replaceAll("\\,\\)",")"));
        return this;
    }


    /**
     * FETCH INSERT  批量入参处理
     *      --  将sqlEngine处理成DAO的BasePage入参
     * @return  BasePage
     */
    public BasePage toParseAddFetchSQL(){
        String insertKeys = basePage.get("insertKeys").toString();
        String insertVals = basePage.get("insertVals").toString();
        this.basePage.put("insertKeys",insertKeys.substring(1,insertKeys.length()));
        this.basePage.put("insertVals",insertVals.substring(1,insertVals.length()));
        return this.basePage;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                          SELECT EX ENGINE                                  ////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 设置拓展EX-Dao中的入参项
     * @param key   入参key
     * @param val   入参Val
     * @return
     */
    public sqlEngine getParamEx(String key,Object val){
        this.basePage.put(key,val);
        return this;
    }

    /**
     * 获取拓展Ex-Dao所需的BasePage内参对象
     * @return
     */
    public BasePage toParseExSQL(){
        return this.basePage;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                           PRIVATE                                        //////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 内部方法(根据ID操作数据)
     *        --用于处理单条数据的delete/update
     * @param id    数据主键ID
     * @return
     */
    private sqlEngine toById(String id){
        StringBuffer sb = new StringBuffer();
        sb.append(" AND ").append(" ID = '").append(id).append("'");
        this.basePage.put("condition",sb);
        return this;
    }

    /**
     * 内部方法 - getWhere代码优化
     * @param type          条件合并类型  0-AND 1-OR 2- AND ( 3-OR(  4 - )
     *                              0/1 : 单字段匹配   2/3 组合字段查询开始  4-组合查询结束（AND收束） 5-组合查询结束（OR收束）
     * @param key           查询字段
     * @param condition     -
     * @param val           对应查询条件
     * @return
     */
    private sqlEngine getWhere(Integer type,String key,String condition,Object val){
        StringBuffer sb = new StringBuffer();
        //*判断val是否为空或'' - 为True则自动越过此条件
        //--val为null则置换为'',防止报错
        val = val == null ? "" : val;
        if (!val.equals("")) {
            //--防止通过Val入参方式进行SQL注入
            val = StringEscapeUtils.escapeSql(val.toString());
            //0.condition为空默认为EQ
            condition = condition == null || condition.trim().equals("") ? "EQ" : condition;
            //1.获取已有WHERE条件
            String elementCondition = this.basePage.getString("condition");
            sb.append(elementCondition == null ? "" : elementCondition);
            sb.append(type == 0 ? key.contains("||") && this.basePage.get("having") == null ? " " : " AND " : type == 1 || type == 5 ? " OR " : type == 2 ? "AND ( " : type == 3 ? " OR (" : " AND ");
            //--防止通过key入参方式进行SQL注入
            String newKey = injectionParse(key);
            sb.append(newKey.contains("||") ? newKey.split("\\|\\|")[0].toUpperCase() + "(" + newKey.split("\\|\\|")[1] + ")" : newKey).append(" ");
            //Engine@Bat-条件结构处理
            //1.condition不是IN,INN,INNU,BET
            switch (condition) {
                case "EQ":
                    sb.append(sqlEngineEnum.EQ.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "LIKE":
                    sb.append(sqlEngineEnum.LIKE.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "GT":
                    sb.append(sqlEngineEnum.GT.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "GTEQ":
                    sb.append(sqlEngineEnum.GTEQ.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "LTEQ":
                    sb.append(sqlEngineEnum.LTEQ.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "LT":
                    sb.append(sqlEngineEnum.LT.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "NEQ":
                    sb.append(sqlEngineEnum.NEQ.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
                case "ISNULL":
                    sb.append(sqlEngineEnum.ISNULL.getCondition());
                    break;
                case "NOTNULL":
                    sb.append(sqlEngineEnum.NOTNULL.getCondition());
                    break;
                case "IN":
                    sb.append(sqlEngineEnum.IN.getCondition()).append(" (");
                    for (String iv : val.toString().split(",")) {
                        if (!iv.trim().equals("") || iv != null)
                            sb.append("'").append(iv).append("',");
                    }
                    sb.deleteCharAt(sb.length() - 1).append(")");
                    break;
                case "NOTIN":
                    sb.append(sqlEngineEnum.NOTIN.getCondition()).append(" (");
                    for (String iv : val.toString().split(",")) {
                        if (!iv.trim().equals("") || iv != null)
                            sb.append("'").append(iv).append("',");
                    }
                    sb.deleteCharAt(sb.length() - 1).append(")");
                    break;
                case "BET":
                    sb.append(sqlEngineEnum.BET.getCondition());
                    sb.append("'").append(val.toString().split(",")[0]).append("'");
                    sb.append(" AND ");
                    sb.append("'").append(val.toString().split(",")[1]).append("'");
                    break;
                default:
                    sb.append(sqlEngineEnum.EQ.getCondition());
                    sb.append(" '").append(val).append("' ");
                    break;
            }
            if (newKey.contains("||")){
                this.basePage.put("having", type == 4 || type == 5 ? sb.toString() + " ) " : sb.toString());
            }else {
                this.basePage.put("condition", type == 4 || type == 5 ? sb.toString() + " ) " : sb.toString());
            }
        }
        return this;
    }


    /**
     * 防止SQL注入(应用于Tables,cols等入参字段)
     *      -- 检测入参字段中是否含有非法字符/'/(单引号)
     * @param key   执行检测入参值
     * @return
     */
    private String injectionParse(String key){
        key = key.contains("\'") ? key.split("\'")[0].trim() : key;
        return key.replaceAll("\'", "").replaceAll("\\_", ".");
    }

    /**
     * 查询字段对应结构合规化处理
     * @param tableName     需要执行合规化的数据表
     * @return
     * @throws Exception
     */
    private String parseTableCols(String tableName) throws Exception{
        if(!Character.isDigit(tableName.substring(tableName.length()-1,tableName.length()).charAt(0))){
            //表名后没有次序标识
            return selColunm(tableName);
        }else{
            //表名后有次序标识
            //1.去除次序标识
            String newTCols = tableName.substring(0,tableName.length()-1);
            //2.置换newTCols与TableName使查询字段符合既定规则
            return selColunm(newTCols).replaceAll(newTCols+"\\.",tableName+"\\.").replaceAll(
                    newTCols+"_",tableName+"_");
        }
    }

    /**
     * 获取当前表全字段信息
     * @param tableName
     * @return
     * @throws Exception
     */
    private String selColunm(String tableName) throws  Exception{
        return  baseTableCfg.getClass().getDeclaredMethod("get"+tableName.toUpperCase()).invoke(baseTableCfg).toString();
    }

    /**
     * 获取当前表连表字段信息
     * @param tableName     外连表名称
     * @return              外连表数组
     * @throws Exception
     */
    private String[] selJoinTable(String tableName) throws  Exception{
        return  baseTableCfg.getClass().getDeclaredMethod("get"+tableName.toUpperCase()+"_JOIN").invoke(baseTableCfg).toString().split(",");
    }
}
