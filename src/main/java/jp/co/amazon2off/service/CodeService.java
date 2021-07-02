package jp.co.amazon2off.service;

import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.mapper.CodeMapper;
import jp.co.amazon2off.pojo.CodePojo;
import jp.co.amazon2off.utils.DateUtils;
import jp.co.amazon2off.utils.FileUtils;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CodeService {

    @Autowired
    private CodeMapper codeMapper;

    /**
     * 领取优惠码
     *
     * @param listingId
     */
    public int receiveCode(Integer listingId) throws Exception {
        CodePojo codePojo = new CodePojo();
        codePojo.setListingId(listingId);
        codePojo.setUserId(SecurityUtil.getCurrentUser().getId());
        codePojo.setReceiveTime(DateUtils.getCurrentTimeMillis());
        if (codeMapper.codeNumByListingId(codePojo) > 0) {
            throw new Exception(ErrorCodeConstants.C_0002);
        }
        return codeMapper.updateCode(codePojo);
    }

    /**
     * 优惠码是否已领取
     *
     * @param listingId
     * @return
     * @throws Exception
     */
    public boolean receiveCheck(Integer listingId) throws Exception {
        CodePojo codePojo = new CodePojo();
        codePojo.setListingId(listingId);
        codePojo.setUserId(SecurityUtil.getCurrentUser().getId());
        if (codeMapper.codeNumByListingId(codePojo) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 商家发布优惠码
     *
     * @param codeFile
     * @param codePojo
     * @param price
     * @throws Exception
     */
    public void addCode(MultipartFile codeFile, CodePojo codePojo, Double price) throws Exception {
        codePojo.setDiscountPercentage(Double.valueOf(String.format("%.2f", codePojo.getDiscountPrice() / price * 100)));
        // 优惠码处理
        List<String> codeList = FileUtils.readFileByInputStream(codeFile.getInputStream());
        // 添加优惠码
        codeMapper.addCode(codeList, codePojo);
    }

}
