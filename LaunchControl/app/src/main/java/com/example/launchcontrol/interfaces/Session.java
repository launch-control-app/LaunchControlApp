package com.example.launchcontrol.interfaces;

//source https://medium.com/@tsaha.cse/advanced-retrofit2-part-2-authorization-handling-ea1431cb86be
public interface Session {

    boolean isLoggedOn();

    void saveToken(String token);

    String getToken();

    void saveEmail(String email);

    String getEmail();

    void savePassword(String password);

    String getPassword();

    void invalidate();
}
