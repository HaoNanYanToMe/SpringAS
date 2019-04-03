package com.prism.springas;

import com.alibaba.fastjson.JSON;
import com.prism.springas.schema.BaseSchema;
import com.prism.springas.schema.exPand.SysMenuExSchema;
import com.prism.springas.schema.exPand.SysUserExSchema;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SCHEMA层测试用例
 *          ---*BaseSchema:针对BaseApi的上层封装提供类
 *          ---*xxExSchema:针对其他拓展业务逻辑的数据封装提供类
 *          .....schema层会将DAO回传的数据进行JSON化处理后返回给controller
 *          --为Controller层提供业务逻辑支持
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class schemaTests {

    @Autowired
    BaseSchema baseSchema;

    @Autowired
    SysMenuExSchema sysMenuExSchema;
    @Autowired
    SysUserExSchema sysUserExSchema;

    @Test
    public void contextLoads() throws Exception{
        System.out.println(testMenu());
        //0.testSchema
//        System.out.println(testSchema());
        //1.selectList
//        System.out.println(selectList());
//        //2.selectById
//        System.out.println(selectById());
//        //3.insertData
//        String insertId = insertData();
//        System.out.println(insertId);
//        //4.deleteData
//        System.out.println(deleteData(JSON.parse(insertId).toString()));
//        //5.updateData
//        System.out.println(updateData());
//        //6.selectEx
//        System.out.println(selectEx());
        //----ps:insertFetch方法不对controller层直接提供,若需使用,请在对应的exSchema中进行调用
    }

    public String testMenu() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getParamEx("uid","4d098ae060b54b1baf0d6d8d77b39563");
        return sysUserExSchema.getRouter("",sqlEngine);
    }

    //Test:selectList-源生表基础多条件查询
    //.....使用本方法会回传当前数据总条数字段(total)
    public String selectList() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("SYSUSER,SYSROLE1")
//				 .getSelCols("SPORTSINFO") //查询全表字段,getSelCols(tableS)
                .getSelCols("SYSUSER","SYSROLE1_NAME") //查询全表字段及其他需展示字段,getSelCols(tables,cols)
//				 .getSort("","")//数据的排序方式
                .getPage(1,10);//数据分页(示例：第1页,每页10条)
//                .getWhereAnd("SPORTSINFO_VERSION","EQ","0")//条件-and(和)
//                .getWhereOr("SPORTSINFO_CREATETIME","BET",
//                        "2019-01-04 17:30:00,2019-01-04 17:45:00");//条件-or(或)
        return this.baseSchema.selectPage("SYSUSER",sqlEngine);
    }

    //Test:selectById--源生表根据ID进行指定性查询(与BaseApi调用方式一致)

    public String selectById() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("SPORTSINFO,TEACHERINFO")//允许连表(须在BaseTable中有指定匹配项)
                .getSelCols("SPORTSINFO","TEACHERINFO_NAME#TCNAME")//与selectList使用方式一致
                .getSelById("50e8d893bfc64ff4b85c5a4aedbb68d9");//所需查询的表主键ID值
        return this.baseSchema.selectById("SPORTSINFO",sqlEngine);
    }

    //Test:insertData-源生表数据新增(单条)
    //--*:新增时字段值不需要标注表名!详参示例代码
    public String insertData(){
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getAddData("NAME","师生关系组")//需要插入的字段及字段值
                 .getAddData("PID","358e50ebe6d346008244d06a59ac2030")//需要插入的字段及字段值
                 .getAddData("ISLOCK","0")//需要插入的字段及字段值
                 .getAddData("ISTOP","0");//需要插入的字段及字段值
        return this.baseSchema.insertData("SYSROLE",sqlEngine);
    }

    //Test:deleteData-源生表数据删除
    //--*:如需使用条件删除,使用方式及注意事项请详参示例代码
    public String deleteData(String delId) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getDelTables("SPORTSINFO")//需要执行删除的数据表(如为多表联合删除,表名请以','分隔,如T1,T2...)
//				 .getNeedDelTable("SPORTSINFO")//需要执行删除的数据表,适用于多表联合删除(*单表可以跳过此步)
//				 .getWhereAnd("SPORTSINFO_VERSION","GTEQ","8");//执行数据删除的条件:(SPORTSINFO_VERSION 大于等于 8)--则执行删除
//														//与select查询配参一致(*注意:使用时,表名不用使用BaseTableCfg的连表指导值,直接使用原表名即可,如TYPE)
//														//如使用了getDeleteById,则跳过此配参项即可
                .getDeleteById(delId);//需要执行删除的数据主键ID值,若使用了getWhereAnd/getWhereOr请跳过此项配参
        return this.baseSchema.deleteData("SPORTSINFO",sqlEngine);
    }

    //Test:updateData--源生表更新(单线程环境)
    public String updateData() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getUpdateTables("SPORTSINFO")//需要执行更新的数据表
                .getUpdateCols("SPORTSINFO_SNAME","更新数据")//需要执行更新的数据字段
                .getUpdCurrentVersion("SPORTSINFO",0)//当前数据版本(version-乐观锁判断):*注意:在使用getUpdateWhereAnd/or时,乐观锁将不会生效!
//                .getUpdateWhereAnd("SPORTSINFO_VERSION","GTEQ","2");//基本陪参方式与getWhereAnd/getWhereOr一致(示例:SPORTSINFO_VERSION 大于等于 8的所有数据则执行更新)
//				 .getUpdateWhereOr("SPORTSINFO_VERSION","GTEQ","2");
				 .getUpdateById("50e8d893bfc64ff4b85c5a4aedbb68d9");//需要执行更新的数据主键ID值,若使用了getUpdateWhereAnd/getUpdateWhereOr请跳过此项配参
        return JSON.toJSONString(this.baseSchema.updateData("SPORTSINFO",sqlEngine));
    }

    //Test:selectEx-业务逻辑拓展方法
    public String selectEx() throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getParamEx("name","测试批量2");//EXDAO中需要的入参项配置:详参dao.testExDAO(*根据自身拓展业务的需求自行设定参数key值即可)
        //tableName:BaseCoreCfg中配置的拓展表Key,如test
        //functionName:exDao中根据自身业务新建的方法名称,如getName
        return JSON.toJSONString(this.baseSchema.selectExPage("test","getName",sqlEngine));
    }
}
