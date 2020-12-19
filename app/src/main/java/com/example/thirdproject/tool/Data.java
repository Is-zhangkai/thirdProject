package com.example.thirdproject.tool;

public class Data {


//     img : http://dummyimage.com/400x400
//      quantity : 8
//      user_id : 1
//     price : 46
//      name : 件手其家
//      good_id : 2
//      info : fugiat minim
//      "good_id": 1,
//      "goods_count": 1,
//      "goods_price": 12

    private String img;
    private int quantity;
    private int user_id;
    private double price;
    private String name;
    private int good_id;
    private String info;
    /**
     * goods_price : 12
     * good_id : 1
     * goods_count : 1
     */
    private double goods_price;

    private int goods_count;

    public void setImg(String img) {
        this.img = img;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUser_id() {
        return user_id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }



    public String getInfo() {
        return info;
    }

    public void setGoods_price(double goods_price) {
        this.goods_price = goods_price;
    }

    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }

    public void setGoods_count(int goods_count) {
        this.goods_count = goods_count;
    }

    public double getGoods_price() {
        return goods_price;
    }

    public int getGood_id() {
        return good_id;
    }

    public int getGoods_count() {
        return goods_count;
    }
}
