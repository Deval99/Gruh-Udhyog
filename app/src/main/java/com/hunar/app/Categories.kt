package com.hunar.app

import java.io.Serializable

public class Categories : Serializable{
    var title: String? = null
    var desc: String? = null
    var image: String? = null

    constructor(title: String?, desc: String?, image: String?) {
        this.title = title
        this.desc = desc
        this.image = image
    }

    constructor() {}
}
