package jp.co.amazon2off.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExcelPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "Code")
    private String code;

}
