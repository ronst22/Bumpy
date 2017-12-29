package com.bumpy.bumpy;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ronst on 12/29/2017.
 */

public class AmbulanceQuestion implements IState {

    @Override
    public void InitActivity(DynamicActivity dynamicActivity, LinearLayout lm, Resources resource) {
        // create the layout params that will be used to define how your
        // button will be displayed


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
        question.setText("Do you need an ambulance?");

        ll.addView(question);

        LinearLayout llButton = new LinearLayout(dynamicActivity);
        llButton.setOrientation(LinearLayout.HORIZONTAL);
        llButton.setGravity(Gravity.CENTER);

        ll.addView(llButton);

        // Create Button
        final Button btnYes = new Button(dynamicActivity);
        // Give button an ID
        btnYes.setId(1);
        btnYes.setText("Yes");
        btnYes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);

        // set the layoutParams on the button
        btnYes.setLayoutParams(params);

        // Set click listener for button
        btnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: Call almbulance
            }
        });


        // Create Button
        final Button btnNo = new Button(dynamicActivity);
        // Give button an ID
        btnNo.setId(2);
        btnNo.setText("No");

        // set the layoutParams on the button
        btnNo.setLayoutParams(params);
        btnNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);

        // Set click listener for button
        btnNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        //Add button to LinearLayout
        llButton.addView(btnYes);
        llButton.addView(btnNo);
        //Add button to LinearLayout defined in XML
        lm.addView(ll);

//        //Create four
//        for (int j = 0; j <= 6; j++) {
//            System.out.println("FOR STATED");
//            // Create LinearLayout
//            LinearLayout ll = new LinearLayout(dynamicActivity);
//            ll.setOrientation(LinearLayout.HORIZONTAL);

//            // Create TextView
//            TextView product = new TextView(dynamicActivity);
//            product.setText(" Product" + j + "    ");
//            ll.addView(product);

//            // Create TextView
//            TextView price = new TextView(dynamicActivity);
//            price.setText("  $" + j + "     ");
//            ll.addView(price);

//            final int index = j;
//            // Set click listener for button
//            btn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
////                    Toast.makeText(getApplicationContext(),
////                            "Clicked Button Index :" + index,
////                            Toast.LENGTH_LONG).show();
//
//                }
//            });

//            //Add button to LinearLayout
//            ll.addView(btn);
//            //Add button to LinearLayout defined in XML
//            lm.addView(ll);
    }
}
