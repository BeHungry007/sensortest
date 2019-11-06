package ParamAnalysis;

import com.aa.run.CmdLineParams;
import org.apache.commons.cli.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import java.net.URI;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Demo {
    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("b","bootstrap_server",true, "kafka httpServer");
        options.addOption("n","es_number_of",true, "es number of replica");
        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args);
        CmdLineParams.setLine(cmdLine);
        String bootstrapSever = CmdLineParams.getBootstrapSever();
        System.out.println(bootstrapSever);
    }

    @Test
    public void test01(){
        Integer a = Integer.valueOf("72", 8);int limit = -Integer.MAX_VALUE;
        System.out.println(limit / 8);
        System.out.println(a);

    }
    
    @Test
    public void test02(){
        LocalTime a = LocalTime.of(3, 33);
        LocalTime b = LocalTime.of(4, 23);
        System.out.println(a.getHour());
        System.out.println(a.compareTo(b));
    }

    @Test
    public void test03(){
        Map<String, Son> map = new HashMap<>();
        Son son = new Son();
        map.put("1", son);
        son.id = "b";
        System.out.println(map.get("1").id);
    }

    @Test
    public void test04(){
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        s1.add(1);
        s1.add(2);
        s2.add(2);
        System.out.println(s1.retainAll(s2));
        System.out.println(s1);
    }

    @Test
    public void test05(){
        String s1 = "a";
        String s2 = "a";
        System.out.println(s1.compareTo(s2));
    }

    @Test
    public void test06(){
        ResourceConfig rc = new ResourceConfig().packages("com.aa.util");
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://182.61.200.7"), rc, false);
        System.out.println(httpServer);
    }
}
