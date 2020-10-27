package com.hfad.mycosts;

public class Buying {
    private String name;
    private String category;
    private String subCategory;
    private int idDB;
    private int price;
    private int dayOfMonth;
    private int weekOfYear;
    private int month;
    private int year;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public int getIdDB() {
        return idDB;
    }

    public int getPrice() {
        return price;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }


    public Buying(String name, String category, String subCategory, int idDB, int price, int dayOfMonth, int weekOfYear, int month, int year) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.idDB = idDB;
        this.price = price;
        this.dayOfMonth = dayOfMonth;
        this.weekOfYear = weekOfYear;
        this.month = month;
        this.year = year;
    }
}

