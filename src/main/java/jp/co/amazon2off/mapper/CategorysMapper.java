package jp.co.amazon2off.mapper;

import jp.co.amazon2off.pojo.CategorysPojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
@Transactional
public interface CategorysMapper {

    /**
     * 获取商品类目
     *
     * @return
     */
    List<CategorysPojo> getCategorys();

}
