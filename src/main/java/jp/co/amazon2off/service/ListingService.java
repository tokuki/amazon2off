package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.CodeMapper;
import jp.co.amazon2off.mapper.ListingMapper;
import jp.co.amazon2off.pojo.CodePojo;
import jp.co.amazon2off.pojo.ListingPojo;
import jp.co.amazon2off.utils.DateUtils;
import jp.co.amazon2off.utils.FileUtils;
import jp.co.amazon2off.utils.ImageUtils;
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
        log.info(">>>>>>>路径>>>>>>>>>>>>>:"+path);
        // 主图处理
        map.putAll(uploadCoverImage(path, coverImageFile, 3, false));
        // 主图大图片路径
        listingPojo.setCoverImage(map.get("imagePath"));
        // 主图小图片路径
        listingPojo.setSmallCoverImage(map.get("smallImagePath"));

        // 副图处理
        if (secondaryImageFile != null && secondaryImageFile.length > 0) {
            map.putAll(uploadSecondaryImage(path, secondaryImageFile, 3, false));
            listingPojo.setSecondaryImageA(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageA(map.get("smallImagePath"));
            listingPojo.setSecondaryImageB(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageB(map.get("smallImagePath"));
            listingPojo.setSecondaryImageC(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageC(map.get("smallImagePath"));
            listingPojo.setSecondaryImageD(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageD(map.get("smallImagePath"));
        }

        listingPojo.setUserId(SecurityUtil.getCurrentUser().getId());
        listingPojo.setAddTime(DateUtils.getCurrentTimeMillis());
        // 优惠码处理
        List<String> codeList = FileUtils.readFileByInputStream(codeFile.getInputStream());
        // 优惠码相关
        CodePojo codePojo = new CodePojo();
        codePojo.setListingId(listingPojo.getId());
        codePojo.setStartTime(startTime);
        codePojo.setEndTime(endTime);
        codePojo.setDiscountPrice(listingPojo.getDiscountPrice());
        codePojo.setDiscountPercentage(Double.valueOf(String.format("%.2f", listingPojo.getDiscountPrice() / listingPojo.getPrice() * 100)));
        codePojo.setAddTime(DateUtils.getCurrentTimeMillis());

        // 添加商品
        listingMapper.addListing(listingPojo);
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
    public List<ListingPojo> getListingList(ListingPojo listingPojo) throws Exception {
        List<Integer> listingIdList = new ArrayList<>();
        if (listingPojo.getType() == 0) {
            List<ListingPojo> list = listingMapper.getListByKeyWords(listingPojo.getKeyWords());
            if (list != null && !list.isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list);
                if (codePojo != null && !codePojo.isEmpty()) {
                    return listingInfoFormat(codePojo, list);
                }
            }
        }
        if (listingPojo.getType() == 1 || listingPojo.getType() == 2 || listingPojo.getType() == 3) {
            List<ListingPojo> list = listingMapper.getListByDiscount(listingPojo);
            if (list != null && !list.isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list);
                if (codePojo != null && !codePojo.isEmpty()) {
                    if (listingPojo.getType() == 1) {
                        return listingInfoFormat(codePojo, list).stream().filter(i -> i.getDiscountPercentage() == 100.00).collect(Collectors.toList());
                    }
                    if (listingPojo.getType() == 2) {
                        return listingInfoFormat(codePojo, list).stream().filter(i -> i.getDiscountPercentage() < 50.00).collect(Collectors.toList());
                    }
                    if (listingPojo.getType() == 3) {
                        return listingInfoFormat(codePojo, list).stream().filter(i -> i.getDiscountPercentage() >= 50.00 && i.getDiscountPercentage() < 100.00).collect(Collectors.toList());
                    }
                }
            }
        }
        // 近期要开始活动的商品（2天）
        if (listingPojo.getType() == 4) {
            listingIdList = codeMapper.getListingIdByTime(System.currentTimeMillis(), DateUtils.getBeforeTwoDay()[0], DateUtils.getBeforeTwoDay()[1]);
        }
        // 优惠码被领取最多的商品
        if (listingPojo.getType() == 5) {
            listingIdList = codeMapper.getListingIdByCode();
        }
        if (listingIdList != null && !listingIdList.isEmpty()) {
            List<ListingPojo> list = listingMapper.getListByListingId(listingIdList);
            if (list != null && !list.isEmpty()) {
                List<CodePojo> codePojo = codeMapper.getTimeByListingId(list);
                if (codePojo != null && !codePojo.isEmpty()) {
                    return listingInfoFormat(codePojo, list);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * 商品详情查询
     *
     * @param listingPojo
     * @return
     */
    public List<ListingPojo> getListingInfo(ListingPojo listingPojo) throws Exception {
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
     * @param listingList
     * @return
     */
    private List<ListingPojo> listingInfoFormat(List<CodePojo> codeList, List<ListingPojo> listingList) {
        Long nowTime = DateUtils.getCurrentTimeMillis();
        Map<Integer, List<CodePojo>> listMap = codeList.stream().collect(Collectors.groupingBy(CodePojo::getListingId));
        for (ListingPojo listingPojo : listingList) {
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
        return listingList;
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
        log.info(">>>>>>>路径>>>>>>>>>>>>>:"+path);
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            // 主图处理
            map.putAll(uploadCoverImage(path, coverImageFile, 3, false));
            // 主图大图片路径
            listingPojo.setCoverImage(map.get("imagePath"));
            // 主图小图片路径
            listingPojo.setSmallCoverImage(map.get("smallImagePath"));
        }

        // 副图处理
        if (secondaryImageFile != null && secondaryImageFile.length > 0) {
            map.putAll(uploadSecondaryImage(path, secondaryImageFile, 3, false));
            listingPojo.setSecondaryImageA(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageA(map.get("smallImagePath"));
            listingPojo.setSecondaryImageB(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageB(map.get("smallImagePath"));
            listingPojo.setSecondaryImageC(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageC(map.get("smallImagePath"));
            listingPojo.setSecondaryImageD(map.get("imagePath"));
            listingPojo.setSmallSecondaryImageD(map.get("smallImagePath"));
        }

        listingPojo.setUpdateTime(DateUtils.getCurrentTimeMillis());
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
        Map<String, String> coverMap = ImageUtils.uploadImage(coverImageFile, path, scale, flag);
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
            Map<String, String> secondaryMap = ImageUtils.uploadImage(secondaryImageFile[i], path, 3, false);
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
