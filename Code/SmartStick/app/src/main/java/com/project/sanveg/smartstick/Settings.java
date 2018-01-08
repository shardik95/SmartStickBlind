package com.project.sanveg.smartstick;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    public SharedPreferences emergenceContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText userNameEdit = (EditText) findViewById(R.id.user_name_edit);

        View emgContactOne = findViewById(R.id.det_one);
        final EditText emgNameOne = (EditText) emgContactOne.findViewById(R.id.contact_name);
        final EditText emgNumberOne = (EditText) emgContactOne.findViewById(R.id.contact_number);

        View emgContactTwo = findViewById(R.id.det_two);
        final EditText emgNameTwo = (EditText) emgContactTwo.findViewById(R.id.contact_name);
        final EditText emgNumberTwo = (EditText) emgContactTwo.findViewById(R.id.contact_number);

        View emgContactThree = findViewById(R.id.det_three);
        final EditText emgNameThree = (EditText) emgContactThree.findViewById(R.id.contact_name);
        final EditText emgNumberThree = (EditText) emgContactThree.findViewById(R.id.contact_number);

        Button save = (Button) findViewById(R.id.save_button);

        emergenceContacts = getSharedPreferences("Emergence_Contacts", Context.MODE_PRIVATE);

        userNameEdit.setText(emergenceContacts.getString("UserName",""));
        emgNameOne.setText(emergenceContacts.getString("EmgNameOne",""));
        emgNumberOne.setText(emergenceContacts.getString("EmgNumberOne",""));
        emgNameTwo.setText(emergenceContacts.getString("EmgNameTwo",""));
        emgNumberTwo.setText(emergenceContacts.getString("EmgNumberTwo",""));
        emgNameThree.setText(emergenceContacts.getString("EmgNameThree",""));
        emgNumberThree.setText(emergenceContacts.getString("EmgNumberThree",""));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();

                String eNameOne = emgNameOne.getText().toString();
                String eNumOne = emgNumberOne.getText().toString();

                String eNameTwo = emgNameTwo.getText().toString();
                String eNumTwo = emgNumberTwo.getText().toString();

                String eNameThree = emgNameThree.getText().toString();
                String eNumThree = emgNumberThree.getText().toString();

                SharedPreferences.Editor editor = emergenceContacts.edit();

                editor.putString("UserName", userName);
                editor.putString("EmgNameOne", eNameOne);
                editor.putString("EmgNumberOne", eNumOne);

                editor.putString("EmgNameTwo", eNameTwo);
                editor.putString("EmgNumberTwo", eNumTwo);

                editor.putString("EmgNameThree", eNameThree);
                editor.putString("EmgNumberThree", eNumThree);
                editor.apply();
                Toast.makeText(Settings.this,"Values Saved",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
