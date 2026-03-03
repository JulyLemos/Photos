package br.edu.ifsp.scl.sdm.dummyproducts.model

data class ProductList(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)