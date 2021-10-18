package jp.co.amazon2off.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页数
     */
    @JsonIgnore
    private Integer currentPage = 1;
    /**
     * 每页个数
     */
    @JsonIgnore
    private Integer limit = 10;
}
