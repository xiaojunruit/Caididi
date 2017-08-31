package com.laoodao.caididi;

import java.util.HashSet;
import java.util.Set;

import ds.gendalf.PrefsConfig;

@PrefsConfig("LocalSetting")
public interface PrefsConfigurator {
    String token = "";
    String hash = "";
    String uid = "";
    int cid = 0;
    Set<String> cookies = new HashSet<>();
    Set<String> keywords = new HashSet<>();

    int versionCode = 0;
    boolean launched = false;
    String launchedAd = "";
}