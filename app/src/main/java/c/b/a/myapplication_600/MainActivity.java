package c.b.a.myapplication_600;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    EditText ip, user, pwd, port,show_01;
    Button ipb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getxml();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
        }
    };

    Runnable shellTask = new Runnable() {
        @Override
        public void run() {
            ssh();
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "");
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
        show_01=(EditText)findViewById(R.id.iped6);
    }

    public void action(){
        ipb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(shellTask).start();
            }
        });
    }


    public void ssh() {
        JSch jsch = new JSch();
        String cmd = "ifconfig";
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
            String buf = null;
            StringBuffer sb = new StringBuffer();
            while ((buf = reader.readLine()) != null) {
                sb.append(buf);
                System.out.println(buf);
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



