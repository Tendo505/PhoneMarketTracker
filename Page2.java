//Phone's Accessory page
package com.gfg.calculator_java;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView PhoneCase, PhoneCase2, PhoneCase3
    private EditText CInputAmount, CInputAmount2, CInputAmount3;
    private Button CAddAmount, CAddAmount2, CAddAmount3;
    private Button ToPhone, ToCart;
    private double AccessoryphoneAmnt, AccessoryphoneAmnt2, AccessoryphoneAmnt3, Accessoryprice,
            Accessoryprice2, Accessoryprice3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Phone.SetText=("Samsung Galaxy S17" +
                "RM17");
        CInputAmount= findViewById(R.id.CInputAmount);
        CAddAmount = findViewById(R.id.CAddAmount);

        Phone2.SetText=("Iphone17" +
                "RM20");
        CInputAmount2= findViewById(R.id.CInputAmount2);
        CddAmount2 = findViewById(R.id.CAddAmount2);

        Phone.SetText3=("Xiomi 17" +
                "RM19");
        CInputAmount3= findViewById(R.id.CInputAmount3);
        CAddAmount3 = findViewById(R.id.CAddAmount3);


        InputAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    AccessoryphoneAmnt = Double.parseDouble(editText.getText().toString());
                    isMultiplication = true;

                }
            }
        });

        InputAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    AccessoryphoneAmnt2= Double.parseDouble(editText.getText().toString());
                    isMultiplication= true;
                }
            }
        });

        InputAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    AccessoryphoneAmnt3 = Double.parseDouble(editText.getText().toString());
                    isMultiplication = true;

                }
            }
        });



        AddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    Accessoryprice = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt * 19);
                }
            }
        });

        AddAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    Accessoryprice2 = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt2 * 20);
                }
            }
        });

        AddAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    Accessoryprice3 = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt3 * 19);
                }
            }
        });

        ToAccessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ToCart.OnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
