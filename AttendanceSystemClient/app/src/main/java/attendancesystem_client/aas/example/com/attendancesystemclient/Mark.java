
package attendancesystem_client.aas.example.com.attendancesystemclient;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.dd.processbutton.iml.ActionProcessButton;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import attendancesystem_client.aas.example.com.attendancesystemclient.util.progressGenerator;
import model.RegistrationManager;
import pub.devrel.easypermissions.EasyPermissions;

public class Mark extends AppCompatActivity implements progressGenerator.OnCompleteListener{

    private TextView classs,roll,name,deviceid,message;
    private ActionProcessButton btnMark;
    private progressGenerator ProgressGenerator;
    private RegistrationManager registrationManager;
    private static final int RC_READ_PHONE_STATE = 101;
    private static final int RC_CHANGE_WIFI_STATE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        getPermissions();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        classs=(TextView) findViewById(R.id.classs);
        roll=(TextView) findViewById(R.id.roll);
        name=(TextView) findViewById(R.id.name);
        deviceid=(TextView) findViewById(R.id.deviceid);
        message=(TextView) findViewById(R.id.message);
        btnMark=(ActionProcessButton) findViewById(R.id.btnMark);
        ProgressGenerator=new progressGenerator(this);
        registrationManager=new RegistrationManager(this);

        classs.setText(registrationManager.getUserClass());
        roll.setText("Roll No: "+registrationManager.getUserRoll());
        name.setText("Name: "+registrationManager.getUsername());
        deviceid.setText("Device Id: "+registrationManager.getUserDeviceId());

        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyClientTask myClientTask = new MyClientTask();
                myClientTask.execute();
                ProgressGenerator.start(btnMark);
                btnMark.setEnabled(false);
            }
        });

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask() {
            dstAddress = "192.168.43.1";
            dstPort = 8080;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (registrationManager.getUserDeviceId() != null|| registrationManager.getUserRoll()!=null||registrationManager
                        .getUserClass()!=null) {
                    dataOutputStream.writeUTF(registrationManager.getUserDeviceId());
                    dataOutputStream.writeUTF(String.valueOf(registrationManager.getUserRoll()));
                    dataOutputStream.writeUTF(registrationManager.getUserClass());
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            switch (response) {

                case "200":
                    message.setText("Attendance marked :D");
                    stopWifi();
                    break;
                case "420":
                    message.setText("Sorry dost, ek lecture ki do attendance nhi milti :p");
                    stopWifi();
                    break;
                default:
                    message.setText(response);
                    break;
            }
            super.onPostExecute(result);
        }
    }

    public WifiManager mWifiManager;

    private void startwifi(){
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
    }

    private void stopWifi() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
        @Override
    public void onComplete() {

    }
}
