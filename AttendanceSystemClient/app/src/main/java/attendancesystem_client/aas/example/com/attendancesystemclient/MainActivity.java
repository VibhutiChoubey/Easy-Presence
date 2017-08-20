package attendancesystem_client.aas.example.com.attendancesystemclient;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import attendancesystem_client.aas.example.com.attendancesystemclient.util.progressGenerator;
import model.RegistrationManager;
import model.user;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements progressGenerator.OnCompleteListener {

    private Spinner classes;
    private EditText etroll,etname;
    private progressGenerator ProgressGenerator;
    private ActionProcessButton bstart;
    String[] items = new String[]{"Select your class","CSE7A","CSE7B"};
    String selected;
    private String DEVICE_ID;
    private RegistrationManager manager;
    private boolean UserAuthenticated = false;

    private static final int RC_READ_PHONE_STATE = 101;
    private static final int RC_CHANGE_WIFI_STATE = 102;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new RegistrationManager(this);
        if (manager.isFirstTime()) {
            getPermissions();
        } else {
            gotoNextActivity();
        }
        setContentView(R.layout.activity_main);

        classes=(Spinner) findViewById(R.id.spinnerclasses);
        etroll=(EditText) findViewById(R.id.etroll);
        etname=(EditText) findViewById(R.id.etname);
        bstart=(ActionProcessButton) findViewById(R.id.btnStart);
        ProgressGenerator=new progressGenerator(MainActivity.this);
        bstart.setMode(ActionProcessButton.Mode.PROGRESS);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        classes.setAdapter(adapter);

        classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),"Authenicated!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Not authentiated!",Toast.LENGTH_LONG).show();
                }
            }
        };
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Check your Internet Connection or drop me a message", Toast.LENGTH_SHORT).show();
                } else {
                    UserAuthenticated = true;
                    database = FirebaseDatabase.getInstance();
                }
            }
        });

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            DEVICE_ID = getDeviceID(this);
        }

        bstart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    Toast.makeText(getApplicationContext(),"All fields are required!",Toast.LENGTH_LONG).show();
                } else if (UserAuthenticated) {
                    manager.setFirstLaunch(false);
                    registerUser();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DEVICE_ID = getDeviceID(this);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private String getDeviceID(MainActivity mainActivity) {
        final String deviceID = ((TelephonyManager) mainActivity.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceID != null) {
            return deviceID;
        } else {
            return Build.SERIAL;
        }
    }

    private void registerUser() {
        final user User=new user(selected,
                Integer.valueOf(etroll.getText().toString()),
                etname.getText().toString(),
                DEVICE_ID);
        User.setUser_id(UUID.randomUUID().toString());
        DatabaseReference userref=reference.child("users");
        Map<String,user> users=new HashMap<>();
        users.put(User.getUser_id(),User);
        userref.setValue(users);

        manager.setUserRoll(Integer.valueOf(etroll.getText().toString()));
        manager.setUserName(etname.getText().toString());
        manager.setUserClass(selected);
        manager.setUserDeviceId(DEVICE_ID);

        ProgressGenerator.start(bstart);
        bstart.setEnabled(false);
        etroll.setEnabled(false);
        etname.setEnabled(false);
        classes.setEnabled(false);
    }

    private boolean validateForm() {
        return  !selected.equals("Select your class") &&
                !etroll.getText().toString().trim().equals("") &&
                !etname.getText().toString().trim().equals("");
    }

    private void gotoNextActivity() {
        startActivity(new Intent(getApplicationContext(),Mark.class));
        finish();
    }

    private void getPermissions() {
        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs to Read Device ID for classification", RC_READ_PHONE_STATE, android.Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.CHANGE_WIFI_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs this permission to function properly", RC_CHANGE_WIFI_STATE, android.Manifest.permission.CHANGE_WIFI_STATE);
        }
    }


    @Override
    public void onComplete() {
        gotoNextActivity();
    }
}
