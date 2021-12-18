package com.example.sergey_kurapov_3025265_ass1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ShoppingCartActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_shopping_cart)

        val productId = Integer.parseInt(intent.getStringExtra("productId"))
        val quantity = Integer.parseInt(intent.getStringExtra("quantity"))

        //add to shopping cart
        val db = DatabaseHandler(this)
        db.addProductToShoppingCart(productId, quantity)

        //Doesn't work as expected
        //get all from shopping cart
        val items = db.allShoppingCartItems


    }

}