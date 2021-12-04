package com.example.gameshop.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author sheng
 * @date 2021/11/22 5:13
 */
@Data
public class Game implements Serializable {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("platform")
    private String platform;

    @SerializedName("stock")
    private int stock;

    @SerializedName("price")
    private String price;

    @SerializedName("status")
    private String status;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("history_price")
    private List<Map<String, String>> history_price;

    @SerializedName("cover_image")
    private String cover_image;
}
