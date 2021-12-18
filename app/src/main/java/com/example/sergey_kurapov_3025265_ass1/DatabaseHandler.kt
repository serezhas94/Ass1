package com.example.sergey_kurapov_3025265_ass1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

internal class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTables = "$createProductsTable; $createShoppingCartTable"

        // create tables
        db.execSQL(createTables)
    }

    private val createProductsTable: String
        private get() = ("create table " + TABLE_PRODUCTS + "(" +
                "product_id integer primary key autoincrement," +
                "product_name string," +
                "product_description text," +
                "price_per_item decimal(10,2)," +
                "quantity integer" +
                ")")
    private val createShoppingCartTable: String
        private get() {
            return ("create table " + TABLE_SHOPPING_CART + "(" +
                    "shopping_cart_id integer primary key autoincrement," +
                    "FOREIGN KEY(product_id) REFERENCES products(product_id)," +
                    "quantity integer" +
                    ")")
        }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // drop products table if exists
        db.execSQL("drop table if exists $TABLE_PRODUCTS")

        // drop shoppingCart table if exists
        db.execSQL("drop table if exists $TABLE_SHOPPING_CART")
        onCreate(db)
    }

    // returns list of all products
    val allProducts: ArrayList<Product>
        get() {
            val products = ArrayList<Product>()

            // select all products string query
            val selectQuery = "select * from $TABLE_PRODUCTS"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            //looping through if any exists
            if (cursor.moveToFirst()) {
                do {
                    val product = getProductFromCursor(cursor)

                    // add to products list
                    products.add(product)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close() // Closing database connection
            return products
        }

    // get product by id
    fun getProduct(productId: Int): Product? {
        var product: Product? = null
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            arrayOf(
                "product_id",
                "product_name",
                "product_description",
                "price_per_item",
                "quantity"
            ),
            "product_id = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            product = getProductFromCursor(cursor)
        }
        cursor.close()
        db.close() // Closing database connection
        return product
    }

    fun addProduct(product: Product) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("product_name", product.productName)
        values.put("product_description", product.productDescription)
        values.put("price_per_item", product.pricePerItem)
        values.put("quantity", product.quantity)

        // Inserting Row
        db.insert(TABLE_PRODUCTS, null, values)
        db.close() // Closing database connection
    }

    fun deleteProduct(productId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_PRODUCTS, "product_id = ?", arrayOf(Integer.toString(productId)))
        db.close()
    }

    // update overall product quantity in products table
    fun updateProductQuantity(productId: Int, quantity: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("quantity", quantity)

        // updating row
        val result = db.update(
            TABLE_PRODUCTS, values,
            "product_id = ?", arrayOf(productId.toString())
        )
        db.close()
        return result
    }

    //return list of items in the shopping cart
    val allShoppingCartItems: ArrayList<ShoppingCartItem>
        get() {
            val items = ArrayList<ShoppingCartItem>()

            // select all items of product in shopping cart string query
            val selectQuery = "select  pr.product_id, pr.product_name," +
                    " pr.product_description, pr.price_per_item, pr.quantity," +
                    " sh.shopping_cart_id, sh.quantity" +
                    " from sh." + TABLE_SHOPPING_CART +
                    " inner join pr." + TABLE_PRODUCTS + " on sh.product_id = pr.product_id"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            //looping through if any exists
            if (cursor.moveToFirst()) {
                do {
                    // retrieve product first
                    val product = getProductFromCursor(cursor)

                    // retrieve shopping cart details
                    val shoppingCartId = cursor.getString(5).toInt()
                    val shoppingQuantity = cursor.getString(6).toInt()

                    // create shopping cart item
                    val shoppingCartItem =
                        ShoppingCartItem(shoppingCartId, product, shoppingQuantity)

                    // add to shopping cart items list
                    items.add(shoppingCartItem)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close() // Closing database connection
            return items
        }

    fun getShoppingCartItemByProductId(productId: Int): ShoppingCartItem? {
        var item: ShoppingCartItem? = null

        // select all items of product in shopping cart string query
        val selectQuery = "select shopping_cart_id, quantity" +
                " from " + TABLE_SHOPPING_CART +
                " where product_id = ?"
        val db = this.readableDatabase
        try {
            val cursor = db.rawQuery(selectQuery, arrayOf(productId.toString()))

            if (cursor != null) {
                cursor.moveToFirst()
                val shoppingCartId = cursor.getString(0).toInt()
                val shoppingQuantity = cursor.getString(1).toInt()
                val product = getProduct(productId)
                item = ShoppingCartItem(shoppingCartId, product, shoppingQuantity)
            }
            cursor.close()
        }
        catch(e:Exception){
            //TO DO
        }
        finally {
            db.close() // Closing database connection
        }

        return item
    }

    fun addProductToShoppingCart(productId: Int, purchasedQuantity: Int): String {
        //get product from products table
        val product = getProduct(productId)

        // check purchased quantity against available quantity of product
        if (product!!.quantity < purchasedQuantity) {
            return "Not available in stock"
        }

        //update available quantity of product
        val leftInStock = product.quantity - purchasedQuantity
        updateProductQuantity(productId, leftInStock)

        // add or update product to shopping cart
        var item = getShoppingCartItemByProductId(productId)

        val db = this.writableDatabase
        val values = ContentValues()
        if (item != null) {
            val updatedQuantity = item.quantity + purchasedQuantity
            values.put("quantity", updatedQuantity) // quantity of purchased product
            db.update(
                TABLE_SHOPPING_CART, values,
                "product_id = ?", arrayOf(productId.toString())
            )

            // delete shopping item if updated to zero quantity purchased
            item = getShoppingCartItemByProductId(productId)
            if (item!!.quantity <= 0) {
                deleteShoppingCartItem(item.shoppingCartId)
            }
        } else {
            values.put("product_id", productId) // product id
            values.put("quantity", purchasedQuantity) // quantity of purchased product

            // Inserting Row
            db.insert(TABLE_SHOPPING_CART, null, values)
        }
        db.close() // Closing database connection
        return "Success"
    }

    fun deleteShoppingCartItem(shoppingCartId: Int) {
        val db = this.writableDatabase
        db.delete(
            TABLE_SHOPPING_CART,
            "shopping_cart_id = ?",
            arrayOf(Integer.toString(shoppingCartId))
        )
        db.close()
    }

    fun deleteAllShoppingCart() {

        // delete all from shopping cart
        val deleteQuery = "delete from $TABLE_SHOPPING_CART"
        val db = this.writableDatabase
        db.execSQL(deleteQuery)
        db.close()
    }

    fun restoreProductQuantityByDeletingCart() {
        val items: List<ShoppingCartItem> = allShoppingCartItems
        for (item in items) {
            //restore product quantity in products
            addProductToShoppingCart(item.product.productId, -1 * item.quantity)
        }
    }

    private fun getProductFromCursor(cursor: Cursor): Product {
        val product = Product()
        product.productId = cursor.getString(0).toInt()
        product.productName = cursor.getString(1)
        product.productDescription = cursor.getString(2)
        product.pricePerItem = cursor.getString(3).toDouble()
        product.quantity = cursor.getString(4).toInt()
        return product
    }

    fun addInitialProducts() {
        val products: ArrayList<Product> = this.allProducts
        if (products.size == 0) {
            val arrayOfProducts = arrayOf(
                arrayOf(
                    "Cooking oil 1L",
                    "For healthier options, opt for olive oil,l rapeseed oil, or other oils choc full of omega 3",
                    "10",
                    "1.75"
                ),
                arrayOf(
                    "Butter 200G",
                    "If you’re making sandwiches, you’re going to need something to spread on your slices. You’ll also need this if you fancy making a cake.",
                    "10",
                    "3.45"
                ),
                arrayOf(
                    "Milk 2L",
                    "A breakfast essential if you have cereal for breakfast, if prefer your hot drinks a lighter shade of brown, or want to make a sauce. " +
                            "Vegans and the health conscious should check out milk alternatives like soy or almond.",
                    "10",
                    "2.00"
                ),
                arrayOf(
                    "Eggs 12",
                    "These versatile little things are essential ingredients for a cake or can be made many different ways for a quick breakfast or lunch.",
                    "10",
                    "3.50"
                ),
                arrayOf(
                    "Onions 1KG",
                    "it’s difficult to imagine a homemade dish that doesn’t utilize these. " +
                            "These keep well in cool dark places and are always good to have a few extra just in case you " +
                            "find yourself doing some extra cooking or having to make bigger portions.",
                    "10",
                    "1.00"
                ),
                arrayOf(
                    "Chopped tomatoes 250G",
                    "These are incredibly handy as it saves skinning and chopping fresh tomatoes yourself. Furthermore, these form the basis of many sauces and meals.",
                    "10",
                    "1.25"
                ),
                arrayOf(
                    "Soup 300G",
                    "Tinned soup tends to contain more nutrients than their instant counterparts. However, they do take up a lot more storage and are a little pricier",
                    "10",
                    "2.40"
                ),
                arrayOf(
                    "Salt 150G",
                    "This will enhance the flavor in your meals. However, if you’re on a low-sodium diet such as the DASH diet, you might want to replace this with some herbs and spices.",
                    "10",
                    "0.99"
                ),
                arrayOf(
                    "Honey 125G",
                    "This as a natural sweetener for things like yogurt and cereal",
                    "10",
                    "3.75"
                ),
                arrayOf(
                    "Sugar 1KG",
                    "Add some sweetness to your tea, your coffee, your cakes, and your life! Buy artificial sweeteners if you concerned about becoming healthier and cutting back on the calories.",
                    "10",
                    "1.14"
                )
            )
            for (arrayProduct: Array<String> in arrayOfProducts) {
                val product = Product()
                product.productName = arrayProduct[0]
                product.productDescription = arrayProduct[1]
                product.quantity = arrayProduct[2].toInt()
                product.pricePerItem = arrayProduct[3].toDouble()
                addProduct(product)
            }
        }
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "shoppingDB"
        private const val TABLE_PRODUCTS = "products"
        private const val TABLE_SHOPPING_CART = "shopping_cart"
    }
}