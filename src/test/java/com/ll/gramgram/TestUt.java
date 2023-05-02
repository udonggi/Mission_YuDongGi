package com.ll.gramgram;

import com.ll.gramgram.standard.util.Ut;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestUt {
    public static boolean setFieldValue(Object o, String fieldName, Object value) {
        return Ut.reflection.setFieldValue(o, fieldName, value);
    }
}