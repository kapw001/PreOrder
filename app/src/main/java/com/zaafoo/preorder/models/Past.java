package com.zaafoo.preorder.models;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */

public class Past {

    String advance;
    String total;
    String totalPaid;
    String restid;
    String restname;
    String tableTotal;
    String foodTotal;
    String tax;
    String booking_id;
    String date;
    String time;
    String tables;
    ArrayList<Menu> menu;
    String booking_success;
    String booking_cancelled;

    public String getBooking_cancelled() {
        return booking_cancelled;
    }

    public void setBooking_cancelled(String booking_cancelled) {
        this.booking_cancelled = booking_cancelled;
    }

    public String getBooking_success() {
        return booking_success;
    }

    public void setBooking_success(String booking_success) {
        this.booking_success = booking_success;
    }

    public ArrayList<Menu> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<Menu> menu) {
        this.menu = menu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getAdvance() {
        return advance;
    }

    public void setAdvance(String advance) {
        this.advance = advance;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(String totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getRestid() {
        return restid;
    }

    public void setRestid(String restid) {
        this.restid = restid;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }

    public String getTableTotal() {
        return tableTotal;
    }

    public void setTableTotal(String tableTotal) {
        this.tableTotal = tableTotal;
    }

    public String getFoodTotal() {
        return foodTotal;
    }

    public void setFoodTotal(String foodTotal) {
        this.foodTotal = foodTotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }
}
