package com.yash.ipcalc;

/*
    1-127 n.h.h.h A
    128-191 n.n.h.h B
    192-223 n.n.n.h C

    1.0.0.0 126.0.0.0
    128.0.0.0 191.255.0.0
    192.0.1.0 223.255.255.0
 */


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {


    EditText ipaddbox;
    EditText subnetbox;
    TextView ipclassans;
    TextView totalsubnets;
    TextView totalhosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();

    }

    private void setListeners() {
        ipaddbox = (EditText) findViewById(R.id.ipet);
        subnetbox = (EditText)findViewById(R.id.subnetet);
        ipclassans = (TextView) findViewById(R.id.ipClass);
        totalsubnets = (TextView) findViewById(R.id.networkstv);
        totalhosts = (TextView) findViewById(R.id.hoststv);
        ((Button)findViewById(R.id.calculate)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        p("On click");
        String IP_address = ((EditText)findViewById(R.id.ipet)).getText().toString();
        p("IP_address"+":"+IP_address);
        String ip_class="";
        if(IP_address.equals(""))
            return;
        ip_class = getIpClass(IP_address);
        p(ip_class);
        if(ip_class.equals("invalid"))
            return;
        ipclassans.setText(ip_class);
        if(subnetbox.getText().toString().equals("")){
            String subnet;
            if (ip_class.equals("A")) {
                subnet = "255.0.0.0";
            } else if (ip_class.equals("B")) {
                subnet = "255.255.0.0";
            } else if (ip_class.equals("C")) {
                subnet = "255.255.255.0";
            }else{
                subnet = "";
            }
            subnetbox.setText(subnet);
        }else{
            String subnet = subnetbox.getText().toString();
            if(!subnet.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+"))
                return;

            String[] subnet_o = subnet.split("\\.");
            p("Length:"+subnet_o.length);
            p(subnet_o[0]+":"+subnet_o[1]+":"+subnet_o[2]+":"+subnet_o[3]);
            subnet_o[0] = Integer.toBinaryString(Integer.parseInt(subnet_o[0]));
            subnet_o[1] = Integer.toBinaryString(Integer.parseInt(subnet_o[1]));
            subnet_o[2] = Integer.toBinaryString(Integer.parseInt(subnet_o[2]));
            subnet_o[3] = Integer.toBinaryString(Integer.parseInt(subnet_o[3]));
            p(subnet_o[0]+":"+subnet_o[1]+":"+subnet_o[2]+":"+subnet_o[3]);
            String binary_subnet = subnet_o[0]+subnet_o[1]+subnet_o[2]+subnet_o[3];
            p(binary_subnet);
            int subnet_bits = 0;
            while(binary_subnet.charAt(subnet_bits) == '1'){
                subnet_bits++;
            }
            p("Subnet Bits:"+subnet_bits);
            p(""+(Math.pow(2,subnet_bits%8)));
            p(""+(Math.pow(2, 32 - subnet_bits)-2));
            totalsubnets.setText(""+Math.pow(2,subnet_bits%8));
            totalhosts.setText(String.format("%d",(int)(Math.pow(2, 32 - subnet_bits)-2)));
        }

    }

    private String getIpClass(String ipAddress) {
        if(!ipAddress.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+"))
            return "invalid";
        int network = Integer.parseInt(ipAddress.substring(0,ipAddress.indexOf('.')));
        String ip_class;

        if (network <= 127 && network > 0){
            ip_class = "A";
        }else if(network <= 191 && network > 0){
            ip_class = "B";
        }else if(network <=223 && network > 0){
            ip_class = "C";
        }else{
            ip_class = "Reserved";
        }
        return ip_class;
    }
    public void p(String msg){
        System.out.println(msg);
    }
}