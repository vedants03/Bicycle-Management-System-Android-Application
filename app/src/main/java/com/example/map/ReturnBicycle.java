package com.example.map;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReturnBicycle extends AppCompatActivity  {
    Bundle bundle;
    String bicyleId,dropPoint,name;
    TextView detail;
    String display;
    Button returnButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref1,ref2;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
            bundle = getIntent().getExtras();
            bicyleId = bundle.getString("bicycleID");
            dropPoint = bundle.getString("drop");
            name = bundle.getString("username");
            detail = (TextView) findViewById(R.id.txt);
            display = "Your Ride Details: \n" + "Your Bicycle: " + bicyleId  + "\nDrop-Location: " + dropPoint+"\n\nAmount To Be Paid: "+bundle.getString("plan");
            detail.setText(display);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

            firebaseDatabase = FirebaseDatabase.getInstance();
            ref1 = firebaseDatabase.getReference("cycles");
            ref2 = firebaseDatabase.getReference("users");

            returnButton = (Button) findViewById(R.id.bt1);

            returnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref1.child(bicyleId).child("isavailable").setValue("true");
                    ref1.child(bicyleId).child("location").setValue(dropPoint.toLowerCase());
                    ref2.child(name).child("taken").setValue("false");
                    Intent intent = new Intent(getApplicationContext(),LocationActivity.class);
                    addNotification();
                    startActivity(intent);
                    finish();
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

    private void addNotification(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification","Alert Notification",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alerts when order is placed");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "My Notification")
                .setSmallIcon(R.drawable.notification) // notification icon
                .setContentTitle("Return Successful") // title for notification
                .setContentText("Thank You for Using Our Services")// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("time","");
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
