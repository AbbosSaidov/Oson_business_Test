package app.oson.business.views

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import app.oson.business.R
import app.oson.business.models.Field
import kotlinx.android.synthetic.main.view_fields.view.*
import android.R.attr.maxLength
import android.text.InputFilter


class FieldView(context: Context?, val field: Field) : RelativeLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_fields, this, true)

        val spinner: Spinner = findViewById(R.id.spinner)
        val linearLayout: LinearLayout = findViewById(R.id.linear_layout)
        val prefixSpinner: Spinner = findViewById(R.id.spinner_prefix)
        val inputNumberEditText: EditText = findViewById(R.id.edit_text_input_number)



        if (field.isList) {
            linearLayout.visibility = View.GONE
        } else if (field.isInput) {

            if (field.prefix.isEmpty())
                prefixSpinner.visibility = View.GONE

            spinner.visibility = View.GONE


            inputNumberEditText.hint = field.label


            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(field.maxLength)
            inputNumberEditText.setFilters(fArray)
        } else {
            linear_layout.visibility = View.VISIBLE
        }

        if (field.inputtype.ch == 0) {
            inputNumberEditText.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            inputNumberEditText.inputType = InputType.TYPE_CLASS_TEXT
        }
    }


}