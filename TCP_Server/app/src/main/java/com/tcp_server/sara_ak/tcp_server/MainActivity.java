package com.tcp_server.sara_ak.tcp_server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {


    private TextView tvClientMsg,tvServerIP,tvServerPort;
    public Button send;
    public EditText msg_to_client;
    private final int SERVER_PORT = 8080; //Define the server port
    boolean Send_OK=false;
    public StringBuilder stringBuilder= new StringBuilder();
    public  Socket socClient = null;
    public  ServerSocket socServer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvClientMsg = (TextView) findViewById(R.id.textViewClientMessage);
        tvServerIP = (TextView) findViewById(R.id.textViewServerIP);
        tvServerPort = (TextView) findViewById(R.id.textViewServerPort);
        tvServerPort.setText(Integer.toString(SERVER_PORT));

        msg_to_client = (EditText)findViewById(R.id.text_to_client) ;
        send = (Button)findViewById(R.id.send);

        //Call method
        getDeviceIpAddress();
        //New thread to listen to incoming connections
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Infinite loop will listen for client requests to connect
                    while (true) {
                        //Accept the client connection and hand over communication to server side client socket
                        socServer = new ServerSocket(SERVER_PORT);
                        socClient = socServer.accept();
                        //For each client new instance of AsyncTask will be created
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(socClient!=null) sendMsg();
                else
                    Toast.makeText(getApplicationContext(), "Not Connected with client", Toast.LENGTH_SHORT).show();


            }
        });


        //        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        //            }
        //        });
    }

    private void sendMsg() {

        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
        //Start the AsyncTask execution
        //Accepted client socket object will pass as the parameter
        serverAsyncTask.execute(msg_to_client.getText().toString());

    }

    private void getDeviceIpAddress() {
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        tvServerIP.setText(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
    }

    class ServerAsyncTask extends AsyncTask<String, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            //Get the accepted socket object
            String msg=params[0];
            try {
                //Get the data input stream comming from the client
                InputStream is = socClient.getInputStream();


                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        socClient.getOutputStream(), true);
                //Write data to the data output stream
                out.println(msg);


                //Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                result = br.readLine();





            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            stringBuilder.append(s+"\n");
            //After finishing the execution of background task data will be write the text view
            tvClientMsg.setText(stringBuilder);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //Close the client connection
        try {
            if (socClient!=null)
                socClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("main", "The onDestroy() event");
    }



}
