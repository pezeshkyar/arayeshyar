package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 1/16/2017.
 */
public class Assistant {
    private int id;
    private String username;
    private int taskGroupId;
    private String name;
    private String family;
    private int errorCode;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(int taskId) {
        this.taskGroupId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getFullName() {
        return name.concat(" ").concat(family);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
