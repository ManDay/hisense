package com.hisense.einkservice;

interface IEinkServiceInterface {
    void setSpeed(char speed);
    void clearScreen();
    char getCurrentSpeed();
    void setTemperature(boolean isNightLight, int brightness);
    boolean isNightLight();
    int getBrightness();
    void setLockedScreen(in char[] lockscreen);
}
