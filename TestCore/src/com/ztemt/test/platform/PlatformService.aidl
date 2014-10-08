package com.ztemt.test.platform;

interface PlatformService {
    void notifyStart();
    void updateProgress(int progress);
    void notifyStop(in byte[] bytes);
    void notifyStopWithName(in byte[] bytes, in String fileName);
}