package com.prism.springas.utils.sqlEngine;

public enum sqlEngineEnum {

    EQ(" = "),//等于
    LIKE(" LIKE "),//模糊查询
    GT(" > "),//大于
    GTEQ("  >= "),//大于等于
    LT("  < "),//小于
    LTEQ("  <=  "),//小于等于
    NEQ(" <> "),//不等于
    IN(" IN "),//包含
    NOTIN(" NOT IN "),//不包含
    ISNULL(" IS NULL "),//为空
    NOTNULL(" IS NOT NULL "),//不为空
    BET(" BETWEEN ");//在……区间,Between and……


    private String value;

    //构造方法
    private sqlEngineEnum(String value){
        this.value = value;
    }

    public String getCondition(){
        return value;
    }
}
