/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.Advancedemblem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 *
 * @author Gregory Fournier
 */
public final class Sounds {
    
   public static Sound pikachu = Gdx.audio.newSound(Gdx.files.internal("soldier/pikachu.wav"));
    public static Sound pikachu2 = Gdx.audio.newSound(Gdx.files.internal("soldier/pikachu2.wav"));
    
    private Sounds() {
        
    }
    
}
