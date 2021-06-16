package jp.co.amazon2off.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Repository
@Transactional
public interface RoleMapper {
    
    /**
     * 获取角色列表
     *
     * @param roleId
     * @return
     */
    String getRole(@Param("roleId") int roleId);
}
