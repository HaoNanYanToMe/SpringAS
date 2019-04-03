package com.prism.springas.utils.hetara;

import com.alibaba.fastjson.JSON;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.hetara.tools.hetaraCoreTool;
import com.prism.springas.utils.hetara.utils.hetaraInitBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hetara规则引擎
 */
@Component
public class hetaraEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    hetaraInitBase hetaraInitBase;

    //hetara规则引擎
    List<BasePage> rules = null;
    //hetara逻辑配参项
    Map execute = null;

    hetaraCoreTool hetaraCoreTool = new hetaraCoreTool();

    //初始化HetaraEngine
    public hetaraEngine(){
        rules = new ArrayList<>();
        execute = new ConcurrentHashMap<>();
        //默认载入正则表达式处理类
        this.execute.put("reg",new hetaraCoreTool());
    }


    /**
     * 预处理相关引擎数据
     * @param code      需要使用的数据引擎Code
     * @param execute   需要进行处理的数据
     * @return
     * @throws Exception
     */
    public String provingEngine(String code, BasePage execute) throws Exception{
        //1.init初始化/从cache缓存中调取符合engineKeyCode的hetara规则引擎数据模型
        Object obj = hetaraInitBase.initRuleBase(code);
        if (obj != null){
            Map map = (ConcurrentHashMap)obj;
            rules = (List<BasePage>)map.get("RULES");
            //2.对入参数据执行再封装
            this.parseExecuteData(execute);
            //3.对数据进行规则校验
            return JSON.toJSONString(this.provingRules(rules.get(0).getString("ID")));
        }else{
            return JSON.toJSONString("=====engineKey:"+code+",引擎不存在");
        }
    }

    //预处理相关引擎规则数据
    public String provingRules(String ruleId) throws Exception{
        List<BasePage> thisRule = this.rules.stream().filter((BasePage rule) -> rule.getString("ID").equals(ruleId))
                .collect(Collectors.toList());
        if (thisRule.size() == 1){
            boolean code = (boolean)hetaraCoreTool.convertToCode(this.excuteEps(thisRule.get(0).getString("EPS")),this.execute);
            //1.根据code值获取下一步待执行操作标识
            String execTag = thisRule.get(0).getString(code ? "TTAG" : "FTAG");
            //2.根据code值获取下一步待执行操作
            String exec = thisRule.get(0).getString(code ? "TEXE" : "FEXE");
            switch (execTag){
                case "S":
                    //表示终止流程并跳出处理且不输出任何值
                    return "##ruleStopEnding##";
                case "E":
                    //表示全规则流程在此节点结束,结果正常输出
                    return exec;
                case "H":
                    //表示全规则流程在此节点结束,结果根据对应规则进行数据处理
                    return hetaraCoreTool.convertToCode(exec,this.execute).toString();
                case "R":
                    //表示规则流程进入下一节点,exec:id
                    return this.provingRules(exec);
                case "C":
                    //表示规则流程进入下一节点并重置指定参数值
                    //param1:我的值变了哦
                    //param2@:我的值需要按照运算表达式进行处理哦
                    //格式->p1:???,p2@:???||ruleId
                    String[] params = exec.split("\\|#\\|")[0].split(",");
                    for (String param:params) {
                        String key = param.split(":")[0];
                        String value = param.split(":")[1];
                        this.execute.put(key.contains("@") ? key.replaceAll("@","") : key,
                                key.contains("@") ? hetaraCoreTool.convertToCode(value,this.execute).toString() : value);
                    }
                    return this.provingRules(exec.split("\\|#\\|")[1]);
                default:
                    return exec;
            }
        }else{
            return "规则ID:"+ruleId+"不存在或重复,请检查后重试!";
        }
    }

    private String excuteEps(String eps) {
        String[] exeEps = eps.split(",");

        StringBuffer execute = new StringBuffer();
        //标记
        int i = 0;
        for (String e : exeEps) {
            if (!e.trim().isEmpty()) {
                if (!e.trim().equals("")) {
                    String[] evalue = e.trim().split("\\|\\|\\|");
                    execute.append(i == 0 ? "" : evalue[0].equals("0") ? " && " : " || ");
                    String value = evalue[3].replaceAll("，", ",");
                    if (evalue[2].equals("7")) {
                        execute.append(" reg.regMatch('").append(value).append("',").append(evalue[1]).append(")");
                    } else if (evalue[2].equals("8")) {
                        execute.append(" reg.regMatchFind('").append(value).append("',").append(evalue[1]).append(")");
                    } else {
                        if (evalue[2].equals("1")) {
                            execute.append(evalue[1]).append(".equals('").append(value).append("')");
                        } else if (evalue[2].equals("2")) {
                            execute.append(evalue[1]).append(" != ").append(value);
                        } else if (evalue[2].equals("3")) {
                            execute.append(evalue[1]).append(" > ").append(value);
                        } else if (evalue[2].equals("4")) {
                            execute.append(evalue[1]).append(" >= ").append(value);
                        } else if (evalue[2].equals("5")) {
                            execute.append(evalue[1]).append(" < ").append(value);
                        } else if (evalue[2].equals("6")) {
                            execute.append(evalue[1]).append(" <= ").append(value);
                        } else if (evalue[2].equals("0")) {
                            execute.append(evalue[1]).append(".contains('").append(value).append("')");
                        }else if (evalue[2].equals("9")) {
                            execute.append(evalue[1]).append(" == ").append(value);
                        }
                    }
                    i++;
                }
            }
        }
        return execute.toString();
    }
    private Map parseExecuteData(BasePage execute){
        //通过匿名函数lambda对execute进行再封装
        execute.keySet().forEach(key -> this.execute.put(key,execute.get(key)));
        return this.execute;
    }
}
