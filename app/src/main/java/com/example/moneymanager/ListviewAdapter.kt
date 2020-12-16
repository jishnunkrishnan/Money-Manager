package com.example.moneymanager

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
class MyListAdapter(private val context: Activity, private val id: Array<String>, private val name: Array<String>, private val email: Array<String>)
    : ArrayAdapter<String>(context, R.layout.custom_listview, name) {

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_listview, null, true)

        val emailText = rowView.findViewById(R.id.tvTransactionDetails) as TextView
        val idText = rowView.findViewById(R.id.tvTransactionDate) as TextView
        val nameText = rowView.findViewById(R.id.tvTransactionAmount) as TextView

        idText.text = "${name[position]}"
        nameText.text = "${email[position]}"
        emailText.text = "${id[position]}"
        return rowView
    }
}