package io.systom.coin.model;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
public class StrategyDeployVersion extends Strategy {

    private Integer version;
    private String description;
    private boolean isTrash;

    public StrategyDeployVersion() {
        super();
        this.version = version;
    }

    public StrategyDeployVersion(Integer id, String userId, Integer version) {
        super(id, userId);
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }
}