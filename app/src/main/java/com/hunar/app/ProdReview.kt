package com.hunar.app


//productReviewId

//orderId
//productID(s)
//sellerID
//customerID
//reviewMessage
//reviewStars
class ProdReview (val ordId: String, val prodId: String , val sellerId: String, val custId : String, val message: String, val stars: String) {
    constructor() : this("","","", "", "", ""){

    }
}