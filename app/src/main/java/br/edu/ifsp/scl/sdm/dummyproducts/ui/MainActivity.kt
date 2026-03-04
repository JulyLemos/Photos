package br.edu.ifsp.scl.sdm.dummyproducts.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.sdm.dummyproducts.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.dummyproducts.model.DummyJSONAPI
import br.edu.ifsp.scl.sdm.dummyproducts.model.Product
import br.edu.ifsp.scl.sdm.dummyproducts.model.ProductList
import com.android.volley.toolbox.ImageRequest
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val productList: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb)
        supportActionBar?.title = "Photos"
        
        amb.photosSp.visibility = View.GONE
        amb.productImagesRv.visibility = View.GONE

        amb.productsSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (productList.isNotEmpty()) {
                    val product = productList[position]
                    
                    val urlRealPrincipal = product.images.firstOrNull() ?: ""
                    val urlRealThumb = product.thumbnail

                    if (urlRealPrincipal.isNotEmpty()) {
                        loadImage(urlRealPrincipal, amb.photoIv)
                    }
                    if (urlRealThumb.isNotEmpty()) {
                        loadImage(urlRealThumb, amb.thumbnailIv)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        searchProducts()
    }

    private fun searchProducts() = Thread {
        try {
            val url = URL("https://dummyjson.com/products")
            val conn = url.openConnection() as HttpURLConnection
            val response = Gson().fromJson(InputStreamReader(conn.inputStream), ProductList::class.java)

            runOnUiThread {
                productList.clear()
                productList.addAll(response.products)

                val names = response.products.map { it.title }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                amb.productsSp.adapter = adapter
            }
        } catch (e: Exception) {
            runOnUiThread { Toast.makeText(this, "Request problem", Toast.LENGTH_SHORT).show() }
        }
    }.start()

    private fun loadImage(url: String, iv: ImageView) {
        val request = ImageRequest(url,
            { response -> iv.setImageBitmap(response) },
            0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
            { error ->
                error.printStackTrace()
            }
        )
        DummyJSONAPI.getInstance(this).addToRequestQueue(request)
    }
}

