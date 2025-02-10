package com.example.newsagencyproject

class DataClass {

    var dataImage: String? = null
    var dataTitle: String? = null
    var dataDesc: String? = null
    var status: String? = "Pending"
    var email: String = ""


    // No-argument constructor (required for Firebase to deserialize data)
    constructor()

    // Constructor with parameters (for setting values when needed)
    constructor(dataImage: String?, dataTitle: String?, dataDesc: String?, status: String,email: String ) {
        this.dataImage = dataImage
        this.dataTitle = dataTitle
        this.dataDesc = dataDesc
        this.status = status
        this.email = email
    }
}
