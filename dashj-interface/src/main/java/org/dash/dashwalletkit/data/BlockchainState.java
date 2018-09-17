package org.dash.dashwalletkit.data;

import java.util.Date;

public class BlockchainState {

    private final Date bestChainDate;
    private final int bestChainHeight;
    private int blocksLeft;

    public BlockchainState(Date bestChainDate, int bestChainHeight, int blocksLeft) {
        this.bestChainDate = bestChainDate;
        this.bestChainHeight = bestChainHeight;
        this.blocksLeft = blocksLeft;
    }

    public Date getBestChainDate() {
        return bestChainDate;
    }

    public int getBestChainHeight() {
        return bestChainHeight;
    }

    public int getBlocksLeft() {
        return blocksLeft;
    }
}
