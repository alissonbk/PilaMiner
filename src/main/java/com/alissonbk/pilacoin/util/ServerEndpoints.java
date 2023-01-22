package com.alissonbk.pilacoin.util;

public class ServerEndpoints {
    private static final String SERVER_URL = "http://srv-ceesp.proj.ufsm.br:8097";
    public static final String GET_ALL_USERS = SERVER_URL + "/usuario/all";
    public static final String SAVE_USER = SERVER_URL + "/usuario";
    public static final String FIND_USER_BY_KEY = SERVER_URL + "/usuario/findByChave";
    public static final String PILA_COIN_VALIDATOR = SERVER_URL + "/pilacoin";
    public static final String PILA_COIN_EXISTS = SERVER_URL + "/pilacoin";
    public static final String VALIDATE_COIN_OTHER_USER = SERVER_URL + "/pilacoin/validaPilaOutroUsuario";
    public static final String VALIDATE_BLOCK = SERVER_URL + "/bloco";
    public static final String GET_BLOCK_BY_NUM = SERVER_URL + "/bloco";
    public static final String GET_ALL_BLOCKS = SERVER_URL + "/bloco/all";
    public static final String VALIDATE_BLOCK_OTHER_USER = SERVER_URL + "/bloco/validaBlocoOutroUsuario";
    public static final String TRANSFER_PILA_COIN = SERVER_URL + "/pilacoin/transfere";

    public static final String WEBSOCKET = "ws://srv-ceesp.proj.ufsm.br:8097/websocket/websocket";
}
