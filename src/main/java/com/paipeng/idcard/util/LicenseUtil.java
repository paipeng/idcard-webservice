package com.paipeng.idcard.util;

import com.paipeng.idcard.entity.License;
import javax0.license3j.crypto.LicenseKeyPair;
import javax0.license3j.io.IOFormat;
import javax0.license3j.io.KeyPairReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class LicenseUtil {
    private final static Logger logger = LogManager.getLogger(LicenseUtil.class.getSimpleName());
    private static LicenseUtil licenseUtil;
    private LicenseKeyPair keyPair;

    public LicenseUtil() {

    }

    public static LicenseUtil getInstance() {
        if (licenseUtil == null) {
            licenseUtil = new LicenseUtil();
        }
        return licenseUtil;
    }

    public void loadKeys(String privateFile, String publicFile) {
        // load private key
        loadPrivateKey(privateFile);
        loadPublicKey(publicFile);

        // load public key
    }



    private void loadPrivateKey(String keyFile) {
        logger.info("loadPrivateKey: " + keyFile);
        final IOFormat format = IOFormat.BINARY;
        try (final KeyPairReader reader = new KeyPairReader(keyFile)) {
            keyPair = merge(keyPair, reader.readPrivate(format));
            final String keyPath = new File(keyFile).getAbsolutePath();
            logger.info("Private key loaded from" + keyPath);
        } catch (Exception e) {
            logger.error("An exception occurred loading the key: " + e);
        }
    }

    private void loadPublicKey(String keyFile) {
        logger.info("loadPublicKey: " + keyFile);
        final IOFormat format = IOFormat.BINARY;
        try (final KeyPairReader reader = new KeyPairReader(keyFile)) {
            keyPair = merge(keyPair, reader.readPublic(format));
            final String keyPath = new File(keyFile).getAbsolutePath();
            logger.info("Public key loaded from" + keyPath);
        } catch (Exception e) {
            logger.error("An exception occurred loading the keys: " + e);
        }
    }

    private LicenseKeyPair merge(LicenseKeyPair oldKp, LicenseKeyPair newKp) {
        if (oldKp == null) {
            return newKp;
        }
        final String cipher = oldKp.cipher();
        if (newKp.getPair().getPublic() != null) {
            return LicenseKeyPair.Create.from(newKp.getPair().getPublic(), oldKp.getPair().getPrivate(), cipher);
        }
        if (newKp.getPair().getPrivate() != null) {
            return LicenseKeyPair.Create.from(oldKp.getPair().getPublic(), newKp.getPair().getPrivate(), cipher);
        }
        return oldKp;
    }


    public License genLicense(License license) {
        return license;
    }
}
