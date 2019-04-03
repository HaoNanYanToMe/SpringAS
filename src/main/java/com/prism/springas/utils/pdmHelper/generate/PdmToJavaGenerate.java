package com.prism.springas.utils.pdmHelper.generate;

import com.prism.springas.utils.pdmHelper.entites.PDMColumn;
import com.prism.springas.utils.pdmHelper.tools.PDMUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
    -pdm生成底层Java源码工具类
*/
public class PdmToJavaGenerate {

    //项目包名,yml文件中配置
    private String packageName= "com.prism.springas";

    public String getPackageName() {
        return packageName;
    }

    //项目根地址
    private String useDir = System.getProperty("user.dir");

    public String getUseDir() {
        return useDir;
    }

    //封装文件头Header
    public StringBuffer getSbHeader(String header){
        StringBuffer sbHeader = new StringBuffer();
        sbHeader.append("package ");
        sbHeader.append(getPackageName());
        sbHeader.append(".");
        sbHeader.append(header);
        sbHeader.append(";");
        sbHeader.append("\n\n");
        sbHeader.append("import ");
        sbHeader.append(getPackageName());
        sbHeader.append(".utils.BasePage;\n");
        sbHeader.append("import org.apache.ibatis.annotations.*;\n");
        sbHeader.append("import org.springframework.stereotype.Component;\n");
        sbHeader.append("\n\n");
        sbHeader.append("import java.util.List;");
        return sbHeader;
    }
    //……执行方法……

    /**
     * 生成SQL
     * @param fileName
     * @throws Exception
     */
    public void baseSQLGenerate(String fileName) throws  Exception {
        PDMUtils pdmUtils = new PDMUtils();
        //获取数据
        List<Map<String, Object>> fileData = pdmUtils.sqlParse(fileName);

        //文件导出地址
        FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") +
                "\\src\\test\\java\\generate\\sql"+
                "\\sql"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".sql");

