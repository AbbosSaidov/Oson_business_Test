package app.oson.business.activities.main.history

import android.os.Bundle
import android.view.View
import app.oson.business.R
import app.oson.business.activities.MyActivity

class FilterActivity : MyActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        titleTextView.setText(R.string.menu_item_main_filter)

    }

    override fun setupActionBar() {
        titleTextView.visibility = View.VISIBLE
        backImageView.visibility=View.VISIBLE
    }

    override fun onClick(v: View?) {
    }
}