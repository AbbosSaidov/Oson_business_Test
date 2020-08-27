package app.oson.business.ui.purchase

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.oson.business.R


class PurchaseItemAdapter(val context: Context, val myDataset: ArrayList<String>) :
    RecyclerView.Adapter<PurchaseItemAdapter.MyViewHolder>() {
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.item_recycler_view_purchase_activity, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){

        holder.textview.text = myDataset[position]
        holder.textview.setOnClickListener {
            mClickListener?.onItemClick(position)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textview = itemView.findViewById<TextView>(R.id.subsidiary_name)

    }

    override fun getItemCount() = myDataset.size

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick( position: Int)
    }

}