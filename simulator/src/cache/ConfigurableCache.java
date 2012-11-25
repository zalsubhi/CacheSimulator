/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import cache_controller.instruction.MemoryAccess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.GCASimulator;

/**
 *
 * @author Nathan
 */
public class ConfigurableCache extends Cache implements ChangeListener{
    
    BufferedReader reader;
    private boolean closed;
    
    private int blockCount;
    private HashMap<Long, Integer> caches;
    private Cache cache;
    
    private long instructionNr;
    
    public ConfigurableCache(int blockCount, File config) {
        this.blockCount = blockCount;
        instructionNr = 0;
        try {
            reader = new BufferedReader(new FileReader(config));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        closed = false;
        caches = new HashMap<>();
        caches.put((long)0, 12);
        
        readMore();
    }
    
    @Override
    public long getInstructionTime(MemoryAccess instr) {
        if(caches.containsKey(instructionNr)) {
            cache = new BasicCache(blockCount, caches.get(instructionNr));
            cache.setCacheHits(getCacheHits());
            cache.setColdMiss(getColdMiss());
            cache.setConflictMiss(getConflictMiss());
            cache.addChangeListener(this);
            
            caches.remove(instructionNr);
            if(caches.isEmpty() && !closed) {
                readMore();
            }
        }
        
        instructionNr++;
        return cache.getInstructionTime(instr);
    }
    
    @Override
    public void clearCacheMemory() {
        cache.clearCacheMemory();
    }
    
    public void readMore() {
        int count = 0;
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        while(line != null && count < GCASimulator.READING_AMOUNT) {
            String[] parts = line.split(" ");
            long instrNr = Long.parseLong(parts[0]);
            int ways = Integer.parseInt(parts[1]);
            
            caches.put(instrNr, ways);
            
            count++;
            if(count < GCASimulator.READING_AMOUNT) {
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        if(line == null) {
            try {
                reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            closed = true;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setCacheHits(cache.getCacheHits());
        setColdMiss(cache.getColdMiss());
        setConflictMiss(cache.getConflictMiss());
    }
    
}
