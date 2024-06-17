package com.weedshop.driver.model;

/**
 * Created by MTPC-83 on 5/17/2017.
 */

public class OrderHistory {
    String id;
    String order_code;
    String user_id;
    String driver_id;
    String store_id;
    String order_date;
    String final_total;
    String status;
    String store_name;
    String user_name;
    String user_image;
    String store_image;
    String user_image_url;
    String store_image_url;
    String product_id;
    String product_name;
    String Order_time;

    public OrderHistory() {
    }

    public OrderHistory(String id, String order_code, String user_id, String driver_id, String store_id,
                        String order_date, String final_total, String status, String store_name,
                        String user_name, String user_image, String store_image, String user_image_url,
                        String store_image_url, String product_id, String product_name) {
        this.id = id;
        this.order_code = order_code;
        this.user_id = user_id;
        this.driver_id = driver_id;
        this.store_id = store_id;
        this.order_date = order_date;
        this.final_total = final_total;
        this.status = status;
        this.store_name = store_name;
        this.user_name = user_name;
        this.user_image = user_image;
        this.store_image = store_image;
        this.user_image_url = user_image_url;
        this.store_image_url = store_image_url;
        this.product_id = product_id;
        this.product_name = product_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getFinal_total() {
        return final_total;
    }

    public void setFinal_total(String final_total) {
        this.final_total = final_total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getStore_image() {
        return store_image;
    }

    public void setStore_image(String store_image) {
        this.store_image = store_image;
    }

    public String getUser_image_url() {
        return user_image_url;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getStore_image_url() {
        return store_image_url;
    }

    public void setStore_image_url(String store_image_url) {
        this.store_image_url = store_image_url;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getOrder_time() {
        return Order_time;
    }

    public void setOrder_time(String order_time) {
        Order_time = order_time;
    }
}