package com.prism.springas.dao.exPand;

import com.prism.springas.utils.BasePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统用户拓展信息DAO
 */
@Mapper
@Component("SysUserExDAO")
public interface SysUserExDAO {

    /**
     *  vue前端页面router数据路由装配
     * @param bp
     * @return
     */
    @Select("<script> <![CDATA[SELECT s.id AS id, s.path AS path, s.title AS title, s.COMPONENT AS component,s.icon AS icon, s.name AS name, s.content AS content, s.version AS version, s.ISLOCKED AS isLock, s.ISPARENT AS isParent, s.PID AS pid, " +
            "CONCAT('import (@/views/',s.COMPONENT, '.vue','\"',')') AS components FROM sysmenu s" +
            " LEFT JOIN sysrolemenu rm ON s.ID = rm.MID LEFT JOIN sysuser u ON u.RID = rm.RID " +
            " where s.ISDELETE = 0 AND s.PID = #{bp.pid} AND u.ID = #{bp.uid} ORDER BY s.SORT ASC ]]> </script>")
    List<BasePage> getRouter(@Param("bp") BasePage bp);
}
