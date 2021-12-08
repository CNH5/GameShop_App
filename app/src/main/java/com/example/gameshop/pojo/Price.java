package com.example.gameshop.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sheng
 * @date 2021/12/8 11:43
 */
@Data
public class Price implements Serializable {
    private String date;
    private float price;
}
