package com.prism.springas.schema.exPand;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.prism.springas.controller.config.BaseBasicExtends;
import com.prism.springas.schema.BaseSchema;
import com.prism.springas.tools.MD5Tool;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.cache.CacheClass;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 菜单管理拓展业务相关接口
 */
@Service
public class SysMenuExSchema extends BaseSchema {

    @Autowired
    MD5Tool md5Tool;

    /**
     * 获取菜单树
     */
    public String getMenuTree(String tableName,sqlEngine sqlEngine) throws Exception{
       return JSON.toJSONString(this.getRootNode());
    }

    private List<BasePage> getMenuTree(String pid){
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getParamEx("pid",pid);
        List<BasePage> menu = (List<BasePage>)super.baseApi.selectExPage("sysMenu",
                "getMenu",sqlEngine);
        List<BasePage> list = new ArrayList<>();
        for (BasePage m: menu) {
            BasePage tree = new BasePage();
            int isParent = Integer.parseInt(m.get("isParent")+"");
            //如果为是父节点
            tree.put("id",m.get("id"));
            tree.put("title",m.get("title"));
            tree.put("expand", false);
            tree.put("version", m.get("version"));
            tree.put("isLock",Integer.parseInt(m.get("isLock")+"")==0 ? false : true);
            if (isParent==0){
                tree.put("children",this.getMenuTree(m.get("id")+""));
            }
            list.add(tree);
        }
        return list;
    }

    //获取组装Tree根节点数据
    private List<BasePage> getRootNode(){
        Object treeList = CacheClass.getCache("menuTree");
        List<BasePage> list = new ArrayList<>();
        //缓存为空
        if(treeList == null) {
            BasePage tree = new BasePage();
            tree.put("id", 0);
            tree.put("title", "Spring A.S·业务菜单管理");
            tree.put("expand", true);
            tree.put("isLock", false);
            tree.put("version", 0);
            tree.put("children", this.getMenuTree("-1"));
            list.add(tree);
            CacheClass.setCache("menuTree", list, -1);
        }else{
            list = (List<BasePage>)treeList;
        }
        return list;
    }


    /**
     * 获取菜单树
     */
    public String getRoleMenuTree(String tableName,sqlEngine sqlEngine) throws Exception{
        return JSON.toJSONString(this.getRoleMenuTree("-1",this.getRoleMenuData(sqlEngine.getBaseParam("rid")+"")));
    }

    private List<BasePage> getRoleMenuTree(String pid,List<BasePage> rmList) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getParamEx("pid",pid);
        List<BasePage> menu = (List<BasePage>)super.baseApi.selectExPage("sysMenu",
                "getMenu",sqlEngine);

