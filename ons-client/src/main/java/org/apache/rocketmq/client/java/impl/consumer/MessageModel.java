package org.apache.rocketmq.client.java.impl.consumer;

enum MessageModel {
    /**
     * In broadcasting mode, each consumer in same group is individual, they would maintain their own consumption
     * offset, and their subscription could be different with each other.
     */
    BROADCASTING("broadcasting"),
    /**
     * In clustering mode, each consumer in same group would maintain consumption offset in common. Which is
     * undefined if consumers have different subscription but belongs to the same group.
     */
    CLUSTERING("clustering");

    private final String name;

    MessageModel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
