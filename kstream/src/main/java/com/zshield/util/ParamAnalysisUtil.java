package com.zshield.util;

import com.zshield.run.CmdLineParams;
import com.zshield.run.OptionsProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ParamAnalysisUtil {
    public static String es_host;
    public static int es_number_of_replica;
    public static String bootstrap_server;
    public static String consumer_client_id;
    public static String base_stream_client_id;
    public static String violation_tream_client_id;
    public static boolean is_upgrade;
    public static String input_topic;
    public static String medium_topic;
    public static String output_topic;
    public static String timeRange;
    public static String stop_pro;
    private static final Logger logger = LoggerFactory.getLogger(ParamAnalysisUtil.class);

    public static void Parsing(String[] args) {
        try {
            CommandLine cmdLine = OptionsProcessor.parseArg(args);
            CmdLineParams.setLine(cmdLine);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String getLocalIpAddress(String bootstrap_server) throws SocketException {
        String[] severIps = new String[1];
        severIps[0]= bootstrap_server.split(":")[0];
        for(Enumeration interfaces = NetworkInterface.getNetworkInterfaces();interfaces.hasMoreElements();) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

            for (Enumeration inetAddresses = networkInterface.getInetAddresses();inetAddresses.hasMoreElements();) {
                InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();
                String ip = inetAddr.getHostAddress();
                for (String severIp : severIps) {
                    if (severIp.equals(ip)) {
                        return severIp;
                    }
                }
            }
        }
        return "localhost";
    }
}
