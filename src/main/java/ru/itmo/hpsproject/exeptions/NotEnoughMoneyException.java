package ru.itmo.hpsproject.exeptions;

public class NotEnoughMoneyException extends Exception{

    public NotEnoughMoneyException() {
        super("Недостаточно средств на счету");
    }
}
