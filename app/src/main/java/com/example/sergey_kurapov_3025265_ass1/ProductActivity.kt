package com.example.sergey_kurapov_3025265_ass1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProductActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val selectedProduct: Product = intent.getSerializableExtra("product") as Product

        // set text values on text views in activity_product
        val txtProductId = findViewById<TextView>(R.id.productId)
        txtProductId.text = selectedProduct.productId.toString()

        val txtProductName = findViewById<TextView>(R.id.productName)
        txtProductName.text = selectedProduct.productName

        val txtProductDesc = findViewById<TextView>(R.id.productDesc)
        txtProductDesc.text = selectedProduct.productDescription

        val txtPrice = findViewById<TextView>(R.id.price)
        txtPrice.text = "€%.2f".format(selectedProduct.pricePerItem)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener(View.OnClickListener { view ->

            val cartActivity: Intent = Intent(this@ProductActivity, ShoppingCartActivity::class.java)

            val txtProductId = findViewById<TextView>(R.id.productId)
            cartActivity.putExtra("productId", txtProductId.text.toString())

            val txtQuantity = findViewById<EditText>(R.id.editQuantity)
            if (txtQuantity.text.toString() != "" && txtQuantity.text.toString() != "0"){
                cartActivity.putExtra("quantity", txtQuantity.text.toString())

                this.startActivity(cartActivity)
            }
        })

    }


}