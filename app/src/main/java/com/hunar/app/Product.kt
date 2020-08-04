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


class Product(val prodCategory: String, val prodName : String, val prodImg: String, val prodDesc: String, val prodPrice: String, val keywords: kwArray, val sellerId: String, val delivered: String, val verif: String, val rating: ProdReview){
    constructor() : this("", "", "","","", kwArray(),"","","", ProdReview()){

    }
}
