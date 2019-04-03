package com.prism.springas.utils;

import org.springframework.stereotype.Component;

/*
	数据全查询自动生成配置*/
@Component("baseTableCfg")
public class BaseTableCfg {

		//=>Hetara规则引擎表(HETARAENGINE)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：HETARAENGINE,HETARATYPE1
		private String HETARAENGINE = "HETARAENGINE.ID AS HETARAENGINE_ID,HETARAENGINE.VERSION AS HETARAENGINE_VERSION,HETARAENGINE.ISDELETE AS HETARAENGINE_ISDELETE,HETARAENGINE.DATETIME AS HETARAENGINE_DATETIME,HETARAENGINE.CREATETIME AS HETARAENGINE_CREATETIME,HETARAENGINE.CODE AS HETARAENGINE_CODE,HETARAENGINE.NAME AS HETARAENGINE_NAME,HETARAENGINE.NOTE AS HETARAENGINE_NOTE,HETARAENGINE.TID AS HETARAENGINE_TID";

		//=>对应关联表字段：HETARAENGINE.TID=HETARATYPE1.ID
		private String HETARAENGINE_JOIN = "HETARAENGINE.TID=HETARATYPE1.ID";

		public String getHETARAENGINE(){	return HETARAENGINE; }
		public String getHETARAENGINE_JOIN(){	return HETARAENGINE_JOIN; }

		public void setHETARAENGINE(String  HETARAENGINE){	 this.HETARAENGINE = HETARAENGINE; }
		public void setHETARAENGINE_JOIN(String  HETARAENGINE_JOIN){	 this.HETARAENGINE_JOIN = HETARAENGINE_JOIN; }

		//=>菜单信息管理表(SYSMENU)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：SYSMENU,SYSMENU1
		private String SYSMENU = "SYSMENU.ID AS SYSMENU_ID,SYSMENU.VERSION AS SYSMENU_VERSION,SYSMENU.ISDELETE AS SYSMENU_ISDELETE,SYSMENU.CREATETIME AS SYSMENU_CREATETIME,SYSMENU.NAME AS SYSMENU_NAME,SYSMENU.CONTENT AS SYSMENU_CONTENT,SYSMENU.ICON AS SYSMENU_ICON,SYSMENU.PID AS SYSMENU_PID,SYSMENU.ISLOCKED AS SYSMENU_ISLOCKED,SYSMENU.CODE AS SYSMENU_CODE,SYSMENU.PATH AS SYSMENU_PATH,SYSMENU.TITLE AS SYSMENU_TITLE,SYSMENU.COMPONENT AS SYSMENU_COMPONENT,SYSMENU.ISPARENT AS SYSMENU_ISPARENT,SYSMENU.SORT AS SYSMENU_SORT,SYSMENU.DATETIME AS SYSMENU_DATETIME";

		//=>对应关联表字段：SYSMENU.PID=SYSMENU1.ID
		private String SYSMENU_JOIN = "SYSMENU.PID=SYSMENU1.ID";

		public String getSYSMENU(){	return SYSMENU; }
		public String getSYSMENU_JOIN(){	return SYSMENU_JOIN; }

		public void setSYSMENU(String  SYSMENU){	 this.SYSMENU = SYSMENU; }
		public void setSYSMENU_JOIN(String  SYSMENU_JOIN){	 this.SYSMENU_JOIN = SYSMENU_JOIN; }

		//=>系统用户表(SYSUSER)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：SYSUSER,SYSROLE1
		private String SYSUSER = "SYSUSER.ID AS SYSUSER_ID,SYSUSER.VERSION AS SYSUSER_VERSION,SYSUSER.ISDELETE AS SYSUSER_ISDELETE,SYSUSER.DATETIME AS SYSUSER_DATETIME,SYSUSER.CREATETIME AS SYSUSER_CREATETIME,SYSUSER.EMAIL AS SYSUSER_EMAIL,SYSUSER.PASSWORD AS SYSUSER_PASSWORD,SYSUSER.NAME AS SYSUSER_NAME,SYSUSER.RID AS SYSUSER_RID,SYSUSER.PHONE AS SYSUSER_PHONE,SYSUSER.MOBILE AS SYSUSER_MOBILE,SYSUSER.LASTIP AS SYSUSER_LASTIP,SYSUSER.TYPE AS SYSUSER_TYPE,SYSUSER.AVATAR AS SYSUSER_AVATAR,SYSUSER.NOTE AS SYSUSER_NOTE,SYSUSER.OPENID AS SYSUSER_OPENID";

