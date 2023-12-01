package pns.project.pz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import pns.project.pz.commands.Command;
import pns.project.pz.commands.CommandType;
import pns.project.pz.commands.values.KeyValue;
import pns.project.pz.communicator.MessageSender;
import pns.project.pz.connector.ConnectionListener;
import pns.project.pz.connector.ConnectionManager;
import pns.project.pz.utils.CommandQueue;
import pns.project.pz.utils.GestureTap;
import pns.project.pz.utils.Settings;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, DialogInterface.OnClickListener,  ConnectionListener {

    private String TAG = MainActivity.class.getCanonicalName();
    private Context ctx;
    private GestureDetector detector;
    private boolean connected;
    private EditText hostEditText;
    private EditText portEditText;
    private EditText pro1EditText;
    private ImageButton connector;
    private MainActivity thisCTX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;

        detector = new GestureDetector(this, new GestureTap());
        connector = findViewById(R.id.connectionBTN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        RelativeLayout touchscreen = findViewById(R.id.touch_screen);
        touchscreen.setOnTouchListener(this);

        MessageSender.getInstance().init();
        ConnectionManager.getInstanceOf().addConnectionManagerListener(this);
        thisCTX = this;
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    public void specialKeysDialog(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View dialogView = getLayoutInflater().inflate(R.layout.special_dialog, null);
        Button spk1, spk2;
        spk1 = dialogView.findViewById(R.id.sp_k1);
        spk2 = dialogView.findViewById(R.id.sp_k2);

        spk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            sendMSG("SP_1 : NO_OP");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });

        spk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            sendMSG("SP_2 : NO_OP");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
        builder.setNeutralButton("Cancel", this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.show();
    }

    final void sendMSG(String message){
        if(connected){
            ConnectionManager.getInstanceOf().sendMessage(message);
        }
    }

    public void connectionDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View dialogView = getLayoutInflater().inflate(R.layout.host_dialog, null);
        hostEditText = dialogView.findViewById(R.id.hostETHD);
        portEditText = dialogView.findViewById(R.id.portETHD);
        pro1EditText = dialogView.findViewById(R.id.pro1ETHD);

        if (Settings.getInstanceOf().getHost() != null && !Settings.getInstanceOf().getHost().isEmpty()) {
            hostEditText.setText(Settings.getInstanceOf().getHost());
        }

        if (Settings.getInstanceOf().getPassword() != null && !Settings.getInstanceOf().getPassword().isEmpty()) {
            pro1EditText.setText(Settings.getInstanceOf().getPassword());
        }
        if (Settings.getInstanceOf().getPort() != 0) {
            portEditText.setText(String.format("%s", Settings.getInstanceOf().getPort()));
        }

        if (connected) {
            builder.setNegativeButton("Disconnect", this);
        } else {
            builder.setPositiveButton("Connect", this);
        }
        builder.setNeutralButton("Cancel", this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (connected) {
            CommandQueue.getInstance().push(Command.of(CommandType.KEYBOARD_INPUT, KeyValue.of(event)));
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (connected) {
            detector.onTouchEvent(motionEvent);
        }

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i(TAG, "Dialog KEY: " + which);
        switch (which) {
            case -1: {
                // connect
                String host = hostEditText.getText().toString();
                String portString = portEditText.getText().toString();
                String password = pro1EditText.getText().toString();
                if (host.isEmpty() || portString.isEmpty() || password.isEmpty()) {
                    return;
                }
                int port = Integer.parseInt(portString);
                Settings.getInstanceOf().setHost(host);
                Settings.getInstanceOf().setPort(port);
                Settings.getInstanceOf().setPassword(password);
                ConnectionManager.getInstanceOf().attemptConnection();
            }
            break;
            case -2: {
                // disconnect
                ConnectionManager.getInstanceOf().disconnect();
            }
            break;
        }
    }


    @Override
    public void onConnected() {
        connected = true;
        final String msg = "Connection established with host machine";

        ConnectionManager.getInstanceOf().sendMessage(Settings.getInstanceOf().getPassword());
        connector.setImageResource(R.drawable.ic_connected);
        Log.i(TAG, msg);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDisconnected() {
        connected = false;
        connector.setImageResource(R.drawable.ic_not_connected);
        final String msg = "Connection disrupted";
        Log.e(TAG, msg);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        connected = false;
        final String msg = "Could not establish connection with host machine";
        Log.e(TAG, msg);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void openFTP(View view) {
        if(connected){

            String url = Settings.getInstanceOf().getHost();
            url = "http://"+url+":8000";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }
}
