package com.prism.springas.utils;

import com.prism.springas.dao.BaseDAO;
import com.prism.springas.dao.exPand.HetaraExDAO;
import com.prism.springas.dao.exPand.SysMenuExDAO;
import com.prism.springas.dao.exPand.SysRoleExDAO;
import com.prism.springas.dao.exPand.SysUserExDAO;
import com.prism.springas.schema.exPand.HetaraExSchema;
import com.prism.springas.schema.exPand.SysMenuExSchema;
import com.prism.springas.schema.exPand.SysRoleExSchema;
import com.prism.springas.schema.exPand.SysUserExSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 全局反射核心变量控制类
 */
@Component
public class BaseCoreCfg {
    @Autowired
    BaseDAO baseDAO;
    @Autowired
    SysMenuExDAO sysMenuExDAO;
    @Autowired
    SysMenuExSchema sysMenuExSchema;
    @Autowired
    SysRoleExDAO sysRoleExDAO;
    @Autowired
    SysRoleExSchema sysRoleExSchema;
    @Autowired
    SysUserExDAO sysUserExDAO;
    @Autowired
    SysUserExSchema sysUserExSchema;
    @Autowired
    HetaraExDAO hetaraExDAO;
    @Autowired
    HetaraExSchema hetaraExSchema;

    /**
     * 获取拓展DAO及Schema对象
     * @param refName   对象标识名称
     * @param refType   反射类型  0-DAO 1-Schema
     * @return
     */
    public Object parseRefObject(String refName,int refType){
        Object obj = new Object();
        if(refName.equals("hetara")){
            //Hetara动态条件引擎
            obj = refType == 0 ? hetaraExDAO : hetaraExSchema;
        }
        if(refName.equals("sysUser")){
            //用户信息
            obj = refType == 0 ? sysUserExDAO : sysUserExSchema;
        }
        if(refName.equals("sysMenu")){
            //菜单管理
            obj = refType == 0 ? sysMenuExDAO : sysMenuExSchema;
        }
        if(refName.equals("sysRole")){
            //权限管理
            obj = refType == 0 ?sysRoleExDAO:sysRoleExSchema;
        }
        return obj;
    }
}
