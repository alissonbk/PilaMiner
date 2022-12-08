package dev.alissonbk.util;

public class ServerEndpoints {
    public static final String GET_ALL_USERS = "http://srv-ceesp.proj.ufsm.br:8097/usuario/all";
    public static final String SAVE_USER = "http://srv-ceesp.proj.ufsm.br:8097/usuario";
    public static final String FIND_USER_BY_KEY = "http://srv-ceesp.proj.ufsm.br:8097/usuario/findByChave";
    public static final String PILA_COIN_VALIDATOR = "http://srv-ceesp.proj.ufsm.br:8097/pilacoin";
    public static final String PILA_COIN_EXISTS = "http://srv-ceesp.proj.ufsm.br:8097/pilacoin";
    public static final String VALIDATE_COIN_OTHER_USER = "http://srv-ceesp.proj.ufsm.br:8097/pilacoin/validaPilaOutroUsuario";
    public static final String WEBSOCKET = "ws://srv-ceesp.proj.ufsm.br:8097/websocket/websocket";
}
