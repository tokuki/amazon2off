package jp.co.amazon2off.utils;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回类
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseResult<T> implements Serializable {
    public static final String ERROR_CODE_SUCCESS = "";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("code")
    private String code;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("data")
    private T data;

    @JsonProperty("date")
    private Long date;

    @JsonIgnore
    private final Map<String, Object> attachments = new HashMap<>();

    /**
     * 直接返回成功
     *
     * @return 应答对象
     */
    public static ResponseResult success() {
        val result = new ResponseResult();
        result.setStatusCode(200);
        result.setCode(SUCCESS);
        result.setDate(DateUtil.getCurrentTimeMillis());
        return result;
    }

    /**
     * 返回成功，并携带一个应答数据对象
     *
     * @param data 应答数据对象
     * @param <T>  数据对象泛型
     * @return 应答对象
     */
    public static <T> ResponseResult success(T data) {
        val result = new ResponseResult<T>();
        result.setStatusCode(200);
        result.setCode(SUCCESS);
        result.setDate(DateUtil.getCurrentTimeMillis());
        result.setData(data);
        return result;
    }

    /**
     * 返回成功，携带一个应答数据对象，并允许扩展额外的应答扩展状态
     *
     * @param data        应答数据对象
     * @param attachments 应答扩展状态
     * @param <T>         数据对象泛型
     * @return 应答对象
     */
    public static <T> ResponseResult success(T data, Map<String, Object> attachments) {
        val result = success(data);
        result.setStatusCode(200);
        result.getAttachments().putAll(attachments);
        result.setDate(DateUtil.getCurrentTimeMillis());
        return result;
    }

    /**
     * 返回失败，携带错误码和错误消息
     *
     * @param code
     * @return
     */
    public static ResponseResult error(@NotBlank String code) {
        val result = new ResponseResult();
        result.setStatusCode(100);
        result.setCode(code);
        result.setDate(DateUtil.getCurrentTimeMillis());
        return result;
    }

    public static ResponseResult error(@NotBlank String code, Map<String, Object> attachments) {
        val result = error(code);
        result.setStatusCode(100);
        result.getAttachments().putAll(attachments);
        result.setDate(DateUtil.getCurrentTimeMillis());
        return result;
    }

    public static <T> ResponseResult error(@NotBlank String code, T data) {
        val result = new ResponseResult<T>();
        result.setStatusCode(100);
        result.setCode(code);
        result.setDate(DateUtil.getCurrentTimeMillis());
        result.setData(data);
        return result;
    }

//    public static ResponseResult<Void> error(@NotBlank String code, @NotBlank String message, Map<String, Object> attachments) {
//        val result = error(code, message);
//        result.getAttachments().putAll(attachments);
//        return result;
//    }

    @JsonAnySetter
    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAttachments() {
        return attachments;
    }

}
