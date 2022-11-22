package com.alirezabashi98.alfafile

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.alirezabashi98.alfafile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val file = getExternalFilesDir(null)!!
        val path = file.path


        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frame_main_container, FileFragment(path))
        transaction.commit()

    }
}