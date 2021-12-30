package atoms.model;

public final class Square {

    public Square(int playerId, int electronsCount) {
        this.playerId = playerId;
        this.electronsCount = electronsCount;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getElectronsCount() {
        return electronsCount;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setElectronsCount(int electronsCount) {
        this.electronsCount = electronsCount;
    }

    public void incElectronsCount() {
        this.electronsCount++;
    }

    public void incElectronsCount(int amount) {
        this.electronsCount += amount;
    }

    public void decElectronsCount() {
        this.electronsCount--;
    }

    public void decElectronsCount(int amount) {
        this.electronsCount -= amount;
    }

    public void changeElectronsCount(int change) {
        electronsCount += change;
    }

    public void set(int playerId, int electronsCount) {
        this.playerId = playerId;
        this.electronsCount = electronsCount;
    }

    private int playerId;
    private int electronsCount;
}
