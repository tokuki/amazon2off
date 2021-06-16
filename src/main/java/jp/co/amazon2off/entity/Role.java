package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    /**
     * ID
     */
    private Integer id;
    /**
     * 角色
     */
    private String role;
}
