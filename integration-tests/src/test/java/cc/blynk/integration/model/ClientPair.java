package cc.blynk.integration.model;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public class ClientPair {

    public TestAppClient appClient;

    public TestHardClient hardwareClient;

    public String token;

    public ClientPair(TestAppClient appClient, TestHardClient hardwareClient, String token) {
        this.appClient = appClient;
        this.hardwareClient = hardwareClient;
        this.token = token;
    }

    public void stop() {
        appClient.stop();
        hardwareClient.stop();
    }
}
