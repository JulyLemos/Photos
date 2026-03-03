package br.edu.ifsp.scl.sdm.dummyproducts.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.edu.ifsp.scl.sdm.dummyproducts.R
import br.edu.ifsp.scl.sdm.dummyproducts.R.string.app_name
import br.edu.ifsp.scl.sdm.dummyproducts.adapter.ProductAdapter
import br.edu.ifsp.scl.sdm.dummyproducts.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.dummyproducts.model.Product
import br.edu.ifsp.scl.sdm.dummyproducts.model.ProductList
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

    companion object {
        const val PRODUCTS_ENDPOINT = "https://dummyjson.com/products/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(app_name)
        })

        amb.productsSp.adapter = productAdapter
        retrieveProducts()
    }

    private fun retrieveProducts() = Thread {
            val productsConnection = URL(PRODUCTS_ENDPOINT).openConnection() as HttpURLConnection
            try {
                if (productsConnection.responseCode == HTTP_OK) {
                    InputStreamReader(productsConnection.inputStream).readText().let {
                        runOnUiThread {
                            productAdapter.addAll(Gson().fromJson(it, ProductList::class.java).products)
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ioe: IOException) {
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                }
            } catch (jse: JsonSyntaxException) {
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.response_problem), Toast.LENGTH_SHORT).show()
                }
            } finally {
                productsConnection.disconnect()
            }
        }.start()

}