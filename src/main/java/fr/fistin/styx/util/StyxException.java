package fr.fistin.styx.util;

/**
 * Created by AstFaster
 * on 06/11/2022 at 17:37
 */
public class StyxException extends RuntimeException {

    public StyxException(String message, Throwable cause) {
        super(message, cause);
    }

    public StyxException(String message) {
        super(message);
    }

    public StyxException(Throwable cause) {
        super(cause);
    }

}
