package com.jishnunkrishnan.moneymanager

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TableAdapter(private val context: Activity, private val id: Array<String>, private val datee: Array<String>, private val typee: Array<String>, private val categoryy: Array<String>,private val memoo: Array<String>, private val amountt: Array<String>)
    :ArrayAdapter<String>(context, R.layout.custom_table, typee) {

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_table, null, true)

        val slNo = rowView.findViewById(R.id.tvSlno) as TextView
        val date = rowView.findViewById(R.id.tvDate) as TextView
        val type = rowView.findViewById(R.id.tvType) as TextView
        val category = rowView.findViewById(R.id.tvCategory) as TextView
        val memo = rowView.findViewById(R.id.tvMemo) as TextView
        val amount = rowView.findViewById(R.id.tvAmount) as TextView

        slNo.text = "${id[position]}"
        date.text = "${datee[position]}"
        type.text = "${typee[position]}"
        category.text = "${categoryy[position]}"
        memo.text = "${memoo[position]}"
        amount.text = "${amountt[position]}"
        return rowView
    }
}