		//=>对应关联表字段：SYSUSER.RID=SYSROLE1.ID
		private String SYSUSER_JOIN = "SYSUSER.RID=SYSROLE1.ID";

		public String getSYSUSER(){	return SYSUSER; }
		public String getSYSUSER_JOIN(){	return SYSUSER_JOIN; }

		public void setSYSUSER(String  SYSUSER){	 this.SYSUSER = SYSUSER; }
		public void setSYSUSER_JOIN(String  SYSUSER_JOIN){	 this.SYSUSER_JOIN = SYSUSER_JOIN; }

		//=>用户权限信息管理表(SYSROLE)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：SYSROLE,SYSROLE1
		private String SYSROLE = "SYSROLE.ID AS SYSROLE_ID,SYSROLE.VERSION AS SYSROLE_VERSION,SYSROLE.ISDELETE AS SYSROLE_ISDELETE,SYSROLE.DATETIME AS SYSROLE_DATETIME,SYSROLE.CREATETIME AS SYSROLE_CREATETIME,SYSROLE.NAME AS SYSROLE_NAME,SYSROLE.NOTE AS SYSROLE_NOTE,SYSROLE.ICON AS SYSROLE_ICON,SYSROLE.ISLOCK AS SYSROLE_ISLOCK,SYSROLE.PID AS SYSROLE_PID,SYSROLE.ISTOP AS SYSROLE_ISTOP,SYSROLE.SORT AS SYSROLE_SORT";

		//=>对应关联表字段：SYSROLE.PID=SYSROLE1.ID
		private String SYSROLE_JOIN = "SYSROLE.PID=SYSROLE1.ID";

		public String getSYSROLE(){	return SYSROLE; }
		public String getSYSROLE_JOIN(){	return SYSROLE_JOIN; }

		public void setSYSROLE(String  SYSROLE){	 this.SYSROLE = SYSROLE; }
		public void setSYSROLE_JOIN(String  SYSROLE_JOIN){	 this.SYSROLE_JOIN = SYSROLE_JOIN; }

		//=>菜单及权限信息关联表(SYSROLEMENU)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：SYSROLEMENU,SYSROLE2,SYSMENU1
		private String SYSROLEMENU = "SYSROLEMENU.ID AS SYSROLEMENU_ID,SYSROLEMENU.VERSION AS SYSROLEMENU_VERSION,SYSROLEMENU.ISDELETE AS SYSROLEMENU_ISDELETE,SYSROLEMENU.DATETIME AS SYSROLEMENU_DATETIME,SYSROLEMENU.CREATETIME AS SYSROLEMENU_CREATETIME,SYSROLEMENU.RID AS SYSROLEMENU_RID,SYSROLEMENU.ADDS AS SYSROLEMENU_ADDS,SYSROLEMENU.DEL AS SYSROLEMENU_DEL,SYSROLEMENU.EDIT AS SYSROLEMENU_EDIT,SYSROLEMENU.SEL AS SYSROLEMENU_SEL,SYSROLEMENU.EXPORTS AS SYSROLEMENU_EXPORTS,SYSROLEMENU.SORT AS SYSROLEMENU_SORT,SYSROLEMENU.MID AS SYSROLEMENU_MID";

		//=>对应关联表字段：SYSROLEMENU.RID=SYSROLE2.ID,SYSROLEMENU.MID=SYSMENU1.ID
		private String SYSROLEMENU_JOIN = "SYSROLEMENU.RID=SYSROLE2.ID,SYSROLEMENU.MID=SYSMENU1.ID";

