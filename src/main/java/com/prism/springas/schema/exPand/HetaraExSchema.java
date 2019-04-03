package com.prism.springas.schema.exPand;

import com.alibaba.fastjson.JSON;
import com.prism.springas.controller.config.BaseBasicExtends;
import com.prism.springas.schema.BaseSchema;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.cache.CacheClass;
import com.prism.springas.utils.hetara.hetaraEngine;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hetara数据优化引擎业务处理入口
 */
@Service
public class HetaraExSchema extends BaseSchema implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    com.prism.springas.utils.hetara.utils.hetaraInitBase hetaraInitBase;
    @Autowired
    BaseBasicExtends baseBasicExtends;
    @Autowired
    hetaraEngine hetaraEngine;

    //获取Hetara数据
    public String getHetaraEngine(String tableName, sqlEngine sqlEngine) throws Exception {
        //1.init初始化/从cache缓存中调取符合engineKeyCode的hetara规则引擎数据模型
        Object obj = hetaraInitBase.initRuleBase(sqlEngine.getBaseParam("code") + "");
        if (obj != null) {
            Map map = (ConcurrentHashMap) obj;
            List<BasePage> rules = (List<BasePage>) map.get("RULES");

            List<BasePage> nodeData = new ArrayList<>();
            List<BasePage> linkData = new ArrayList<>();
            for (BasePage rule : rules) {
                BasePage node = new BasePage();
                node.put("key", rule.get("ID"));
                node.put("text", rule.get("NAME"));
                node.put("category", "Next");
                nodeData.add(node);

                String nodeToF = "F";//默认为false
                String ttag = rule.get("TTAG") + "";
                String ftag = rule.get("FTAG") + "";
                ttag = ttag.equals("null") ? "N" : ttag;
                ftag = ftag.equals("null") ? "N" : ftag;

                //为True时直接输出构建节点
                //两次循环以判断T和F
                for (int i = 0; i < 2; i++) {
                    nodeToF = i == 0 ? "T" : "F";
                    //二次校验数据准确性
                    if (rule.get(nodeToF + "TAG") != null) {
                        String currentTag = rule.get(nodeToF + "TAG") + "";
                        if (currentTag.equals("E") || currentTag.equals("H") || currentTag.equals("S")) {
                            BasePage nodeT = new BasePage();
                            nodeT.put("key", rule.get("ID") + "-" + nodeToF);
                            nodeT.put("text", currentTag.equals("H") ? "数据预处理后输出最终结果" :
                                    currentTag.equals("S") ? "忽略" : rule.get(nodeToF + "EXE"));
                            nodeT.put("category", "Stop");

                            BasePage linkT = new BasePage();
                            linkT.put("from", rule.get("ID"));
                            linkT.put("to", rule.get("ID") + "-" + nodeToF);
                            linkT.put("text", nodeToF.equals("T") ? "通过" : "未通过");
                            linkData.add(linkT);
                            nodeData.add(nodeT);
                        } else {
                            if (!currentTag.equals("N")) {
                                BasePage linkT = new BasePage();
                                linkT.put("from", rule.get("ID"));
                                linkT.put("to", ttag.equals("R") || ftag.equals("R") ? rule.get(nodeToF + "EXE") :
                                        rule.get(nodeToF + "EXE").toString().split("\\|#\\|")[1]);
                                linkT.put("text", nodeToF.equals("T") ? "通过" : "未通过");
                                linkData.add(linkT);
                            }
                        }
                    }
                }
            }
            Map result = new HashMap();
            result.put("nodeData", nodeData);
            result.put("linkData", linkData.stream().distinct().collect(Collectors.toList()));

            return JSON.toJSONString(result);
        } else {
            return JSON.toJSONString("=====engineKey:当前数据引擎尚未创建");
        }
    }

    //新增引擎节点
    @Transactional(readOnly = false)
    public String insertRuleNode(String tableName, sqlEngine sqlEngine) throws Exception {
        sqlEngine ins = new sqlEngine();
        ins.getAddData("NAME", sqlEngine.getBaseParam("NAME"));
        ins.getAddData("SORT", sqlEngine.getBaseParam("SORT"));
        ins.getAddData("CODE", sqlEngine.getBaseParam("CODE"));
        ins.getAddData("ISP", sqlEngine.getBaseParam("ISP"));
        ins.getAddData("EPS", sqlEngine.getBaseParam("EPS"));
        ins.getAddData("TTAG", sqlEngine.getBaseParam("TTAG"));
        ins.getAddData("TEXE", sqlEngine.getBaseParam("TEXE"));
        ins.getAddData("FTAG", sqlEngine.getBaseParam("FTAG"));
        ins.getAddData("FEXE", sqlEngine.getBaseParam("FEXE"));
        ins.getAddData("EID", sqlEngine.getBaseParam("EID"));
        ins.getAddData("CPTYPE", sqlEngine.getBaseParam("CPTYPE"));
        ins.getAddData("ISNECE", sqlEngine.getBaseParam("ISNECE"));
        ins.getAddData("PID", sqlEngine.getBaseParam("PID"));
        String insert = this.insertData("HETARARULES", ins);
        if (!insert.equals("")) {
            //新增成功,重置缓存
            CacheClass.removeCache("RL-" + sqlEngine.getBaseParam("ECODE"));
        }
        return insert;
    }

    //更新引擎节点
    @Transactional(readOnly = false)
    public String editRuleNode(String tableName, sqlEngine sqlEngine) throws Exception {
        sqlEngine upd = new sqlEngine();
        upd.getUpdateTables("HETARARULES")
                .getUpdateById(sqlEngine.getBaseParam("ID") + "")
                .getUpdateCols("HETARARULES_NAME", sqlEngine.getBaseParam("NAME"))
                .getUpdateCols("HETARARULES_SORT", sqlEngine.getBaseParam("SORT"))
                .getUpdateCols("HETARARULES_CODE", sqlEngine.getBaseParam("CODE"))
                .getUpdateCols("HETARARULES_ISP", sqlEngine.getBaseParam("ISP"))
                .getUpdateCols("HETARARULES_EPS", sqlEngine.getBaseParam("EPS"))
                .getUpdateCols("HETARARULES_TTAG", sqlEngine.getBaseParam("TTAG"))
                .getUpdateCols("HETARARULES_TEXE", sqlEngine.getBaseParam("TEXE"))
                .getUpdateCols("HETARARULES_FEXE", sqlEngine.getBaseParam("FEXE"))
                .getUpdateCols("HETARARULES_FTAG", sqlEngine.getBaseParam("FTAG"))
                .getUpdateCols("HETARARULES_CPTYPE", sqlEngine.getBaseParam("CPTYPE"))
                .getUpdateCols("HETARARULES_ISNECE", sqlEngine.getBaseParam("ISNECE"));
        int updGenNode = this.baseApi.updateData("HETARARULES", upd);
        if (updGenNode > 0) {
            for (int i = 0; i < 2; i++) {
                String tof = i == 0 ? "T" : "F";
                sqlEngine updChild = new sqlEngine();
                updChild.getUpdateTables("HETARARULES");
                String id = "";
                if (sqlEngine.getBaseParam(tof + "TAG") != null) {
                    if (sqlEngine.getBaseParam(tof + "TAG").equals("R")) {
                        id = sqlEngine.getBaseParam(tof + "EXE") + "";
                        if (!this.ruleIsTopNode(id)) {
                            updChild.getUpdateById(id)
                                    .getUpdateCols("HETARARULES_PID", sqlEngine.getBaseParam("ID"));
                            this.baseApi.updateData("HETARARULES", updChild);
                        }
                    } else if (sqlEngine.getBaseParam(tof + "TAG").equals("C")) {
                        id = sqlEngine.getBaseParam(tof + "EXE").toString().split("\\|#\\|")[1];
                        if (!this.ruleIsTopNode(id)) {
                            updChild.getUpdateById(id)
                                    .getUpdateCols("HETARARULES_PID",
                                            sqlEngine.getBaseParam("ID"));
                            this.baseApi.updateData("HETARARULES", updChild);
                        }
                    }
                }
            }
            CacheClass.removeCache("RL-" + sqlEngine.getBaseParam("ECODE"));
        }
        return updGenNode > 0 ? "更新成功" : "更新失败";
    }

    @Transactional(readOnly = false)
    public String delRuleNode(String tableName, sqlEngine sqlEngine) throws Exception {
        String delId = sqlEngine.getBaseParam("ID") + "";
        //判断需删除的数据是否为固定节点(结束)
        boolean isChildNode = delId.contains("-T") || delId.contains("-F");
        int result = 0;
        if (isChildNode) {
            sqlEngine updChild = new sqlEngine();
            updChild.getUpdateTables("HETARARULES")
                    .getUpdateById(delId.split("-")[0])
                    .getUpdateCols("HETARARULES_" + delId.split("-")[1] + "TAG", "N")
                    .getUpdateCols("HETARARULES_" + delId.split("-")[1] + "EXE", " ");
            result = this.baseApi.updateData("HETARARULES", updChild);
        } else {
            sqlEngine del = new sqlEngine();
            del.getDelTables("HETARARULES")
                    .getDeleteById(delId);
            result = this.baseApi.deleteData("HETARARULES", del);
        }
        if (result > 0) {
            //删除成功
            CacheClass.removeCache("RL-" + sqlEngine.getBaseParam("ECODE"));
        }
        return result > 0 ? "删除成功" : "删除失败";
    }

    //获取规则序列号
    public String getNextCount(String tableName, sqlEngine sqlEngine) throws Exception{
        sqlEngine sel = new sqlEngine();
        sel.getSelTable("HETARARULES")
                 .getSelCols("","HETARARULES_SORT")
                 .getWhereAnd("HETARARULES_EID","EQ",sqlEngine.getBaseParam("EID"))
                 .getSort("HETARARULES_SORT","0")
                 .getPage(1,1);
        List<BasePage> basePages = this.baseApi.selectPage("HETARARULES",sel);
        if (basePages.size()>0){
            return JSON.toJSONString(Integer.parseInt(basePages.get(0).get("HETARARULES_SORT")+"") + 1);
        }else{
            return JSON.toJSONString(0);
        }
    }

    //库表数据优化(源生数据优化)
    //tableSource:
    //selTable:
    //selCol:
    //sortColumns:
    //sortTypes:
    //condition:源数据遴选配参
    //engineCode:使用的数据优化引擎CODE编码
    //patchCount:单次最多执行条数上限
    public String useHetaraInDataSource(String tableName, sqlEngine sqlEngine) throws Exception {
        //获取源数据
        sqlEngine dataSource = baseBasicExtends.jsonToSqlEngine(0, JSON.toJSONString(sqlEngine.getBaseParam("condition")));

        String engineCode = sqlEngine.getBaseParam("engineCode") + "";
        //1.必传参数[需查询数据表表名]
        String selTable = sqlEngine.getBaseParam("tableSource") + "";
        String tables = sqlEngine.getBaseParam("selTable") + "";
        String columns = sqlEngine.getBaseParam("selCol") + "";
        dataSource.getSelTable(tables)
                //2.非必传参数[查询指定表全表字段,查询指定表特定字段](如都不传,则视为查询全部)
                .getSelCols(tables == null ? "" : tables, columns == null ? "" : columns);
        //4.是否需要执行排序
        String sortColumns = sqlEngine.getBaseParam("sortColumns") + "";
        String sortTypes = sqlEngine.getBaseParam("sortTypes") + "";
        if (sortColumns != null && !sortColumns.equals("")) {
            dataSource.getSort(sortColumns, sortTypes);
        }

        List<BasePage> sourceList = this.baseApi.selectPage(selTable, dataSource);
        List<BasePage> executeList = new ArrayList<>();
        for (BasePage sourceWait : sourceList) {
            String execute = hetaraEngine.provingEngine(engineCode, sourceWait);
            execute = execute.length() > 1 ? execute.substring(1,execute.length()-1) : "";
            if (!execute.equals("##ruleStopEnding##")) {
                sourceWait.put("TUSER_NAME", sourceWait.get("TUSER_NAME")+"("+execute+")");
                executeList.add(sourceWait);
            }
        }

        Map map = new HashMap();
        map.put("data", executeList);
        map.put("total", executeList.size());
        return JSON.toJSONString(map);
    }

    private boolean ruleIsTopNode(String id) throws Exception {
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("HETARARULES")
                .getSelCols("", "HETARARULES_ISNECE")
                .getSelById(id);
        BasePage basePage = this.baseApi.selectById("HETARARULES", sqlEngine);
        return basePage == null ? false : Integer.parseInt(basePage.get("HETARARULES_ISNECE") + "") == 1 ? true : false;
    }
}
