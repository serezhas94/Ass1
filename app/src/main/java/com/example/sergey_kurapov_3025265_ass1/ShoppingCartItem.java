package com.example.sergey_kurapov_3025265_ass1;

public class ShoppingCartItem {
    private int _shoppingCartId;
    private Product _product;
    private int _quantity;

    public ShoppingCartItem(){}

    public ShoppingCartItem(int shoppingCartId, Product product, int quantity){
        _shoppingCartId = shoppingCartId;
        _product = product;
        _quantity = quantity;
    }

    public int getShoppingCartId(){
        return _shoppingCartId;
    }
    public void setShoppingCartId(int shoppingCartId){
        _shoppingCartId = shoppingCartId;
    }

    public Product getProduct(){
        return _product;
    }
    public void setProduct(Product product){
        _product = product;
    }

    public int getQuantity(){
        return _quantity;
    }
    public void setQuantity(int quantity){
        _quantity = quantity;
    }
}
