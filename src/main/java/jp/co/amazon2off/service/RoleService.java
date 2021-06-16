package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 获取角色列表
     *
     * @param roleId
     * @return
     */
    public String getRole(int roleId) {
        return roleMapper.getRole(roleId);
    }
}
