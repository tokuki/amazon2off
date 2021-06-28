package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
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
