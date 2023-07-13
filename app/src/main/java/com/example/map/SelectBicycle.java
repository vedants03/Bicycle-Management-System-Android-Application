package com.example.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SelectBicycle extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String source;
    String destination;
    ArrayList<String> bicycles = new ArrayList<>();
    Spinner bicycleSelect;
    ListView plan;
    TextView location;
    ArrayAdapter<String> select;
    Button proceed;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String bicycleSelected,planSelected;
    static String currentTime;
    String[] plans = {"Rs 15 / 5 hours","Rs 30/ 20 hours","Rs 100/ 7 days","Rs 500 / 1 month"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Bundle bundle = getIntent().getExtras();
        bicycleSelect = (Spinner)findViewById(R.id.bicyclespinner);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        location = (TextView)findViewById(R.id.loctv);
        plan = (ListView) findViewById(R.id.planlist);

        ArrayAdapter<String> plan_select = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_single_choice,plans);
        plan.setAdapter(plan_select);
        plan.setChoiceMode(1);

        proceed = (Button)findViewById(R.id.proceed_button_select);

        source = bundle.getString("source");
        destination = bundle.getString("destination");
        location.setText(source);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Bundle bundle = new Bundle();
               bundle.putString("username",firebaseUser.getEmail());
               bundle.putString("bicycleId",bicycleSelected);
               bundle.putString("source",source);
                bundle.putString("destination",destination);
                planSelected = "";
                SparseBooleanArray checked = plan.getCheckedItemPositions();
                for (int j = 0; j < 4; j++)
                    if (checked.get(j)) {
                        String item = plans[j];
                        planSelected += item;
                    }
                bundle.putString("plan",planSelected);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               currentTime = sdf.format(Calendar.getInstance().getTime());
               bundle.putString("booktime",currentTime);
               Intent intent = new Intent(getApplicationContext(),BookBicycle.class);
               intent.putExtras(bundle);
               startActivity(intent);
            }
        });



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("cycles");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            int count = 0;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                count++;
                                String loc = data.child("location").getValue().toString();
                                String avail = String.valueOf(data.child("isavailable").getValue());
                                Log.d("bool",avail);
                                if (loc.equals(source.toLowerCase()) && avail.equals("true")){
                                    String id = data.child("id").getValue().toString();
                                    bicycles.add(id);
                                }
                            }
                            select = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,bicycles);
                            bicycleSelect.setAdapter(select);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No bicycles are available at this location",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SelectBicycle.this, "Error", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SelectBicycle.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });

    bicycleSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int index =adapterView.getSelectedItemPosition();
            bicycleSelected = bicycles.get(index);
            //Toast.makeText(getBaseContext(),"Item selected: "+bicycles.get(index),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

    plan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int index = position;
            planSelected = plans[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=  getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            Toast.makeText(getApplicationContext(),"Logging out...",Toast.LENGTH_SHORT).show();
            Intent intent3  =new Intent(getApplicationContext(),LoginActivity.class);
            firebaseAuth.signOut();
            startActivity(intent3);
        }
        return super.onOptionsItemSelected(item);
    }
}
