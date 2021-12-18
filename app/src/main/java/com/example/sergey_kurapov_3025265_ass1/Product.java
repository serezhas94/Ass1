package com.example.sergey_kurapov_3025265_ass1;

public class Product implements java.io.Serializable {
    private int _productId;
    private String _productName;
    private String _productDescription;
    private  double _pricePerItem;
    private int _quantity;

    public Product(){}

    public Product(int productId, String productName, String productDescription, double pricePerItem, int quantity){
        _productId = productId;
        _productName = productName;
        _productDescription = productDescription;
        _pricePerItem = pricePerItem;
        _quantity = quantity;
    }

    public int getProductId(){
        return _productId;
    }
    public void setProductId(int productId){
        _productId = productId;
    }

    public String getProductName(){
        return _productName;
    }
    public void setProductName(String productName){
        _productName = productName;
    }

    public String getProductDescription(){
        return _productDescription;
    }
    public void setProductDescription(String productDescription){
        _productDescription = productDescription;
    }

    public double getPricePerItem(){
        return _pricePerItem;
    }
    public void setPricePerItem(double pricePerItem){
        _pricePerItem = pricePerItem;
    }

    public int getQuantity(){
        return _quantity;
    }
    public void setQuantity(int quantity){
        _quantity = quantity;
    }
}
