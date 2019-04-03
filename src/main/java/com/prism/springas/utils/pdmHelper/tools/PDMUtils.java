package com.prism.springas.utils.pdmHelper.tools;

import com.prism.springas.utils.pdmHelper.entites.*;
import com.prism.springas.utils.pdmHelper.parse.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    -pdm生成工具类
 */
public class PDMUtils {

    Parser pdmParser = new Parser();

    /**
     * SQL  -@Delete - according pk_id delete opposite data
     * 删除语句 - 根据传入的表主键ID删除指定数据
     * @return
     */
    public StringBuffer deleteSQLUtil(){
        StringBuffer sb = new StringBuffer();
        sb.append(" DELETE ").append(" ${bp.needDelTables} ").append(" FROM ");
        sb.append(" ${bp.tables} ");
        sb.append(" WHERE 1 = 1 " ).append(" ${bp.condition} ");
        return sb;
    }


    /**
     * SQL -@SELECT - according pk_id select opposite dataList
     * 查询语句 - 查询对应数据
     * @parseSQL engine finish sql 经由引擎处理成功的SQL语句
     * @return
     */
    public StringBuffer selectListSQLUtil(){
        StringBuffer sb = new StringBuffer();
        sb.append(" ${bp.parseSQL} ");
        return sb;
    }

    /**
     * SQL - @SELECT BY ONE
     * 查询语句 - 查询单条
     * @param tableName 数据
     * @columns - 所需查询字段
     * @tables  - 数据表[引擎生成-包含子表]
     * @byOneCondition  -检索条件[引擎生成]
     * @return
     */
    public StringBuffer selectByOneSQLUtil(String tableName){
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ${bp.columns}  FROM ${bp.tables} WHERE ").append(" 1 = 1 ");
        sb.append(" AND ").append(" ").append(tableName).append(".ID").append(" = ").append(" #{bp.byOneCondition}");
        return sb;
    }

    /**
     * SQL -@INSERT
     * 新增数据
     * @param tableName
     * @return
     */
    public StringBuffer insertSQLUtil(String tableName){
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO ");
        sb.append(" " + tableName.toUpperCase());
        sb.append(" ( ").append(" ${bp.insertKeys} ").append(" ) ");
        sb.append(" VALUES  ").append(" ${bp.insertVals} ");
        return sb;
    }

    /**
     * SQL -@UPDATE
     * 更新数据
     * @return
     */
    public StringBuffer updateSQLUtil(){
        StringBuffer sb = new StringBuffer();
        sb.append(" UPDATE ").append(" ${bp.tables } ");
        sb.append(" SET ").append(" ${bp.updateCols} ");
        sb.append(" WHERE 1 = 1 ").append(" ${bp.condition} ");
        return sb;
    }


    /**
     * PDM to Parse Join Map（GEN CFG file）
     * 根据pdm生成对应的连表字段进行匹配(用于生成cfg核心文件)
     * *主要用于匹配外联表与外联字段
     * @param mapRef
     * @param columnList
     * @return
     */
    public Map<String,StringBuffer> joinParse(List<Map<String,Object>> mapRef,List<PDMColumn> columnList){
        StringBuffer joinTables = new StringBuffer();//连表的表名
        StringBuffer joinTableCols = new StringBuffer();//连表的信息字段
        joinTables.append(columnList.get(0).getTable().getCode().toUpperCase()+",");

        for (PDMColumn c: columnList) {
            for (Map<String, Object> m : mapRef) {
                if ((c.getTable().getCode()+"_"+c.getCode()).equals(m.get("codeName"))) {
                    joinTables.append(m.get("leftCodeName") + ",");
                    joinTableCols.append(c.getTable().getCode() + "." + c.getCode() + "=");
                    joinTableCols.append(m.get("leftCodeName") + "." + m.get("parentTableKey"));
                    joinTableCols.append(",");
                }
            }
        }
        Map<String,StringBuffer> reMap  = new HashMap<>();
        reMap.put("joinTables",joinTables.deleteCharAt(joinTables.length()-1));
        reMap.put("joinTableCols",joinTableCols.toString().equals("") ? new StringBuffer() : joinTableCols.deleteCharAt(joinTableCols.length()-1));
        return reMap;
    }

