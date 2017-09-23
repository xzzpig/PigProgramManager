package com.xzzpig.pigprogrammanager;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.VariableProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public class APITest {
    HashMap<Object, Object> map = new HashMap<>();

    @Test
    public void solveArgs() throws Exception {
        API.echo(API.solveArgs("aaaa", "-a", "-b", "bbb", "-c", "ccc", "ddd", "-e"));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void echo() throws Exception {
        API.echo("aaaaa");
        API.echo("!bbbbb");
        API.verbose = true;
        API.echo("!ccccc");
    }

    @Test
    public void findVars() {
        API.echo(API.findVars("aaaa${bbb},${ccc:ddd}${eee:fff,jjj},${hhh:iii:jjj}"));
    }

    @Test
    public void solveVars() {
        API.registerValuableProvider(new VariableProvider() {
            @Override
            public String name() {
                return "bbb";
            }

            @Override
            public String provide(String... args) {
                return "b*3";
            }
        });
        API.registerValuableProvider(new VariableProvider() {
            @Override
            public String name() {
                return "ccc";
            }

            @Override
            public String provide(String... args) {
                return null;
            }
        });
        API.registerValuableProvider(new VariableProvider() {
            @Override
            public String name() {
                return "eee";
            }

            @Override
            public String provide(String... args) {
                return "e*3&" + Arrays.toString(args);
            }
        });
        API.echo("aaaa${bbb},${ccc:ddd}${eee:fff,jjj},${hhh:iii:jjj}\n", API.solveVars("aaaa${bbb},${ccc:ddd}${eee:fff,jjj},${hhh:iii:jjj}"));
    }

    @Test
    public void hash() {
        map.put("aaa", "bbb");
        map.put(new String2(), "ccc");
        API.echo(map.get(new String2()));
        API.echo(map.get("aaa"));
    }

    public class String2 {
        String string;

        String2() {
            this.string = "aaa";
        }

        @Override
        public int hashCode() {
            return string.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return string.equals(obj + "");
        }
    }

}