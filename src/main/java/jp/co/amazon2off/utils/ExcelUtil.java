package jp.co.amazon2off.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import jp.co.amazon2off.constant.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {

    /**
     * 读取excel
     *
     * @param file  导入的文件流
     * @param model 生成的类
     * @param <T>
     * @return 对象数组
     */
    public static <T> List<T> readExcel(InputStream file, Class<T> model, int count) throws Exception {
        List<T> list = new ArrayList<>();
        EasyExcel
                // 读取的文件
                .read(file)
                // 反射获取类型
                .head(model)
                // excel类型
                .excelType(ExcelTypeEnum.XLSX)
                // 读取的excel左下角(sheet)的名字
                .sheet(0)
                // 注册监听器
                .registerReadListener(new AnalysisEventListener<T>() {
                    @Override
                    public void invoke(T t, AnalysisContext analysisContext) {
                        if (list.size() < count) {
                            list.add(t);
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                        log.info("Excel读取完毕" + model);
                    }
                }).doRead();
        return list;
    }

    /**
     * 响应给浏览器的excel文件
     *
     * @param response servlet响应对象
     * @param fileName 设置文件明
     * @param list     数据列表
     * @param clazz    响应类
     * @param <T>
     * @throws IOException
     */
    public static <T> void writerExcel(HttpServletResponse response, String fileName, List<T> list, Class<T> clazz) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyExcel没有关系
            String encode = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encode + ".xlsx");
            EasyExcel.write(response.getOutputStream(), clazz)
                    //设置不自动关闭流
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(fileName)
                    .doWrite(list);
        } catch (Exception e) {
            //重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(ResponseResult.error(ErrorCodeConstants.EXC_0001, map));
        }
    }
}
