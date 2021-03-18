package org.dashboard.dto;

public class Build {
    private String artifact;
    private String name;
    private String group;
    private String version;
    private Object time;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Build{" +
            "artifact='" + artifact + '\'' +
            ", name='" + name + '\'' +
            ", group='" + group + '\'' +
            ", version='" + version + '\'' +
            ", time=" + time +
            ", id='" + id + '\'' +
            '}';
    }
}
