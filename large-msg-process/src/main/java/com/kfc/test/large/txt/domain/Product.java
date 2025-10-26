package com.kfc.test.large.txt.domain;


import com.kfc.test.large.txt.annotation.FieldSchema;
import com.kfc.test.large.txt.annotation.FileBodySchema;

@FileBodySchema(
        type = FileBodySchema.ParseType.DELIMITER,
        delimiter = ",",
        charset = "UTF-8",
        headerClass = FileHeader.class
)
public class Product {
    @FieldSchema(index = 0)
    private int id;

    @FieldSchema(index = 1)
    private String name;

    @FieldSchema(index = 2)
    private double price;

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}