        System.out.println("========================");
        System.out.println("SQL生成开始……");
        try {
            StringBuffer sb = new StringBuffer();

            for (Map<String,Object> m : fileData){
                sb.append("#");
                sb.append(m.get("tableName")+"("+m.get("tableCode")+")");
                sb.append("\n");
                sb.append("create table ");
                sb.append(m.get("tableCode"));
                sb.append("(\n");

                StringBuffer pk = new StringBuffer();
                for (PDMColumn c : (List<PDMColumn>)m.get("columns")){
                    sb.append("\t"+c.getCode().toUpperCase());
                    sb.append("\t\t"+c.getDataType());
                    sb.append("\t\t"+(c.getMandatory() == 1 ?"not null":"null"));
                    sb.append("  COMMENT \'");
                    sb.append(c.getName());
//                    sb.append(c.getComment()==null?"":"->"+c.getComment());
                    sb.append("\',\n");
                }
                sb.append("\n\tconstraint ");
                sb.append("PK_"+m.get("tableCode"));
                sb.append(" primary key ");
                sb.append(" clustered(ID)\n)");
                sb.append("  COMMENT =\'");
                sb.append(m.get("tableName"));
                sb.append("\';\n");
            }
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();

            System.out.println("success");
            System.out.println("========================");
        }catch (IOException e) {

            System.out.println("error");
            System.out.println("========================");
        }
    }

    /**
     * 生成BaseDAO
     * @param fileName
     * @throws Exception
     */
    public void baseDaoGenerate(String fileName) throws  Exception{
        PDMUtils pdmUtils = new PDMUtils();
        //获取数据
        List<Map<String,Object>> fileData = pdmUtils.sqlParse(fileName);

        //文件导出地址
        FileWriter fileWriter = new FileWriter(getUseDir()+
                "\\src\\test\\java\\generate\\dao"+
                "\\BaseDAO.java");

        System.out.println("========================");
        System.out.println("BASEDAO生成开始……");
        try {
            StringBuffer sb = new StringBuffer();
            //文件头
            sb.append(getSbHeader("dao"));
            sb.append("\n/*");
            sb.append("\n\t数据持久化-基本简单用例[增删改查]操作");
            sb.append("*/");
            sb.append("\n");
            sb.append("@Mapper\n");
            sb.append("@Component(\"baseDAO\")\n");
            sb.append("public interface BaseDAO {\n");

            //生成内容
            for (Map<String,Object> m:fileData) {
                sb.append("\n");
                sb.append("\t/*\n\t\t==========================================================");
                sb.append("\n\t\t======>>表名信息："+m.get("tableName")+"("+m.get("tableCode")+")");
                sb.append("\n\t\t======>>生成时间:"+new Date());
                sb.append("\n\t\t======>>编辑人:@YanHaoNan");
                sb.append("\n\t\t======>>备注:生成表"+m.get("tableCode"));
                sb.append("\n\t\t==========================================================\n\t*/");

                /*
                    全部查询
                */
                sb.append("\n\n");
                sb.append("\t/*\n");
                sb.append("\t\t->");
                sb.append(m.get("tableName")+"表 -- 根据条件查询数据（含分页及自选指定Columns）\n\t");
                sb.append("*/\n");
                sb.append("\t@Select(\"");
                sb.append(m.get("selectSQL"));
                sb.append("\")\n\t");
                sb.append("List<BasePage> ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_SelectPage");
                sb.append("(@Param(\"bp\") BasePage bp);");
                sb.append("\n");


                 /*
                    全部查询
                */
                sb.append("\n\n");
                sb.append("\t/*\n");
                sb.append("\t\t->");
                sb.append(m.get("tableName")+"表 -- 根据条件查询数据（根据ID指定查询）\n\t");
                sb.append("*/\n");
                sb.append("\t@Select(\"");
                sb.append(m.get("selectByOneSQL"));
                sb.append("\")\n\t");
                sb.append("BasePage ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_SelectByOne");
                sb.append("(@Param(\"bp\") BasePage bp);");
                sb.append("\n");

                /*
                    新增
                * */
                sb.append("\n\n");
                sb.append("\t/*\n");
                sb.append("\t\t->");
                sb.append(m.get("tableName")+"表 -- 创建数据 \n\t");
                sb.append("*/\n");
                sb.append("\t@Insert(\"");
                sb.append(m.get("insertSQL"));
                sb.append("\")\n\t");
                sb.append("int ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_InsertData");
                sb.append("(@Param(\"bp\") BasePage bp);");
                sb.append("\n");

                /*
                    修改
                * */
                sb.append("\n\n");
                sb.append("\t/*\n");
                sb.append("\t\t->");
                sb.append(m.get("tableName")+"表 -- 编辑修改数据 \n\t");
                sb.append("*/\n");
                sb.append("\t@Update(\"");
                sb.append(m.get("updSQL"));
                sb.append("\")\n\t");
                sb.append("int ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_UpdateData");
                sb.append("(@Param(\"bp\") BasePage bp);");
                sb.append("\n");

                /*
                    删除
                * */
                sb.append("\n\n");
                sb.append("\t/*\n");
                sb.append("\t\t->");
                sb.append(m.get("tableName")+"表 -- 删除数据（暴力删除）\n\t");
                sb.append("*/\n");
                sb.append("\t@Delete(\"");
                sb.append(m.get("deleteSQL"));
                sb.append("\")\n\t");
                sb.append("int ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_DeleteData");
                sb.append("(@Param(\"bp\") BasePage bp);");
                sb.append("\n");
            }
            sb.append("\n}");
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();

            System.out.println("success");
            System.out.println("========================");
        }catch (IOException e) {
            System.out.println("error");
            System.out.println("========================");
        }
    }

    public void baseCfgGenerate(String fileName) throws  Exception {
        PDMUtils pdmUtils = new PDMUtils();
        //获取数据
        List<Map<String, Object>> fileData = pdmUtils.tableParse(fileName);

        //文件导出地址
        FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") +
                "\\src\\test\\java\\generate\\config"+
                "\\BaseTableCfg.java");

        System.out.println("========================");
        System.out.println("CFG生成开始……");
        try {
            StringBuffer sb = new StringBuffer();
            //文件头
            sb.append(getSbHeader("utils"));
            sb.append("\n/*");
            sb.append("\n\t数据全查询自动生成配置");
            sb.append("*/\n");
            sb.append("@Component(\"baseTableCfg\")\n");
            sb.append("public class BaseTableCfg {\n");
            //生成内容
            for (Map<String,Object> m:fileData) {
                sb.append("\n\t\t//");
                sb.append("=>"+m.get("tableName"));
                sb.append("("+m.get("tableCode")+")数据全查询字段");
                sb.append("\n\t\t//");
                sb.append("=>@YanHaoNan,"+new Date()+","+"原生生成代码");
                sb.append("\n\t\t//");
                sb.append("=>对应关联表："+m.get("joinTable").toString());
                sb.append("\n");
                sb.append("\t\tprivate String ");
                sb.append(m.get("tableCode").toString().toUpperCase());
                sb.append(" = \"");
                sb.append(m.get("columns"));
                sb.append("\";\n");

                sb.append("\n\t\t//");
                sb.append("=>对应关联表字段："+m.get("joinTableCol").toString());
                sb.append("\n");
                sb.append("\t\tprivate String ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append(" = \"");
                sb.append(m.get("joinTableCol"));
                sb.append("\";\n");
                //getter
                sb.append("\n\t\tpublic String get");
                sb.append(m.get("tableCode").toString().toUpperCase());
                sb.append("(){\treturn ");
                sb.append(m.get("tableCode").toString().toUpperCase());
                sb.append("; }\n");
                sb.append("\t\tpublic String get");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append("(){\treturn ");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append("; }\n");
                //setter
                sb.append("\n\t\tpublic void set");
                sb.append(m.get("tableCode").toString().toUpperCase());
                sb.append("(String  "+m.get("tableCode").toString().toUpperCase()+"){\t this.");
                sb.append(m.get("tableCode").toString().toUpperCase());
                sb.append(" = " + m.get("tableCode").toString().toUpperCase());
                sb.append("; }\n");
                sb.append("\t\tpublic void set");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append("(String  "+m.get("tableCode").toString().toUpperCase()+"_JOIN"+"){\t this.");
                sb.append(m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append(" = " + m.get("tableCode").toString().toUpperCase()+"_JOIN");
                sb.append("; }\n");
            }
            sb.append("\n}");
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();

            System.out.println("success");
            System.out.println("========================");
        }catch(IOException e) {
            System.out.println("error");
            System.out.println("========================");
        }
    }

}
