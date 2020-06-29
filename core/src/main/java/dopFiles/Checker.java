package dopFiles;

import exceptions.FailedCheckException;

public interface Checker<T> {
    T checker(T t) throws FailedCheckException;
}