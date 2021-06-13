package com.fortradestudio.custom

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import java.lang.Exception

abstract class RemoveButtonListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v is AppCompatButton){
            if(v.parent is ProfileImagesViewGroup){

                val profileImagesViewGroup = v.parent as ProfileImagesViewGroup
                profileImagesViewGroup.deleteEffect()

            }else{
                throw Exception("Remove listener only applicable to buttons that are child to ProfileImagesViewGroup ")
            }
        }else{
            throw Exception("Button Type For ProfileImagesViewGroup must be AppCompatButton located at index 0 of child tree")
        }
    }

}