package com.todokanai.musicplayer.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity:AppCompatActivity() {

    abstract fun onHandlePermissions()

}