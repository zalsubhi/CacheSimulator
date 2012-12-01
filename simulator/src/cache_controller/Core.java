/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cache_controller;

import cache.Cache;
import cache_controller.instruction.Instruction;
import cache_controller.instruction.InstructionThread;
import inputreader.InstructionInputFileReader;
import java.util.LinkedList;

/**
 *
 * @author Nathan
 */
public class Core {

    private Cache cache;
    
    private long previousCacheMiss;
    private long previousCacheHits;
    
    private LinkedList<InstructionThread> threads;
    
    public Core(Cache cache) {
        this.cache = cache;
        
        previousCacheMiss = 0;
        previousCacheHits = 0;
        
        threads = new LinkedList<>();
    }
    
    private InstructionThread getExecutingThread() {
        InstructionThread chosenThread = null;
        
        InstructionThread thread = null;
        for(int i = 0; i < getThreadCount(); i++) {
            thread = threads.pop();
            threads.addLast(thread);

            thread.decreaseWaitingTime();
            if(thread.getWaitingTime() == 0 && chosenThread == null) {
                chosenThread = thread;
            }
        }
        
        if(chosenThread != null) {
            // zet volgende thread keuze klaar, de thread vlak na degene die nu gekozen is
            do {
                thread = threads.pop();
                threads.addLast(thread);
            } while(thread.getId() != chosenThread.getId());
        }
        
        return chosenThread;
    }
    
    public void execute() {
        if(!threads.isEmpty()) {
            InstructionThread thread = getExecutingThread();
            
            if(thread != null) {
                thread.setNextInstruction();
                Instruction instr = thread.getInstruction();
                if(instr == null) {
                    // geen instructies over, thread is klaar
                    threads.remove(thread);
                } else {
                    thread.setWaitingTime(instr.getExecutionTime(cache));
                }
            }
        }        
    }
    
    public void addThread(long thread, InstructionInputFileReader reader) {
        threads.addFirst(new InstructionThread(thread, reader));
    }
    
    public int getThreadCount() {
        return threads.size();
    }
    
    public void print(long id) {
        long missDiff = cache.getTotalMisses() - previousCacheMiss;
        long hitDiff = cache.getCacheHits() - previousCacheHits;
        if(missDiff != 0 || hitDiff != 0) {
            //System.out.println("" + id + " " + instruction.getInstructionAdress());
            System.out.println("" + id + " " + missDiff);
            System.out.println("" + id + " " + hitDiff);
        }
        
        previousCacheMiss = cache.getTotalMisses();
        previousCacheHits = cache.getCacheHits();
    }
    
}