        List<BasePage> list = new ArrayList<>();
        for (BasePage m: menu) {
            BasePage tree = new BasePage();
            int isParent = Integer.parseInt(m.get("isParent")+"");
            //如果为是父节点
            tree.put("id",m.get("id"));
            tree.put("title",m.get("title"));
            tree.put("expand", false);
            tree.put("version", m.get("version"));
            tree.put("disabled",Integer.parseInt(m.get("isLock")+"")==0 ? false : true);
            tree.put("isLock",Integer.parseInt(m.get("isLock")+"")==0 ? false : true);
            tree.put("checked", false);
            for (BasePage rm : rmList) {
                if (rm.getString("SYSROLEMENU_MID").equals(m.getString("id")))
                    tree.put("checked", isParent==0 ? false : true);
                    //判断已经存储的权限信息主键,用以在新增时判断
                    tree.put("rmid",rm.getString("SYSROLEMENU_ID"));
            }
            if (isParent==0){
                tree.put("children",this.getRoleMenuTree(m.get("id")+"",rmList));
            }
            list.add(tree);
        }
        return list;
    }

    //获取当前权限已绑定菜单信息
    private List<BasePage> getRoleMenuData(String rid) throws Exception{
        sqlEngine rm = new sqlEngine();
        rm.getSelTable("SYSROLEMENU")
                .getSelCols("SYSROLEMENU")
                .getWhereAnd("SYSROLEMENU_RID","EQ",rid)
                .getWhereAnd("SYSROLEMENU_ISDELETE","EQ",0);
        return super.baseApi.selectPage("SYSROLEMENU",rm);
    }
    /**
     * 装配缓存-新增菜单节点
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String insertMenuNode(String tableName,sqlEngine sqlEngine) throws Exception{
        String insert =super.insertData("SYSMENU",sqlEngine);
        if (!insert.equals("")){
            sqlEngine sel = new sqlEngine();
            sel.getSelCols("","SYSMENU_PID#pid")
               .getSelTable("SYSMENU")
               .getSelById(insert.replaceAll("\"",""));
            BasePage selMenu = super.baseApi.selectById("SYSMENU",sel);
            //重置父节点信息
            sqlEngine upd = new sqlEngine();
            upd.getUpdateTables("SYSMENU")
                    .getUpdateById(selMenu.get("pid")+"")
                    .getUpdateCols("SYSMENU_ISPARENT",0);
            super.baseApi.updateData("SYSMENU",upd);
            //新增成功,重置缓存
            CacheClass.removeCache("menuTree");
            this.getRootNode();
        }
        return insert;
    }

    @Transactional(readOnly = false)
    public String updateMenuNode(String tableName,sqlEngine sqlEngine) throws Exception{
        sqlEngine.getUpdateTables("SYSMENU")
                 .getUpdateById(sqlEngine.getBaseParam("updId")+"")
                 .getUpdCurrentVersion("SYSMENU",
                         Integer.parseInt(sqlEngine.getBaseParam("version")+""));
        String update =super.updateData("SYSMENU",sqlEngine);
        if (update.contains("成功")){
            //更新成功,重置缓存
            CacheClass.removeCache("menuTree");
            this.getRootNode();
        }
        return update;
    }

    @Transactional(readOnly = false)
    public String removeMenuNode(String tableName,sqlEngine sqlEngine) throws Exception{
        sqlEngine.getUpdateTables("SYSMENU")
                 .getUpdateById(sqlEngine.getBaseParam("updId")+"")
                 .getUpdCurrentVersion("SYSMENU",
                        Integer.parseInt(sqlEngine.getBaseParam("version")+""))
                 .getUpdateCols("SYSMENU_ISDELETE",1);
        return this.updateMenuNode("SYSMENU",sqlEngine);
    }

    /**
     * 获取菜单管理抽屉数据
     * @param tableName
     * @param sqlEngine
     *          CODE : 前端获取的数据权限字段CODE值
     * @return
     * @throws Exception
     */
    public String getDrawerData(String tableName,sqlEngine sqlEngine) throws Exception{
        sqlEngine.getSelTable("TABLEFOLDER")
                 .getSelCols("","")
                 .getSort("TABLEFOLDER_CREATETIME","4");
        List<BasePage> folders = (List<BasePage>)super.baseApi.selectPage("TABLEFOLDER",sqlEngine);
        List<BasePage> list = new ArrayList<>();

        List<String> target = new ArrayList<>();

        String targetKeys = sqlEngine.getBaseParam("CODE")+"";
        for (BasePage f:folders) {
            BasePage draw = new BasePage();
            draw.put("key",f.get("ID"));
            draw.put("name",f.get("NAME"));
            draw.put("label",f.get("CODE"));
            draw.put("description",f.get("NOTE"));
            draw.put("disabled",Integer.parseInt(f.get("ISDELETE")+"") == 0 ? false : true);
            list.add(draw);
            for (String s : targetKeys.split(",")) {
                if(!s.trim().equals("")){
                    if (f.getString("CODE").equals(s)){
                        target.add(f.get("ID")+"");
                    }
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("mockData",list);
        map.put("targetKeys",target);
        return JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect);
    }
}
