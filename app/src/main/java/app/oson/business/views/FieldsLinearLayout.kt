package app.oson.business.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import app.oson.business.models.Field
import app.oson.business.models.PurchaseSendField
import com.google.gson.Gson
import kotlinx.android.synthetic.main.view_fields.view.*

class FieldsLinearLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    fun buildWithFields(fields: List<Field>) {

        for (field in fields) {
            println("AAAAAA")
            val fieldView = FieldView(context, field)

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            fieldView.layoutParams = params

            addView(fieldView, params)
        }
    }


    fun getPurchaseData(): String? {
        var purchaseFields = ArrayList<PurchaseSendField>()

        for (i in 0..(childCount - 1)) {
            val fieldView = getChildAt(i) as FieldView

            val purchaseField = getPurchaseField(fieldView)

            if (purchaseField == null)
                return null

            purchaseFields.add(purchaseField)
        }

        println(purchaseFields.size)


        return Gson().toJson(purchaseFields)
    }

    fun getPurchaseField(fieldView: FieldView): PurchaseSendField? {
        val field = fieldView.field;
        val purchaseField = PurchaseSendField()

        purchaseField.fID = field.fID
        if (field.prefix.isEmpty()) {
            purchaseField.prefix = ""
        } else {
            purchaseField.prefix = field.prefix[spinner.selectedItemPosition]
        }
        purchaseField.key = ""
        purchaseField.value = fieldView.edit_text_input_number.text.toString()


        when {
            field.isInput ->{
                if(!(field.minLength <= purchaseField.value.length && purchaseField.value.length < field.maxLength)){
                    return null
                }
            }
        }

        return purchaseField
    }


}