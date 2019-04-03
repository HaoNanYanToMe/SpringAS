package com.prism.springas.dao.exPand;

import com.prism.springas.utils.BasePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 菜单管理业务拓展DAO
 */
@Mapper
@Component("sysMenuExDAO")
public interface SysMenuExDAO {

    /**
     * 获取菜单树
     * @param bp
     *         pid : 父节点编号
     * @return
     */
    @Select("<script> <![CDATA[SELECT s.id AS id, s.path AS path, s.title AS title, s.COMPONENT AS component,s.icon AS icon, s.name AS name, s.content AS content, s.version AS version, s.ISLOCKED AS isLock, s.ISPARENT AS isParent, s.PID AS pid, " +
            "CONCAT('import (@/views/',s.COMPONENT, '.vue','\"',')') AS components FROM sysmenu s where s.ISDELETE = 0 AND s.PID = #{bp.pid} ORDER BY s.SORT ASC ]]> </script>")
    List<BasePage> getMenu(@Param("bp") BasePage bp);
}
