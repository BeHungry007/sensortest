package com.zshield.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanningPacakageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScanningPacakageUtil.class);
    private Class<?> superStrategy;
    private ClassLoader classLoader = ScanningPacakageUtil.class.getClassLoader();
    private List<Class<?>> eleStrategyList = new ArrayList<>();
    
    public void addClass(String packName) {
        URL url = classLoader.getResource(packName.replace(".", "/"));
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            //本地自己可见的代码
            findClassLocal(packName);
        } else {
            //引用jar包的代码
            findClassJar(packName);
        }
    }

    private void findClassJar(String packName) {
        JarFile jarFile = null;
        String pathName = packName.replace(".", "/");
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw  new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            
            if (jarEntryName.contains(packName) && !jarEntryName.equals(packName + "/")) {
                //递归遍历子目录
                if (jarEntry.isDirectory()) {
                    String clazzName = jarEntry.getName().replace("/",".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix);
                }
                if (jarEntry.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/",".").replace(".class",""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (superStrategy.isAssignableFrom(clazz) && !superStrategy.getName().equals(clazz.getName())) {
                        eleStrategyList.add(clazz);
                    }
                }
            }
        }
    }

    private void findClassLocal(String packName) {
        URI url = null;
        try {
            url = classLoader.getResource(packName.replace(".","/")).toURI();
        } catch (URISyntaxException e) {
            throw  new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File chiFile) {
                if (chiFile.isDirectory()) {
                    findClassLocal(packName + "." + chiFile.getName())
                }
                if (chiFile.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        String classPath = packName + "." + chiFile.getName().replace(".class","");
                        clazz = classLoader.loadClass(classPath);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (superStrategy.isAssignableFrom(clazz) && !superStrategy.getName().equals(clazz.getName())) {
                        eleStrategyList.add(clazz);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public  ScanningPacakageUtil(Class<?> superStrategy) {
        this.superStrategy = superStrategy;
    }

    public Class<?> getSuperStrategy() {
        return superStrategy;
    }

    public void setSuperStrategy(Class<?> superStrategy) {
        this.superStrategy = superStrategy;
    }

    public List<Class<?>> getEleStrategyList() {
        return eleStrategyList;
    }

}
