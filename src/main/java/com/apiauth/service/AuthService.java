package com.apiauth.service;

import com.apiauth.model.User;
import com.apiauth.repository.AuthServerRepository;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthServerRepository repository = new AuthServerRepository();

    public ServiceResult signup(Map<String, String> body) {
        String[] required = new String[]{"email", "password", "doc_number", "username", "full_name"};
        for (String field : required) {
            if (!body.containsKey(field)) {
                return error(HttpURLConnection.HTTP_BAD_REQUEST, "Campo obrigatorio ausente: " + field);
            }
        }

        if (repository.findUserByEmail(body.get("email")).isPresent()) {
            return error(HttpURLConnection.HTTP_CONFLICT, "Email ja cadastrado");
        }
        if (repository.findUserByDocument(body.get("doc_number")).isPresent()) {
            return error(HttpURLConnection.HTTP_CONFLICT, "Documento ja cadastrado");
        }

        User user = new User(null,
                body.get("email"),
                body.get("password"),
                body.get("doc_number"),
                body.get("username"),
                body.get("full_name"),
                true,
                null,
                null);

        user = repository.createUser(user);
        repository.setLoggedIn(user.getId(), true);
        user.setLoggedin(true);

        String token = issueToken(user);
        String response = "{\"token\":\"" + token + "\",\"user\":" + userToJson(user) + "}";
        return new ServiceResult(HttpURLConnection.HTTP_CREATED, response);
    }

    public ServiceResult login(Map<String, String> body) {
        if (!body.containsKey("login") || !body.containsKey("password")) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Informe login e password");
        }

        Optional<User> userOptional = repository.findUserByEmail(body.get("login"));
        if (userOptional.isEmpty()) {
            return error(HttpURLConnection.HTTP_NOT_FOUND, "Usuario nao encontrado");
        }

        User user = userOptional.get();
        if (!user.getPassword().equals(body.get("password"))) {
            return error(HttpURLConnection.HTTP_UNAUTHORIZED, "Senha invalida");
        }

        repository.setLoggedIn(user.getId(), true);
        user.setLoggedin(true);

        String token = issueToken(user);
        String response = "{\"token\":\"" + token + "\"}";
        return new ServiceResult(HttpURLConnection.HTTP_OK, response);
    }

    public ServiceResult recover(Map<String, String> body) {
        if (!body.containsKey("document") || !body.containsKey("email") || !body.containsKey("new_password")) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Campos document, email e new_password sao obrigatorios");
        }

        Optional<User> userOptional = repository.findUserByEmailAndDocument(body.get("email"), body.get("document"));
        if (userOptional.isEmpty()) {
            return error(HttpURLConnection.HTTP_NOT_FOUND, "Dados nao conferem para o mesmo usuario");
        }

        User user = userOptional.get();
        repository.updatePassword(user.getId(), body.get("new_password"));
        user.setPassword(body.get("new_password"));
        repository.setLoggedIn(user.getId(), true);
        user.setLoggedin(true);

        String token = issueToken(user);
        String response = "{\"token\":\"" + token + "\"}";
        return new ServiceResult(HttpURLConnection.HTTP_OK, response);
    }

    public ServiceResult logout(String token) {
        if (token == null || token.isBlank()) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Cabecalho Authorization: SDWork <token> obrigatorio");
        }

        Optional<User> userOptional = repository.findUserByToken(token);
        if (userOptional.isEmpty()) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Token invalido");
        }

        User user = userOptional.get();
        repository.deleteToken(token);
        repository.setLoggedIn(user.getId(), false);
        user.setLoggedin(false);

        return new ServiceResult(HttpURLConnection.HTTP_OK, "{\"message\":\"Logout realizado\"}");
    }

    public ServiceResult me(String token) {
        if (token == null || token.isBlank()) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Cabecalho Authorization: SDWork <token> obrigatorio");
        }

        Optional<User> userOptional = repository.findUserByToken(token);
        if (userOptional.isEmpty()) {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, "Token invalido");
        }

        User user = userOptional.get();
        return new ServiceResult(HttpURLConnection.HTTP_OK, userToJson(user));
    }

    private ServiceResult error(int status, String message) {
        return new ServiceResult(status, "{\"error\":\"" + escape(message) + "\"}");
    }

    private String issueToken(User user) {
        String raw = user.getEmail() + ":" + user.getDocNumber();
        String generated = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        repository.saveToken(user.getId(), generated);
        return generated;
    }

    private String userToJson(User user) {
        return "{"
                + "\"id\":" + user.getId() + ","
                + "\"email\":\"" + escape(user.getEmail()) + "\","
                + "\"doc_number\":\"" + escape(user.getDocNumber()) + "\","
                + "\"password\":\"" + escape(user.getPassword()) + "\","
                + "\"username\":\"" + escape(user.getUsername()) + "\","
                + "\"full_name\":\"" + escape(user.getFullName()) + "\","
                + "\"loggedin\":" + user.getLoggedin() + ","
                + "\"created_at\":\"" + escape(String.valueOf(user.getCreatedAt())) + "\","
                + "\"updated_at\":\"" + escape(String.valueOf(user.getUpdatedAt())) + "\""
                + "}";
    }

    private String escape(String message) {
        return message.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static class ServiceResult {
        private final int status;
        private final String body;

        public ServiceResult(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public int status() {
            return status;
        }

        public String body() {
            return body;
        }
    }
}
