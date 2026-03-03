package service;

import org.junit.jupiter.api.*;
import server.Server;

public class UserServiceTests {
    private static Server server;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
    }

    @Test
    public void newUserSuccess(){

    }

    @Test
    public void userTaken(){

    }
}
