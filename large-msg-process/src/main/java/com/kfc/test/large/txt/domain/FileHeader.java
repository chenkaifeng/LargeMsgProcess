package com.kfc.test.large.txt.domain;

import com.kfc.test.large.txt.annotation.FieldSchema;
import com.kfc.test.large.txt.annotation.FileHeaderSchema;

@FileHeaderSchema(
        type = FileHeaderSchema.ParseType.FIXED_LENGTH,
        charset = "GBK"
)
public class FileHeader {
    @FieldSchema(index = 0, length = 3)
    private String version;

    @FieldSchema(index = 1, length = 4)
    private int recordCount;

    public String getVersion() { return version; }
    public int getRecordCount() { return recordCount; }

    @Override
    public String toString() {
        return "FileHeader{version='" + version + "', recordCount=" + recordCount + "}";
    }
}