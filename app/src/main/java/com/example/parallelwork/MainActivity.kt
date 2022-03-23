package com.example.parallelwork

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.parallelwork.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        lifecycleScope.launchWhenStarted {
//            sampleFlow().collect {
//                Log.d(TAG, "collect()")
//            }

//            val job = SupervisorJob()
            val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
                Log.d(
                    TAG,
                    "CoroutineExceptionHandler >>> Error Message: ${throwable.message ?: "NA"}"
                )
                Log.d(TAG, "CoroutineExceptionHandler >>> coroutineContext: $coroutineContext")
            }

            CoroutineScope(Dispatchers.IO).launch {

                Log.d(TAG, "CoroutineScope >>> ${Thread.currentThread().name}")

                (1..10).map {
                    async {
                        kotlin.runCatching {
                            Log.d(TAG, "async >>> ${Thread.currentThread().name}")
                            backgroundTask(it)
                        }
//                        try {
//                            Log.d(TAG, "async >>> ${Thread.currentThread().name}")
//                            backgroundTask(it)
//                        } catch (e: Exception) {
//                            "Error Response $it"
//                        }
                    }
                }.awaitAll().apply {
                    Log.d(TAG, "awaitAll >>> ${Thread.currentThread().name}")
                    Log.d(TAG, "awaitAll >>> ${this.size}")
                    Log.d(TAG, "awaitAll >>> $this")
                }
            }
        }
    }

    private fun sampleFlow() = flow {
        Log.d(TAG, "flow start")
        emit((1..10).map { backgroundTask(it) })
        Log.d(TAG, "flow end")
    }

    private suspend fun backgroundTask(id: Int) : String {
        Log.d(TAG, "backgroundTask >>> ${Thread.currentThread().name}")

        Log.d(TAG, "backgroundTask() start: #$id")
        delay(Random.nextLong(100, 1000))
        delay(1000)
        Log.d(TAG, "backgroundTask() end: #$id")
        if (id == 4) {
            throw RuntimeException("Failure in background task")
        }
        return "Response $id"
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}