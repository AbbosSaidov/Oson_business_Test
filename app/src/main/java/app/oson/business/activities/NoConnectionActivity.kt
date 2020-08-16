package app.oson.business.activities

import android.os.Bundle
import android.view.View
import app.oson.business.R

class NoConnectionActivity : MyActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)


    }

    override fun setupActionBar() {
        backImageView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        if (v == backImageView){
            finish()
        }
    }
}