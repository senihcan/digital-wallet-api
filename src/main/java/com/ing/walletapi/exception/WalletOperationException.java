package com.ing.walletapi.exception;

public class WalletOperationException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 719683397402426599L;

	public WalletOperationException(String message) {
        super(message);
    }

    public WalletOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}