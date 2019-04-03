package com.prism.springas.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prism.springas.schema.exPand.SysRoleExSchema;
import com.prism.springas.tools.IPTool;
import com.prism.springas.tools.MD5Tool;
import com.prism.springas.utils.BasePage;
import com.prism.springas.utils.cache.CacheClass;
import com.prism.springas.utils.http.SessionUtil;
import com.prism.springas.utils.oAuth2.oAuthToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class AccessCheck {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IPTool ipTool;
    @Autowired
    MD5Tool md5Tool;

    @Pointcut("execution(public * com.prism.springas.controller.BaseController.*(..))")
    public void recordController(){}

//    @Around("recordController()")
    public String loginProcess(ProceedingJoinPoint point) throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        BasePage user = SessionUtil.getUser(request);

        Map<String, Object> map = new HashMap<>();
        map.put("msg","登录凭证(token)已失效。");
        map.put("executeTag","0");
        if (user != null){
            oAuthToken oAuthToken = new oAuthToken(request,null);
            String currentUser =  CacheClass.getCache(oAuthToken.GetCookie("UUID"))+"";
            //TOKEN鉴权
            if (currentUser.equals(
                    md5Tool.md5Password(oAuthToken.GetCookie("TOKEN")+"|"+ipTool.GetRealIP(request))
            )){
                //鉴权匹配成功
                Object[] args = point.getArgs();
                //数据菜单访问权限判定
                if (this.checkCurrentRole(point.getSignature().getName(), args[0].toString(), args[1].toString(), user.get("SYSUSER_RID") + "")) {
                    //...通过了数据访问验证
                    try {
                        Object returnValue = point.proceed(args);
                        map.put("msg", "认证通过");
                        map.put("executeTag", "1");
                        if (returnValue.toString().substring(0, 1).equals("[")) {
                            JSONArray pJson = JSONArray.parseArray(returnValue.toString());
                            map.put("executeData", pJson);
                        } else if (returnValue.toString().substring(0, 1).equals("{")) {
                            JSONObject pJson = JSONObject.parseObject(returnValue.toString());
                            map.put("executeData", pJson);
                        } else {
                            map.put("executeData", returnValue != null ?
                                    returnValue.toString().replace("\"", "") : "系统数据格式转换出错,请联系管理员");
                        }
                    }catch (Throwable e){
                        map.put("msg","接口认证失败,请联系管理员。");
                        map.put("executeTag","2");
                        logger.error("---AccessCheck ERROR:(后台接口出现严重错误)-->"+e.getMessage(),e);
                    }
                } else {
                    //...未通过数据访问验证
                    map.put("msg","无此操作权限!");
                    map.put("executeTag","-1");
                }
            }
        }
        return JSON.toJSONString(map);
    }

    @Autowired
    SysRoleExSchema sysRoleExSchema;

    private boolean checkCurrentRole(String signatrue, String tableName, String fucName, String rid) throws Exception {
        Object roleMenusCache = CacheClass.getCache(rid);
        List<BasePage> roleMenus = new ArrayList<>();
        if (roleMenusCache != null) {
            roleMenus = (List<BasePage>) roleMenusCache;
        } else {
            roleMenus = sysRoleExSchema.isCheckRole(rid, tableName);
            //权限缓存-1小时
            CacheClass.setCache(rid, roleMenus, 3600);
        }
        boolean re = false;
        if (roleMenus.size() > 0) {
            int add = 0;
            int upd = 0;
            int del = 0;
            int slc = 0;
            int exports = 0;
            for (BasePage bp : roleMenus) {
                //非禁用状态下，统计计算累加次数
                if (Integer.parseInt(bp.get("SYSROLEMENU_ISDELETE") + "") == 0) {
                    if (bp.getString("SYSMENU1_CODE").contains(signatrue.equals("exFunction")?tableName:tableName.split(",")[0])) {
                        add += Integer.parseInt(bp.get("SYSROLEMENU_ADDS") + "");
                        del += Integer.parseInt(bp.get("SYSROLEMENU_DEL") + "");
                        upd += Integer.parseInt(bp.get("SYSROLEMENU_EDIT") + "");
                        slc += Integer.parseInt(bp.get("SYSROLEMENU_SEL") + "");
                        exports += Integer.parseInt(bp.get("SYSROLEMENU_EXPORTS") + "");
                    }
                }
            }

            if (signatrue.contains("selectBase")
                    || signatrue.contains("selectById") ||
                    fucName.contains("select")
                    || fucName.contains("get")
                    || fucName.contains("slc")
                    || signatrue.contains("eCharsBase")) {
                //查询相关方法处理(含导出)
                if (slc > 0) {
                    re = true;
                }
            } else if (signatrue.contains("deleteAffair") ||
                    signatrue.contains("deleteForce") ||
                    fucName.contains("del")) {
                //删除相关方法处理
                if (del > 0) {
                    re = true;
                }
            } else if (signatrue.contains("insertData") ||
                    fucName.contains("insert")) {
                //新增相关方法处理
                if (add > 0) {
                    re = true;
                }
            } else if (signatrue.contains("updateData") ||
                    fucName.contains("update") ||
                    fucName.contains("edit")) {
                //修改相关方法处理
                if (upd > 0) {
                    re = true;
                }
            } else if (signatrue.contains("exPortData")
                    || fucName.contains("exports")) {
                //导出相关方法处理
                if (exports > 0) {
                    re = true;
                }
            }
        }
        return re;
    }
}
