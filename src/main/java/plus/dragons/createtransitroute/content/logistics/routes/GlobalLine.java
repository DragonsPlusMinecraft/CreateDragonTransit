package plus.dragons.createtransitroute.content.logistics.routes;

import java.util.Set;

public class GlobalLine {

    private String name;
    private String code;
    private String color;
    private Mode mode;
    private Set<String> stations;


    public enum Mode{
        LINEAR,
        CIRCULAR
    }
}