		public String getSYSROLEMENU(){	return SYSROLEMENU; }
		public String getSYSROLEMENU_JOIN(){	return SYSROLEMENU_JOIN; }

		public void setSYSROLEMENU(String  SYSROLEMENU){	 this.SYSROLEMENU = SYSROLEMENU; }
		public void setSYSROLEMENU_JOIN(String  SYSROLEMENU_JOIN){	 this.SYSROLEMENU_JOIN = SYSROLEMENU_JOIN; }

		//=>数据表仓库(TABLEFOLDER)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：TABLEFOLDER
		private String TABLEFOLDER = "TABLEFOLDER.ID AS TABLEFOLDER_ID,TABLEFOLDER.VERSION AS TABLEFOLDER_VERSION,TABLEFOLDER.ISDELETE AS TABLEFOLDER_ISDELETE,TABLEFOLDER.DATETIME AS TABLEFOLDER_DATETIME,TABLEFOLDER.CREATETIME AS TABLEFOLDER_CREATETIME,TABLEFOLDER.CODE AS TABLEFOLDER_CODE,TABLEFOLDER.NAME AS TABLEFOLDER_NAME,TABLEFOLDER.NOTE AS TABLEFOLDER_NOTE";

		//=>对应关联表字段：
		private String TABLEFOLDER_JOIN = "";

		public String getTABLEFOLDER(){	return TABLEFOLDER; }
		public String getTABLEFOLDER_JOIN(){	return TABLEFOLDER_JOIN; }

		public void setTABLEFOLDER(String  TABLEFOLDER){	 this.TABLEFOLDER = TABLEFOLDER; }
		public void setTABLEFOLDER_JOIN(String  TABLEFOLDER_JOIN){	 this.TABLEFOLDER_JOIN = TABLEFOLDER_JOIN; }

		//=>Hetara规则表(HETARARULES)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：HETARARULES,HETARAENGINE1,HETARARULES2
		private String HETARARULES = "HETARARULES.ID AS HETARARULES_ID,HETARARULES.VERSION AS HETARARULES_VERSION,HETARARULES.ISDELETE AS HETARARULES_ISDELETE,HETARARULES.DATETIME AS HETARARULES_DATETIME,HETARARULES.CREATETIME AS HETARARULES_CREATETIME,HETARARULES.CODE AS HETARARULES_CODE,HETARARULES.NAME AS HETARARULES_NAME,HETARARULES.SORT AS HETARARULES_SORT,HETARARULES.EID AS HETARARULES_EID,HETARARULES.ISP AS HETARARULES_ISP,HETARARULES.EPS AS HETARARULES_EPS,HETARARULES.TTAG AS HETARARULES_TTAG,HETARARULES.TEXE AS HETARARULES_TEXE,HETARARULES.FTAG AS HETARARULES_FTAG,HETARARULES.FEXE AS HETARARULES_FEXE,HETARARULES.PID AS HETARARULES_PID,HETARARULES.CPTYPE AS HETARARULES_CPTYPE,HETARARULES.ISNECE AS HETARARULES_ISNECE";

		//=>对应关联表字段：HETARARULES.EID=HETARAENGINE1.ID,HETARARULES.PID=HETARARULES2.ID
		private String HETARARULES_JOIN = "HETARARULES.EID=HETARAENGINE1.ID,HETARARULES.PID=HETARARULES2.ID";

		public String getHETARARULES(){	return HETARARULES; }
		public String getHETARARULES_JOIN(){	return HETARARULES_JOIN; }

		public void setHETARARULES(String  HETARARULES){	 this.HETARARULES = HETARARULES; }
		public void setHETARARULES_JOIN(String  HETARARULES_JOIN){	 this.HETARARULES_JOIN = HETARARULES_JOIN; }

