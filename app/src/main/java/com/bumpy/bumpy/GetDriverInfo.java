package com.bumpy.bumpy;

import android.content.res.Resources;
import android.media.MediaCas;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ronst on 12/29/2017.
 */

public class GetDriverInfo implements IState {
    @Override
    public void InitActivity(final DynamicActivity dynamicActivity, LinearLayout lm, Resources resource) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        LinearLayout ll = new LinearLayout(dynamicActivity);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);

        // Create TextView
        TextView question = new TextView(dynamicActivity);
        question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);
//        question.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        question.setLayoutParams(params);
        question.setText("Insert other driver information");

        ll.addView(question);

        LinearLayout llName = new LinearLayout(dynamicActivity);
        llName.setOrientation(LinearLayout.HORIZONTAL);
        llName.setGravity(Gravity.CENTER);
        ll.addView(llName);

        LinearLayout llId = new LinearLayout(dynamicActivity);
        llId.setOrientation(LinearLayout.HORIZONTAL);
        llId.setGravity(Gravity.CENTER);
        ll.addView(llId);

        LinearLayout llcarNum = new LinearLayout(dynamicActivity);
        llcarNum.setOrientation(LinearLayout.HORIZONTAL);
        llcarNum.setGravity(Gravity.CENTER);
        ll.addView(llcarNum);

        LinearLayout llInsurance = new LinearLayout(dynamicActivity);
        llInsurance.setOrientation(LinearLayout.HORIZONTAL);
        llInsurance.setGravity(Gravity.CENTER);
        ll.addView(llInsurance);

        TextView insertName = new TextView(dynamicActivity);
        insertName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        insertName.setText("Insert driver full name: ");

        final EditText nameText = new EditText(dynamicActivity);
        nameText.setInputType(InputType.TYPE_CLASS_TEXT);
        nameText.setHint("Israel Israeli");

        llName.addView(insertName);
        llName.addView(nameText);

        TextView insertId = new TextView(dynamicActivity);
        insertId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        insertId.setText("Insert driver id: ");

        final EditText idtext = new EditText(dynamicActivity);
        idtext.setInputType(InputType.TYPE_CLASS_NUMBER);
        idtext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
        idtext.setHint("123456789");

        llId.addView(insertId);
        llId.addView(idtext);

        TextView carNum = new TextView(dynamicActivity);
        carNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        carNum.setText("Insert car number: ");

        final EditText carNumText = new EditText(dynamicActivity);
        carNumText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        carNumText.setHint("12345678");

        llcarNum.addView(carNum);
        llcarNum.addView(carNumText);

        TextView insurance = new TextView(dynamicActivity);
        insurance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        insurance.setText("Insert insurance number: ");

        final EditText insuranceText = new EditText(dynamicActivity);
        insuranceText.setInputType(InputType.TYPE_CLASS_NUMBER);
        insuranceText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});
        insuranceText.setHint("123456789");

        llInsurance.addView(insurance);
        llInsurance.addView(insuranceText);

        // Create Button
        final Button submit = new Button(dynamicActivity);
        // Give button an ID
        submit.setId(1);
        submit.setText("Submit");
        submit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);

        // set the layoutParams on the button
        submit.setLayoutParams(params);

        // Set click listener for button
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nameText.getText().length() <= 2)
                {
                    Toast.makeText(dynamicActivity.getApplicationContext(),
                            "Please insert the other driver name",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (idtext.getText().length() <= 2)
                {
                    Toast.makeText(dynamicActivity.getApplicationContext(),
                            "Please insert the other driver identification number",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (carNumText.getText().length() <= 2)
                {
                    Toast.makeText(dynamicActivity.getApplicationContext(),
                            "Please insert the other driver car number",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (insuranceText.getText().length() <= 2)
                {
                    Toast.makeText(dynamicActivity.getApplicationContext(),
                            "Please insert the other driver insurance number",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                SessionData.carNumber = carNumText.getText().toString();
                SessionData.driverId = idtext.getText().toString();
                SessionData.driverName = nameText.getText().toString();
                SessionData.insuranceNum = insuranceText.getText().toString();

                SessionData.sendData(dynamicActivity);
                Toast.makeText(dynamicActivity.getApplicationContext(),
                        "Success, Accident reported",
                        Toast.LENGTH_LONG).show();
            }
        });

        ll.addView(submit);

        //Add button to LinearLayout defined in XML
        lm.addView(ll);
    }
}
