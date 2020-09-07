package app.oson.business.activities.main.history

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.main.purchase.PurchaseItemAdapter
import app.oson.business.fragments.FragmentPurchaseList
import app.oson.business.models.Merchant
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView.OnTagClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FilterActivity : MyActivity(),PurchaseItemAdapter.ItemClickListener{

    lateinit var tags:List<String>
    lateinit var dateIn: LinearLayout
    lateinit var dateFrom: LinearLayout
    lateinit var TextdateFrom: AppCompatCheckedTextView
    lateinit var TextdateIn: AppCompatCheckedTextView
    lateinit var subsidiaryTitle: AppCompatTextView
    lateinit var buttonFilter: AppCompatButton
    lateinit var buttonDropFilter: AppCompatButton
    var fragmentPurchaseList: FragmentPurchaseList? = null

    lateinit var recyclerView: RecyclerView
    lateinit var clickSubsidiariesItem: View
    lateinit var listviewOfBottomSheetAdapter: RecyclerView.Adapter<*>
    lateinit var listviewOfBottomSheetManager: LinearLayoutManager
    lateinit var bottomSheet2dialog: BottomSheetDialog
    var subsidaryList: ArrayList<String>? = null
    var merchantList: ArrayList<Merchant>? = null
    lateinit var color1:IntArray
    lateinit var color2:IntArray
    lateinit var mTagContainerLayout:TagContainerLayout

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        titleTextView.setText(R.string.menu_item_main_filter)
        mTagContainerLayout = findViewById<View>(R.id.tagContainerLayout) as TagContainerLayout
        color1 = intArrayOf(
            resources.getColor(R.color.colorPrimaryDark),
            resources.getColor(R.color.colorPrimaryDark),
            resources.getColor(R.color.colorBack),
            Color.YELLOW
        )
        color2 = intArrayOf(
            resources.getColor(R.color.colorBack),
            resources.getColor(R.color.colorPrimaryDark),
            resources.getColor(R.color.colorPrimaryDark),
            Color.YELLOW
        )
        fragmentPurchaseList = FragmentPurchaseList()
        TextdateFrom = findViewById<AppCompatCheckedTextView>(R.id.textDateFrom)
        buttonFilter = findViewById<AppCompatButton>(R.id.button_filter)
        buttonFilter.setOnClickListener(this)
        buttonDropFilter = findViewById<AppCompatButton>(R.id.button_clear_filter)
        buttonDropFilter.setOnClickListener(this)
        TextdateIn = findViewById<AppCompatCheckedTextView>(R.id.textDateIn)
        subsidiaryTitle = findViewById<AppCompatTextView>(R.id.bottom_sheet_click_text)
        clickSubsidiariesItem = findViewById<View>(R.id.bottom_sheet_click_view2)
        dateIn = findViewById<LinearLayout>(R.id.dateIn)
        dateIn.setOnClickListener(this)
        clickSubsidiariesItem.setOnClickListener(this)
        dateFrom = findViewById<LinearLayout>(R.id.dateFrom)
        dateFrom.setOnClickListener(this)

        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

        subsidaryList = ArrayList<String>()

        for (i in merchantList!!.indices){
            subsidaryList!!.add(merchantList!![i].name)
        }
        subsidiaryTitle.text = resources.getString(R.string.dialogfragment_main_bill_purchase_filter_choose_merchant_title)
        setupTagView()
    }
    private fun setupTagView(){
        tags= listOf(
            "Все",
            "Сегодня",
            "Вчера",
            "За неделю",
            "За прошлую неделю",
            "За месяц",
            "За прошлый месяц",
            "За год"
        )
        val colors: MutableList<IntArray> = ArrayList()
        //int[] color = {TagBackgroundColor, TabBorderColor, TagTextColor, TagSelectedBackgroundColor}

        colors.add(color1)
        colors.add(color2)
        colors.add(color2)
        colors.add(color2)
        colors.add(color2)
        colors.add(color2)
        colors.add(color2)
        colors.add(color2)

        mTagContainerLayout.setTags(tags, colors)

        mTagContainerLayout.backgroundColor= resources.getColor(R.color.colorBack)
        mTagContainerLayout.borderColor=Color.TRANSPARENT
        mTagContainerLayout.setOnTagClickListener(object : OnTagClickListener{
            override fun onTagClick(position: Int, text: String){
                val colors2: MutableList<IntArray> = ArrayList()
                for (i in 0..7){
                    if(i == position){
                        colors2.add(color1)
                    }else{
                        colors2.add(color2)
                    }
                }
                mTagContainerLayout.removeAllTags()
                mTagContainerLayout.setTags(tags, colors2)

                val currentTime = Calendar.getInstance()

                when (position) {
                    0 -> {
                        TextdateFrom.text = ""
                        TextdateIn.text = ""
                    }
                    1 -> {
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    2 -> {
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.DAY_OF_YEAR, -1)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    3 -> {
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.DAY_OF_YEAR, -7)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    4 -> {
                        currentTime.add(Calendar.WEEK_OF_MONTH, -1)
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.WEEK_OF_MONTH, -1)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    5 -> {
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.MONTH, -1)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    6 -> {
                        currentTime.add(Calendar.MONTH, -1)
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.MONTH, -1)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                    7 -> {
                        TextdateFrom.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                        currentTime.add(Calendar.YEAR, -1)
                        TextdateIn.text = currentTime.get(Calendar.DAY_OF_MONTH).toString()+"."+currentTime.get(Calendar.MONTH).toString()+"."+currentTime.get(Calendar.YEAR).toString()
                    }
                }
            }

            override fun onTagLongClick(position: Int, text: String){
                // ...
            }

            override fun onSelectedTagDrag(position: Int, text: String) {
                // ...
            }
            override fun onTagCrossClick(position: Int) {
                // ...
            }
        })
    }
    private fun showCalendarDialog(t:Int){
        val dialog = DatePickerFragmentDialog.newInstance { view, year, monthOfYear, dayOfMonth ->
            if(t==1){
                TextdateFrom.text = "$dayOfMonth.$monthOfYear.$year"
            }else{
                TextdateIn.text = "$dayOfMonth.$monthOfYear.$year"
            }
        }
        dialog.setMaxDate(System.currentTimeMillis())
        dialog.show(supportFragmentManager, "tag")

    /*    val dialog = Dialog(this@FilterActivity)
        dialog.setCancelable(false)
        //dialog.setView(layoutInflater.inflate(R.layout.calendar_main, null));
        dialog.setContentView(layoutInflater.inflate(R.layout.calendar_main, null))
        dialog.setCancelable(true)
        val currentTime: Date = Calendar.getInstance().time

        val cal = dialog.findViewById<View>(R.id.calendarView1) as? CalendarView
        cal!!.maxDate =currentTime.time

        cal.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->

            if(t==1){
                TextdateFrom.text = "$year.$month.$dayOfMonth"
            }else{
                TextdateIn.text = "$year.$month.$dayOfMonth"
            }
        })
        dialog.show()*/
    }
    override fun setupActionBar(){
        titleTextView.visibility = View.VISIBLE
        backImageView.visibility = View.VISIBLE
    }
    private fun subsidiaryListDialog(){

        bottomSheet2dialog = BottomSheetDialog(this)
        bottomSheet2dialog.setContentView(R.layout.bottom_sheet)

        recyclerView = bottomSheet2dialog.findViewById<RecyclerView>(R.id.recycler_view)!!

        listviewOfBottomSheetManager = LinearLayoutManager(this)
        recyclerView.layoutManager =listviewOfBottomSheetManager
        listviewOfBottomSheetAdapter= PurchaseItemAdapter(this,subsidaryList!!)
        (listviewOfBottomSheetAdapter as PurchaseItemAdapter).setClickListener(this)
        recyclerView.adapter =listviewOfBottomSheetAdapter

    }
    override fun onClick(v:View?){
        when (v){
            dateIn -> {
                showCalendarDialog(1)
            }
            dateFrom -> {
                showCalendarDialog(2)
            }
            clickSubsidiariesItem -> {
                subsidiaryListDialog()
                bottomSheet2dialog.show()
            }
            buttonFilter->{
                fragmentPurchaseList!!.getPurchaseList()
            }
            buttonDropFilter->{
                subsidiaryTitle.text = resources.getString(R.string.dialogfragment_main_bill_purchase_filter_choose_merchant_title)
                TextdateFrom.text = ""
                TextdateIn.text = ""
                val colors2: MutableList<IntArray> = ArrayList()
                for(i in 0..7){
                    if(i == 0){
                        colors2.add(color1)
                    }else{
                        colors2.add(color2)
                    }
                }
                mTagContainerLayout.removeAllTags()
                mTagContainerLayout.setTags(tags, colors2)
            }
        }
    }
    override fun onItemClick(position: Int){
        bottomSheet2dialog.hide()
        subsidiaryTitle.text = subsidaryList!![position]
    }
}