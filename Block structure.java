
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
    private int difficulty;

    // Constructor
    public Block(String data, String previousHash, int difficulty) {
        this.data = data;
        this.previousHash = previousHash;
        this.difficulty = difficulty;
        this.timeStamp = new Date().getTime();
        this.nonce = 0;
        this.hash = calculateHash();
    }

    // Calculate hash for this block
    public String calculateHash() {
        String calculatedhash = applySha256(
            previousHash + 
            Long.toString(timeStamp) + 
            Integer.toString(nonce) + 
            data
        );
        return calculatedhash;
    }

    // Mine the block with proof of work
    public void mineBlock(int difficulty) {
        String target = getDifficultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    // Create difficulty string (e.g., "000" for difficulty 3)
    public String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // Apply SHA-256 hash algorithm
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Getters and Setters
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    // toString method for display
    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + "'" +
                ", previousHash='" + previousHash + "'" +
                ", data='" + data + "'" +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                ", difficulty=" + difficulty +
                '}';
    }
}



import java.util.Arrays;

/**
 * Blockchain class that manages blocks using an array
 * Implements setBlock, getBlock, blocksExplorer, and mineBlock methods
 */
class Blockchain {
    private Block[] blockchain;
    private int size;
    private static final int INITIAL_CAPACITY = 10;
    private int difficulty = 4; // Default mining difficulty

    // Constructor
    public Blockchain() {
        blockchain = new Block[INITIAL_CAPACITY];
        size = 0;
        // Create genesis block
        createGenesisBlock();
    }

    // Create the first block (Genesis Block)
    private void createGenesisBlock() {
        Block genesisBlock = new Block("Genesis Block", "0", difficulty);
        genesisBlock.mineBlock(difficulty);
        blockchain[0] = genesisBlock;
        size = 1;
    }

    /**
     * setBlock method - Adds a new block to the blockchain
     * @param data The data to be stored in the new block
     */
    public void setBlock(String data) {
        // Check if array needs to be resized
        if (size >= blockchain.length) {
            resizeArray();
        }

        // Get the hash of the previous block
        String previousHash = blockchain[size - 1].getHash();

        // Create new block
        Block newBlock = new Block(data, previousHash, difficulty);

        // Mine the block
        newBlock.mineBlock(difficulty);

        // Add to blockchain
        blockchain[size] = newBlock;
        size++;

        System.out.println("New block added to blockchain at index: " + (size - 1));
    }

    /**
     * getBlock method - Retrieves a block by its index
     * @param index The index of the block to retrieve
     * @return The block at the specified index, or null if index is invalid
     */
    public Block getBlock(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index: " + index + ". Blockchain size: " + size);
            return null;
        }
        return blockchain[index];
    }

    /**
     * blocksExplorer method - Displays all blocks in the blockchain
     */
    public void blocksExplorer() {
        System.out.println("\n========== BLOCKCHAIN EXPLORER ==========");
        System.out.println("Total blocks in blockchain: " + size);
        System.out.println("Current difficulty: " + difficulty);
        System.out.println("==========================================\n");

        for (int i = 0; i < size; i++) {
            Block block = blockchain[i];
            System.out.println("Block #" + i + ":");
            System.out.println("  Hash: " + block.getHash());
            System.out.println("  Previous Hash: " + block.getPreviousHash());
            System.out.println("  Data: " + block.getData());
            System.out.println("  Timestamp: " + block.getTimeStamp());
            System.out.println("  Nonce: " + block.getNonce());
            System.out.println("  Difficulty: " + block.getDifficulty());
            System.out.println("  Mined: " + isBlockMined(block));
            System.out.println("  Valid: " + isValidBlock(block, i));
            System.out.println();
        }
    }

    /**
     * mineBlock method - Mines a specific block at given index
     * @param index The index of the block to mine
     * @param newDifficulty The difficulty level for mining
     */
    public void mineBlock(int index, int newDifficulty) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index for mining: " + index);
            return;
        }

        Block block = blockchain[index];
        System.out.println("Mining block at index " + index + " with difficulty " + newDifficulty);
        block.setDifficulty(newDifficulty);
        block.mineBlock(newDifficulty);
    }

    // Helper method to resize array when needed
    private void resizeArray() {
        int newCapacity = blockchain.length * 2;
        Block[] newArray = new Block[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = blockchain[i];
        }
        blockchain = newArray;
        System.out.println("Blockchain array resized to capacity: " + newCapacity);
    }

    // Validate if a block is properly mined
    private boolean isBlockMined(Block block) {
        String target = new String(new char[block.getDifficulty()]).replace('\0', '0');
        return block.getHash().substring(0, block.getDifficulty()).equals(target);
    }

    // Validate if a block is valid (correct previous hash)
    private boolean isValidBlock(Block block, int index) {
        if (index == 0) {
            return "0".equals(block.getPreviousHash());
        }
        return blockchain[index - 1].getHash().equals(block.getPreviousHash());
    }

    // Validate entire blockchain
    public boolean isChainValid() {
        for (int i = 1; i < size; i++) {
            Block currentBlock = blockchain[i];
            Block previousBlock = blockchain[i - 1];

            // Check if current block hash is correct
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal at block " + i);
                return false;
            }

            // Check if previous hash matches
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal at block " + i);
                return false;
            }

            // Check if block is mined
            if (!isBlockMined(currentBlock)) {
                System.out.println("Block " + i + " is not mined properly");
                return false;
            }
        }
        return true;
    }

    // Getters and setters
    public int getSize() {
        return size;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    // Get blockchain statistics
    public void getChainStats() {
        System.out.println("\n========== BLOCKCHAIN STATISTICS ==========");
        System.out.println("Total blocks: " + size);
        System.out.println("Current difficulty: " + difficulty);
        System.out.println("Chain valid: " + isChainValid());
        System.out.println("Array capacity: " + blockchain.length);

        if (size > 1) {
            System.out.println("Latest block hash: " + blockchain[size-1].getHash());
            System.out.println("Genesis block hash: " + blockchain[0].getHash());
        }
        System.out.println("==========================================\n");
    }
}

