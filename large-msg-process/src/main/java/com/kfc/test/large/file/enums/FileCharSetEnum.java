package com.kfc.test.large.file.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: Chenkf
 * @create: 2025/02/12
 **/
public enum FileCharSetEnum {

    GBK("GBK", "GBK格式"),

    UTF_8("UTF-8", "UTF-8格式");

    /** 枚举代码 */
    private String code;

    /** 枚举描述 */
    private String desc;

    private FileCharSetEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**

     * 根据代码获取枚举，如果code对应的枚举不存在，则返回null

     * @param code 枚举代码

     * @return     对应的枚举对象

     */
    public static FileCharSetEnum getByCode(String code) {
        for (FileCharSetEnum eachValue : FileCharSetEnum.values()) {
            if (StringUtils.equals(code, eachValue.getCode())) {
                return eachValue;
            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
