package jp.co.amazon2off.mapper;

import jp.co.amazon2off.pojo.CodeExcelPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
@Transactional
public interface CodeMapper {

    /**
     * 添加折扣码
     *
     * @param codeExcelPojo
     * @param listingId
     * @param userId
     * @param addTime
     */
    void addCode(@Param("codeExcelPojo") List<CodeExcelPojo> codeExcelPojo, @Param("listingId") Integer listingId, @Param("userId") Integer userId, @Param("addTime") Long addTime);
}
