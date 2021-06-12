package com.kelvin.dompet.Entity;

public class detailEntity {
    private int tId;
    private String date;
    private String category;
    private int ammount;
    private String description;
    private String photo;

    public detailEntity(int tId, String date, String category, int ammount, String description, String photo) {
        this.tId = tId;
        this.date = date;
        this.category = category;
        this.ammount = ammount;
        this.description = description;
        this.photo = photo;
    }

    public detailEntity() {
    }

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmmount() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount = ammount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
