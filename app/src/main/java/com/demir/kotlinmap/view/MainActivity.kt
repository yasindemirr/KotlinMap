package com.demir.kotlinmap.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.demir.kotlinmap.R
import com.demir.kotlinmap.adepter.PlaceAdepter
import com.demir.kotlinmap.databinding.ActivityMainBinding
import com.demir.kotlinmap.model.Place
import com.demir.kotlinmap.roomDb.PlaceDataBase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val compositeDisposable=CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       val db= Room.databaseBuilder(applicationContext, PlaceDataBase::class.java,"Places")
           .build()
        val dao=db.placeDao()
        compositeDisposable.add(
            dao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerResponsable)
        )
    }
    private fun handlerResponsable(list:List<Place>){
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        val adepter= PlaceAdepter(list)
        binding.recyclerView.adapter=adepter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
       menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.addPlace){
            val intent=Intent(this, MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



}