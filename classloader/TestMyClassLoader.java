package com.xyc;

public class TestMyClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MyClassLoader myClassLoader = new MyClassLoader("/Users/YuChen_Xu/Desktop/","myClassLoader");
        Class c = myClassLoader.loadClass("test");
        System.out.println(c.getClassLoader());
        c.newInstance();

    }
}
