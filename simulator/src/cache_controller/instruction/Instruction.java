/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cache_controller.instruction;

import cache.Cache;

/**
 *
 * @author naclaeys
 */
public abstract class Instruction {
    
    // description van instructie in trace file
    private String description;
    
    private long instructionAdress;
    
    public Instruction(String description, long instructionAdress) {
        this.description = description;
        this.instructionAdress = instructionAdress;
    }

    public String getDescription() {
        return description;
    }

    public long getInstructionAdress() {
        return instructionAdress;
    }
    
    public abstract long getExecutionTime(Cache cache);
    
}