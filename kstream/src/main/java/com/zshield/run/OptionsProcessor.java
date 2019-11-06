package com.zshield.run;

import org.apache.commons.cli.*;

public class OptionsProcessor {
    public static CommandLine parseArg(String[] args) throws ParseException {
        Options options = new Options();
        /**
         * 第一个参数：参数的简单形式
         * 第二个参数：参数的复杂形式
         * 第三个参数：是否需要额外的输入
         * 第四个参数：对参数的描述信息
         */
        options.addOption("b","bootstrap_server",true, "kafka httpServer");
        options.addOption("n","es_number_of",true, "es number of replica");
        options.addOption("e","es_host",true, "es host");
        options.addOption("u","upgrade",false, "upgrade");
        options.addOption("r","recent",true, "recent");
        options.addOption("s","stop",true, "stop");
        options.addOption("h","help",true, "help");

        CommandLineParser parser = new PosixParser();
        //Parse the args according to the specified options.
        CommandLine cmdLine = parser.parse(options, args);
        if (cmdLine.hasOption("help")) {
            System.exit(-1);
        }

        if (cmdLine.hasOption("bootstrap_server")) {
            throw new ParseException("bootstrap_server is indespensible");
        }
        return cmdLine;
    }


}
