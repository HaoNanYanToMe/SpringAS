package com.prism.springas.schema.exPand;

import com.alibaba.fastjson.JSON;
import com.prism.springas.schema.BaseSchema;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.cache.CacheClass;
import com.prism.springas.utils.sqlEngine.sqlEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  - 权限管理相关拓展业务接口
 */
@Service
public class SysRoleExSchema extends BaseSchema {

    /**
     * 获取菜单树
     */
    public String getRoleTree(String tableName,sqlEngine sqlEngine) throws Exception{
        return JSON.toJSONString(this.getRootNode());
    }

    private List<BasePage> getRoleTree(String pid) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("SYSROLE")
                 .getSelTable("SYSROLE")
                 .getWhereAnd("SYSROLE_PID","EQ",pid)
                 .getWhereAnd("SYSROLE_ISDELETE","EQ",0)
                 .getSort("SYSROLE_SORT","2");
        List<BasePage> role = (List<BasePage>)super.baseApi.selectPage("SYSROLE",sqlEngine);
        List<BasePage> list = new ArrayList<>();
        for (BasePage r: role) {
            BasePage tree = new BasePage();
            int isParent = Integer.parseInt(r.get("ISTOP")+"");
            //如果为是父节点
            tree.put("id",r.get("ID"));
            tree.put("title",r.get("NAME"));
            tree.put("expand", false);
            tree.put("version", r.get("VERSION"));
            tree.put("isLock",Integer.parseInt(r.get("ISLOCK")+"")==0 ? false : true);
            if (isParent==1){
                tree.put("children",this.getRoleTree(r.get("ID")+""));
            }
            list.add(tree);
        }
        return list;
    }

    //获取组装Tree根节点数据
    private List<BasePage> getRootNode() throws Exception{
        Object treeList = CacheClass.getCache("roleTree");
        List<BasePage> list = new ArrayList<>();
        //缓存为空
        if(treeList == null) {
            BasePage tree = new BasePage();
            tree.put("id", 0);
            tree.put("title", "Spring A.S·业务权限管理");
            tree.put("expand", true);
            tree.put("isLock", false);
            tree.put("version", 0);
            tree.put("children", this.getRoleTree("-1"));
            list.add(tree);
            CacheClass.setCache("roleTree", list, -1);
        }else{
            list = (List<BasePage>)treeList;
        }
        return list;
    }

    /**
     * 装配缓存-新增权限节点
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String insertRoleNode(String tableName,sqlEngine sqlEngine) throws Exception{
        String insert =super.insertData("SYSROLE",sqlEngine);
        if (!insert.equals("")){
            sqlEngine sel = new sqlEngine();
            sel.getSelCols("","SYSROLE_PID#pid")
                    .getSelTable("SYSROLE")
                    .getSelById(insert.replaceAll("\"",""));
            BasePage selMenu = super.baseApi.selectById("SYSROLE",sel);
            //重置父节点信息
            sqlEngine upd = new sqlEngine();
            upd.getUpdateTables("SYSROLE")
                    .getUpdateById(selMenu.get("pid")+"")
                    .getUpdateCols("SYSROLE_ISTOP",1);
            super.baseApi.updateData("SYSROLE",upd);
            //新增成功,重置缓存
            CacheClass.removeCache("roleTree");
            this.getRootNode();
        }
        return insert;
    }

    /**
     * 装配缓存-更新权限节点
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String updateRoleNode(String tableName,sqlEngine sqlEngine) throws Exception{
        sqlEngine.getUpdateTables("SYSROLE")
                .getUpdateById(sqlEngine.getBaseParam("updId")+"")
                .getUpdCurrentVersion("SYSROLE",
                        Integer.parseInt(sqlEngine.getBaseParam("version")+""));
        String update =super.updateData("SYSROLE",sqlEngine);
        if (update.contains("成功")){
            //更新成功,重置缓存
            CacheClass.removeCache("roleTree");
            this.getRootNode();
        }
        return update;
    }

    /**
     * 装配缓存-删除选定的权限节点
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String removeRoleNode(String tableName,sqlEngine sqlEngine) throws Exception{
        sqlEngine.getUpdateTables("SYSROLE")
                .getUpdateById(sqlEngine.getBaseParam("updId")+"")
                .getUpdCurrentVersion("SYSROLE",
                        Integer.parseInt(sqlEngine.getBaseParam("version")+""))
                .getUpdateCols("SYSROLE_ISDELETE",1);
        return this.updateRoleNode("SYSMENU",sqlEngine);
    }

    /**
     * 获取当前选中菜单权限状态
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    public String selectRoleMenuNodes(String tableName,sqlEngine sqlEngine) throws Exception{
        String rid = sqlEngine.getBaseParam("roleId")+"";
        String mid = sqlEngine.getBaseParam("menuId")+"";
        BasePage newBase = this.exsisRoleMenuNode(rid,mid);
        //未添加的数据默认为不启用
        BasePage noData = new BasePage();
        noData.put("RID",rid);
        noData.put("MID",mid);
        noData.put("ADDS",newBase.isEmpty() ? "noAdd" : Integer.parseInt(newBase.get("SYSROLEMENU_ADDS")+"") == 0 ? "noAdd" : "isAdd");
        noData.put("DEL",newBase.isEmpty() ? "noDel" : Integer.parseInt(newBase.get("SYSROLEMENU_DEL")+"") == 0 ? "noDel" : "isDel");
        noData.put("EDIT",newBase.isEmpty() ? "noEdit" : Integer.parseInt(newBase.get("SYSROLEMENU_EDIT")+"") == 0 ? "noEdit" : "isEdit");
        noData.put("SEL",newBase.isEmpty() ? "noSel" : Integer.parseInt(newBase.get("SYSROLEMENU_SEL")+"") == 0 ? "noSel" : "isSel");
        noData.put("EXPORTS",newBase.isEmpty() ? "noExport" : Integer.parseInt(newBase.get("SYSROLEMENU_EXPORTS")+"") == 0 ? "noExport" : "isExport");
        return JSON.toJSONString(noData);
    }

    //判断当前节点是否存在
    private BasePage exsisRoleMenuNode(String rid,String mid) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("SYSROLEMENU")
                 .getSelCols("SYSROLEMENU")
                 .getWhereAnd("SYSROLEMENU_RID","EQ",rid)
                 .getWhereAnd("SYSROLEMENU_MID","EQ",mid);

        List<BasePage> rm = super.baseApi.selectPage("SYSROLEMENU",sqlEngine);
        BasePage newBase = rm.size() > 0 ? rm.get(0) : new BasePage();
        return newBase;
    }


    /**
     * 编辑菜单权限树
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String editRoleMenu(String tableName,sqlEngine sqlEngine) throws Exception{
        String mid = sqlEngine.getBaseParam("mid")+"";
        String rid = sqlEngine.getBaseParam("rid")+"";

        String result = "";
        if (mid.equals("")){
            //菜单为空,视为删除此权限下所有绑定的菜单信息
            sqlEngine del = new sqlEngine();
            del.getDelTables("SYSROLEMENU")
                    .getWhereAnd("SYSROLEMENU_RID","EQ",rid);
            result = Integer.parseInt(super.deleteData("SYSROLEMENU",del)) > 0 ? "菜单权限全部移除成功。" :"菜单权限移除失败";
        }else{
            String [] menuList = mid.substring(1,mid.length()).split(",");
            //删除未勾选的数据
            sqlEngine del2 = new sqlEngine();
            del2.getDelTables("SYSROLEMENU")
                    .getWhereAnd("SYSROLEMENU_RID","EQ",rid)
                    .getWhereAnd("SYSROLEMENU_MID","NOTIN",mid.substring(1,mid.length()));
            String delMsg = super.deleteData("SYSROLEMENU",del2);
            for (String m:menuList) {
                if (!m.equals("")){
                    BasePage newBase = this.exsisRoleMenuNode(rid,m);
                        if (newBase.isEmpty()){
                            //为空则创建
                            result = !this.insertRoleMenuParent(rid,m).equals("") ? "菜单权限更新成功" : "菜单权限更新失败";
                        }else{
                            result = "菜单权限更新成功";
                        }
                    }
                }
        }
        CacheClass.removeCache(rid);
        return JSON.toJSONString(result);
    }


    /**
     * 更新子权限信息
     * @param tableName
     * @param sqlEngine
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public String updateRoleMenu(String tableName,sqlEngine sqlEngine) throws Exception{
        String rid = sqlEngine.getBaseParam("roleId")+"";
        String mid = sqlEngine.getBaseParam("menuId")+"";
        int isOrNo = Integer.parseInt(sqlEngine.getBaseParam("isOrNo") + "");

        String type = sqlEngine.getBaseParam("roleType")+"";
        type = type.equals("0") ? "SEl" : type.equals("1") ? "ADDS" : type.equals("2") ? "EDIT" : type.equals("3") ? "DEL"
                    :type.equals("4") ? "EXPORTS":"";

        Map map = new HashMap();
        map.put("result",-1);
        map.put("resultData","type入参值有误，请检查后重试");
        map.put("data","");

        if(!type.equals("")) {
            BasePage newBase = this.exsisRoleMenuNode(rid,mid);
            if (newBase.isEmpty()){
                //为空则新建
                String insert = this.insertRoleMenu(rid,mid,type,isOrNo);
                //追加父节点
                this.insertNoCreateParentNode(rid,mid);
                map.put("result",insert.equals("") ? 0 : 1);
                map.put("resultData",insert.equals("") ? "子权限更新失败":"子权限更新成功");
            }else{
                //不为空则更新
                sqlEngine upd = new sqlEngine();
                upd.getUpdateTables("SYSROLEMENU")
                        .getUpdateCols("SYSROLEMENU_" + type,isOrNo)
                        .getUpdateById(newBase.getString("SYSROLEMENU_ID"));
                super.baseApi.updateData("SYSROLEMENU",upd);
                int updResult = super.baseApi.updateData("SYSROLEMENU",upd);
                map.put("result",updResult);
                map.put("resultData",updResult==0?"子权限更新失败":"子权限更新成功");
            }

        }
        CacheClass.removeCache(rid);
        return JSON.toJSONString(map);
    }

    //判断父节点是否存在
    private void insertNoCreateParentNode(String rid,String mid) throws Exception{
        sqlEngine sel = new sqlEngine();
        sel.getSelCols("","SYSMENU_PID#pid")
                .getSelTable("SYSMENU")
                .getSelById(mid);
        BasePage selMenu = super.baseApi.selectById("SYSMENU",sel);
        if (selMenu!=null){
            if (!selMenu.getString("pid").equals("-1")){
                BasePage newBase = this.exsisRoleMenuNode(rid,selMenu.get("pid")+"");
                if (newBase.isEmpty()){
                   this.insertRoleMenuParent(rid,selMenu.get("pid")+"");
                   //继续追上级节点
                   this.insertNoCreateParentNode(rid,selMenu.get("pid")+"");
                }
            }
        }
    }

    //创建新的权限菜单绑定关系(直接绑定菜单时使用)
    private String insertRoleMenuParent(String rid,String mid){
        CacheClass.removeCache(rid);
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getAddData("RID",rid)
                .getAddData("MID",mid)
                .getAddData("ADDS",1)
                .getAddData("SEL",1)
                .getAddData("DEL",1)
                .getAddData("EDIT",1)
                .getAddData("EXPORTS",1);
        return  super.insertData("SYSROLEMENU",sqlEngine);
    }

    //创建新的权限菜单绑定关系(子权限编辑使用)
    private String insertRoleMenu(String rid,String mid,String type,int value){
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getAddData("RID",rid)
                 .getAddData("MID",mid);
        if (type!=null){
            sqlEngine.getAddData(type,value);
        }
        CacheClass.removeCache(rid);
        return super.insertData("SYSROLEMENU",sqlEngine);
    }

    /**
     * 鉴权操作,获取当前接口是否具有访问权限
     * @param rid
     * @param tableName
     * @return
     * @throws Exception
     */
    public List<BasePage> isCheckRole(String rid,String tableName) throws Exception{
        sqlEngine sqlEngine = new sqlEngine();
        sqlEngine.getSelTable("SYSROLEMENU,SYSMENU1").
                getSelCols("SYSROLEMENU","SYSMENU1_CODE").
                getWhereAnd("SYSROLEMENU_RID","EQ",rid);
        return baseApi.selectPage("SYSROLEMENU",sqlEngine);
    }
}
