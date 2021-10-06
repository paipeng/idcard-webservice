package com.paipeng.idcard.util;

import com.paipeng.idcard.entity.License;
import javax0.license3j.Feature;
import javax0.license3j.crypto.LicenseKeyPair;
import javax0.license3j.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;

public class LicenseUtil {
    private final static Logger logger = LogManager.getLogger(LicenseUtil.class.getSimpleName());
    private static LicenseUtil licenseUtil;
    private LicenseKeyPair keyPair;
    private String outputFilePath;

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

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
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


    public License genLicense(License license) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        logger.info("genLicense: " + license.getOwner());
        javax0.license3j.License license3j = new javax0.license3j.License();

        for (String featureString : license.getFeatureStrings()) {
            Feature feature = Feature.Create.from(featureString);
            license3j.add(feature);
        }

        // sign
        license3j.sign(keyPair.getPair().getPrivate(), "SHA-512");

        String base64Signature = Base64.getEncoder().encodeToString(license3j.getSignature());
        logger.info("signature: " + base64Signature);
        license.setSignature(base64Signature);

        // verify
        verify(license3j);

        // save to file/gen
        license.setFilePath(license.getUuid() + ".license");
        String filePath = saveLicense(license3j, System.getenv("PROJ_HOME") + "/" + outputFilePath + "/" + license.getFilePath());
        logger.info("saveLicense filePath: " + filePath);
        if (filePath == null) {
            license.setFilePath(null);
        }
        return license;
    }


    private void generate(String privateKeyFile, String publicKeyFile) {
        final String algorithm = "RSA";
        final String sizeString = "2048";
        final IOFormat format = IOFormat.BINARY;
        final int size;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            logger.error("Option size has to be a positive decimal integer value. " +
                    sizeString + " does not qualify as such.");
            return;
        }
        generateKeys(algorithm, size);
        try (final KeyPairWriter writer = new KeyPairWriter(privateKeyFile, publicKeyFile)) {
            writer.write(keyPair, format);
            final String privateKeyPath = new File(privateKeyFile).getAbsolutePath();
            logger.info("Private key saved to " + privateKeyPath);
            logger.info("Public key saved to " + new File(publicKeyFile).getAbsolutePath());
        } catch (IOException e) {
            logger.error("An exception occurred saving the keys: " + e);
        }
    }

    private void generateKeys(String algorithm, int size) {
        try {
            keyPair = LicenseKeyPair.Create.from(algorithm, size);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Algorithm " + algorithm + " is not handled by the current version of this application.", e);
        }
    }

    private String saveLicense(javax0.license3j.License license, String outputPath) {
        logger.info("saveLicense: " + outputPath);
        if (license == null) {
            logger.error("There is no license to save.");
            return null;
        }
        try {
            final String fileName = outputPath;
            final LicenseWriter reader = new LicenseWriter(fileName);
            reader.write(license, IOFormat.BINARY);
            logger.info("License was saved into the file " + new File(fileName).getAbsolutePath());
            return fileName;
        } catch (IOException e) {
            logger.error("Error writing license file " + e);
            return null;
        }
    }

    private void verify(javax0.license3j.License license) {
        if (keyPair == null || keyPair.getPair() == null || keyPair.getPair().getPublic() == null) {
            logger.error("There is no public key to verify the license with.");
            return;
        }
        if (license.isOK(keyPair.getPair().getPublic())) {
            logger.info("License is properly signed.");
        } else {
            logger.error("License is not signed properly.");
        }
    }

    public License verifyLicense(byte[] bytes) throws IOException {
        if (keyPair == null || keyPair.getPair() == null || keyPair.getPair().getPublic() == null) {
            logger.error("There is no public key to verify the license with.");
            return null;
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        LicenseReader reader = new LicenseReader(inputStream);
        javax0.license3j.License license3j = reader.read();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final LicenseWriter licenseWriter = new LicenseWriter(baos);
        licenseWriter.write(license3j, IOFormat.STRING);
        String dumpString = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        logger.info("License:\n" + dumpString);
        License license = new License();

        String[] lines = dumpString.split("\n");
        for (String line : lines) {
            if (line.startsWith("app")) {
                license.setApp(line.split("=")[1]);
            } else if (line.startsWith("expire")) {
                license.setExpire(Timestamp.valueOf(line.split("=")[1]));
            } else if (line.startsWith("licenseSignature")) {
                license.setSignature(line.split("=")[1]);
            } else if (line.startsWith("nanogrid")) {
                license.setNanogrid(line.split("=")[1].equals("1"));
            } else if (line.startsWith("owner")) {
                license.setOwner(line.split("=")[1]);
            } else if (line.startsWith("uuid")) {
                license.setUuid(line.split("=")[1]);
            }
        }
        return license;
    }
}
