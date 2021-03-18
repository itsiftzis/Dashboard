package org.dashboard.dto;

public class Info {

    public static Info emptyInfo() {
        Build build = new Build();
        Info info = new Info();
        info.setBuild(build);
        return info;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    private Build build;

    @Override
    public String toString() {
        return "Info{" +
            "build=" + build +
            '}';
    }
}
