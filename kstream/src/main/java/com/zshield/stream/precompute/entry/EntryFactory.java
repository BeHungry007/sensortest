package com.zshield.stream.precompute.entry;

import com.google.gson.JsonObject;
import com.zshield.util.ScanningPacakageUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntryFactory {
    private static final Logger logger = LoggerFactory.getLogger(EntryFactory.class);

    public static final String ENTRYPACKAGE = "com.zshield.stream.precompute.entry";

    public static final Class<?> SUPERSTRATEGY = EntryInterface.class;

    private Map<Class<?>,EntryBean> entryMethodList;

    private EntryFactory(){}

    private static class SingletonPatternHolder {
        private static final EntryFactory entryFactory = new EntryFactory();
    }

    public static EntryFactory getInstance() {
        return SingletonPatternHolder.entryFactory;
    }

    public List<EntryInterface> create(JsonObject log) {
        ArrayList<EntryInterface> entrys = new ArrayList<>();
        if (entryMethodList.size() == 0) {
            loadPackage();
        }
        Set<Map.Entry<Class<?>, EntryBean>> classEntryBeanSet = entryMethodList.entrySet();
        for (Map.Entry<Class<?>, EntryBean> classEntryBeanEntry : classEntryBeanSet) {
            Class<?> k = classEntryBeanEntry.getKey();
            EntryFactory.EntryBean v = classEntryBeanEntry.getValue();
            try {
                EntryInterface entry = v.create(log);
                if (entry != null) {
                    entrys.add(entry);
                }
            } catch (ReflectiveOperationException e) {
                logger.error("class - {} Execute isUpdate method error, log {} ",k.getName(), log, e);
            }
        }
        return entrys;
    }

    private Map<Class<?>, EntryBean> loadPackage() {
        ScanningPacakageUtil scanningPacakageUtil = new ScanningPacakageUtil(EntryFactory.SUPERSTRATEGY);
        scanningPacakageUtil.addClass(EntryFactory.ENTRYPACKAGE);
        List<Class<?>> entryList = scanningPacakageUtil.getEleStrategyList();
        for (int i = 0; i < entryList.size(); i++) {
            Class<?> Clazz = entryList.get(i);
            Constructor<?> classes;
            Object objects;
            try {
                classes = Clazz.getConstructor();
                objects = classes.newInstance();
            } catch (ReflectiveOperationException e) {
                logger.warn("class - did not find the default constructor ", Clazz.getName());
                EntryTemp entryTemp = new EntryTemp(Clazz).invoke();
                Clazz = entryTemp.getNewclazz();
                objects = entryTemp.getObjects();
                logger.warn("Success Create a new class {}", Clazz.getName());
            }
            Method create = null;
            try {
                create = Clazz.getMethod("create", JsonObject.class);
            } catch (NoSuchMethodException e) {
                logger.error("class - {} No find create(JsonObject.class) method",Clazz.getName(), e);
            }

            EntryBean bean = new EntryBean(create, objects);
            entryMethodList.put(Clazz, bean);
        }
        return entryMethodList;
    }



    public static class EntryBean {
        private Method create;
        private Object object;

        public EntryBean(Method create, Object object) {
            this.create = create;
            this.object = object;
        }


        public EntryInterface create(JsonObject jsonObject) throws InvocationTargetException, IllegalAccessException {
            return (EntryInterface)create.invoke(object, jsonObject);
        }
    }

    private class EntryTemp {
        private Class<?> clazz;
        private Class<?> newclazz;
        private Object objects;

        public EntryTemp(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Object getObjects() {
            return objects;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Class<?> getNewclazz() {
            return newclazz;
        }

        public EntryTemp invoke() {
            ClassPool pool = ClassPool.getDefault();
            try {
                pool.get(clazz.getName() + "temp");
            } catch (NotFoundException e) {
                logger.error("create a new class {}", clazz.getName() + "temp");
                try {
                    CtClass oldClass = pool.get(clazz.getName());
                    oldClass.setName(clazz.getName() + "temp");
                    CtClass newClass = oldClass;
                    newClass.addConstructor(CtNewConstructor.defaultConstructor(newClass));
                    newclazz = newClass.toClass();
                } catch (Exception e1) {
                    logger.error("failure Create a new class {}", clazz.getName() + "temp", e1);
                }
            }
            if (newclazz == null) {
                try {
                    newclazz = this.clazz.getClassLoader().loadClass(clazz.getName() + "temp");
                } catch (ClassNotFoundException e) {
                    logger.error("failure load the class {}", clazz.getName() + "temp");
                }
            }
            Constructor<?> classes = null;
            try {
                classes = newclazz.getConstructor();
                objects = classes.newInstance();
            } catch (NoSuchMethodException |InstantiationException | IllegalAccessException|InvocationTargetException e ) {
                logger.warn("class - {} did not find the default constructor ", newclazz.getName());
            }
            return  this;
        }
    }

}
