package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.CodeMapper;
import jp.co.amazon2off.mapper.ListingMapper;
import jp.co.amazon2off.pojo.CodeExcelPojo;
import jp.co.amazon2off.pojo.ListingPojo;
import jp.co.amazon2off.utils.DateUtils;
import jp.co.amazon2off.utils.ExcelUtil;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ListingService {

    @Autowired
    private ListingMapper listingMapper;
    @Autowired
    private CodeMapper codeMapper;

    /**
     * 商品添加
     *
     * @param listingPojo
     * @param multipartFile
     * @throws Exception
     */
    public void addListing(ListingPojo listingPojo, MultipartFile multipartFile) throws Exception {
        listingPojo.setUserId(SecurityUtil.getCurrentUser().getId());
        listingPojo.setDiscountPercentage(String.format("%.2f", listingPojo.getDiscountPrice() / listingPojo.getPrice() * 100));
        listingPojo.setAddTime(DateUtils.getCurrentTimeMillis());
        listingMapper.addListing(listingPojo);
        if (listingPojo.getId() != null) {
            InputStream file = multipartFile.getInputStream();
            List<CodeExcelPojo> list = ExcelUtil.readExcel(file, CodeExcelPojo.class, 5);
            if (!list.isEmpty() && list.size() > 0) {
                codeMapper.addCode(list, listingPojo.getId(), listingPojo.getUserId(), listingPojo.getAddTime());
            }
        }
    }

    /**
     * 商品查询
     *
     * @param keyWords
     * @return
     * @throws Exception
     */
    public List<ListingPojo> getListingList(String keyWords) throws Exception {
        return listingMapper.getListingList(keyWords);
    }

    /**
     * 商品销量TOP
     *
     * @return
     */
    public List<ListingPojo> getListingTopInf() {
        List<ListingPojo> getListingTopId = listingMapper.getListingTopId();
        if (getListingTopId != null && !getListingTopId.isEmpty()) {
            return listingMapper.getListingTopInf(getListingTopId);
        }
        return new ArrayList<ListingPojo>();
    }
}
