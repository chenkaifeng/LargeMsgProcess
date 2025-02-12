package com.kfc.test.large.file.model;

import com.kfc.test.large.file.annotation.FixedLengthField;
import com.kfc.test.large.file.annotation.FixedLengthType;
import com.kfc.test.large.file.enums.FixedLengthTypeEnum;
import lombok.Data;

@Data
@FixedLengthType(type = FixedLengthTypeEnum.BYTE)
public class Person {

    @FixedLengthField(length = 20, trim = true)
    private String name;

    @FixedLengthField(length = 3, trim = true)
    private int age;

    @FixedLengthField(length = 30, trim = true)
    private String address;

}
