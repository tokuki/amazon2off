package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.CategorysMapper;
import jp.co.amazon2off.pojo.CategorysPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CategorysService {

    @Autowired
    private CategorysMapper categorysMapper;

    /**
     * 获取商品类目
     *
     * @return
     */
    public List<CategorysPojo> getCategorys() {
        return categorysMapper.getCategorys();
    }
}
