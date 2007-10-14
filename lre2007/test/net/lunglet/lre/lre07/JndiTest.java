package net.lunglet.lre.lre07;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.Test;

public final class JndiTest {
    @Test
    public void test() throws NamingException {
        InitialContext context = new InitialContext();
        System.out.println(context.lookup(Context.INITIAL_CONTEXT_FACTORY));
    }
}
