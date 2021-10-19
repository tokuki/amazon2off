package jp.co.amazon2off.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jp.co.amazon2off.mapper.CodeMapper;
import jp.co.amazon2off.mapper.ListingMapper;
import jp.co.amazon2off.pojo.CodePojo;
import jp.co.amazon2off.pojo.ListingPojo;
import jp.co.amazon2off.utils.DateUtil;
import jp.co.amazon2off.utils.FileUtils;
import jp.co.amazon2off.utils.ImageUtil;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ListingService {


    @Value("${uploadDir}")
    private String uploadDir;

    @Autowired
    private ListingMapper listingMapper;
    @Autowired
    private CodeMapper codeMapper;

    /**
     * 商品添加
     *
     * @param listingPojo
     * @param coverImageFile
     * @param secondaryImageFile
     * @param codeFile
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, String> addListing(ListingPojo listingPojo, MultipartFile coverImageFile, MultipartFile[] secondaryImageFile,
                                          MultipartFile codeFile, Long startTime, Long endTime) throws Exception {
        Map<String, String> map = new HashMap<>();
        // 存储路径
        String path = System.getProperty("java.io.tmpdir") + uploadDir;
        log.info(">>>>>>>路径>>>>>>>>>>>>>:" + path);
        // 主图处理
        map.putAll(uploadCoverImage(path, coverImageFile, 3, false));
        // 主图大图片路径
        listingPojo.setCoverImage(map.get("coverImage"));
        // 主图小图片路径
        listingPojo.setSmallCoverImage(map.get("smallCoverImage"));

        // 副图处理
        if (secondaryImageFile != null && secondaryImageFile.length > 0) {
            map.putAll(uploadSecondaryImage(path, secondaryImageFile, 3, false));
            listingPojo.setSecondaryImageA(map.get("secondaryImageA"));
            listingPojo.setSmallSecondaryImageA(map.get("smallSecondaryImageA"));
            listingPojo.setSecondaryImageB(map.get("secondaryImageB"));
            listingPojo.setSmallSecondaryImageB(map.get("smallSecondaryImageB"));
            listingPojo.setSecondaryImageC(map.get("secondaryImageC"));
            listingPojo.setSmallSecondaryImageC(map.get("smallSecondaryImageC"));
            listingPojo.setSecondaryImageD(map.get("secondaryImageD"));
            listingPojo.setSmallSecondaryImageD(map.get("smallSecondaryImageD"));
        }

        listingPojo.setUserId(SecurityUtil.getCurrentUser().getId());
        listingPojo.setAddTime(DateUtil.getCurrentTimeMillis());
        // 优惠码处理
        List<String> codeList = FileUtils.readFileByInputStream(codeFile.getInputStream());
        // 优惠码相关
        CodePojo codePojo = new CodePojo();
        codePojo.setStartTime(startTime);
        codePojo.setEndTime(endTime);
        codePojo.setDiscountPrice(listingPojo.getDiscountPrice());
        codePojo.setDiscountPercentage(Double.valueOf(String.format("%.2f", listingPojo.getDiscountPrice() / listingPojo.getPrice() * 100)));
        codePojo.setAddTime(DateUtil.getCurrentTimeMillis());

        // 添加商品
        listingMapper.addListing(listingPojo);
        codePojo.setListingId(listingPojo.getId());
        // 添加优惠码
        codeMapper.addCode(codeList, codePojo);
        return map;
    }

    /**
     * 图片下载
     *
     * @param imageName
     * @param response
     * @throws Exception
     */
    public void downloadImage(String imageName, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + imageName);

        String fileUrl = System.getProperty("java.io.tmpdir") + uploadDir + "/" + imageName;
        File file;
        InputStream is = null;
        OutputStream out = null;
        try {
            file = new File(fileUrl);
            if (file.exists()) {
                is = new FileInputStream(file);
                out = response.getOutputStream();
                IOUtils.copy(is, out);
            }
        } finally {
            is.close();
            out.close();
        }
    }

    /**
     * 商品查询
     *
     * @param listingPojo
     * @return
     * @throws Exception
     */
    public IPage<ListingPojo> getListingList(Page<ListingPojo> page, ListingPojo listingPojo) throws Exception {
        List<Integer> listingIdList = new ArrayList<>();
        if (listingPojo.getType() == 0) {
            IPage<ListingPojo> list = listingMapper.getListByKeyWords(page, listingPojo.getKeyWords());
            if (list.getRecords() != null && !list.getRecords().isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list.getRecords());
                if (codePojo != null && !codePojo.isEmpty()) {
                    return list.setRecords(listingInfoFormat(codePojo, list.getRecords()));
                }
            }
        }
        if (listingPojo.getType() == 1 || listingPojo.getType() == 2 || listingPojo.getType() == 3) {
            IPage<ListingPojo> list = listingMapper.getListByDiscount(page, listingPojo);
            if (list.getRecords() != null && !list.getRecords().isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list.getRecords());
                if (codePojo != null && !codePojo.isEmpty()) {
                    return list.setRecords(listingInfoFormat(codePojo, list.getRecords()));
                }
            }
        }
        // 近期要开始活动的商品（2天）
        if (listingPojo.getType() == 4) {
            listingIdList = codeMapper.getListingIdByTime(System.currentTimeMillis(), DateUtil.getBeforeTwoDay()[0], DateUtil.getBeforeTwoDay()[1]);
        }
        // 优惠码被领取最多的商品
        if (listingPojo.getType() == 5) {
            listingIdList = codeMapper.getListingIdByCode();
        }
        if (listingIdList != null && !listingIdList.isEmpty()) {
            IPage<ListingPojo> list = listingMapper.getListByListingId(page, listingIdList);
            if (list.getRecords() != null && !list.getRecords().isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list.getRecords());
                if (codePojo != null && !codePojo.isEmpty()) {
                    return list.setRecords(listingInfoFormat(codePojo, list.getRecords()));
                }
            }
        }
        return new Page<>();
    }

    /**
     * 商品详情查询
     *
     * @param listingPojo
     * @return
     */
    public ListingPojo getListingInfo(ListingPojo listingPojo) throws Exception {
        return listingMapper.getListingInfo(listingPojo);
    }

    /**
     * 根据品类获取商品列表
     *
     * @param category
     * @return
     */
    public List<ListingPojo> getListingListByCategory(Integer category) throws Exception {
        List<ListingPojo> list = listingMapper.getListingListByCategory(category);
        if (list != null && !list.isEmpty()) {
            List<CodePojo> codePojo = codeMapper.getTimeByListingId(list);
            if (codePojo != null && !codePojo.isEmpty()) {
                return listingInfoFormat(codePojo, list);
            }
        }
        return list;
    }

    /**
     * 商家优惠卷一览&商品一览
     *
     * @return
     */
    public List<ListingPojo> getSellerCodeList() throws Exception {
        Integer userId = SecurityUtil.getCurrentUser().getId();
        List<ListingPojo> list = listingMapper.getSellerCodeList(userId);
        if (list != null && !list.isEmpty()) {
            List<CodePojo> codeList = codeMapper.getTimeByUserId(userId);
            if (codeList != null && !codeList.isEmpty()) {
                return listingInfoFormat(codeList, list);
            }
        }
        return list;
    }

    /**
     * 组装商品活动时间
     *
     * @param codeList
     * @param list
     * @return
     */
    private List<ListingPojo> listingInfoFormat(List<CodePojo> codeList, List<ListingPojo> list) {
        Long nowTime = DateUtil.getCurrentTimeMillis();
        Map<Integer, List<CodePojo>> listMap = codeList.stream().collect(Collectors.groupingBy(CodePojo::getListingId));
        for (ListingPojo listingPojo : list) {
            Long dValue = null;
            for (CodePojo codePojo : listMap.get(listingPojo.getId())) {
                Long dis = nowTime - codePojo.getStartTime();
                if (dValue == null) {
                    dValue = dis;
                    listingPojo.setStartTime(codePojo.getStartTime());
                    listingPojo.setEndTime(codePojo.getEndTime());
                    listingPojo.setDiscountPrice(codePojo.getDiscountPrice());
                    listingPojo.setDiscountPercentage(codePojo.getDiscountPercentage());
                    continue;
                }
                if (Math.min(dis, dValue) == dis) {
                    dValue = dis;
                    listingPojo.setStartTime(codePojo.getStartTime());
                    listingPojo.setEndTime(codePojo.getEndTime());
                    listingPojo.setDiscountPrice(codePojo.getDiscountPrice());
                    listingPojo.setDiscountPercentage(codePojo.getDiscountPercentage());
                }
            }
        }
        return list;
    }

    /**
     * 普通用户优惠卷一览
     *
     * @return
     * @throws Exception
     */
    public List<ListingPojo> getBuyerCodeList() throws Exception {
        Integer userId = SecurityUtil.getCurrentUser().getId();
        return listingMapper.getBuyerCodeList(userId);
    }

    /**
     * 商家商品编辑
     *
     * @param listingPojo
     * @param coverImageFile
     * @param secondaryImageFile
     * @throws Exception
     */
    public Map<String, String> updateListingInfo(ListingPojo listingPojo, MultipartFile coverImageFile, MultipartFile[] secondaryImageFile) throws Exception {
        Map<String, String> map = new HashMap<>();
        // 存储路径
        String path = System.getProperty("java.io.tmpdir") + uploadDir;
        log.info(">>>>>>>路径>>>>>>>>>>>>>:" + path);
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            // 主图处理
            map.putAll(uploadCoverImage(path, coverImageFile, 3, false));
            // 主图大图片路径
            listingPojo.setCoverImage(map.get("coverImage"));
            // 主图小图片路径
            listingPojo.setSmallCoverImage(map.get("smallCoverImage"));
        }

        // 副图处理
        if (secondaryImageFile != null && secondaryImageFile.length > 0) {
            map.putAll(uploadSecondaryImage(path, secondaryImageFile, 3, false));
            listingPojo.setSecondaryImageA(map.get("secondaryImageA"));
            listingPojo.setSmallSecondaryImageA(map.get("smallSecondaryImageA"));
            listingPojo.setSecondaryImageB(map.get("secondaryImageB"));
            listingPojo.setSmallSecondaryImageB(map.get("smallSecondaryImageB"));
            listingPojo.setSecondaryImageC(map.get("secondaryImageC"));
            listingPojo.setSmallSecondaryImageC(map.get("smallSecondaryImageC"));
            listingPojo.setSecondaryImageD(map.get("secondaryImageD"));
            listingPojo.setSmallSecondaryImageD(map.get("smallSecondaryImageD"));
        }

        listingPojo.setUpdateTime(DateUtil.getCurrentTimeMillis());
        listingMapper.updateListingInfo(listingPojo);
        if (listingPojo.getPrice() != null) {
            listingPojo.setDiscountPercentage(Double.valueOf(String.format("%.2f", listingPojo.getDiscountPrice() / listingPojo.getPrice() * 100)));
            codeMapper.updateCodePercentage(listingPojo);
        }
        return map;
    }

    /**
     * 热门推荐
     *
     * @param listingPojo
     * @return
     * @throws Exception
     */
    public List<ListingPojo> getPopularListing(ListingPojo listingPojo) throws Exception {
        List<ListingPojo> list = new ArrayList<>();
        Random random = new Random();
        int ranNum = random.nextInt(2);
        switch (ranNum) {
            case 0:
                list = listingMapper.getPopularListingByPer(listingPojo);
                break;
            case 1:
                list = listingMapper.getPopularListingByCode(listingPojo);
                break;
        }
        if (list != null && !list.isEmpty()) {
            List<CodePojo> codeList = codeMapper.getTimeByListingId(list);
            if (codeList != null && !codeList.isEmpty()) {
                return listingInfoFormat(codeList, list);
            }
        }
        return list;
    }

    /**
     * 主图处理
     *
     * @param path
     * @param coverImageFile
     * @param scale
     * @param flag
     * @return
     * @throws Exception
     */
    private Map<String, String> uploadCoverImage(String path, MultipartFile coverImageFile, int scale, boolean flag) throws Exception {
        Map<String, String> map = new HashMap<>();
        // 主图上传
        Map<String, String> coverMap = ImageUtil.uploadImage(coverImageFile, path, scale, flag);
        // 设置主图片回显路径
        map.put("coverImage", coverMap.get("imagePath"));
        map.put("smallCoverImage", coverMap.get("smallImagePath"));
        return map;
    }

    /**
     * 副图处理
     *
     * @param path
     * @param secondaryImageFile
     * @param scale
     * @param flag
     * @return
     * @throws Exception
     */
    private Map<String, String> uploadSecondaryImage(String path, MultipartFile[] secondaryImageFile, int scale, boolean flag) throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < secondaryImageFile.length; i++) {
            Map<String, String> secondaryMap = ImageUtil.uploadImage(secondaryImageFile[i], path, 3, false);
            switch (i) {
                case 0:
                    map.put("secondaryImageA", secondaryMap.get("imagePath"));
                    map.put("smallSecondaryImageA", secondaryMap.get("smallImagePath"));
                    break;
                case 1:
                    map.put("secondaryImageB", secondaryMap.get("imagePath"));
                    map.put("smallSecondaryImageB", secondaryMap.get("smallImagePath"));
                    break;
                case 2:
                    map.put("secondaryImageC", secondaryMap.get("imagePath"));
                    map.put("smallSecondaryImageC", secondaryMap.get("smallImagePath"));
                    break;
                case 3:
                    map.put("secondaryImageD", secondaryMap.get("imagePath"));
                    map.put("smallSecondaryImageD", secondaryMap.get("smallImagePath"));
                    break;
            }
        }
        return map;
    }
}
