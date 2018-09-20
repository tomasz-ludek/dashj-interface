package org.dashj.dashjinterface.util;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.wallet.DeterministicKeyChain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RestrictedAccessUtil {

    @SuppressWarnings("unchecked")
    public static List<ECKey> invokeGetKeys(DeterministicKeyChain object, boolean includePrivateKeys) {
        try {
            Method method = object.getClass().getDeclaredMethod("getKeys", boolean.class);
            method.setAccessible(true);
            return (List<ECKey>) method.invoke(object, includePrivateKeys);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
