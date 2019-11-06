package com.aa.run;

import org.apache.commons.cli.CommandLine;

public class CmdLineParams {
    public static CommandLine line;

    public static void setLine(CommandLine cmdLine) {
        CmdLineParams.line = cmdLine;
    }

    public static String getBootstrapSever() {
        String bootstrap_sever = line.getOptionValue("bootstrap_server");
        return bootstrap_sever;
    }
}
