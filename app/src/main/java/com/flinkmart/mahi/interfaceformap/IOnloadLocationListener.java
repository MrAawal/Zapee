package com.flinkmart.mahi.interfaceformap;

import java.util.List;

public interface IOnloadLocationListener {
    void onloadsucces(List<Latlong>latLngs);
    void onloadfaild(String message);
}
