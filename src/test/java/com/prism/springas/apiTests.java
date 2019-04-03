package com.prism.springas;

import com.alibaba.fastjson.JSON;
import com.prism.springas.api.BaseApi;
import com.prism.springas.controller.BaseController;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.echars.eCharsEngine;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * API层测试用例
 * 		--为Schema业务逻辑处理层提供底层数据支持
 * 		----接口使用须知：
 * 			**.API层接口仅暴露给schema层做内部的业务处理
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class apiTests {

	@Autowired
	BaseApi baseApi;

	@Autowired
	BaseController baseController;
	@Test
	public void contextLoads() throws Exception{
//		System.out.println(baseController.exFunction("sysUser",
//				"loginSystem",1,"{'name':'yanhaonan@xdf.cn','pwd':'505b817da68d197e50604ef433e9cd2a'}"));
		//0.insertFetch--表数据批量(可用作测试类的数据初始化)
		//...fetchSize:批量初始数据条数
//		System.out.println(insertFetch(10));
		//1.selectList
		System.out.println(selectList());
		//2.selectById
//		System.out.println(selectById());
		//3.insertData
//		String insertId = insertData();
//		System.out.println(insertId);
		//4.deleteData
//		System.out.println(deleteData(insertId));
		//5.updateData
//		System.out.println(updateData());
		//6.selectEx
//		System.out.println(selectEx());
	}

	//Test:selectBase-源生表基础多条件查询
	public String selectList() throws Exception{
		sqlEngine sqlEngine = new sqlEngine();
		sqlEngine.getSelTable("TUSER,DEPTINFO1")
				.getSelCols("","DEPTINFO1_NAME,TUSER_CREATETIME,COUNT||DEPTINFO1_NAME,COUNT||TUSER_CREATETIME")
				.getWhereAnd("TUSER_DEPT","NEQ","NULL");
//		sqlEngine.getSelTable("SPORTSINFO")
//////				 .getSelCols("SPORTSINFO") //查询全表字段,getSelCols(tableS)
////				 .getSelCols("","SPORTSINFO_SNAME#MYNAME") //查询全表字段及其他需展示字段,getSelCols(tables,cols)
//////				 .getSort("","")//数据的排序方式
//////				 .getPage(1,10)//数据分页(示例：第1页,每页10条)
////				 .getWhereAnd("SPORTSINFO_VERSION","EQ","0")//条件-and(和)
////				 .getWhereOr("SPORTSINFO_CREATETIME","BET",
////						 "2019-01-04 17:30:00,2019-01-04 17:45:00");//条件-or(或)
		eCharsEngine eCharsEngine = new eCharsEngine(0,baseApi.selectPage("TUSER",sqlEngine));
//		return eCharsEngine.basicPie("DEPTINFO1_NAME","G_DEPTINFO1_NAME").toPasrseDoughnut("test","部门").getEcharsData();
//		return eCharsEngine.basicLine("DEPTINFO1_NAME","G_DEPTINFO1_NAME").toParseBasicChars("test").getEcharsData();
		return eCharsEngine.moreLine("TUSER_CREATETIME","G_DEPTINFO1_NAME","DEPTINFO1_NAME").toParseMoreChars("test").getEcharsData();
	}

	//Test:selectById-源生表根据ID进行指定性查询
	//...使用selectList正确入参也可以实现本接口的功能,*但须注意两个接口的返回值
	//selectList:List<BasePage>
	//selectById:BasePage
	public String selectById() throws Exception{
		sqlEngine sqlEngine = new sqlEngine();
		sqlEngine.getSelTable("SPORTSINFO,TEACHERINFO")//允许连表(须在BaseTable中有指定匹配项)
				 .getSelCols("SPORTSINFO","TEACHERINFO_NAME#TCNAME")//与selectList使用方式一致
				 .getSelById("50e8d893bfc64ff4b85c5a4aedbb68d9");//所需查询的表主键ID值
		return JSON.toJSONString(this.baseApi.selectById("SPORTSINFO",sqlEngine));
	}

	//Test:insertData-源生表数据新增(单条)
	//--*:新增时字段值不需要标注表名!详参示例代码
	public String insertData(){
		sqlEngine sqlEngine = new sqlEngine();
		sqlEngine.getAddData("EMAIL","yanhaonan@xdf.cn");//需要插入的字段及字段值
//				 .getAddData("TID","4385bfa8a621403eb2f5758925b655cd");//需要插入的字段及字段值
		return this.baseApi.insertData("SYSUSER",sqlEngine);
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
		return JSON.toJSONString(this.baseApi.deleteData("SPORTSINFO",sqlEngine));
	}

	//Test:updateData--源生表更新(单线程环境)
	public String updateData() throws Exception{
		sqlEngine sqlEngine = new sqlEngine();
		sqlEngine.getUpdateTables("SPORTSINFO")
				 .getUpdateCols("SPORTSINFO_SNAME","更新数据")
				 .getUpdCurrentVersion("SPORTSINFO",0)//
				 .getUpdateWhereAnd("SPORTSINFO_VERSION","GTEQ","2");//基本陪参方式与getWhereAnd/getWhereOr一致(示例:SPORTSINFO_VERSION 大于等于 8的所有数据则执行更新)
//				 .getUpdateWhereOr("SPORTSINFO_VERSION","GTEQ","2");
//				 .getUpdateById("50e8d893bfc64ff4b85c5a4aedbb68d9");//需要执行更新的数据主键ID值,若使用了getUpdateWhereAnd/getUpdateWhereOr请跳过此项配参
		return JSON.toJSONString(this.baseApi.updateData("SPORTSINFO",sqlEngine));
	}


	//Test:insertFetch-源生表数据批量新增
	//.....此接口适用于大批量数据导入,可大幅提升insert语句的执行效率
	//.....建议批次新增数量每次不大于1000(根据硬件内存配置自行判断即可)
	public String insertFetch(int fetchSize) throws Exception{
		sqlEngine sqlEngine = new sqlEngine();
		List<BasePage> list = new ArrayList<>();

		//批量数据List,基本配参格式请参照本示例
		//--->List<BasePage>/BasePage
		for (int i = 0;i<fetchSize;i++){
			BasePage b = new BasePage();
			b.put("SNAME","数据批量测试"+i);
			list.add(b);
		}

		sqlEngine.getAddFetchCols("SNAME")//需新增的数据列,字段顺序不要求,建议配置此项参数时,按照BasePage封装对象的KEY值由上至下进行字段排序
										 //---->*数据字段不需要加表名,直接使用字段值即可！
				 .getAddFetchVals(list);//数据配参List<BasePage>对象
		return JSON.toJSONString(this.baseApi.insertFetchData("SPORTSINFO",sqlEngine));
	}

	//Test:selectEx-业务逻辑拓展方法
	public String selectEx() throws Exception{
		sqlEngine sqlEngine = new sqlEngine();
		sqlEngine.getParamEx("name","测试批量2");//EXDAO中需要的入参项配置:详参dao.testExDAO(*根据自身拓展业务的需求自行设定参数key值即可)
		//tableName:BaseCoreCfg中配置的拓展表Key,如test
		//functionName:exDao中根据自身业务新建的方法名称,如getName
		return JSON.toJSONString(this.baseApi.selectExPage("test","getName",sqlEngine));
	}
}

