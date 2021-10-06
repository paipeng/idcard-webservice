package com.paipeng.idcard.service;

import com.paipeng.idcard.config.ApplicationConfig;
import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.model.LicenseBase64;
import com.paipeng.idcard.repository.LicenseRepository;
import com.paipeng.idcard.util.LicenseUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class LicneseService extends BaseService {
    private final static Logger logger = LogManager.getLogger(LicneseService.class.getSimpleName());

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ApplicationConfig applicationConfig;

    public List<License> getLicenses() throws Exception {
        logger.info("getLicenses");
        User user = getUserFromSecurity();
        if (user != null) {
            return licenseRepository.findAllByUser(user);
        } else {
            logger.error("no user found");
            throw new Exception("403");
        }
    }

    public License getLicenseById(Long id) {
        logger.info("getLicenseById");
        return licenseRepository.findById(id).orElse(null);
    }

    public License save(License license) throws Exception {
        logger.info("save: " + license);
        User user = getUserFromSecurity();
        if (user != null) {
            license.setUser(user);
            license.setUuid(UUID.randomUUID().toString());
            return licenseRepository.saveAndFlush(license);
        } else {
            logger.error("this licnese doesn't belong to this user");
            throw new Exception("403");
        }

    }

    public void delete(Long id) throws Exception {
        logger.info("delete: " + id);
        License license = licenseRepository.findById(id).orElse(null);
        if (license != null) {
            if (license.getFilePath() != null) {
                throw new Exception("409");
            }
            User currentUser = getUserFromSecurity();
            if (currentUser.getId() == license.getUser().getId()) {
                licenseRepository.delete(license);
            } else {
                logger.error("this licnese doesn't belong to this user");
                throw new Exception("403");
            }
        } else {
            logger.error("license not found -> 404");
            throw new Exception("404");
        }
    }

    public License update(Long id, License license) throws Exception {
        logger.info("update: " + id);
        License localLicense = licenseRepository.findById(id).orElse(null);
        if (localLicense != null) {
            if (localLicense.getFilePath() != null) {
                throw new Exception("409");
            }
            User currentUser = getUserFromSecurity();
            if (currentUser.getId().equals(localLicense.getUser().getId())) {
                // update
                localLicense.setOwner(license.getOwner());
                localLicense.setApp(license.getApp());
                localLicense.setExpire(license.getExpire());
                localLicense.setNanogrid(license.isNanogrid());

                localLicense = licenseRepository.saveAndFlush(localLicense);
                return localLicense;
            } else {
                logger.error("this licnese doesn't belong to this user");
                throw new Exception("403");
            }
        } else {
            logger.error("license not found -> 404");
            return null;
        }
    }

    public License genLicenseFile(Long id) throws Exception {
        logger.info("genLicenseFile: " + id);
        License license = licenseRepository.findById(id).orElse(null);
        if (license != null) {
            User currentUser = getUserFromSecurity();
            if (currentUser.getId().equals(license.getUser().getId())) {
                LicenseUtil.getInstance().loadKeys(
                        System.getenv("PROJ_HOME") + "/" + applicationConfig.getLicensePrivateKeyFile(),
                        System.getenv("PROJ_HOME") + "/" + applicationConfig.getLicensePublicKeyFile());
                LicenseUtil.getInstance().setOutputFilePath(applicationConfig.getLicenseOutputFilepath());
                try {
                    license = LicenseUtil.getInstance().genLicense(license);
                    return licenseRepository.saveAndFlush(license);
                } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    logger.error(e.getMessage());
                }
            } else {
                logger.error("this licnese doesn't belong to this user");
                throw new Exception("403");
            }
        }
        return license;
    }

    public byte[] downloadLicenseFileById(Long id) throws Exception {
        logger.info("downloadLicenseFileById: " + id);
        License license = licenseRepository.findById(id).orElse(null);
        if (license != null) {
            User currentUser = getUserFromSecurity();
            if (currentUser.getId().equals(license.getUser().getId())) {
                try {
                    String path = System.getenv("PROJ_HOME") + "/" + applicationConfig.getLicenseOutputFilepath() + "/" + license.getFilePath();
                    logger.info("downloadLicenseFilePath: " + path);
                    File file = new File(path);
                    InputStream is = new FileInputStream(file);
                    return IOUtils.toByteArray(is);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new Exception("404");
                }
            } else {
                logger.error("this licnese doesn't belong to this user");
                throw new Exception("403");
            }
        } else {
            logger.error("license not found by given id");
        }
        throw new Exception("404");
    }


    public LicenseBase64 downloadLicenseFile2ById(Long id) throws Exception {
        logger.info("downloadLicenseFileById: " + id);
        License license = licenseRepository.findById(id).orElse(null);
        if (license != null) {
            User currentUser = getUserFromSecurity();
            if (currentUser.getId().equals(license.getUser().getId())) {
                try {
                    String path = System.getenv("PROJ_HOME") + "/" + applicationConfig.getLicenseOutputFilepath() + "/" + license.getFilePath();
                    logger.info("downloadLicenseFilePath: " + path);
                    File file = new File(path);
                    InputStream is = new FileInputStream(file);
                    byte[] bytes =  IOUtils.toByteArray(is);
                    LicenseBase64 licenseBase64 = new LicenseBase64();
                    licenseBase64.setBase64(Base64.getEncoder().encodeToString(bytes));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new Exception("404");
                }
            } else {
                logger.error("this licnese doesn't belong to this user");
                throw new Exception("403");
            }
        } else {
            logger.error("license not found by given id");
        }
        throw new Exception("404");
    }
}
