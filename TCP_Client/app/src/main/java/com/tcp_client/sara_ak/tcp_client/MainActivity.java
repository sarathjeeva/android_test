package com.tcp_client.sara_ak.tcp_client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {


    public TextView tvServerMessage;
    public EditText msg_to_server;
    StringBuilder stringBuilder = new StringBuilder();
    private Button send_msg;
    public Socket socket ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    if (socket==null)socket = new Socket("192.168.1.100", Integer.parseInt( "8080"));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();




        tvServerMessage = (TextView) findViewById(R.id.textViewServerMessage);
        msg_to_server = (EditText) findViewById(R.id.input_text);

//        ClientAsyncTask clientAST = new ClientAsyncTask();
//        //Pass the server ip, port and client message to the AsyncTask
//        clientAST.execute(new String[] { "192.168.1.100", "8080",""});


        send_msg = (Button)findViewById(R.id.send);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(socket!=null) sendMsg();
                    else
                    Toast.makeText(getApplicationContext(), "Server Not Running", Toast.LENGTH_SHORT).show();
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




    public void sendMsg(){
        //Create an instance of AsyncTask
        ClientAsyncTask clientAST = new ClientAsyncTask();
        //Pass the server ip, port and client message to the AsyncTask
        clientAST.execute( msg_to_server.getText().toString() );
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


    /**
     * AsyncTask which handles the communication with the server
     */
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {


                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();

                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                //Write data to the output stream of the client socket
                out.println(params[0]);
                //Buffer the data coming from the input stream

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket

            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            //Write server message to the text view
            stringBuilder.append(s+"\n");

            tvServerMessage.setText(stringBuilder);
        }
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("main", "The onDestroy() event");
    }


}