    /**
     * PDM主外链表的数据封装处理
     * @param pdm
     * @return
     */
    public List<Map<String,Object>> refJoinParse(PDM pdm){
        List<PDMReference> referenceList = pdm.getReferences();
        List<Map<String,Object>> mapRef = new ArrayList<>();
        String sc = "";
        String cc = "";
        int i = 0;
        for (PDMReference r: referenceList) {
            List<PDMReferenceJoin> joinList =r.getJoins();
            for (PDMReferenceJoin j: joinList) {
                Map<String,Object> map = new HashMap<>();
                //将外链字段存入
                String jcode = r.getParentTable().getCode();
                map.put("codeName",j.getChildTable_Col().getTable().getCode()+"_"+j.getChildTable_Col().getCode());
                //重置数字.
                if (!cc.equals(r.getChildTable().getCode())){
                    i = 0 ;
                }
                i++;
                cc = r.getChildTable().getCode();
                map.put("leftCodeName",jcode + i );
                //存入JOIN表表名及主键数据
                map.put("parentTableName",jcode);
                map.put("parentTableKey",j.getParentTable_Col().getCode());
                map.put("parentTableData",j.getParentTable_Col().getTable());
                mapRef.add(map);
            }
        }
        return  mapRef;
    }

    /**
     * GEN CFG file Main
     * cfg文件生成主入口
     * @param fileName
     * @return
     * @throws Exception
     */
    public List<Map<String , Object>> tableParse(String fileName) throws Exception{
        PDM p = pdmParser.pdmParser(fileName);
        List<Map<String,Object>> mapRef = refJoinParse(p);
        List<Map<String , Object>> list = new ArrayList<>();
        for (PDMTable t:p.getTables()) {
            Map<String,StringBuffer> map = joinParse(mapRef,t.getColumns());
            Map<String , Object> m = new HashMap<>();
            m.put("tableName",t.getName());//表名(一般为中文)
            m.put("tableCode",t.getCode());//表Code(一般为英文)
            m.put("columns",selAllTableCol(t));//表内字段
            m.put("joinTable",map.get("joinTables"));//外联表信息
            m.put("joinTableCol",map.get("joinTableCols"));//外联表关系对应字段（与joinTable一一对应）
            list.add(m);
        }
        return list;
    }

    /**
     * 获取全查询表字段
     * @param t    查询表对象
     * @return
     */
    public String selAllTableCol(PDMTable t){
        StringBuffer sb = new StringBuffer();
        for (PDMColumn c : t.getColumns()){
            sb.append(t.getCode());
            sb.append(".");
            sb.append(c.getCode());
            sb.append(" AS ");
            sb.append(t.getCode());
            sb.append("_");
            sb.append(c.getCode());
            sb.append(",");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

    /**
     * GEN BaseDAO Basic cfg
     * BaseDAO数据组配
     * @param fileName
     * @return
     * @throws Exception
     */
    public List<Map<String , Object>> sqlParse(String fileName) throws Exception{
        PDM p = pdmParser.pdmParser(fileName);
        List<Map<String , Object>> list = new ArrayList<>();
        for (PDMTable t:p.getTables()) {
            Map<String , Object> map = new HashMap<>();
            map.put("tableName",t.getName().toUpperCase());
            map.put("tableCode",t.getCode().toUpperCase());
            map.put("selectSQL",selectListSQLUtil());
            map.put("deleteSQL",deleteSQLUtil());
            map.put("selectByOneSQL",selectByOneSQLUtil(t.getCode()));
            map.put("insertSQL",insertSQLUtil(t.getCode()));
            map.put("updSQL",updateSQLUtil());
            map.put("selectOneSQL",selectListSQLUtil());
            map.put("columns",t.getColumns());
            list.add(map);
        }
        return list;
    }
}
