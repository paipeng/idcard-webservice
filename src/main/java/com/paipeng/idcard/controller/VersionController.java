package com.paipeng.idcard.controller;

import com.paipeng.idcard.config.ApplicationConfig;
import com.paipeng.idcard.config.VersionConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VersionController {
    private Logger log;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private VersionConfig versionConfig;

    public VersionController() {
        this.log = LogManager.getLogger(this.getClass().getName());
    }

    @GetMapping("/version")
    public Map version() {
        log.trace("version");
        Map versionMap = new HashMap();
        versionMap.put("name", versionConfig.getName());
        versionMap.put("version", versionConfig.getVersion());
        versionMap.put("time", stampToDate(Calendar.getInstance().getTimeInMillis()));
        versionMap.put("sha256", getSha256String());
        versionMap.put("tomcat", System.getProperty("catalina.base") + "/webapps/");
        versionMap.put("createData", versionConfig.getCreateData());
        versionMap.put("currentDir", System.getenv("PROJ_HOME"));

        return versionMap;
    }

    private String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    private String getSha256String() {
        String result = "";
        InputStream in;

        try {
            Process pro = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "sha256sum " + System.getProperty("catalina.base")});
            pro.waitFor();
            in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            result = read.readLine();
            if (result == null) {
                result = "";
            } else {
                result = result.split(" ")[0];
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

}
