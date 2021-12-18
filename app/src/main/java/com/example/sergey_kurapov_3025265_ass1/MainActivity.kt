package com.example.sergey_kurapov_3025265_ass1

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity()  {
    // private fields of the class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //database handler
        val db = DatabaseHandler(this)

        // if required add catalog of products
        db.addInitialProducts()

        // get all products
        val products = db.allProducts

        // create adapter to populate products in list view
        val adapter = ProductAdapter(this, products)

        // get list view by id from activity_main
        val shoppingList = findViewById<ListView>(R.id.shopping_ListView)

        // populate products in list view
        shoppingList.adapter = adapter

        // list view item click listener
        shoppingList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                val productActivity: Intent = Intent(this@MainActivity, ProductActivity::class.java)

                val selectedItem: Product = parent.getItemAtPosition(position) as Product
                productActivity.putExtra("product", selectedItem)

                this.startActivity(productActivity)
            }
    }


}