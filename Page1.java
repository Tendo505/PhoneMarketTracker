//Phone page
package com.gfg.calculator_java;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView Phone, Phone2, Phone3
    private EditText InputAmount, InputAmount2, InputAmount3;
    private Button AddAmount, AddAmount2, AddAmount3;
    private Button ToAccessory, ToCart;
    private double phoneAmnt, phoneAmnt2, phoneAmnt3, price, price2, price3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Phone.SetText=("Samsung Galaxy S17" +
                "RM1799");
        InputAmount= findViewById(R.id.InputAmount);
        AddAmount = findViewById(R.id.AddAmount);

        Phone2.SetText=("Iphone17" +
                "RM3999");
        InputAmount2= findViewById(R.id.InputAmount2);
        AddAmount2 = findViewById(R.id.AddAmount2);

        Phone.SetText3=("Xiomi 17" +
                "RM3989");
        InputAmount3= findViewById(R.id.InputAmount3);
        AddAmount3 = findViewById(R.id.AddAmount3);


        InputAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    phoneAmnt = Double.parseDouble(editText.getText().toString());
                    isMultiplication = true;

                }
            }
        });

        InputAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    phoneAmnt2= Double.parseDouble(editText.getText().toString());
                    isMultiplication= true;
                }
            }
        });

        InputAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    phoneAmnt3 = Double.parseDouble(editText.getText().toString());
                    isMultiplication = true;

                }
            }
        });



        AddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    price = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt * 1999);
                }
            }
        });

        AddAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    price2 = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt2 * 3999);
                }
            }
        });

        AddAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    price3 = Double.parseDouble(editText.getText().toString());
                    resultText.setText(String.valueOf(phoneAmnt3 * 3989);
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
