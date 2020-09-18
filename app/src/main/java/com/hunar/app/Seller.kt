package com.hunar.app

//lateinit var sellerAddr : String
//lateinit var sellerCat : String;
//lateinit var sellerCity : String;
//lateinit var sellerDesc : String;
//lateinit var sellerName : String;
//lateinit var sellerPhone : String;
//lateinit var sellerState : String;

class Seller(val sellerId: String, val sellerAddr: String, val sellerCat : String, val sellerCity: String, val sellerDesc: String, val sellerName: String, val sellerPhone: String, val sellerState: String){
    constructor() : this("","", "", "","","", "",""){

    }
}
