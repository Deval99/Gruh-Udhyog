package com.hunar.app


//ProductId

//SellerId
//productName
//productImage
//productDescription
//productPrice
//productKeywords
//productCertificate
//productCategories


class Product(val prodCategory: String, val prodName : String, val prodImg: String, val prodDesc: String, val prodPrice: String, val keywords: String, val sellerId: String, var quantity: Int){
    constructor() : this("", "", "","","", "","", 0){

    }
}
