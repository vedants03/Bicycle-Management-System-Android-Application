package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookBicycle extends AppCompatActivity {
    TextView bookingDetails;
    Bundle bundle;
    String finalDetails,userName;
    String[] name;
    Button book;
    String bicycleSelected,key;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("cycles");
        databaseReference1 = firebaseDatabase.getReference().child("users");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        bookingDetails = (TextView)findViewById(R.id.bookingdetails);
        bundle = getIntent().getExtras();
        userName = bundle.getString("username");
        bicycleSelected = bundle.getString("bicycleId");
        finalDetails="";
        finalDetails += "Username: " + bundle.getString("username") + "\n";
        finalDetails += "Bicycle-Id: " + bundle.getString("bicycleId") + "\n";
        finalDetails += "Pick-up Point: " + bundle.getString("source") + "\n";
        finalDetails += "Drop-point: " + bundle.getString("destination") + "\n";
        finalDetails += "Plan Selected: " + bundle.getString("plan")+ "\n";
        name = userName.split("@");
        bookingDetails.setText(finalDetails);
        book = (Button) findViewById(R.id.bookbicycle);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(bicycleSelected).child("isavailable").setValue(false);
                databaseReference1.child(name[0]).child("taken").setValue("true");
                Bundle myBundle = new Bundle();
                myBundle.putString("bicycleID",bicycleSelected);
                myBundle.putString("drop",bundle.getString("destination"));
                myBundle.putString("username",name[0]);
                myBundle.putString("plan",bundle.getString("plan"));
                Intent intent = new Intent(getApplicationContext(),ReturnBicycle.class);
                intent.putExtras(myBundle);
                addNotification();
                startActivity(intent);
            }
        });


    }

    private void addNotification(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification","Alert Notification",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alerts when order is placed");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "My Notification")
                .setSmallIcon(R.drawable.notification) // notification icon
                .setContentTitle("Booking Successful") // title for notification
                .setContentText("Thank You for Using Our Services")// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("time","");
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
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