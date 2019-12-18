package com.ics.fvfs.Model;

import java.io.Serializable;

public class OfferModel implements Serializable {
    private String productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private String categoryId;
    private String inStock;
    private String price;
    private String unitValue;
    private String unit;
    private String increament;
    private String mrp;
    private String todayDeals;
    private String offersCat;
    private String dealsDescription;
    private String offersCatDesc;
    private String emi;
    private String warranty;

    private String pack1;
    private String pack2;
    private String mrp1;
    private String mrp2;
    private String price1;
    private String price2;


    public OfferModel(String productId, String productName, String productDescription, String productImage, String categoryId, String inStock, String price, String unitValue, String unit, String increament, String mrp, String todayDeals, String offersCat, String dealsDescription, String offersCatDesc, String emi, String warranty, String pack1, String pack2, String mrp1, String mrp2, String price1, String price2) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productImage = productImage;
        this.categoryId = categoryId;
        this.inStock = inStock;
        this.price = price;
        this.unitValue = unitValue;
        this.unit = unit;
        this.increament = increament;
        this.mrp = mrp;
        this.todayDeals = todayDeals;
        this.offersCat = offersCat;
        this.dealsDescription = dealsDescription;
        this.offersCatDesc = offersCatDesc;
        this.emi = emi;
        this.warranty = warranty;
        this.pack1 = pack1;
        this.pack2 = pack2;
        this.mrp1 = mrp1;
        this.mrp2 = mrp2;
        this.price1 = price1;
        this.price2 = price2;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIncreament() {
        return increament;
    }

    public void setIncreament(String increament) {
        this.increament = increament;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getTodayDeals() {
        return todayDeals;
    }

    public void setTodayDeals(String todayDeals) {
        this.todayDeals = todayDeals;
    }

    public String getOffersCat() {
        return offersCat;
    }

    public void setOffersCat(String offersCat) {
        this.offersCat = offersCat;
    }

    public String getDealsDescription() {
        return dealsDescription;
    }

    public void setDealsDescription(String dealsDescription) {
        this.dealsDescription = dealsDescription;
    }

    public String getOffersCatDesc() {
        return offersCatDesc;
    }

    public void setOffersCatDesc(String offersCatDesc) {
        this.offersCatDesc = offersCatDesc;
    }

    public String getEmi() {
        return emi;
    }

    public void setEmi(String emi) {
        this.emi = emi;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getPack1() {
        return pack1;
    }

    public void setPack1(String pack1) {
        this.pack1 = pack1;
    }

    public String getPack2() {
        return pack2;
    }

    public void setPack2(String pack2) {
        this.pack2 = pack2;
    }

    public String getMrp1() {
        return mrp1;
    }

    public void setMrp1(String mrp1) {
        this.mrp1 = mrp1;
    }

    public String getMrp2() {
        return mrp2;
    }

    public void setMrp2(String mrp2) {
        this.mrp2 = mrp2;
    }

    public String getPrice1() {
        return price1;
    }

    public void setPrice1(String price1) {
        this.price1 = price1;
    }

    public String getPrice2() {
        return price2;
    }

    public void setPrice2(String price2) {
        this.price2 = price2;
    }
}
