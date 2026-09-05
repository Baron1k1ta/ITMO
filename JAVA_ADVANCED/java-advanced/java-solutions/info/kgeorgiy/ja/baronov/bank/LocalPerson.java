package info.kgeorgiy.ja.baronov.bank;

import java.io.Serializable;
import java.util.Map;

public record LocalPerson(String name, String surname, int passportID, Map<String, LocalAccount> accounts) implements Person, Serializable {}
