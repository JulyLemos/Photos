package br.edu.ifsp.scl.sdm.dummyproducts.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.dummyproducts.R
import br.edu.ifsp.scl.sdm.dummyproducts.R.string.app_name
import br.edu.ifsp.scl.sdm.dummyproducts.adapter.ProductAdapter
import br.edu.ifsp.scl.sdm.dummyproducts.adapter.ProductImageAdapter
import br.edu.ifsp.scl.sdm.dummyproducts.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.dummyproducts.model.DummyJSONAPI
import br.edu.ifsp.scl.sdm.dummyproducts.model.Photo
import br.edu.ifsp.scl.sdm.dummyproducts.model.Product
import br.edu.ifsp.scl.sdm.dummyproducts.model.ProductList
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val productList: MutableList<Product> = mutableListOf()
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(this, productList)
    }
    private val productImageList: MutableList<Bitmap> = mutableListOf()
    private val productImageAdapter: ProductImageAdapter by lazy {
        ProductImageAdapter(this, productImageList)
    }

    companion object {
        const val PHOTOS_ENDPOINT = "https://jsonplaceholder.typicode.com/photos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = "Photos"
        })

        amb.productsSp.apply {
            adapter = productAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val size = productImageList.size
                    productImageList.clear()
                    productImageAdapter.notifyItemRangeRemoved(0, size)
                    retrieveProductImages(productList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //NSA
                }
            }
        }

        amb.productImagesRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productImageAdapter
        }

//        amb.photosSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                amb.productImagesRv.visibility = View.GONE
//                val photo = photoList[position]
//
//                val fixedThumbURL = "${photo.thumbnailUrl.replace("via.placeholder.com","placehold.co")}/FFF.png"
//
//                retrieveImage(photo.url, amb.photoIv)
//                retrieveImage(fixedThumbURL, amb.thumbnailIv)
//
//                amb.photoIv.visibility = View.VISIBLE
//                amb.thumbnailIv.visibility = View.VISIBLE
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        retrieveProducts()
        retrievePhotos()
    }

    private fun retrieveProducts() =
        DummyJSONAPI.ProductListRequest({ productList ->
            productList.products.also {
                productAdapter.addAll(it)
            }
        }, {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        }).also {
            DummyJSONAPI.getInstance(this).addToRequestQueue(it)
        }

//Depois do código de cima, esse passa a ser desnecessário, é outra versão do retorno dos produtos
//        StringRequest(
//            Request.Method.GET,
//            PRODUCTS_ENDPOINT, //passa por ele, onde vai buscar os produtos
//            { response ->
//                Gson().fromJson(response, ProductList::class.java).products.also {
//                    productAdapter.addAll(it)
//                }
//            },
//            {
//                Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
//            }).also {
//            DummyJSONAPI.getInstance(this).addToRequestQueue(it)
//        }


//    private fun retrieveProducts() = Thread {
//            val productsConnection = URL(PRODUCTS_ENDPOINT).openConnection() as HttpURLConnection
//            try {
//                if (productsConnection.responseCode == HTTP_OK) {
//                    InputStreamReader(productsConnection.inputStream).readText().let {
//                        runOnUiThread {
//                            productAdapter.addAll(Gson().fromJson(it, ProductList::class.java).products)
//                        }
//                    }
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (ioe: IOException) {
//                runOnUiThread {
//                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
//                }
//            } catch (jse: JsonSyntaxException) {
//                runOnUiThread {
//                    Toast.makeText(this, getString(R.string.response_problem), Toast.LENGTH_SHORT).show()
//                }
//            } finally {
//                productsConnection.disconnect()
//            }
//        }.start()

    private fun retrieveProductImages(product: Product) =
        product.images.forEach { imageUrl ->
            ImageRequest(
                imageUrl,
                {response ->
                productImageList.add(response)
                productImageAdapter.notifyItemInserted(productImageList.lastIndex)
            },
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ARGB_8888,
                {
                Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT)
                    .show()
            }).also {
                DummyJSONAPI.getInstance(this).addToRequestQueue(it)
            }
        }



//    private fun retrieveProductImages(product: Product) = Thread{
//        product.images.forEach { imageUrl ->
//            val imageConnection = URL(imageUrl).openConnection() as HttpURLConnection
//            try {
//                if (imageConnection.responseCode == HTTP_OK) {
//                    BufferedInputStream(imageConnection.inputStream).let{
//                        val imageBitmap = BitmapFactory.decodeStream(it)
//                        runOnUiThread {
//                            productImageList.add(imageBitmap)
//                            productImageAdapter.notifyItemInserted(productImageList.lastIndex)
//                        }
//                    }
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (ioe: IOException) {
//                runOnUiThread {
//                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
//                }
//            }  finally {
//                imageConnection.disconnect()
//            }
//
//        }
//    }.start()

    private val photoList: MutableList<Photo> = mutableListOf()

    private fun retrievePhotos() = Thread{
        val connection = URL(PHOTOS_ENDPOINT).openConnection() as HttpURLConnection
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val json = InputStreamReader(connection.inputStream).readText()
                val photos = Gson().fromJson(json, Array<Photo>::class.java).toList()
                runOnUiThread {
                    photoList.clear()
                    photoList.addAll(photos)

                    val titles = photos.map { it.title }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titles)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }
        } finally {
            connection.disconnect()
        }
    }.start()

    private fun retrieveImage(
        imageUrl: String, imageView: ImageView) = Thread {
            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            try {
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val bitmap = BitmapFactory.decodeStream(connection.inputStream)
                    runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }finally {
                connection.disconnect()
            }
    }.start()

}

