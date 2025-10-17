package service;

import com.sun.net.httpserver.HttpServer;

public class AuthServer {
    public static void register(HttpServer server) {
        server.createContext("/api/v1/auth/signup", withMethod("POST", AuthServer::signup));
        server.createContext("/api/v1/auth/login", withMethod("POST", AuthServer::login));
        server.createContext("/api/v1/auth/recuperar-senha", withMethod("POST", AuthServer::recuperarSenha));
        server.createContext("/api/v1/auth/logout", withMethod("POST", AuthServer::logout));
        server.createContext("/api/v1/auth/me", withMethod("GET", AuthServer::me));
    }
    public void signup() {

    }
    public void login() {

    }
    public void recuperarSenha() {

    }
    public void logout() {

    }
    public void me() {

    }
}