package c.b.a.myapplication_600;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    EditText ip, user, pwd, port,shell,show_01;
    Button ipb;
    String buf,result,cmdlist;
    int max;
    Spinner linux1;
    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getxml();
        action();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("status");
            if(val.equals("ok")){
                show_01.setText(result);
                show_01.setTextSize(10);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                //System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuu"+result);
            }
            else {
                System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            }

        }
    };

    Runnable shellTask = new Runnable() {
        @Override
        public void run() {
            ssh(shell.getText().toString());
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("status", "ok");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };


    Runnable shellTask2 = new Runnable( ) {
        @Override
        public void run() {
            if(linux1.getSelectedItem().toString().equals("show-ip")){
                ssh("ifconfig | grep \"inet addr\"");
            }
           else if(linux1.getSelectedItem().toString().equals("show-cpu")){
                ssh("cat /proc/cpuinfo ");
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("status", "ok");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    public void getxml(){
        ip = (EditText) findViewById(R.id.iped);
        user = (EditText) findViewById(R.id.iped2);
        pwd = (EditText) findViewById(R.id.iped3);
        port = (EditText) findViewById(R.id.iped4);
        ipb = (Button) findViewById(R.id.ipbt1);
        shell=(EditText)findViewById(R.id.iped5);
        show_01=(EditText)findViewById(R.id.iped6);
        linux1=(Spinner)findViewById(R.id.spinner);

        //show_01.setInputType(InputType.TYPE_NULL);
    }

    public void action(){
        ipb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(shellTask).start();
            }
        });
        list.add("show-ip");
        list.add("show-mac");
        list.add("show-route");
        list.add("show-dns");
        list.add("show-cpu");
        list.add("show-memory");
        linux1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list));
        linux1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    new Thread(shellTask2).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void ssh(String cmd1) {
        JSch jsch = new JSch();
        String cmd = cmd1;
        Session session = null;
        try {
            session = jsch.getSession(user.getText().toString(), ip.getText().toString(), Integer.parseInt(port.getText().toString()));
            session.setPassword(pwd.getText().toString());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            int timeout = 60000000;
            session.setTimeout(timeout);
            session.connect();
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            InputStream in = channelExec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
             buf = null;
            result="";
            StringBuffer sb = new StringBuffer();
            while ((buf = reader.readLine()) != null) {
                int max2;
                max=buf.trim().length();
                sb.append(buf.trim());
                result+=buf.trim()+"\n";


               // System.out.println(buf);

            }
            reader.close();
            channelExec.disconnect();
            if (null != session) {
                session.disconnect();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



