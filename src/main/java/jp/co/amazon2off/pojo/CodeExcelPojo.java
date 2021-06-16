package jp.co.amazon2off.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExcelPojo {

    @ExcelProperty(value = "Code")
    private String code;

}
