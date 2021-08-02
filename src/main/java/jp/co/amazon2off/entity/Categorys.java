package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categorys {

    /**
     * ID
     */
    private Integer id;
    /**
     * 商品类目
     */
    private String catalog;
}
