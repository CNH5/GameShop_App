package com.example.gameshop.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sheng
 * @date 2021/11/17 23:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclePackGame {
    private long id;

    private int num;

    private String name;

    private double now_price;

    private double old_price;

    private String cover_image;

    private String platform;

    private String status;

    private int stock;

    private boolean selected;
}
