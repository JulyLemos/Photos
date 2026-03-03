package br.edu.ifsp.scl.sdm.dummyproducts.model

data class Review(
    val comment: String,
    val date: String,
    val rating: Int,
    val reviewerEmail: String,
    val reviewerName: String
)