package app.oson.business.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.oson.business.R
import app.oson.business.models.Merchant
import app.oson.business.models.Purchase
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration

import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.oson.business.activities.main.history.FilterActivity
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.MerchantService
import app.oson.business.api.services.PurchaseService
import kotlin.collections.ArrayList


class FragmentPurchaseList : Fragment(){

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var recyclerView: RecyclerView
    lateinit var purchaseAdapter: PurchaseListAdapter

    var purchaseList: ArrayList<Purchase>? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        val view = inflater.inflate(R.layout.fragment_item_purchase_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_search)
        purchaseAdapter = PurchaseListAdapter()

        recyclerView.adapter = purchaseAdapter;
        recyclerView.setLayoutManager(LinearLayoutManager(activity))
        recyclerView.addItemDecoration(StickyRecyclerHeadersDecoration(purchaseAdapter))

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            purchaseList?.clear()
            purchaseAdapter.notifyDataSetChanged()

            getPurchaseList()
        }
        getPurchaseList()

        return view
    }

    inner class PurchaseListAdapter() : RecyclerView.Adapter<PurchaseListAdapter.Holder>(),
        StickyRecyclerHeadersAdapter<PurchaseListAdapter.HeaderHolder> {

        // adapter
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_recycler_view_fragment_item_purchase, parent, false)

            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {

            var sum: String = (purchaseList!![position].amount / 100).toString() + " сўм"
            var check: String = "Чек " + purchaseList!![position].receiptId.toString()

            var number: String
            if (purchaseList!![position].phoneNumber.isEmpty()) {
                number = resources.getString(R.string.viewholder_item_unknown_field)
            } else
                number = "+" + purchaseList!![position].phoneNumber

            var format = SimpleDateFormat("HH:mm")
            val date = format.parse(purchaseList!![position].time.substring(11, 16))

            holder.nameTextView.text = number
            holder.timeTextView.text = format.format(date)
            holder.sumTextView.text = sum
            holder.checkCodeTextView.text = check

            holder.linearLayout.setOnClickListener { view ->
                dialogPurchaseHistory(purchaseList!![position])
            }

        }

        override fun getItemCount(): Int {
            if (purchaseList == null)
                return 0;
            return purchaseList!!.size;
        }


        //header adapter

        override fun getHeaderId(position: Int): Long {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val date = format.parse(purchaseList!![position].time.substring(0, 11))


            return date.time
        }

        override fun onCreateHeaderViewHolder(parent: ViewGroup?): HeaderHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_recycler_view_fragment_item_purchase_header, parent, false)

            return HeaderHolder(view)
        }

        override fun onBindHeaderViewHolder(holder: HeaderHolder?, position: Int) {
            holder?.textView?.setText(dateFormat(purchaseList!![position].time))
        }


        inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.text_view_date)

        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val nameTextView = itemView.findViewById<TextView>(R.id.text_view_name)
            val timeTextView = itemView.findViewById<TextView>(R.id.text_view_time)
            val sumTextView = itemView.findViewById<TextView>(R.id.text_view_sum)
            val checkCodeTextView = itemView.findViewById<TextView>(R.id.text_view_check_code)
            val linearLayout = itemView.findViewById<LinearLayout>(R.id.linear_layout)

        }

    }

    fun dateFormat(string: String): String? {
        var format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(string.substring(0, 11))

        format = SimpleDateFormat("dd MMM. yyyy")


        return format.format(date)
    }


    fun dialogPurchaseHistory(purchase: Purchase) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_purchase_history)


        val nameTextView = dialog.findViewById<TextView>(R.id.text_view_name)
        val timeTextView = dialog.findViewById<TextView>(R.id.text_view_time)
        val sumTextView = dialog.findViewById<TextView>(R.id.text_view_sum)
        val checkCodeTextView = dialog.findViewById<TextView>(R.id.text_view_check_code)
        val okTextView = dialog.findViewById<TextView>(R.id.text_view_ok)

        var sum: String = (purchase.amount / 100).toString() + " сўм"
        var check: String = "Чек " + purchase.receiptId.toString()
        var number: String = "+" + purchase.phoneNumber

        var format = SimpleDateFormat("HH:mm")
        val date = format.parse(purchase.time.substring(11, 16))

        nameTextView.text = number
        timeTextView.text = dateFormat(purchase.time) + "   " + format.format(date)
        sumTextView.text = sum
        checkCodeTextView.text = check
        okTextView.setOnClickListener { view ->
            dialog.cancel()
        }

        dialog.show()
    }

    fun onOpenFilterDialog(){
        if(merchantList == null){
            getMerchantList()
        }else
            showDialogFilter()
    }

    lateinit var phoneNumberTextView: AppCompatCheckedTextView
    lateinit var fromDateTextView: AppCompatCheckedTextView
    lateinit var toDateTextView: AppCompatCheckedTextView
    lateinit var clearNumberImageButton: AppCompatImageButton
    lateinit var clearFromDateImageButton: AppCompatImageButton
    lateinit var clearToDateImageButton: AppCompatImageButton

    fun showDialogFilter(){
        if (merchantList == null)
            return

        /*     val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_filter)

        var spinner = dialog.findViewById<Spinner>(R.id.spinner)
        val arrayList = ArrayList<String>()
        arrayList.add("Все поставщики")
        for (i in merchantList!!.indices) {
            arrayList.add(merchantList!![i].name)
        }

        val spinnerAdapter = ArrayAdapter(this.context, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter


        phoneNumberTextView = dialog.findViewById<AppCompatCheckedTextView>(R.id.text_view_check_number)
        phoneNumberTextView.setOnClickListener { view ->
            dialogFilterItemSearch()
        }
        clearNumberImageButton = dialog.findViewById<AppCompatImageButton>(R.id.image_button_clear_number)
        clearNumberImageButton.setOnClickListener { view ->
            phoneNumberTextView.setText(resources.getString(R.string.dialogfragment_main_bill_purchase_filter_choose_date_from_tap_to_select_hint))
            clearNumberImageButton.visibility = View.GONE
            userId = null
        }
        fromDateTextView = dialog.findViewById<AppCompatCheckedTextView>(R.id.text_view_check_from_date)
        fromDateTextView.setOnClickListener { view ->
            showDialogDatePicker(1)
        }
        clearFromDateImageButton = dialog.findViewById<AppCompatImageButton>(R.id.image_button_clear_from_date)
        clearFromDateImageButton.setOnClickListener { view ->
            fromDateTextView.setText(resources.getString(R.string.dialogfragment_main_bill_purchase_filter_choose_date_from_tap_to_select_hint))
            clearFromDateImageButton.visibility = View.GONE
            fromDate = null
        }
        toDateTextView = dialog.findViewById<AppCompatCheckedTextView>(R.id.text_view_check_to_date)
        toDateTextView.setOnClickListener { view ->
            showDialogDatePicker(2)
        }
        clearToDateImageButton = dialog.findViewById<AppCompatImageButton>(R.id.image_button_clear_to_date)
        clearToDateImageButton.setOnClickListener { view ->
            toDateTextView.setText(resources.getString(R.string.dialogfragment_main_bill_purchase_filter_choose_date_from_tap_to_select_hint))
            clearToDateImageButton.visibility = View.GONE
            toDate = null
        }
        var okTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_ok)
        var cancelTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_cancel)

        okTextView.setOnClickListener { view ->
            merchantId =
                    if (spinner.selectedItemPosition == 0) null else merchantList?.get(spinner.selectedItemPosition - 1)?.id;

            purchaseList?.clear()
            purchaseAdapter.notifyDataSetChanged()
            getPurchaseList()

            dialog.cancel()
            (activity as MyActivity).clearImageView.visibility = View.VISIBLE
        }

        cancelTextView.setOnClickListener { view ->
            dialog.cancel()
        }


        dialog.show()
        */

        val intent = Intent(activity, FilterActivity::class.java)
        startActivity(intent)
    }


    fun showDialogDatePicker(helper: Int) {

        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_calendar)

        var datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)

        if (helper == 1) {
            toDate?.let {
                datePicker.maxDate = it
            }
        } else {
            fromDate?.let {
                datePicker.minDate = it
            }
        }

        var okTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_ok)
        var cancelTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_cancel)


        okTextView.setOnClickListener { view ->
            val year = datePicker.year - 1900
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val format = SimpleDateFormat("dd MMM. yyyy")

//            userId =

            if (helper == 1) {
                fromDate = Date(year, month, day, 0, 0).time


                fromDateTextView.setText(resources.getString(R.string.start_date) + " " + format.format(fromDate))
                clearFromDateImageButton.visibility = View.VISIBLE
            } else {
                toDate = Date(year, month, day, 23, 59).time

                toDateTextView.setText(resources.getString(R.string.start_date) + " " + format.format(toDate))
                clearToDateImageButton.visibility = View.VISIBLE
            }

            dialog.cancel()
        }

        cancelTextView.setOnClickListener { view ->
            dialog.cancel()
        }

        dialog.show()
    }

    fun clear() {
        merchantId = null
        userId = null
        toDate = null
        fromDate = null

        purchaseList?.clear()
        purchaseAdapter?.notifyDataSetChanged()
        getPurchaseList()
    }

    var numberList: ArrayList<String>? = null
    lateinit var listView: ListView
    lateinit var searchView: SearchView
    var adapter: ArrayAdapter<String>? = null

    fun dialogFilterItemSearch() {

        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_filter_item_searchable)

        searchView = dialog.findViewById<SearchView>(R.id.search_view)
        listView = dialog.findViewById<ListView>(R.id.list_view)

        numberList = ArrayList()
        for (i in purchaseList!!.indices) {
            if (!purchaseList!![i].phoneNumber.isEmpty())
                numberList!!.add(purchaseList!![i].phoneNumber)
        }
        val list = numberList!!.distinct()

        adapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list)
        listView!!.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            for (i in purchaseList!!.indices) {
                if (list[position].equals(purchaseList!![i].phoneNumber)) {
                    var
                    userId = purchaseList!![i].uid

                }
            }

            phoneNumberTextView.setText("+" + list!![position])
            clearNumberImageButton.visibility = View.VISIBLE

            dialog.cancel()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (numberList!!.contains(query)) {
                    adapter!!.getFilter().filter(query)

                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.getFilter().filter(newText);
                return false
            }
        })
        dialog.show()
    }

    var merchantList: ArrayList<Merchant>? = null
    fun getMerchantList() {

        MerchantService().merchantList(
            callback = object : BaseCallback<Merchant.MerchantList>{
                override fun onLoading() {

                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                }

                override fun onSuccess(response: Merchant.MerchantList) {
                    merchantList = response.arrayList

                    showDialogFilter()
                }
            }
        )
    }

    var merchantId: Long? = null
    var userId: Long? = null
    var toDate: Long? = null
    var fromDate: Long? = null

    fun getPurchaseList() {
        if (purchaseList == null) {
            swipeRefreshLayout.isRefreshing = true
        }

        PurchaseService().getPurchaseList(
            merchantId = merchantId,
            userId = userId,
            fromDate = fromDate,
            toDate = toDate,
            callback = object : BaseCallback<Purchase.PurchaseList> {
                override fun onLoading() {

                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onSuccess(response: Purchase.PurchaseList) {
                    swipeRefreshLayout.isRefreshing = false

                    purchaseList = response.arrayList
                    purchaseAdapter.notifyDataSetChanged()
                }
            }
        )
    }
}