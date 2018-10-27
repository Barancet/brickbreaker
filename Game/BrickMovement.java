/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import javafx.animation.Transition;
import javafx.animation.TranslateTransition;

/**
 *
 * @author Baran Cetin
 */
public class BrickMovement extends Transition {

    TranslateTransition tt;

    public BrickMovement() {
        tt = new TranslateTransition();
        
    }

    @Override
    public void interpolate(double frac) {

    }

}
