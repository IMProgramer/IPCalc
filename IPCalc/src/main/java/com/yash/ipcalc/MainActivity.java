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
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {


    EditText ipaddbox;
    EditText subnetbox;
    TextView ipClassAns;
    TextView subnetsAns;
    TextView hostsAns;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();

    }

    private void setListeners() {
        ipaddbox = (EditText) findViewById(R.id.ipet);
        subnetbox = (EditText)findViewById(R.id.subnetet);
        ipClassAns = (TextView) findViewById(R.id.ipClass);
        subnetsAns = (TextView) findViewById(R.id.networkstv);
        hostsAns = (TextView) findViewById(R.id.hoststv);
        ((Button)findViewById(R.id.calculate)).setOnClickListener(this);
        ((Button)findViewById(R.id.b255)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subnetbox.append("255");
            }
        });

        ((Button)findViewById(R.id.bdot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subnetbox.append(".");
            }
        });
    }

    @Override
    public void onClick(View view) {
        p("On click");
        String IP_address = ipaddbox.getText().toString();
        p("IP_address"+":"+IP_address);

        // Validate IP address
        if(IP_address.equals("") || !validateIpAddress(IP_address))
            return;

        // Get IP class
        String ip_class = getIpClass(IP_address);
        p(ip_class);
        ipClassAns.setText(ip_class);

        // Get binary IP address
        String binaryIpAddress = getBinaryAddress(IP_address);
        String subnet = subnetbox.getText().toString();
        //if Subnet is Empty create get a default
        if (subnet.equals("")) {
            subnet = getDefaultSubnet(ip_class);
            subnetbox.setText(subnet);
            ((Button)findViewById(R.id.calculate)).performClick();

            // make popup for invalid subnet and focus to subnet box
        } else if (!validateSubnetMask(subnet,ip_class)) {
            Toast.makeText(this,"Invalid subnet",Toast.LENGTH_LONG).show();
            subnetbox.requestFocus();
            clear();

            // if Subnet is Valid
        } else {
            // Get binary format of Subnet Mask
            String binarySubnet = getBinaryAddress(subnet);

            int totalSubnets = getTotalSubnets(binarySubnet,ip_class);
            p("" + totalSubnets);
            subnetsAns.setText("" + totalSubnets);

            int hostsPerSubnet = getHostsPerSubnet(binarySubnet);
            p("" + hostsPerSubnet);
            hostsAns.setText(String.format("" + hostsPerSubnet));

            String networkAddress = getNetworkAddress(binaryIpAddress,binarySubnet);
            p("network address: "+networkAddress);
            ((TextView)findViewById(R.id.networkAddresstv)).setText(networkAddress);

            String broadcastAddress = getBroadcastAddress(binaryIpAddress,binarySubnet);
            p("broadcast address: "+broadcastAddress);
            ((TextView)findViewById(R.id.BroadcastAddresstv)).setText(broadcastAddress);

            String hostRange="";
            hostRange+=networkAddress.substring(0,networkAddress.lastIndexOf('.')+1);
            hostRange+=Integer.parseInt(networkAddress.substring(networkAddress.lastIndexOf('.')+1))+1;
            hostRange+=" - ";
            hostRange+=broadcastAddress.substring(0,broadcastAddress.lastIndexOf('.')+1);
            hostRange+=Integer.parseInt(broadcastAddress.substring(broadcastAddress.lastIndexOf('.')+1))-1;
            ((TextView)findViewById(R.id.hostRangetv)).setText(hostRange);
        }

    }

    private void clear() {
        hostsAns.setText("");
        subnetsAns.setText("");
    }

    private int getTotalSubnets(String binarySubnet, String ip_class) {
        int leastBits =8;
        if (ip_class.equals("B")) {
            leastBits = 16;

        } else if (ip_class.equals("C")) {
            leastBits = 24;

        }
        int numOf1s = binarySubnet.indexOf("0");
        int subnetBits = numOf1s - leastBits;
        return (int) Math.pow(2, subnetBits);
    }

    private String getDefaultSubnet(String ip_class) {
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
        return subnet;
    }

    private String getIpClass(String ipAddress) {

        int network = Integer.parseInt(ipAddress.substring(0, ipAddress.indexOf('.')));
        String ip_class;

        if (network <= 127 && network > 0) {
            ip_class = "A";
        } else if (network <= 191 && network > 0) {
            ip_class = "B";
        } else if (network <= 223 && network > 0) {
            ip_class = "C";
        } else {
            ip_class = "Reserved";
        }
        return ip_class;
    }
    public void p(String msg){
        System.out.println(msg);
    }

    public boolean validateIpAddress(String address){
        if(!address.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+"))
            return false;
        String octs[] = address.split("\\.");
        Boolean ret = true;
        for(String x: octs) {
            if (Integer.parseInt(x) > 255)
                ret = false;
        }
        return ret;
    }

    public boolean validateSubnetMask(String subnet, String ip_class){
        if(!validateIpAddress(subnet))
            return false;
        subnet = getBinaryAddress(subnet);
        int leastBits=8;
        if (ip_class.equals("A")) {
            leastBits = 8;

        } else if (ip_class.equals("B")) {
            leastBits = 16;

        } else if (ip_class.equals("C")) {
            leastBits = 24;

        }
        p(subnet.indexOf("0")+"");
        if(subnet.matches("1*0*") && subnet.indexOf("0")>=leastBits)
            return true;
        else
            return false;
    }

    public int getHostsPerSubnet(String binarySubnet) {
        return (int) Math.pow(2, 32 - binarySubnet.indexOf("0")) - 2;
    }

    public String getBinaryAddress(String subnet){
        String[] subnet_o = subnet.split("\\.");
        p("Length:"+subnet_o.length);
        p(subnet_o[0]+":"+subnet_o[1]+":"+subnet_o[2]+":"+subnet_o[3]);
        // convert to binary
        subnet_o[0] = Integer.toBinaryString(Integer.parseInt(subnet_o[0]));
        subnet_o[1] = Integer.toBinaryString(Integer.parseInt(subnet_o[1]));
        subnet_o[2] = Integer.toBinaryString(Integer.parseInt(subnet_o[2]));
        subnet_o[3] = Integer.toBinaryString(Integer.parseInt(subnet_o[3]));
        // convert each oct of 8 bits
        for (int i=0;i<subnet_o.length;i++){
            while (subnet_o[i].length() != 8)
                subnet_o[i] = "0"+subnet_o[i];
        }
        String s = subnet_o[0]+"."+subnet_o[1]+"."+subnet_o[2]+"."+subnet_o[3];
        ((TextView)findViewById(R.id.subnetBinary)).setText(s);
        String binary_subnet = subnet_o[0]+subnet_o[1]+subnet_o[2]+subnet_o[3];
        p("Binary Subnet:"+binary_subnet);

        return binary_subnet;
    }

    public String getBroadcastAddress(String binaryIpAddress,String binarySubnet){
        String broadcastAddress=binaryIpAddress.substring(0,binarySubnet.indexOf('0'));
        String ans="";
        for(int x = binarySubnet.indexOf('0');x<32;x++){
            broadcastAddress+='1';
        }
        for (int x=0;x<24;x+=8){
            ans+=getDecimal(broadcastAddress.substring(x,x+8))+".";

        }
        ans+=getDecimal(broadcastAddress.substring(broadcastAddress.length()-8));

        return ans;
    }

    public String getNetworkAddress(String binaryIpAddress,String binarySubnet){
        String networkAddress=binaryIpAddress.substring(0,binarySubnet.indexOf('0'));
        String ans="";
        for(int x = binarySubnet.indexOf('0');x<32;x++){
            networkAddress+='0';
        }
        for (int x=0;x<24;x+=8){
            ans+=getDecimal(networkAddress.substring(x,x+8))+".";
        }

        ans+=getDecimal(networkAddress.substring(networkAddress.length()-8));
        return ans;

    }

    public int getDecimal(String binary) {
        int decimal=0;

        for (int i = binary.length()-1; i >= 0 ; i--) {
            if(binary.charAt(i) == '1')
                decimal+=Math.pow(2,binary.length()-1-i);
        }
        return decimal;
    }

    public String doOr(String s1, String s2){
        String ans = "";
        for(int x=0;x<s1.length();x++){
            if(s1.charAt(x)=='1' || s2.charAt(x)=='1'){
                ans+='1';
            }else{
                ans+='0';
            }
        }
        return ans;
    }
}