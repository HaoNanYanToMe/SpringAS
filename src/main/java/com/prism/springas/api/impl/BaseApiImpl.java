package com.prism.springas.api.impl;

import com.prism.springas.api.BaseApi;
import com.prism.springas.api.tools.BaseApiExtends;
import com.prism.springas.tools.BaseTool;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component("baseApi")
public class BaseApiImpl extends BaseApiExtends implements BaseApi,Serializable{

    private static final long serialVersionUID = 1L;

    @Override
    public List<BasePage> selectPage(String tableName, sqlEngine sqlEngine) {
        return  (List<BasePage>)invokeMethod(0,tableName,"SelectPage",sqlEngine.toParseEngineSQL());
    }

    @Override
    public List<BasePage> selectPageCount(String tableName, sqlEngine sqlEngine) {
        sqlEngine.getCount();
        return selectPage(tableName,sqlEngine);
    }

    @Override
    public BasePage selectById(String tableName, sqlEngine sqlEngine) {
        return (BasePage)invokeMethod(0,tableName,"SelectByOne",
                sqlEngine.removeEngineCondition("sortSQL","sqlPage").toParseEngineSQL());
    }

    @Override
    public int deleteData(String tableName, sqlEngine sqlEngine) {
        return (Integer)invokeMethod(0,tableName,"DeleteData",sqlEngine.toParseEngineSQL());
    }

    @Override
    public String insertData(String tableName, sqlEngine sqlEngine){
        String UUID = BaseTool.getUUID();
        sqlEngine.getAddData("ID",UUID);
        sqlEngine.getAddData("VERSION",0);
        sqlEngine.getAddData("ISDELETE",0);
        sqlEngine.getAddData("DATETIME",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        sqlEngine.getAddData("CREATETIME",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //判断insert是否成功,成功则返回UUID,失败则返回''
        return (Integer)invokeMethod(0,tableName,"InsertData",sqlEngine.toParseInsertSQL()) == 0 ? ""
                :UUID;
    }

    @Override
    public int updateData(String tableName, sqlEngine sqlEngine) {
        Object result = invokeMethod(0,tableName,"UpdateData",sqlEngine.toParseUpdateSQL());
        return result == null ? 0 : (Integer)result;
    }

    @Override
    public int insertFetchData(String tableName, sqlEngine sqlEngine) {
        return (Integer)invokeMethod(0,tableName,"InsertData",sqlEngine.toParseAddFetchSQL());
    }

    @Override
    public Object selectExPage(String tableName,String functionName,sqlEngine sqlEngine) {
        return invokeMethod(1,tableName,functionName,sqlEngine.toParseExSQL());
    }
}