		//=>教职员工信息管理表(TUSER)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：TUSER,DEPTINFO1,SYSUSER2
		private String TUSER = "TUSER.ID AS TUSER_ID,TUSER.VERSION AS TUSER_VERSION,TUSER.ISDELETE AS TUSER_ISDELETE,TUSER.DATETIME AS TUSER_DATETIME,TUSER.CREATETIME AS TUSER_CREATETIME,TUSER.NAME AS TUSER_NAME,TUSER.SEX AS TUSER_SEX,TUSER.EMAIL AS TUSER_EMAIL,TUSER.TELPHONE AS TUSER_TELPHONE,TUSER.IDCARD AS TUSER_IDCARD,TUSER.OPENID AS TUSER_OPENID,TUSER.SCHOOL AS TUSER_SCHOOL,TUSER.MAJOR AS TUSER_MAJOR,TUSER.DEGREE AS TUSER_DEGREE,TUSER.DEPT AS TUSER_DEPT,TUSER.TEACH AS TUSER_TEACH,TUSER.CTYPE AS TUSER_CTYPE,TUSER.ISADMIN AS TUSER_ISADMIN,TUSER.CUSER AS TUSER_CUSER,TUSER.BINDTIME AS TUSER_BINDTIME,TUSER.PASSTYPE AS TUSER_PASSTYPE,TUSER.FORMID AS TUSER_FORMID";

		//=>对应关联表字段：TUSER.DEPT=DEPTINFO1.ID,TUSER.CUSER=SYSUSER2.ID
		private String TUSER_JOIN = "TUSER.DEPT=DEPTINFO1.ID,TUSER.CUSER=SYSUSER2.ID";

		public String getTUSER(){	return TUSER; }
		public String getTUSER_JOIN(){	return TUSER_JOIN; }

		public void setTUSER(String  TUSER){	 this.TUSER = TUSER; }
		public void setTUSER_JOIN(String  TUSER_JOIN){	 this.TUSER_JOIN = TUSER_JOIN; }

		//=>应聘部门信息表(DEPTINFO)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：DEPTINFO
		private String DEPTINFO = "DEPTINFO.ID AS DEPTINFO_ID,DEPTINFO.VERSION AS DEPTINFO_VERSION,DEPTINFO.ISDELETE AS DEPTINFO_ISDELETE,DEPTINFO.DATETIME AS DEPTINFO_DATETIME,DEPTINFO.CREATETIME AS DEPTINFO_CREATETIME,DEPTINFO.NAME AS DEPTINFO_NAME,DEPTINFO.NOTE AS DEPTINFO_NOTE";

		//=>对应关联表字段：
		private String DEPTINFO_JOIN = "";

		public String getDEPTINFO(){	return DEPTINFO; }
		public String getDEPTINFO_JOIN(){	return DEPTINFO_JOIN; }

		public void setDEPTINFO(String  DEPTINFO){	 this.DEPTINFO = DEPTINFO; }
		public void setDEPTINFO_JOIN(String  DEPTINFO_JOIN){	 this.DEPTINFO_JOIN = DEPTINFO_JOIN; }

		//=>Hetara规则引擎类型表(HETARATYPE)数据全查询字段
		//=>@YanHaoNan,Mon Mar 04 10:15:11 CST 2019,原生生成代码
		//=>对应关联表：HETARATYPE
		private String HETARATYPE = "HETARATYPE.ID AS HETARATYPE_ID,HETARATYPE.VERSION AS HETARATYPE_VERSION,HETARATYPE.ISDELETE AS HETARATYPE_ISDELETE,HETARATYPE.DATETIME AS HETARATYPE_DATETIME,HETARATYPE.CREATETIME AS HETARATYPE_CREATETIME,HETARATYPE.NAME AS HETARATYPE_NAME,HETARATYPE.NOTE AS HETARATYPE_NOTE";

		//=>对应关联表字段：
		private String HETARATYPE_JOIN = "";

		public String getHETARATYPE(){	return HETARATYPE; }
		public String getHETARATYPE_JOIN(){	return HETARATYPE_JOIN; }

		public void setHETARATYPE(String  HETARATYPE){	 this.HETARATYPE = HETARATYPE; }
		public void setHETARATYPE_JOIN(String  HETARATYPE_JOIN){	 this.HETARATYPE_JOIN = HETARATYPE_JOIN; }

}